package nl.bramjanssens;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

class StructuredConcurrencyTest {

    private StructuredConcurrency target;

    @BeforeEach
    void setUp() {
        this.target = new StructuredConcurrency();
    }

    @Test
    void handle() throws ExecutionException, InterruptedException {
        Response handle = target.handle(42, 42);
    }
}
