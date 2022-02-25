package at.pl.razer.util;

public class BetterSleep {

    private static final long PRECISION = 1000 * 60;

    public void sleep(long time) throws InterruptedException {
        long end = System.currentTimeMillis() + time;
        long wait;
        while ((wait = end - System.currentTimeMillis()) > 0) {
            Thread.sleep(Math.min(PRECISION, wait));
        }
    }

}
