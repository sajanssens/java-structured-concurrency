package nl.bramjanssens;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UnstructuredConcurrencyTest {

    private UnstructuredConcurrency target;
    private final Response arthurDent = new Response("Arthur Dent", 1337);

    @BeforeEach
    void setUp() {
        this.target = new UnstructuredConcurrency();
    }

    @Test
    void happyFlow() throws ExecutionException, InterruptedException {
        Response result = target.handle(42, 42);
        assertEquals(arthurDent, result);
    }

    @Test
    void whenFetchOrderFailsThenThereIsAnUnnecessaryLongWaitOnFindUser() {
        assertThrows(ExecutionException.class, () -> target.handle(42, -42));

        // findUser is executed while it could have been cancelled
    }

    @Test
    void whenFindUserFailsThenFetchOrderLeaks() throws InterruptedException {
        assertThrows(ExecutionException.class, () -> target.handle(0, 42));

        // fetchOrder is executed while it could have been cancelled
        Thread.sleep(1005);
    }

    @Test
    void whenParentThreadIsInterruptedAllSubtasksLeak() throws InterruptedException {
        Thread parent = new Thread(this::runHandle);
        parent.start();
        parent.interrupt();

        // findUser and fetchOrder are still executed while they could have been cancelled
        Thread.sleep(1200);
    }

    private void runHandle() {
        try {
            target.handle(42, 42);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            System.out.println("Parent is interrupted!");
        }
    }
}
