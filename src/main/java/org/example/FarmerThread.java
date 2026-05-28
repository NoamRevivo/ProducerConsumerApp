package org.example;
import javax.swing.SwingUtilities;
public class FarmerThread extends WorkerThread
{
    private static final int MAX_PICKING_TIME_MS = 3000;

    public FarmerThread(Warehouse warehouse, FarmerPanel panel)
    {
        super(warehouse, panel);
    }
    @Override
    protected boolean doWork() throws InterruptedException
    {
        SwingUtilities.invokeLater(() -> myPanel.updateStatus("קוטף תפוזים בשדה..."));
        int pickingTime = randomGenerator.nextInt(MAX_PICKING_TIME_MS);
        Thread.sleep(pickingTime);
        SwingUtilities.invokeLater(() -> {
            myPanel.updateStatus("ממתין להכניס למחסן...");
            myPanel.startWaitingClock();
        });
        long waitTimeNow = warehouse.putOrange(totalWaitTime);
        SwingUtilities.invokeLater(() -> myPanel.stopWaitingClock());
        if (waitTimeNow == -1)
        {
            return false;
        }
        totalWaitTime = totalWaitTime + waitTimeNow;
        return true;
    }
}