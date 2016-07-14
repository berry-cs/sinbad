/**
 * This class is meant to run in a separate thread and displays a message
 * and then periodic dots (for example, as a file is being downloaded). The
 * constructor takes an initial message and a delay amount (milliseconds). It
 * waits for that delay amount before printing the initial message, and then
 * periodically prints out dots after that. 
 */
package core.util;

public class DotPrinter implements Runnable {
    private final static int MAX_DELAY = 3000;
    private final static int DELAY_INCR = 250;

    private static boolean enabled = true;
    
    private String firstMessage;
    private int firstMessageDelay;  // how long to wait before showing the first message
    private boolean firstMessagePrinted;

    private int delay = 500;

    public DotPrinter(String firstMessage) {
        this(firstMessage, MAX_DELAY);
    }
    
    public DotPrinter(String firstMessage, int firstMessageDelay) {
        this.firstMessage = firstMessage;
        this.firstMessageDelay = firstMessageDelay;
    }
    
    public static void setEnabled(boolean enabled) {
        enabled = false;
    }

    @Override
    public void run() {
        if (!enabled) { return; }
        
        try {
            Thread.sleep(firstMessageDelay);
            System.out.println(firstMessage);
            System.out.flush();
            firstMessagePrinted = true;
            
            while (true) {
                System.out.print(".");
                Thread.sleep(delay);
                delay = delay >= MAX_DELAY ? delay : delay + DELAY_INCR;
            }
        } catch (InterruptedException e) {
            if (firstMessagePrinted) {
                System.out.println("Done");
                System.out.flush();
            }
            // ok just stop...
            return;
        }
    }
    
}