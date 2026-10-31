
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;

public class LoginFrame extends JFrame {
    private final JTextField txtUser = Theme.field();
    private final JPasswordField txtPass = Theme.style(new JPasswordField());

    public LoginFrame() {
        super("PayrollPro - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(780, 450);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new GridLayout(1, 2));

        // ---- Left brand panel ----
        JPanel left = new JPanel(new GridBagLayout());
        left.setBackground(Theme.PRIMARY);
        JLabel brand = new JLabel("<html><div style='text-align:center'>"
                + "<span style='font-size:34px'><b>PayrollPro</b></span><br><br>"
                + "<span style='font-size:13px'>Employee Payroll<br>Management System</span></div></html>");
        brand.setForeground(Color.WHITE);
        left.add(brand);

        // ---- Right form panel ----
        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBackground(Color.WHITE);
        right.setBorder(new EmptyBorder(60, 50, 40, 50));

        JLabel h = new JLabel("Welcome back");
        h.setFont(Theme.TITLE);
        h.setForeground(Theme.TEXT);
        JLabel sub = new JLabel("Sign in to continue");
        sub.setFont(Theme.FONT);
        sub.setForeground(Color.GRAY);

        JButton btn = Theme.button("LOGIN", Theme.PRIMARY);
        btn.addActionListener(e -> login());
        JLabel hint = new JLabel("Default: admin / admin123");
        hint.setFont(Theme.FONT);
        hint.setForeground(Color.GRAY);

        for (JComponent c : new JComponent[]{h, sub, Theme.label("Username"), txtUser,
                Theme.label("Password"), txtPass, btn, hint}) {
            c.setAlignmentX(Component.LEFT_ALIGNMENT);
        }
        txtUser.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        txtPass.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

        right.add(h);
        right.add(sub);
        right.add(Box.createVerticalStrut(25));
        right.add(Theme.label("Username"));
        right.add(Box.createVerticalStrut(5));
        right.add(txtUser);
        right.add(Box.createVerticalStrut(15));
        right.add(Theme.label("Password"));
        right.add(Box.createVerticalStrut(5));
        right.add(txtPass);
        right.add(Box.createVerticalStrut(25));
        right.add(btn);
        right.add(Box.createVerticalStrut(15));
        right.add(hint);

        root.add(left);
        root.add(right);
        setContentPane(root);
        getRootPane().setDefaultButton(btn); // Enter key triggers login
    }

    private void login() {
        String u = txtUser.getText().trim();
        String p = new String(txtPass.getPassword());
        if (u.isEmpty() || p.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter username and password.",
                    "Missing Data", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            if (new UserDAO().authenticate(u, p)) {
                new MainFrame(u).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.",
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}