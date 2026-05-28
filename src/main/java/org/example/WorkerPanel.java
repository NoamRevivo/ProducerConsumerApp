package org.example;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class WorkerPanel extends JPanel
{
    //קבועים
    private static final int ROWS = 4;
    private static final int COLS = 1;
    //קבועים
    private JLabel lblName;
    private JLabel lblStatus;
    private JLabel lblCount;
    private JLabel lblTimer;
    private String actionPrefix;
    private Timer watchTimer;
    private long waitStartTime;

    public WorkerPanel(String name, Color backgroundColor, String actionPrefix)
    {
        this.actionPrefix = actionPrefix;
        setLayout(new GridLayout(ROWS, COLS));
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setBackground(backgroundColor);
        lblName = new JLabel(name);
        lblStatus = new JLabel("סטטוס: מוכן לעבודה");
        lblCount = new JLabel(actionPrefix + " עד כה: 0 תפוזים");
        lblTimer = new JLabel("זמן המתנה: 0.0 שניות");
        add(lblName);
        add(lblStatus);
        add(lblCount);
        add(lblTimer);
    }
    public void updateStatus(String statusText)
    {
        lblStatus.setText("סטטוס: " + statusText);
    }
    public void updateCount(int count)
    {
        lblCount.setText(actionPrefix + " עד כה: " + count + " תפוזים");
    }
    public void startWaitingClock()
    {
        if (watchTimer != null && watchTimer.isRunning()) {
            watchTimer.stop();
        }
        waitStartTime = System.currentTimeMillis();
        watchTimer = new Timer(100, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double seconds = (System.currentTimeMillis() - waitStartTime) / 1000.0;
                lblTimer.setText("זמן המתנה: " + String.format("%.1f", seconds) + " שניות");
            }
        });
        watchTimer.start();
    }
    public void stopWaitingClock()
    {
        if (watchTimer != null) {
            watchTimer.stop();
        }
    }
}