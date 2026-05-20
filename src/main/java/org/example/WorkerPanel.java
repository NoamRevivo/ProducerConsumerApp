package org.example;
import javax.swing.*;
import java.awt.*;
public abstract class WorkerPanel extends JPanel
{
    private JLabel lblName;
    private JLabel lblStatus;
    private JLabel lblCount;
    private String actionPrefix;
    public WorkerPanel(String name, Color backgroundColor, String actionPrefix)
    {
        this.actionPrefix = actionPrefix;
        setLayout(new GridLayout(3, 1));
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