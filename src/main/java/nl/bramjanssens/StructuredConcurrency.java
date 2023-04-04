package nl.bramjanssens;

import jdk.incubator.concurrent.StructuredTaskScope;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static nl.bramjanssens.HitchhikersService.INSTANCE;

public class StructuredConcurrency {

    private final HitchhikersService service = INSTANCE;

    Response handle(int userId, int orderId) throws ExecutionException, InterruptedException {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            Future<String> user = scope.fork(() -> service.findUser(userId));
            Future<Integer> order = scope.fork(() -> service.fetchOrder(orderId));

            scope.join();           // Join both forks          (can throw InterruptedException)
            scope.throwIfFailed();  // ... and propagate errors (can throw ExecutionException)

            // Here, both forks have succeeded, so compose their results
            return new Response(user.resultNow(), order.resultNow());
        }
    }
}
