package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ProducerConsumerApp extends JFrame {

    public static void main(String[] args) {
        ProducerConsumerApp app = new ProducerConsumerApp();
        app.setVisible(true);
    }

    public ProducerConsumerApp() {
        setTitle("מטלה מקביליות - יצרן וצרכן (תפוזים)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLayout(new BorderLayout());

        // יצירת המשאב המשותף (המחסן)
        Warehouse warehouse = new Warehouse();

        // פאנל לחקלאים (צד ימין)
        JPanel farmersContainer = new JPanel();
        farmersContainer.setLayout(new GridLayout(3, 1, 5, 5));
        farmersContainer.setBorder(BorderFactory.createTitledBorder("חקלאים (יצרנים)"));
        farmersContainer.setPreferredSize(new Dimension(250, 0));

        // פאנל לנהגים (צד שמאל)
        JPanel driversContainer = new JPanel();
        driversContainer.setLayout(new GridLayout(3, 1, 5, 5));
        driversContainer.setBorder(BorderFactory.createTitledBorder("נהגים (צרכנים)"));
        driversContainer.setPreferredSize(new Dimension(250, 0));

        // יצירת חקלאים (ירושה ופולימורפיזם)
        for (int i = 1; i <= 3; i = i + 1) {
            FarmerPanel panel = new FarmerPanel("חקלאי " + i);
            farmersContainer.add(panel);

            FarmerThread farmer = new FarmerThread(warehouse, panel);
            farmer.start();
        }

        // יצירת נהגים (ירושה ופולימורפיזם)
        for (int i = 1; i <= 3; i = i + 1) {
            DriverPanel panel = new DriverPanel("נהג " + i);
            driversContainer.add(panel);

            DriverThread driver = new DriverThread(warehouse, panel);
            driver.start();
        }

        // כפתורים לשליטה על המחסן (למטה)
        JPanel buttonsPanel = new JPanel();
        JButton btnIncrease = new JButton("הגדל קיבולת מחסן");
        JButton btnDecrease = new JButton("הקטן קיבולת מחסן");

        // מאזין לכפתור הגדלה - כתיבה אנושית של שנה א' ללא למבדא
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

        // הוספת הרכיבים לחלון הראשי
        add(driversContainer, BorderLayout.WEST); // נהגים משמאל
        add(warehouse, BorderLayout.CENTER);      // מחסן באמצע
        add(farmersContainer, BorderLayout.EAST); // חקלאים מימין
        add(buttonsPanel, BorderLayout.SOUTH);    // כפתורים למטה
    }
}