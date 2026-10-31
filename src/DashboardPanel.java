
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class DashboardPanel extends JPanel {
    private final JLabel lblTotal = new JLabel("0");
    private final JLabel lblActive = new JLabel("0");
    private final JLabel lblDepts = new JLabel("0");
    private final JLabel lblPayroll = new JLabel("0.00");
    private Map<String, Integer> deptData = new LinkedHashMap<>();
    private final JPanel chart;

    public DashboardPanel() {
        setLayout(new BorderLayout(0, 20));
        setBackground(Theme.BG);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("Dashboard");
        title.setFont(Theme.TITLE);
        title.setForeground(Theme.TEXT);
        add(title, BorderLayout.NORTH);

        JPanel cardsRow = new JPanel(new GridLayout(1, 4, 20, 0));
        cardsRow.setOpaque(false);
        cardsRow.add(card("Total Employees", lblTotal, Theme.PRIMARY));
        cardsRow.add(card("Active Employees", lblActive, Theme.GREEN));
        cardsRow.add(card("Departments", lblDepts, Theme.PURPLE));
        cardsRow.add(card("This Month's Net Payroll", lblPayroll, Theme.ORANGE));
        cardsRow.setPreferredSize(new Dimension(0, 120));

        chart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawChart((Graphics2D) g.create(), getWidth(), getHeight());
            }
        };
        chart.setBackground(Color.WHITE);

        JPanel center = new JPanel(new BorderLayout(0, 20));
        center.setOpaque(false);
        center.add(cardsRow, BorderLayout.NORTH);
        center.add(chart, BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);
    }

    private JPanel card(String caption, JLabel value, Color color) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(color);
        p.setBorder(new EmptyBorder(18, 20, 18, 20));
        JLabel c = new JLabel(caption);
        c.setForeground(new Color(255, 255, 255, 220));
        c.setFont(Theme.BOLD);
        value.setForeground(Color.WHITE);
        value.setFont(new Font("Segoe UI", Font.BOLD, 30));
        p.add(c, BorderLayout.NORTH);
        p.add(value, BorderLayout.CENTER);
        return p;
    }

    public void refresh() {
        try {
            EmployeeDAO ed = new EmployeeDAO();
            LocalDate now = LocalDate.now();
            deptData = ed.departmentCounts();
            lblTotal.setText(String.valueOf(ed.countAll()));
            lblActive.setText(String.valueOf(ed.countActive()));
            lblDepts.setText(String.valueOf(deptData.size()));
            lblPayroll.setText(Theme.money(new PayrollDAO().totalNet(now.getMonthValue(), now.getYear())));
            chart.repaint();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Could not load dashboard: " + ex.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void drawChart(Graphics2D g2, int w, int h) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
        g2.setColor(Theme.TEXT);
        g2.drawString("Employees by Department", 24, 32);
        g2.setFont(Theme.BOLD);
        if (deptData.isEmpty()) {
            g2.drawString("No data yet", 24, 65);
            g2.dispose();
            return;
        }
        int max = Collections.max(deptData.values());
        int n = deptData.size();
        int left = 40, top = 70, bottom = h - 45;
        int slot = (w - 2 * left) / n;
        int bw = Math.min(80, Math.max(20, slot - 24));
        FontMetrics fm = g2.getFontMetrics();
        Color[] palette = {Theme.PRIMARY, Theme.GREEN, Theme.PURPLE, Theme.ORANGE, Theme.RED};

        g2.setColor(new Color(226, 232, 240));
        g2.drawLine(left, bottom, w - left, bottom);

        int i = 0;
        for (Map.Entry<String, Integer> e : deptData.entrySet()) {
            int bh = (int) ((bottom - top) * (e.getValue() / (double) max));
            int x = left + i * slot + (slot - bw) / 2;
            g2.setColor(palette[i % palette.length]);
            g2.fillRoundRect(x, bottom - bh, bw, bh, 12, 12);
            g2.setColor(Theme.TEXT);
            String val = String.valueOf(e.getValue());
            g2.drawString(val, x + (bw - fm.stringWidth(val)) / 2, bottom - bh - 8);
            g2.drawString(e.getKey(), x + (bw - fm.stringWidth(e.getKey())) / 2, bottom + 20);
            i++;
        }
        g2.dispose();
    }
}