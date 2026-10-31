
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class Theme {
    public static final Color PRIMARY = new Color(37, 99, 235);
    public static final Color DARK    = new Color(15, 23, 42);
    public static final Color BG      = new Color(241, 245, 249);
    public static final Color GREEN   = new Color(22, 163, 74);
    public static final Color RED     = new Color(220, 38, 38);
    public static final Color ORANGE  = new Color(234, 88, 12);
    public static final Color PURPLE  = new Color(124, 58, 237);
    public static final Color TEXT    = new Color(30, 41, 59);

    public static final Font FONT  = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font BOLD  = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font TITLE = new Font("Segoe UI", Font.BOLD, 24);

    public static JButton button(String text, Color bg) {
        JButton b = new JButton(text);
        b.setFont(BOLD);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setOpaque(true);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setBorder(new EmptyBorder(9, 18, 9, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    public static <T extends JTextField> T style(T f) {
        f.setFont(FONT);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                new EmptyBorder(6, 10, 6, 10)));
        f.setPreferredSize(new Dimension(160, 34));
        return f;
    }

    public static JTextField field() {
        return style(new JTextField());
    }

    public static JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(BOLD);
        l.setForeground(TEXT);
        return l;
    }

    public static void styleTable(JTable t) {
        t.setRowHeight(30);
        t.setFont(FONT);
        t.setForeground(TEXT);
        t.setShowVerticalLines(false);
        t.setGridColor(new Color(226, 232, 240));
        t.setSelectionBackground(new Color(191, 219, 254));
        t.setSelectionForeground(TEXT);
        t.setFillsViewportHeight(true);

        DefaultTableCellRenderer header = new DefaultTableCellRenderer();
        header.setBackground(DARK);
        header.setForeground(Color.WHITE);
        header.setFont(BOLD);
        header.setOpaque(true);
        header.setHorizontalAlignment(SwingConstants.CENTER);
        t.getTableHeader().setDefaultRenderer(header);
        t.getTableHeader().setPreferredSize(new Dimension(0, 34));
        t.getTableHeader().setReorderingAllowed(false);
    }

    public static String money(double v) {
        return String.format("%,.2f", v);
    }
}