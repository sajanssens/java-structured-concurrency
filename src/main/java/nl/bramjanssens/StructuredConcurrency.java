package nl.bramjanssens;

import jdk.incubator.concurrent.StructuredTaskScope;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class StructuredConcurrency {

    Response handle(int userId, int orderId) throws ExecutionException, InterruptedException {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            Future<String> user = scope.fork(() -> findUser(userId));
            Future<Integer> order = scope.fork(() -> fetchOrder(orderId));

            scope.join();           // Join both forks
            scope.throwIfFailed();  // ... and propagate errors

            // Here, both forks have succeeded, so compose their results
            return new Response(user.resultNow(), order.resultNow());
        }
    }

    private String findUser(int userId) {
        System.out.println("findUser");
        if (userId == 42) return "Arthur Dent";
        throw new IllegalArgumentException("This is not a hitchhiker!");
    }

    private int fetchOrder(int orderId) {
        System.out.println("fetchOrder");
        return orderId == 42 ? 1337 : 0;
    }
}
