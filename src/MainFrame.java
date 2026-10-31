
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MainFrame extends JFrame {
    private final CardLayout cards = new CardLayout();
    private final JPanel content = new JPanel(cards);
    private final DashboardPanel dashboard = new DashboardPanel();
    private final EmployeePanel employees = new EmployeePanel();
    private final PayrollPanel payroll = new PayrollPanel();
    private final List<JButton> navButtons = new ArrayList<>();

    public MainFrame(String user) {
        super("PayrollPro - Employee Payroll System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1250, 720);
        setMinimumSize(new Dimension(1050, 600));
        setLocationRelativeTo(null);

        // ---- Sidebar ----
        JPanel side = new JPanel();
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setBackground(Theme.DARK);
        side.setPreferredSize(new Dimension(220, 0));
        side.setBorder(new EmptyBorder(25, 12, 20, 12));

        JLabel logo = new JLabel("PayrollPro");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        logo.setBorder(new EmptyBorder(0, 12, 25, 0));
        side.add(logo);

        addNav(side, "Dashboard", "dash");
        addNav(side, "Employees", "emp");
        addNav(side, "Payroll & Payslips", "pay");

        side.add(Box.createVerticalGlue());

        JLabel who = new JLabel("Logged in as: " + user);
        who.setForeground(new Color(148, 163, 184));
        who.setFont(Theme.FONT);
        who.setAlignmentX(Component.LEFT_ALIGNMENT);
        who.setBorder(new EmptyBorder(0, 4, 10, 0));
        side.add(who);

        JButton logout = Theme.button("Logout", Theme.RED);
        logout.setAlignmentX(Component.LEFT_ALIGNMENT);
        logout.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        logout.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Log out now?", "Logout",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                dispose();
                new LoginFrame().setVisible(true);
            }
        });
        side.add(logout);

        content.add(dashboard, "dash");
        content.add(employees, "emp");
        content.add(payroll, "pay");

        add(side, BorderLayout.WEST);
        add(content, BorderLayout.CENTER);
        showCard("dash");
    }

    private void addNav(JPanel side, String text, String card) {
        JButton b = new JButton(text);
        b.setFont(new Font("Segoe UI", Font.BOLD, 14));
        b.setForeground(Color.WHITE);
        b.setBackground(Theme.DARK);
        b.setOpaque(true);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setBorder(new EmptyBorder(0, 14, 0, 0));
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setAlignmentX(Component.LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.putClientProperty("card", card);
        b.addActionListener(e -> showCard(card));
        navButtons.add(b);
        side.add(b);
        side.add(Box.createVerticalStrut(5));
    }

    private void showCard(String card) {
        switch (card) {
            case "dash": dashboard.refresh(); break;
            case "emp":  employees.refresh(); break;
            case "pay":  payroll.refreshEmployees(); break;
        }
        cards.show(content, card);
        for (JButton b : navButtons)
            b.setBackground(card.equals(b.getClientProperty("card")) ? Theme.PRIMARY : Theme.DARK);
    }
}