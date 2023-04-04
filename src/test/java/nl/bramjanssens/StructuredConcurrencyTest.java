package nl.bramjanssens;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StructuredConcurrencyTest {

    private StructuredConcurrency target;
    private final Response arthurDent = new Response("Arthur Dent", 1337);

    @BeforeEach
    void setUp() {
        this.target = new StructuredConcurrency();
    }

    @Test
    void happyFlow() throws ExecutionException, InterruptedException {
        Response result = target.handle(42, 42);
        assertEquals(arthurDent, result);
    }

    @Test
    void whenFetchOrderFailsFindUserIsCancelled() {
        ExecutionException e = assertThrows(ExecutionException.class, () -> target.handle(42, -42));

        // Assert that findUser is not called (FOR NOW: see console)

        // Original exception is retained:
        assertEquals(IllegalArgumentException.class, e.getCause().getClass());
        assertEquals("Black hole", e.getCause().getMessage());
    }

    @Test
    void whenFindUserFailsFetchOrderIsCancelled() {
        ExecutionException e = assertThrows(ExecutionException.class, () -> target.handle(44, 42));

        // Assert that fetchOrder is not called (FOR NOW: see console)

        // Original exception is retained:
        assertEquals(IllegalArgumentException.class, e.getCause().getClass());
        assertEquals("This is not a hitchhiker!", e.getCause().getMessage());
    }

    @Test
    void whenParentThreadFailsAllSubtasksAreCancelled() {
        new Thread(() -> {
            runHandle();
            throw new RuntimeException();
        }).start();
    }

    private void runHandle() {
        try {
            target.handle(42, 42);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
