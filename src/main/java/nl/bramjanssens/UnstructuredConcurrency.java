package nl.bramjanssens;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static nl.bramjanssens.HitchhikersService.INSTANCE;

public class UnstructuredConcurrency {

    private final HitchhikersService service = INSTANCE;
    private final ExecutorService esvc = Executors.newCachedThreadPool();

    Response handle(int userId, int orderId) throws ExecutionException, InterruptedException {
        Future<String> user = esvc.submit(() -> service.findUser(userId));
        Future<Integer> order = esvc.submit(() -> service.fetchOrder(orderId));

        var theUser = user.get();   // join findUser (can throw ExecutionException, InterruptedException)
        var theOrder = order.get();  // join fetchOrder (can throw ExecutionException, InterruptedException)

        return new Response(theUser, theOrder);
    }

    // Problems:
    // - If findUser() takes a long time to execute, but fetchOrder() fails in the meantime,
    //   then handle() will wait unnecessarily for findUser() by blocking on user.get() rather than cancelling it
    // - If findUser() throws an exception then user.get() will throw an exception
    //   but fetchOrder() will continue to run in its own thread: thread leakage.
    // - If the thread executing handle() is interrupted, the interruption will
    //   not propagate to the subtasks: both the findUser() and fetchOrder() threads will leak.
}
