package org.example;
public class DriverThread extends WorkerThread
{
    public final int BOUND=2000;
    public DriverThread(Warehouse warehouse, DriverPanel panel)
    {
        super(warehouse, panel);
    }
    @Override
    protected boolean doWork() throws InterruptedException
    {
        myPanel.updateStatus("נח בבית...");
        int restingTime = randomGenerator.nextInt(2000);
        Thread.sleep(restingTime);
        myPanel.updateStatus("נוסע לכיוון המחסן...");
        int drivingTime = 2000 + randomGenerator.nextInt(1000);
        Thread.sleep(drivingTime);
        myPanel.updateStatus("ממתין לאסוף תפוזים...");
        long waitTimeNow = warehouse.takeOrange(totalWaitTime);
        if (waitTimeNow == -1)
        {
            return false;
        }
        totalWaitTime = totalWaitTime + waitTimeNow;
        return true;
    }
}