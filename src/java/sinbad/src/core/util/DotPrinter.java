package core.util;

public class DotPrinter implements Runnable {
    private final static int MAX_DELAY = 3000;
    private final static int DELAY_INCR = 250;
    private int delay = 500;
    
    @Override
    public void run() {
        try {
            while (true) {
                System.out.print(".");
                Thread.sleep(delay);
                delay = delay >= MAX_DELAY ? delay : delay + DELAY_INCR;
            }
        } catch (InterruptedException e) {
            System.out.println("Done");
            System.out.flush();
            // ok just stop...
            return;
        }
    }
    
}