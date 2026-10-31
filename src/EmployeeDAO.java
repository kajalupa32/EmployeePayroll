
import java.sql.*;
import java.util.*;

public class EmployeeDAO {

    public boolean add(Employee e) throws SQLException {
        String sql = "INSERT INTO employees(name,email,phone,department,designation,join_date,basic_salary,status) "
                   + "VALUES(?,?,?,?,?,?,?,?)";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            fill(ps, e);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean update(Employee e) throws SQLException {
        String sql = "UPDATE employees SET name=?,email=?,phone=?,department=?,designation=?,"
                   + "join_date=?,basic_salary=?,status=? WHERE emp_id=?";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            fill(ps, e);
            ps.setInt(9, e.id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM employees WHERE emp_id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Employee> getAll(String keyword) throws SQLException {
        String sql = "SELECT * FROM employees WHERE name LIKE ? OR department LIKE ? "
                   + "OR designation LIKE ? OR CAST(emp_id AS CHAR) LIKE ? ORDER BY emp_id DESC";
        List<Employee> list = new ArrayList<>();
        String k = "%" + (keyword == null ? "" : keyword.trim()) + "%";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            for (int i = 1; i <= 4; i++) ps.setString(i, k);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        }
        return list;
    }

    public int countAll() throws SQLException {
        return scalarInt("SELECT COUNT(*) FROM employees");
    }

    public int countActive() throws SQLException {
        return scalarInt("SELECT COUNT(*) FROM employees WHERE status='Active'");
    }

    public Map<String, Integer> departmentCounts() throws SQLException {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql = "SELECT department, COUNT(*) FROM employees GROUP BY department ORDER BY department";
        try (Connection c = db.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) map.put(rs.getString(1), rs.getInt(2));
        }
        return map;
    }

    private int scalarInt(String sql) throws SQLException {
        try (Connection c = db.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private void fill(PreparedStatement ps, Employee e) throws SQLException {
        ps.setString(1, e.name);
        ps.setString(2, e.email);
        ps.setString(3, e.phone);
        ps.setString(4, e.department);
        ps.setString(5, e.designation);
        ps.setDate(6, java.sql.Date.valueOf(e.joinDate));
        ps.setDouble(7, e.basicSalary);
        ps.setString(8, e.status);
    }

    private Employee map(ResultSet rs) throws SQLException {
        Employee e = new Employee();
        e.id = rs.getInt("emp_id");
        e.name = rs.getString("name");
        e.email = rs.getString("email");
        e.phone = rs.getString("phone");
        e.department = rs.getString("department");
        e.designation = rs.getString("designation");
        e.joinDate = rs.getDate("join_date").toLocalDate();
        e.basicSalary = rs.getDouble("basic_salary");
        e.status = rs.getString("status");
        return e;
    }
}