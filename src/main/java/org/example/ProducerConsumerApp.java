import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class ProducerConsumerApp extends JFrame {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ProducerConsumerApp().setVisible(true);
        });
    }

    public ProducerConsumerApp() {
        setTitle("Producer Consumer - Oranges");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLayout(new BorderLayout());

        Warehouse warehouse = new Warehouse();

        // פאנל חקלאים - צד ימין [cite: 23]
        JPanel farmersPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        farmersPanel.setBorder(BorderFactory.createTitledBorder("Farmers (Producers)"));
        farmersPanel.setPreferredSize(new Dimension(250, 0));

        // פאנל נהגים - צד שמאל [cite: 23]
        JPanel driversPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        driversPanel.setBorder(BorderFactory.createTitledBorder("Drivers (Consumers)"));
        driversPanel.setPreferredSize(new Dimension(250, 0));

        // יצירת והפעלת התהליכונים
        for (int i = 1; i <= 3; i++) {
            FarmerUI farmerUI = new FarmerUI("Farmer " + i);
            farmersPanel.add(farmerUI);
            new Farmer(warehouse, farmerUI).start();
        }

        for (int i = 1; i <= 3; i++) {
            DriverUI driverUI = new DriverUI("Driver " + i);
            driversPanel.add(driverUI);
            new Driver(warehouse, driverUI).start();
        }

        // פאנל תחתון - כפתורי קיבולת מחסן [cite: 40]
        JPanel controlPanel = new JPanel();
        JButton btnInc = new JButton("Increase Capacity");
        JButton btnDec = new JButton("Decrease Capacity");

        btnInc.addActionListener(e -> warehouse.changeCapacity(1));
        btnDec.addActionListener(e -> warehouse.changeCapacity(-1));

        controlPanel.add(btnInc);
        controlPanel.add(btnDec);

        add(driversPanel, BorderLayout.WEST); // צד שמאל [cite: 23]
        add(warehouse, BorderLayout.CENTER); // אמצע [cite: 23]
        add(farmersPanel, BorderLayout.EAST); // צד ימין [cite: 23]
        add(controlPanel, BorderLayout.SOUTH);
    }
}

// --- מחלקת המחסן (The Monitor) ---
class Warehouse extends JPanel {
    private int capacity = 10;
    private int oranges = 0;

    public Warehouse() {
        setBorder(BorderFactory.createTitledBorder("Warehouse"));
    }

    // מתודה מסונכרנת להכנסת תפוז על ידי חקלאי
    public synchronized long putOrange(long accumulatedWait) throws InterruptedException {
        long startWait = System.currentTimeMillis();

        while (oranges >= capacity) { // המתנה עד שיהיה מקום פנוי [cite: 28]
            long currentWait = System.currentTimeMillis() - startWait;
            long timeLeftToFire = 10000 - accumulatedWait - currentWait;

            if (timeLeftToFire <= 0) {
                return -1; // החקלאי בזבז מעל 10 שניות ולכן מפוטר [cite: 30]
            }
            wait(timeLeftToFire); // ישן ולא מבזבז CPU 
        }

        long actualWaitThisTurn = System.currentTimeMillis() - startWait;
        oranges++;
        repaint(); // עדכון הממשק הגרפי
        notifyAll(); // מעיר את הנהגים שממתינים לתפוזים 
        return actualWaitThisTurn;
    }

    // מתודה מסונכרנת להוצאת תפוז על ידי נהג
    public synchronized long takeOrange(long accumulatedWait) throws InterruptedException {
        long startWait = System.currentTimeMillis();

        while (oranges == 0) { // המתנה עד שיהיו תפוזים [cite: 35]
            long currentWait = System.currentTimeMillis() - startWait;
            long timeLeftToFire = 10000 - accumulatedWait - currentWait;

            if (timeLeftToFire <= 0) {
                return -1; // הנהג בזבז מעל 10 שניות ולכן מפוטר [cite: 36]
            }
            wait(timeLeftToFire);
        }

        long actualWaitThisTurn = System.currentTimeMillis() - startWait;
        oranges--;
        repaint();
        notifyAll(); // מעיר חקלאים שממתינים למקום פנוי 
        return actualWaitThisTurn;
    }

    public synchronized void changeCapacity(int amount) {
        if (capacity + amount >= oranges && capacity + amount > 0) {
            capacity += amount;
            repaint();
            notifyAll(); // אם הגדלנו קיבולת, נעיר את החקלאים שאולי ישנו
        }
    }

