
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EmployeePanel extends JPanel {
    private final JTextField txtId = Theme.field(), txtName = Theme.field(), txtEmail = Theme.field(),
            txtPhone = Theme.field(), txtDesig = Theme.field(), txtJoin = Theme.field(),
            txtBasic = Theme.field(), txtSearch = Theme.field();
    private final JComboBox<String> cmbDept = new JComboBox<>(Employee.DEPARTMENTS);
    private final JComboBox<String> cmbStatus = new JComboBox<>(new String[]{"Active", "Inactive"});
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Name", "Email", "Phone", "Department", "Designation", "Joined", "Basic Salary", "Status"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);
    private final EmployeeDAO dao = new EmployeeDAO();
    private List<Employee> list = new ArrayList<>();

    public EmployeePanel() {
        setLayout(new BorderLayout(0, 15));
        setBackground(Theme.BG);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("Employee Management");
        title.setFont(Theme.TITLE);
        title.setForeground(Theme.TEXT);

        // ---- Form ----
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(15, 15, 10, 15));
        txtId.setEditable(false);
        txtId.setBackground(new Color(241, 245, 249));
        txtJoin.setText(LocalDate.now().toString());
        cmbDept.setFont(Theme.FONT);
        cmbStatus.setFont(Theme.FONT);

        addField(form, "Employee ID", txtId, 0, 0);
        addField(form, "Full Name *", txtName, 1, 0);
        addField(form, "Email", txtEmail, 2, 0);
        addField(form, "Phone", txtPhone, 0, 1);
        addField(form, "Department", cmbDept, 1, 1);
        addField(form, "Designation", txtDesig, 2, 1);
        addField(form, "Join Date (yyyy-MM-dd)", txtJoin, 0, 2);
        addField(form, "Basic Salary *", txtBasic, 1, 2);
        addField(form, "Status", cmbStatus, 2, 2);

        JButton btnAdd = Theme.button("Add", Theme.GREEN);
        JButton btnUpd = Theme.button("Update", Theme.PRIMARY);
        JButton btnDel = Theme.button("Delete", Theme.RED);
        JButton btnClr = Theme.button("Clear", Theme.ORANGE);
        btnAdd.addActionListener(e -> addEmployee());
        btnUpd.addActionListener(e -> updateEmployee());
        btnDel.addActionListener(e -> deleteEmployee());
        btnClr.addActionListener(e -> clearForm());

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        btns.setBackground(Color.WHITE);
        btns.add(btnAdd); btns.add(btnUpd); btns.add(btnDel); btns.add(btnClr);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(title, BorderLayout.NORTH);
        JPanel formWrap = new JPanel(new BorderLayout());
        formWrap.setBorder(new EmptyBorder(12, 0, 0, 0));
        formWrap.setOpaque(false);
        JPanel formCard = new JPanel(new BorderLayout());
        formCard.add(form, BorderLayout.CENTER);
        formCard.add(btns, BorderLayout.SOUTH);
        formWrap.add(formCard, BorderLayout.CENTER);
        top.add(formWrap, BorderLayout.CENTER);

        // ---- Search + table ----
        JPanel searchBar = new JPanel(new BorderLayout(10, 0));
        searchBar.setOpaque(false);
        searchBar.add(Theme.label("Search (name / department / designation / ID):"), BorderLayout.WEST);
        searchBar.add(txtSearch, BorderLayout.CENTER);
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { loadTable(); }
            public void removeUpdate(DocumentEvent e) { loadTable(); }
            public void changedUpdate(DocumentEvent e) { loadTable(); }
        });

        Theme.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) fillFormFromSelection();
        });

        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setOpaque(false);
        center.add(searchBar, BorderLayout.NORTH);
        center.add(new JScrollPane(table), BorderLayout.CENTER);

        add(top, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
    }

    private void addField(JPanel p, String label, JComponent comp, int col, int row) {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 8, 5, 8);
        g.anchor = GridBagConstraints.WEST;
        g.gridx = col * 2; g.gridy = row;
        p.add(Theme.label(label), g);
        g.gridx = col * 2 + 1; g.weightx = 1; g.fill = GridBagConstraints.HORIZONTAL;
        p.add(comp, g);
    }

    public void refresh() {
        loadTable();
    }

    private void loadTable() {
        try {
            list = dao.getAll(txtSearch.getText());
            model.setRowCount(0);
            for (Employee e : list) {
                model.addRow(new Object[]{e.id, e.name, e.email, e.phone, e.department,
                        e.designation, e.joinDate, Theme.money(e.basicSalary), e.status});
            }
        } catch (SQLException ex) {
            error("Could not load employees: " + ex.getMessage());
        }
    }

    private void fillFormFromSelection() {
        int r = table.getSelectedRow();
        if (r < 0 || r >= list.size()) return;
        Employee e = list.get(r);
        txtId.setText(String.valueOf(e.id));
        txtName.setText(e.name);
        txtEmail.setText(e.email);
        txtPhone.setText(e.phone);
        cmbDept.setSelectedItem(e.department);
        txtDesig.setText(e.designation);
        txtJoin.setText(e.joinDate.toString());
        txtBasic.setText(String.valueOf(e.basicSalary));
        cmbStatus.setSelectedItem(e.status);
    }

    /** Reads and validates the form. Throws IllegalArgumentException with a friendly message. */
    private Employee readForm() {
        Employee e = new Employee();
        e.name = txtName.getText().trim();
        if (e.name.isEmpty()) throw new IllegalArgumentException("Name is required.");
        e.email = txtEmail.getText().trim();
        if (!e.email.isEmpty() && !e.email.matches("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$"))
            throw new IllegalArgumentException("Enter a valid email address.");
        e.phone = txtPhone.getText().trim();
        if (!e.phone.isEmpty() && !e.phone.matches("\\d{10,15}"))
            throw new IllegalArgumentException("Phone must contain 10-15 digits.");
        e.department = (String) cmbDept.getSelectedItem();
        e.designation = txtDesig.getText().trim();
        try {
            e.joinDate = LocalDate.parse(txtJoin.getText().trim());
        } catch (DateTimeException ex) {
            throw new IllegalArgumentException("Join date must be in yyyy-MM-dd format.");
        }
        try {
            e.basicSalary = Double.parseDouble(txtBasic.getText().trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Basic salary must be a number.");
        }
        if (e.basicSalary <= 0) throw new IllegalArgumentException("Basic salary must be greater than 0.");
        e.status = (String) cmbStatus.getSelectedItem();
        return e;
    }

    private void addEmployee() {
        try {
            dao.add(readForm());
            info("Employee added successfully.");
            clearForm();
            loadTable();
        } catch (IllegalArgumentException ex) {
            warn(ex.getMessage());
        } catch (SQLException ex) {
            error("Database error: " + ex.getMessage());
        }
    }

    private void updateEmployee() {
        if (txtId.getText().isEmpty()) { warn("Select an employee from the table first."); return; }
        try {
            Employee e = readForm();
            e.id = Integer.parseInt(txtId.getText());
            dao.update(e);
            info("Employee updated successfully.");
            clearForm();
            loadTable();
        } catch (IllegalArgumentException ex) {
            warn(ex.getMessage());
        } catch (SQLException ex) {
            error("Database error: " + ex.getMessage());
        }
    }

    private void deleteEmployee() {
        if (txtId.getText().isEmpty()) { warn("Select an employee from the table first."); return; }
        int ok = JOptionPane.showConfirmDialog(this,
                "Delete this employee and all their payroll records?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (ok != JOptionPane.YES_OPTION) return;
        try {
            dao.delete(Integer.parseInt(txtId.getText()));
            info("Employee deleted.");
            clearForm();
            loadTable();
        } catch (SQLException ex) {
            error("Database error: " + ex.getMessage());
        }
    }

    private void clearForm() {
        txtId.setText(""); txtName.setText(""); txtEmail.setText(""); txtPhone.setText("");
        txtDesig.setText(""); txtBasic.setText("");
        txtJoin.setText(LocalDate.now().toString());
        cmbDept.setSelectedIndex(0);
        cmbStatus.setSelectedIndex(0);
        table.clearSelection();
    }

    private void info(String m)  { JOptionPane.showMessageDialog(this, m, "Success", JOptionPane.INFORMATION_MESSAGE); }
    private void warn(String m)  { JOptionPane.showMessageDialog(this, m, "Validation", JOptionPane.WARNING_MESSAGE); }
    private void error(String m) { JOptionPane.showMessageDialog(this, m, "Error", JOptionPane.ERROR_MESSAGE); }
}