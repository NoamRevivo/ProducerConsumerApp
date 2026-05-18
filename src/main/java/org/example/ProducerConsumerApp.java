import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class ProducerConsumerApp extends JFrame {

    public static void main(String[] args) {
        // רץ רגיל בלי InvokeLater של מתכנתים מתקדמים
        ProducerConsumerApp app = new ProducerConsumerApp();
        app.setVisible(true);
    }
    public ProducerConsumerApp() {
        setTitle("בעיית יצרן צרכן - תפוזים");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 600);
        setLayout(new BorderLayout());
        Warehouse warehouse = new Warehouse();
        // פאנל חקלאים
        JPanel farmersPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        farmersPanel.setBorder(BorderFactory.createTitledBorder("חקלאים"));
        farmersPanel.setPreferredSize(new Dimension(250, 0));
        // פאנל נהגים
        JPanel driversPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        driversPanel.setBorder(BorderFactory.createTitledBorder("נהגים"));
        driversPanel.setPreferredSize(new Dimension(250, 0));
        // יצירת חקלאים ונהגים
        for (int i = 1; i <= 3; i++) {
            FarmerUI farmerUI = new FarmerUI("חקלאי " + i);
            farmersPanel.add(farmerUI);
            Farmer f = new Farmer(warehouse, farmerUI);
            f.start();
        }
        for (int i = 1; i <= 3; i++) {
            DriverUI driverUI = new DriverUI("נהג " + i);
            driversPanel.add(driverUI);
            Driver d = new Driver(warehouse, driverUI);
            d.start();
        }
        // כפתורים
        JPanel controlPanel = new JPanel();
        JButton btnInc = new JButton("הגדל קיבולת");
        JButton btnDec = new JButton("הקטן קיבולת");
        btnInc.addActionListener(e -> warehouse.changeCapacity(1));
        btnDec.addActionListener(e -> warehouse.changeCapacity(-1));
        controlPanel.add(btnInc);
        controlPanel.add(btnDec);
        add(driversPanel, BorderLayout.WEST);
        add(warehouse, BorderLayout.CENTER);
        add(farmersPanel, BorderLayout.EAST);
        add(controlPanel, BorderLayout.SOUTH);
    }
}
// --- מחלקת המחסן ---
class Warehouse extends JPanel {
    private int capacity = 10;
    private int oranges = 0;

    public Warehouse() {
        setBorder(BorderFactory.createTitledBorder("מחסן תפוזים"));
    }

    // מכניס תפוז
    public synchronized long putOrange(long waitTimeSoFar) throws InterruptedException {
        long t1 = System.currentTimeMillis();
        while (oranges >= capacity) {
            long timePassed = System.currentTimeMillis() - t1;
            long timeLeft = 10000 - waitTimeSoFar - timePassed;

            if (timeLeft <= 0) {
                return -1; // עברו 10 שניות, מחזיר -1 כדי לפטר
            }
            wait(timeLeft); // ישן עד שיהיה מקום או שייגמר הזמן
        }
        long t2 = System.currentTimeMillis();
        oranges++;
        repaint();
        notifyAll(); // מעיר נהגים
        return (t2 - t1); // מחזיר כמה זמן הוא חיכה הפעם
    }

    // מוציא תפוז
    public synchronized long takeOrange(long waitTimeSoFar) throws InterruptedException {
        long t1 = System.currentTimeMillis();

        while (oranges == 0) {
            long timePassed = System.currentTimeMillis() - t1;
            long timeLeft = 10000 - waitTimeSoFar - timePassed;

            if (timeLeft <= 0) {
                return -1; // עברו 10 שניות, מפוטר
            }
            wait(timeLeft);
        }
        long t2 = System.currentTimeMillis();
        oranges--;
        repaint();
        notifyAll(); // מעיר חקלאים
        return (t2 - t1);
    }
    public synchronized void changeCapacity(int amount) {
        if (capacity + amount >= oranges && capacity + amount > 0) {
            capacity += amount;
            repaint();
            notifyAll();
        }
    }
    // מצייר את המחסן
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawString("קיבולת: " + capacity, 20, 30);
        g.drawString("תפוזים: " + oranges, 20, 50);
        int x = 20;
        int y = 70;
        for (int i = 0; i < capacity; i++) {
            if (i < oranges) {
                g.setColor(Color.ORANGE);
                g.fillOval(x, y, 30, 30);
            } else {
                g.setColor(Color.LIGHT_GRAY);
                g.drawOval(x, y, 30, 30);
            }
            x += 40;
            if (x > getWidth() - 50) {
                x = 20;
                y += 40;
            }
        }
    }
}
// --- מחלקת החקלאי ---
class Farmer extends Thread {
    private Warehouse warehouse;
    private FarmerUI ui;
    private long totalWait = 0;
    private int count = 0;
    private Random rand = new Random();
    public Farmer(Warehouse w, FarmerUI ui) {
        this.warehouse = w;
        this.ui = ui;
    }
    @Override
    public void run() {
        try {
            while (true) {
                ui.setStatus("קוטף...");
                Thread.sleep(rand.nextInt(3000)); // עד 3 שניות
                ui.setStatus("ממתין למחסן...");
                long waitedNow = warehouse.putOrange(totalWait);
                if (waitedNow == -1) {
                    ui.setStatus("מפוטר!");
                    break;
                }
                totalWait += waitedNow;
                count++;
                ui.setCount(count);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
// --- מחלקת הנהג ---
class Driver extends Thread {
    private Warehouse warehouse;
    private DriverUI ui;
    private long totalWait = 0;
    private int count = 0;
    private Random rand = new Random();
    public Driver(Warehouse w, DriverUI ui) {
        this.warehouse = w;
        this.ui = ui;
    }
    @Override
    public void run() {
        try {
            while (true) {
                ui.setStatus("נח בבית...");
                Thread.sleep(rand.nextInt(2000));
                ui.setStatus("נוסע...");
                Thread.sleep(2000 + rand.nextInt(1000)); // 2 עד 3 שניות
                ui.setStatus("מנסה לקחת...");
                long waitedNow = warehouse.takeOrange(totalWait);
                if (waitedNow == -1) {
                    ui.setStatus("מפוטר!");
                    break;
                }
                totalWait += waitedNow;
                count++;
                ui.setCount(count);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
// --- ממשק החקלאי ---
    class FarmerUI extends JPanel {
    private JLabel lblStatus;
    private JLabel lblCount;
    public FarmerUI(String name) {
        setLayout(new GridLayout(2, 1));
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        setBackground(Color.GREEN);
        lblStatus = new JLabel(name + ": מוכן");
        lblCount = new JLabel("קטף: 0");
        add(lblStatus);
        add(lblCount);
    }
    public void setStatus(String text) {
        lblStatus.setText(text);
    }
    public void setCount(int c) {
        lblCount.setText("קטף: " + c + " תפוזים");
    }
}
    // --- ממשק הנהג ---
    class DriverUI extends JPanel
    {
        private JLabel lblStatus;
        private JLabel lblCount;
        public DriverUI(String name)
        {
            setLayout(new GridLayout(2, 1));
            setBorder(BorderFactory.createLineBorder(Color.BLACK));
            setBackground(Color.CYAN);
            lblStatus = new JLabel(name + ": מוכן");
            lblCount = new JLabel("אסף: 0 תפוזים"); // אתחול התחלתי ברור
            add(lblStatus);
            add(lblCount);
        }
        public void setStatus(String text)
        {
            lblStatus.setText(text);
        }
        public void setCount(int c)
        {
            lblCount.setText("אסף: " + c + " תפוזים"); // <--- השינוי כאן
        }
    }