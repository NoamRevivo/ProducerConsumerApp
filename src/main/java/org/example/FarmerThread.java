package org.example;

public class FarmerThread extends WorkerThread
{
    public FarmerThread(Warehouse warehouse, FarmerPanel panel)
    {
        super(warehouse, panel);
    }
    @Override
    protected boolean doWork() throws InterruptedException
    {
        myPanel.updateStatus("קוטף תפוזים בשדה...");
        int pickingTime = randomGenerator.nextInt(3000);
        Thread.sleep(pickingTime);
        myPanel.updateStatus("ממתין להכניס למחסן...");
        long waitTimeNow = warehouse.putOrange(totalWaitTime);
        if (waitTimeNow == -1)
        {
            return false;
        }
        totalWaitTime = totalWaitTime + waitTimeNow;
        return true;
    }
}