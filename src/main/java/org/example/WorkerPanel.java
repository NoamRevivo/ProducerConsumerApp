package org.example;
import javax.swing.*;
import java.awt.*;
public abstract class WorkerPanel extends JPanel
{
    private JLabel lblName;
    private JLabel lblStatus;
    private JLabel lblCount;
    private String actionPrefix;
    public final int ROWS=3;
    public final int COLS=1;
    public WorkerPanel(String name, Color backgroundColor, String actionPrefix)
    {
        this.actionPrefix = actionPrefix;
        setLayout(new GridLayout(ROWS, COLS));
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setBackground(backgroundColor);
        lblName = new JLabel(name);
        lblStatus = new JLabel("סטטוס: מוכן לעבודה");
        lblCount = new JLabel(actionPrefix + " עד כה: 0 תפוזים");
        add(lblName);
        add(lblStatus);
        add(lblCount);
    }
    public void updateStatus(String statusText)
    {
        lblStatus.setText("סטטוס: " + statusText);
    }
    public void updateCount(int count)
    {
        lblCount.setText(actionPrefix + " עד כה: " + count + " תפוזים");
    }
}