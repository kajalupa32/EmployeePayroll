
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PayrollPanel extends JPanel {
    private static final String[] MONTHS = {"January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"};

    private final JComboBox<String> cmbMonth = new JComboBox<>(MONTHS);
    private final JComboBox<Employee> cmbEmp = new JComboBox<>();
    private final JSpinner spYear = new JSpinner(
            new SpinnerNumberModel(LocalDate.now().getYear(), 2000, 2100, 1));
    private final JTextField txtBonus = Theme.field(), txtOther = Theme.field();
    private final JLabel lblTotal = new JLabel("Total Net Payout: 0.00");

    private final String[] cols = {"ID", "Emp ID", "Name", "Department", "Basic", "HRA", "DA",
            "Bonus", "Gross", "PF", "Tax", "Other Ded.", "Net Salary"};
    private final DefaultTableModel model = new DefaultTableModel(cols, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);

    private final EmployeeDAO empDao = new EmployeeDAO();
    private final PayrollDAO payDao = new PayrollDAO();
    private List<Employee> emps = new ArrayList<>();
    private List<Payroll> rows = new ArrayList<>();

    public PayrollPanel() {
        setLayout(new BorderLayout(0, 15));
        setBackground(Theme.BG);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("Payroll & Payslips");
        title.setFont(Theme.TITLE);
        title.setForeground(Theme.TEXT);

        spYear.setEditor(new JSpinner.NumberEditor(spYear, "#"));
        spYear.setFont(Theme.FONT);
        cmbMonth.setFont(Theme.FONT);
        cmbEmp.setFont(Theme.FONT);
        cmbMonth.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
        txtBonus.setText("0");
        txtOther.setText("0");
        txtBonus.setPreferredSize(new Dimension(90, 34));
        txtOther.setPreferredSize(new Dimension(90, 34));
        cmbEmp.setPreferredSize(new Dimension(200, 34));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        row1.setBackground(Color.WHITE);
        row1.add(Theme.label("Month")); row1.add(cmbMonth);
        row1.add(Theme.label("Year")); row1.add(spYear);
        row1.add(Theme.label("Employee")); row1.add(cmbEmp);
        row1.add(Theme.label("Bonus")); row1.add(txtBonus);
        row1.add(Theme.label("Other Deduction")); row1.add(txtOther);

        JButton btnGen = Theme.button("Generate Payroll", Theme.GREEN);
        JButton btnLoad = Theme.button("Load Records", Theme.PRIMARY);
        JButton btnSlip = Theme.button("View Payslip", Theme.PURPLE);
        JButton btnCsv = Theme.button("Export CSV", Theme.ORANGE);
        JButton btnDel = Theme.button("Delete Record", Theme.RED);
        btnGen.addActionListener(e -> generate());
        btnLoad.addActionListener(e -> load());
        btnSlip.addActionListener(e -> showPayslip());
        btnCsv.addActionListener(e -> exportCsv());
        btnDel.addActionListener(e -> deleteSelected());

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        row2.setBackground(Color.WHITE);
        row2.add(btnGen); row2.add(btnLoad); row2.add(btnSlip); row2.add(btnCsv); row2.add(btnDel);

        JPanel controls = new JPanel(new BorderLayout());
        controls.add(row1, BorderLayout.NORTH);
        controls.add(row2, BorderLayout.SOUTH);

        JPanel top = new JPanel(new BorderLayout(0, 12));
        top.setOpaque(false);
        top.add(title, BorderLayout.NORTH);
        top.add(controls, BorderLayout.CENTER);

        Theme.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotal.setForeground(Theme.GREEN);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(lblTotal, BorderLayout.SOUTH);
    }

    /** Called by MainFrame whenever this tab is opened. */
    public void refreshEmployees() {
        try {
            emps = empDao.getAll("");
            cmbEmp.removeAllItems();
            Employee all = new Employee();
            all.id = 0;
            all.name = "ALL ACTIVE EMPLOYEES";
            cmbEmp.addItem(all);
            for (Employee e : emps) cmbEmp.addItem(e);
            load();
        } catch (SQLException ex) {
            error("Could not load employees: " + ex.getMessage());
        }
    }

    private int month() { return cmbMonth.getSelectedIndex() + 1; }
    private int year()  { return (Integer) spYear.getValue(); }

    private double parseMoney(String s) {
        s = s.trim();
        if (s.isEmpty()) return 0;
        double v = Double.parseDouble(s);
        if (v < 0) throw new NumberFormatException();
        return v;
    }

    private void generate() {
        try {
            double bonus = parseMoney(txtBonus.getText());
            double other = parseMoney(txtOther.getText());
            List<Employee> targets = new ArrayList<>();
            Employee sel = (Employee) cmbEmp.getSelectedItem();
            if (sel == null || sel.id == 0) {
                for (Employee e : emps) if ("Active".equals(e.status)) targets.add(e);
            } else {
                targets.add(sel);
            }
            int created = 0, skipped = 0;
            for (Employee e : targets) {
                if (payDao.exists(e.id, month(), year())) { skipped++; continue; }
                payDao.insert(Payroll.calculate(e, month(), year(), bonus, other));
                created++;
            }
            load();
            JOptionPane.showMessageDialog(this,
                    "Generated: " + created + "\nAlready existed (skipped): " + skipped,
                    "Payroll Result", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Bonus and deduction must be valid non-negative numbers.",
                    "Validation", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            error("Database error: " + ex.getMessage());
        }
    }

    private void load() {
        try {
            rows = payDao.getByMonth(month(), year());
            model.setRowCount(0);
            double total = 0;
            for (Payroll p : rows) {
                model.addRow(new Object[]{p.id, p.empId, p.empName, p.department,
                        Theme.money(p.basic), Theme.money(p.hra), Theme.money(p.da),
                        Theme.money(p.bonus), Theme.money(p.gross), Theme.money(p.pf),
                        Theme.money(p.tax), Theme.money(p.otherDeduction), Theme.money(p.net)});
                total += p.net;
            }
            lblTotal.setText("Total Net Payout for " + MONTHS[month() - 1] + " " + year()
                    + ":  " + Theme.money(total) + "   (" + rows.size() + " employees)");
        } catch (SQLException ex) {
            error("Could not load payroll: " + ex.getMessage());
        }
    }

    private Payroll selected() {
        int r = table.getSelectedRow();
        if (r < 0 || r >= rows.size()) {
            JOptionPane.showMessageDialog(this, "Select a payroll row first.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return rows.get(r);
    }

    private void deleteSelected() {
        Payroll p = selected();
        if (p == null) return;
        if (JOptionPane.showConfirmDialog(this, "Delete payroll record of " + p.empName + "?",
                "Confirm", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;
        try {
            payDao.delete(p.id);
            load();
        } catch (SQLException ex) {
            error("Database error: " + ex.getMessage());
        }
    }

    // ---------- Payslip ----------
    private void showPayslip() {
        Payroll p = selected();
        if (p == null) return;

        StringBuilder sb = new StringBuilder();
        String line = "=".repeat(52);
        sb.append(line).append("\n");
        sb.append(String.format("%34s%n", "PAYROLLPRO PVT. LTD."));
        sb.append(String.format("%35s%n", "SALARY SLIP - " + MONTHS[p.month - 1].toUpperCase() + " " + p.year));
        sb.append(line).append("\n");
        sb.append(String.format(" Employee ID  : %d%n", p.empId));
        sb.append(String.format(" Name         : %s%n", p.empName));
        sb.append(String.format(" Department   : %s%n", p.department));
        sb.append(String.format(" Designation  : %s%n", p.designation));
        sb.append("-".repeat(52)).append("\n");
        sb.append(" EARNINGS\n");
        sb.append(slipLine("Basic Salary", p.basic));
        sb.append(slipLine("House Rent Allowance (20%)", p.hra));
        sb.append(slipLine("Dearness Allowance (10%)", p.da));
        sb.append(slipLine("Bonus", p.bonus));
        sb.append(slipLine("GROSS EARNINGS", p.gross));
        sb.append("-".repeat(52)).append("\n");
        sb.append(" DEDUCTIONS\n");
        sb.append(slipLine("Provident Fund (12%)", p.pf));
        sb.append(slipLine("Income Tax", p.tax));
        sb.append(slipLine("Other Deductions", p.otherDeduction));
        sb.append(slipLine("TOTAL DEDUCTIONS", p.pf + p.tax + p.otherDeduction));
        sb.append(line).append("\n");
        sb.append(slipLine("NET SALARY", p.net));
        sb.append(line).append("\n");
        sb.append("   This is a computer generated payslip.\n");

        JTextArea area = new JTextArea(sb.toString());
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        area.setEditable(false);
        area.setBorder(new EmptyBorder(15, 15, 15, 15));

        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog dlg = new JDialog(owner, "Payslip - " + p.empName, Dialog.ModalityType.APPLICATION_MODAL);
        JButton print = Theme.button("Print", Theme.PRIMARY);
        print.addActionListener(e -> {
            try {
                area.print();
            } catch (java.awt.print.PrinterException ex) {
                error("Printing failed: " + ex.getMessage());
            }
        });
        JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER));
        south.add(print);
        dlg.add(new JScrollPane(area), BorderLayout.CENTER);
        dlg.add(south, BorderLayout.SOUTH);
        dlg.setSize(520, 560);
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
    }

    private String slipLine(String label, double v) {
        return String.format("   %-30s %16s%n", label, Theme.money(v));
    }

    // ---------- CSV export ----------
    private void exportCsv() {
        if (rows.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nothing to export for this month.",
                    "Empty", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("payroll_" + MONTHS[month() - 1] + "_" + year() + ".csv"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try (PrintWriter pw = new PrintWriter(fc.getSelectedFile(), "UTF-8")) {
            pw.println("EmpID,Name,Department,Basic,HRA,DA,Bonus,Gross,PF,Tax,OtherDeduction,NetSalary");
            for (Payroll p : rows) {
                pw.printf("%d,\"%s\",\"%s\",%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f%n",
                        p.empId, p.empName, p.department, p.basic, p.hra, p.da, p.bonus,
                        p.gross, p.pf, p.tax, p.otherDeduction, p.net);
            }
            JOptionPane.showMessageDialog(this, "Exported successfully.");
        } catch (IOException ex) {
            error("Export failed: " + ex.getMessage());
        }
    }

    private void error(String m) {
        JOptionPane.showMessageDialog(this, m, "Error", JOptionPane.ERROR_MESSAGE);
    }
}