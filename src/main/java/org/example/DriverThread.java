package org.example;

import javax.swing.SwingUtilities;

public class DriverThread extends WorkerThread
{
    //קבועים
    private static final int MAX_REST_TIME_MS = 2000;
    private static final int BASE_DRIVING_TIME_MS = 2000;
    private static final int MAX_RANDOM_DRIVING_TIME_MS = 1000;
    //קבועים
    public DriverThread(Warehouse warehouse, DriverPanel panel)
    {
        super(warehouse, panel);
    }
    @Override
    protected boolean doWork() throws InterruptedException
    {
        SwingUtilities.invokeLater(() -> myPanel.updateStatus("נח בבית..."));
        int restingTime = randomGenerator.nextInt(MAX_REST_TIME_MS);
        Thread.sleep(restingTime);
        SwingUtilities.invokeLater(() -> myPanel.updateStatus("נוסע לכיוון המחסן..."));
        int drivingTime = BASE_DRIVING_TIME_MS + randomGenerator.nextInt(MAX_RANDOM_DRIVING_TIME_MS);
        Thread.sleep(drivingTime);
        SwingUtilities.invokeLater(() -> {
            myPanel.updateStatus("ממתין לאסוף תפוזים...");
            myPanel.startWaitingClock();
        });
        long waitTimeNow = warehouse.takeOrange(totalWaitTime);
        SwingUtilities.invokeLater(() -> myPanel.stopWaitingClock());
        if (waitTimeNow == -1)
        {
            return false;
        }
        totalWaitTime = totalWaitTime + waitTimeNow;
        return true;
    }
}