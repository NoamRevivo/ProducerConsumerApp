package org.example;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ProducerConsumerApp extends JFrame {
//קבועים לחלון
    private static final int WINDOW_WIDTH = 1000;
    private static final int WINDOW_HEIGHT = 650;
//קבועים לחלון הממשק הגרפי
    //קבועים למספר חקלאים ונהגים
    private static final int NUM_FARMERS = 3;
    private static final int NUM_DRIVERS = 3;
//קבועים למספר חקלאים ונהגים

    //  קבועים לעיצוב הפאנלים
    private static final int SIDE_PANEL_WIDTH = 250;
    private static final int GRID_ROWS = 3;
    private static final int GRID_COLS = 1;
    private static final int GRID_GAP_H = 5;
    private static final int GRID_GAP_V = 5;
    //  קבועים לעיצוב הפאנלים

    public static void main(String[] args) {
        ProducerConsumerApp app = new ProducerConsumerApp();
        app.setVisible(true);
    }

    public ProducerConsumerApp() {
        setTitle("מטלה מקביליות - בעיית יצרן וצרכן");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLayout(new BorderLayout());

        // יצירת המשאב המשותף (המחסן)
        Warehouse warehouse = new Warehouse();

        // יצירת פאנלים צדדיים באמצעות מתודת העזר
        JPanel farmersContainer = createSidePanel("חקלאים (יצרנים)");
        JPanel driversContainer = createSidePanel("נהגים (צרכנים)");

        // יצירת חקלאים והפעלת הת'רדים שלהם
        for (int i = 1; i <= NUM_FARMERS; i++) {
            FarmerPanel panel = new FarmerPanel("חקלאי " + i);
            farmersContainer.add(panel);
            new FarmerThread(warehouse, panel).start();
        }

        // יצירת נהגים והפעלת הת'רדים שלהם
        for (int i = 1; i <= NUM_DRIVERS; i++) {
            DriverPanel panel = new DriverPanel("נהג " + i);
            driversContainer.add(panel);
            new DriverThread(warehouse, panel).start();
        }

        // כפתורים לשליטה על המחסן
        JPanel buttonsPanel = new JPanel();
        JButton btnIncrease = new JButton("הגדל קיבולת מחסן");
        JButton btnDecrease = new JButton("הקטן קיבולת מחסן");

        // מאזין לכפתור הגדלה
        btnIncrease.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                warehouse.increaseCapacity();
            }
        });

        // מאזין לכפתור הקטנה
        btnDecrease.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                warehouse.decreaseCapacity();
            }
        });
        buttonsPanel.add(btnIncrease);
        buttonsPanel.add(btnDecrease);
        add(driversContainer, BorderLayout.WEST); // נהגים משמאל
        add(warehouse, BorderLayout.CENTER);      // מחסן באמצע
        add(farmersContainer, BorderLayout.EAST); // חקלאים מימין
        add(buttonsPanel, BorderLayout.SOUTH);    // כפתורים למטה
    }
    private JPanel createSidePanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(GRID_ROWS, GRID_COLS, GRID_GAP_H, GRID_GAP_V));
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.setPreferredSize(new Dimension(SIDE_PANEL_WIDTH, 0));
        return panel;
    }
}