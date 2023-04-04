package nl.bramjanssens;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UnstructuredConcurrencyTest {

    private UnstructuredConcurrency target;
    private Response arthurDent = new Response("Arthur Dent", 1337);

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
        ExecutionException e = assertThrows(ExecutionException.class, () -> target.handle(42, -42));

        // findUser is executed while it could have been cancelled
    }

    @Test
    void whenFindUserFailsThenFetchOrderLeaks() throws InterruptedException {
        ExecutionException e = assertThrows(ExecutionException.class, () -> target.handle(0, 42));

        // fetchOrder is executed while it could have been cancelled
        Thread.sleep(1005);
    }

    @Test
    void whenParentThreadFailsAllSubtasksLeak() throws InterruptedException {
        assertThrows(RuntimeException.class, this::runHandleInFailingParentThread);

        // findUser and fetchOrder are still executed while they could have been cancelled
        Thread.sleep(1005);
    }

    private void runHandleInFailingParentThread() {
        new Thread(this::handle).start();
        throw new RuntimeException("Parent fails");
    }

    private void handle() {
        try {
            target.handle(42, 42);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
