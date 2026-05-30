package org.example;
import javax.swing.*;
import java.awt.*;

public class Warehouse extends JPanel
{
    private static final int INITIAL_CAPACITY = 10;
    private static final long MAX_WAIT_TIME_MS = 10000; // 10 שניות במילישניות
    private static final int ORANGE_SIZE = 30;
    private static final int SPACING = 40;
    private static final int START_X = 20;
    private static final int START_Y = 70;
    private int capacity;
    private int currentOranges;

    public Warehouse()
    {
        this.capacity = INITIAL_CAPACITY;
        this.currentOranges = 0;
        setBorder(BorderFactory.createTitledBorder("מחסן מרכזי"));
    }

    public synchronized long putOrange(long totalWaitTimeSoFar) throws InterruptedException
    {
        long startTime = System.currentTimeMillis();
        while (currentOranges == capacity)
        {
            long timePassedSoFar = System.currentTimeMillis() - startTime;
            long timeLeftToWait = MAX_WAIT_TIME_MS - totalWaitTimeSoFar - timePassedSoFar;
            if (timeLeftToWait <= 0)
            {
                return -1; // החקלאי חצה את רף ה-10 שניות ולכן יפוטר
            }
            wait(timeLeftToWait);
        }
        long endTime = System.currentTimeMillis();
        currentOranges = currentOranges + 1;
        repaint();
        notifyAll();
        return (endTime - startTime);
    }

    public synchronized long takeOrange(long totalWaitTimeSoFar) throws InterruptedException
    {
        long startTime = System.currentTimeMillis();
        while (currentOranges == 0)
        {
            long timePassedSoFar = System.currentTimeMillis() - startTime;
            long timeLeftToWait = MAX_WAIT_TIME_MS - totalWaitTimeSoFar - timePassedSoFar;
            if (timeLeftToWait <= 0)
            {
                return -1; // הנהג חצה את רף ה-10 שניות ולכן יפוטר
            }
            wait(timeLeftToWait);
        }
        long endTime = System.currentTimeMillis();
        currentOranges = currentOranges - 1;
        repaint();
        notifyAll();
        return (endTime - startTime);
    }

    public synchronized void increaseCapacity()
    {
        capacity = capacity + 1;
        repaint();
        notifyAll();
    }

    public synchronized void decreaseCapacity()
    {
        if (capacity > currentOranges)
        {
            if (capacity > 1)
            {
                capacity = capacity - 1;
                repaint();
            }
            else
            {
                // הקיבולת היא כבר 1 ולא ניתן להקטין יותר
                SwingUtilities.invokeLater(() ->
                        JOptionPane.showMessageDialog(this,
                                "לא ניתן להקטין. קיבולת המחסן המינימלית היא 1.",
                                "שגיאת קיבולת",
                                JOptionPane.WARNING_MESSAGE)
                );
            }
        }
        else
        {
            SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(this,
                            "לא ניתן להקטין את המחסן מכיוון שהוא כרגע מלא בתפוזים.",
                            "שגיאת קיבולת",
                            JOptionPane.WARNING_MESSAGE)
            );
        }
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawString("קיבולת מקסימלית: " + capacity, 20, 30);
        g.drawString("תפוזים במחסן כעת: " + currentOranges, 20, 50);
        int drawX = START_X;
        int drawY = START_Y;
        for (int i = 0; i < capacity; i = i + 1)
        {
            if (i < currentOranges)
            {
                g.setColor(Color.ORANGE);
                g.fillOval(drawX, drawY, ORANGE_SIZE, ORANGE_SIZE);
            }
            else
            {
                g.setColor(Color.LIGHT_GRAY);
                g.drawOval(drawX, drawY, ORANGE_SIZE, ORANGE_SIZE);
            }
            drawX = drawX + SPACING;
            if (drawX > getWidth() - 50)
            {
                drawX = START_X;
                drawY = drawY + SPACING;
            }
        }
    }
}