    // ציור המחסן והתפוזים בצורות בסיסיות [cite: 24, 25]
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawString("Capacity: " + capacity, 10, 20);
        g.drawString("Oranges inside: " + oranges, 10, 40);

        int x = 10;
        int y = 60;
        for (int i = 0; i < capacity; i++) {
            if (i < oranges) {
                g.setColor(Color.ORANGE);
                g.fillOval(x, y, 30, 30); // תפוז קיים
            } else {
                g.setColor(Color.LIGHT_GRAY);
                g.drawOval(x, y, 30, 30); // מקום פנוי לתפוז
            }
            x += 40;
            if (x > getWidth() - 40) { // מעבר שורה בציור
                x = 10;
                y += 40;
            }
        }
    }
}

// --- מחלקת החקלאי (Producer) ---
class Farmer extends Thread {
    private final Warehouse warehouse;
    private final FarmerUI ui;
    private long totalWaitTime = 0; // סכימת זמן ההמתנה [cite: 29]
    private int producedCount = 0;
    private final Random rand = new Random();

    public Farmer(Warehouse warehouse, FarmerUI ui) {
        this.warehouse = warehouse;
        this.ui = ui;
    }

    @Override
    public void run() {
        try {
            while (true) {
                ui.updateStatus("Picking oranges...");
                Thread.sleep(rand.nextInt(3001)); // קטיף למשך עד 3 שניות [cite: 27]

                ui.updateStatus("Waiting to store...");
                long waitThisTurn = warehouse.putOrange(totalWaitTime);

                if (waitThisTurn == -1) {
                    ui.updateStatus("FIRED! (Wait > 10s)"); // מפוטר ולא קוטף יותר [cite: 30, 31]
                    break;
                }

                totalWaitTime += waitThisTurn;
                producedCount++;
                ui.updateCount(producedCount); // עדכון הלייב של המונה 
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

// --- מחלקת הנהג (Consumer) ---
class Driver extends Thread {
    private final Warehouse warehouse;
    private final DriverUI ui;
    private long totalWaitTime = 0; // סכימת זמן המתנה
    private int consumedCount = 0;
    private final Random rand = new Random();

    public Driver(Warehouse warehouse, DriverUI ui) {
        this.warehouse = warehouse;
        this.ui = ui;
    }

    @Override
    public void run() {
        try {
            while (true) {
                ui.updateStatus("Waiting at home...");
                Thread.sleep(rand.nextInt(2000)); // מצב המתנה התחלתי [cite: 33]

                ui.updateStatus("Driving to warehouse...");
                Thread.sleep(2000 + rand.nextInt(1001)); // נסיעה בין 2-3 שניות [cite: 34]

                ui.updateStatus("Loading oranges...");
                long waitThisTurn = warehouse.takeOrange(totalWaitTime);

                if (waitThisTurn == -1) {
                    ui.updateStatus("FIRED! (Wait > 10s)"); // מפוטר ולא חוזר לאסוף [cite: 36]
                    break;
                }

                totalWaitTime += waitThisTurn;
                consumedCount++;
                ui.updateCount(consumedCount); // עדכון הלייב של המונה
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

// --- מחלקות הממשק הגרפי לחקלאי ונהג (בסיסיות) [cite: 24] ---
class FarmerUI extends JPanel {
    private JLabel statusLabel;
    private JLabel countLabel;

    public FarmerUI(String name) {
        setLayout(new GridLayout(2, 1));
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setBackground(new Color(200, 255, 200)); // צבע ירקרק לחקלאי

        statusLabel = new JLabel(name + ": Ready");
        countLabel = new JLabel("Produced: 0"); // המונה שמתעדכן בלייב

        add(statusLabel);
        add(countLabel);
    }

    public void updateStatus(String status) {
        SwingUtilities.invokeLater(() -> statusLabel.setText(status));
    }

    public void updateCount(int count) {
        SwingUtilities.invokeLater(() -> countLabel.setText("Produced: " + count));
    }
}

class DriverUI extends JPanel {
    private JLabel statusLabel;
    private JLabel countLabel;

    public DriverUI(String name) {
        setLayout(new GridLayout(2, 1));
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setBackground(new Color(200, 200, 255)); // צבע כחלחל לנהג

        statusLabel = new JLabel(name + ": Ready");
        countLabel = new JLabel("Taken: 0"); // המונה שמתעדכן בלייב

        add(statusLabel);
        add(countLabel);
    }

    public void updateStatus(String status) {
        SwingUtilities.invokeLater(() -> statusLabel.setText(status));
    }

    public void updateCount(int count) {
        SwingUtilities.invokeLater(() -> countLabel.setText("Taken: " + count));
    }
}