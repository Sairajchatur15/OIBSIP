import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.UUID;

public class ReservationSystem extends JFrame {
    private Connection conn;

    public ReservationSystem() {
        initDB();
        setTitle("Train Reservation System");
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initDB() {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:reservation.db");
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS users (username TEXT PRIMARY KEY, password TEXT)");
            stmt.execute("CREATE TABLE IF NOT EXISTS trains (train_no INTEGER PRIMARY KEY, train_name TEXT)");
            stmt.execute("CREATE TABLE IF NOT EXISTS reservations (pnr TEXT PRIMARY KEY, passenger_name TEXT, train_no INTEGER, train_name TEXT, class_type TEXT, journey_date TEXT, source_station TEXT, destination_station TEXT)");
            stmt.execute("INSERT OR IGNORE INTO users VALUES ('admin','admin123')");
            stmt.execute("INSERT OR IGNORE INTO trains VALUES (101,'Express 101')");
            stmt.execute("INSERT OR IGNORE INTO trains VALUES (202,'Shatabdi 202')");
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void initUI() {
        setLayout(new BorderLayout());
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Login", createLoginPanel());
        tabs.addTab("Book", createBookPanel());
        tabs.addTab("Cancel", createCancelPanel());
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel createLoginPanel() {
        JPanel p = new JPanel(new GridLayout(3, 2, 5, 5));
        JTextField user = new JTextField();
        JPasswordField pass = new JPasswordField();
        JButton btn = new JButton("Login");
        btn.addActionListener(e -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE username=? AND password=?")) {
                ps.setString(1, user.getText());
                ps.setString(2, new String(pass.getPassword()));
                ResultSet rs = ps.executeQuery();
                if (rs.next()) JOptionPane.showMessageDialog(this, "Access granted!");
                else JOptionPane.showMessageDialog(this, "Access denied!");
            } catch (SQLException ex) { ex.printStackTrace(); }
        });
        p.add(new JLabel("Username:")); p.add(user);
        p.add(new JLabel("Password:")); p.add(pass);
        p.add(new JLabel()); p.add(btn);
        return p;
    }

    private JPanel createBookPanel() {
        JPanel p = new JPanel(new GridLayout(8, 2, 5, 5));
        JTextField name = new JTextField();
        JTextField trainNo = new JTextField();
        JTextField trainName = new JTextField(); trainName.setEditable(false);
        JTextField classType = new JTextField();
        JTextField date = new JTextField();
        JTextField source = new JTextField();
        JTextField dest = new JTextField();

        trainNo.addActionListener(e -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT train_name FROM trains WHERE train_no=?")) {
                ps.setInt(1, Integer.parseInt(trainNo.getText()));
                ResultSet rs = ps.executeQuery();
                trainName.setText(rs.next() ? rs.getString(1) : "Not found");
            } catch (Exception ex) { trainName.setText("Invalid"); }
        });

        JButton book = new JButton("Insert / Book");
        book.addActionListener(e -> {
            if (name.getText().trim().isEmpty() || trainNo.getText().trim().isEmpty() || trainName.getText().trim().equals("Not found") || trainName.getText().trim().equals("Invalid") || classType.getText().trim().isEmpty() || date.getText().trim().isEmpty() || source.getText().trim().isEmpty() || dest.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields required."); return;
            }
            if (!date.getText().matches("\\d{4}-\\d{2}-\\d{2}")) {
                JOptionPane.showMessageDialog(this, "Date format: YYYY-MM-DD"); return;
            }
            String pnr = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO reservations VALUES (?,?,?,?,?,?,?,?)")) {
                ps.setString(1, pnr);
                ps.setString(2, name.getText());
                ps.setInt(3, Integer.parseInt(trainNo.getText()));
                ps.setString(4, trainName.getText());
                ps.setString(5, classType.getText());
                ps.setString(6, date.getText());
                ps.setString(7, source.getText());
                ps.setString(8, dest.getText());
                ps.executeUpdate();
                JOptionPane.showMessageDialog(this, "Booking Confirmed!\nPNR: " + pnr + "\nPassenger: " + name.getText() + "\nTrain: " + trainName.getText());
            } catch (SQLException ex) { ex.printStackTrace(); }
        });

        p.add(new JLabel("Passenger Name:")); p.add(name);
        p.add(new JLabel("Train Number:")); p.add(trainNo);
        p.add(new JLabel("Train Name:")); p.add(trainName);
        p.add(new JLabel("Class:")); p.add(classType);
        p.add(new JLabel("Date (YYYY-MM-DD):")); p.add(date);
        p.add(new JLabel("Source:")); p.add(source);
        p.add(new JLabel("Destination:")); p.add(dest);
        p.add(new JLabel()); p.add(book);
        return p;
    }

    private JPanel createCancelPanel() {
        JPanel p = new JPanel(new GridLayout(5, 2, 5, 5));
        JTextField pnrField = new JTextField();
        JTextArea details = new JTextArea(); details.setEditable(false);
        JButton fetch = new JButton("Fetch");
        JButton confirm = new JButton("Confirm Cancellation");

        fetch.addActionListener(e -> {
            try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM reservations WHERE pnr=?")) {
                ps.setString(1, pnrField.getText().trim());
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    details.setText("PNR: " + rs.getString("pnr") + "\nName: " + rs.getString("passenger_name") + "\nTrain: " + rs.getString("train_name") + "\nClass: " + rs.getString("class_type") + "\nDate: " + rs.getString("journey_date") + "\nFrom: " + rs.getString("source_station") + "\nTo: " + rs.getString("destination_station"));
                } else details.setText("No booking found.");
            } catch (SQLException ex) { ex.printStackTrace(); }
        });
        confirm.addActionListener(e -> {
            int conf = JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel this booking?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (conf == JOptionPane.YES_OPTION) {
                try (PreparedStatement ps = conn.prepareStatement("DELETE FROM reservations WHERE pnr=?")) {
                    ps.setString(1, pnrField.getText().trim());
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Booking cancelled.");
                    details.setText(""); pnrField.setText("");
                } catch (SQLException ex) { ex.printStackTrace(); }
            }
        });
        p.add(new JLabel("PNR Number:")); p.add(pnrField);
        p.add(new JLabel("Booking Details:")); p.add(new JScrollPane(details));
        p.add(fetch); p.add(confirm);
        return p;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ReservationSystem().setVisible(true));
    }
}
