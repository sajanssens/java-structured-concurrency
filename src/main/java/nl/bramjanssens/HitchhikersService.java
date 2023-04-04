package nl.bramjanssens;

public enum HitchhikersService {
    INSTANCE;

    public String findUser(int userId) {
        makeSlowConnection(100);
        System.out.println("findUser executed");
        if (userId == 42) return "Arthur Dent";
        throw new IllegalArgumentException("This is not a hitchhiker!");
    }

    public int fetchOrder(int orderId) {
        if (orderId < 0) throw new IllegalArgumentException("Black hole");

        makeSlowConnection(1000);
        System.out.println("fetchOrder executed");
        return orderId == 42 ? 1337 : 0;
    }

    private static void makeSlowConnection(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}
