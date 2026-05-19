package org.example;

import java.util.Random;

public abstract class WorkerThread extends Thread {
    protected Warehouse warehouse;
    protected WorkerPanel myPanel;
    protected long totalWaitTime;
    protected int orangesHandled;
    protected Random randomGenerator;

    public WorkerThread(Warehouse warehouse, WorkerPanel panel) {
        this.warehouse = warehouse;
        this.myPanel = panel;
        this.totalWaitTime = 0;
        this.orangesHandled = 0;
        this.randomGenerator = new Random();
    }

    @Override
    public void run() {
        try {
            while (true) {
                // קריאה פולימורפית
                boolean stillWorking = doWork();

                if (stillWorking == false) {
                    myPanel.updateStatus("מפוטר! (המתין מעל 10 שניות)");
                    break;
                }

                orangesHandled = orangesHandled + 1;
                myPanel.updateCount(orangesHandled);
            }
        } catch (InterruptedException e) {
            System.out.println("התהליך הופסק באופן חריג.");
        }
    }

    // פולימורפיזם - הבנים יממשו את זה
    protected abstract boolean doWork() throws InterruptedException;
}