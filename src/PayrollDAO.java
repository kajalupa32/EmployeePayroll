
import java.sql.*;
import java.util.*;

public class PayrollDAO {

    public boolean exists(int empId, int month, int year) throws SQLException {
        String sql = "SELECT 1 FROM payroll WHERE emp_id=? AND pay_month=? AND pay_year=?";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, empId);
            ps.setInt(2, month);
            ps.setInt(3, year);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean insert(Payroll p) throws SQLException {
        String sql = "INSERT INTO payroll(emp_id,pay_month,pay_year,basic,hra,da,bonus,gross,pf,tax,"
                   + "other_deduction,net_salary) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, p.empId);
            ps.setInt(2, p.month);
            ps.setInt(3, p.year);
            ps.setDouble(4, p.basic);
            ps.setDouble(5, p.hra);
            ps.setDouble(6, p.da);
            ps.setDouble(7, p.bonus);
            ps.setDouble(8, p.gross);
            ps.setDouble(9, p.pf);
            ps.setDouble(10, p.tax);
            ps.setDouble(11, p.otherDeduction);
            ps.setDouble(12, p.net);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean delete(int id) throws SQLException {
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM payroll WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Payroll> getByMonth(int month, int year) throws SQLException {
        String sql = "SELECT p.*, e.name, e.department, e.designation FROM payroll p "
                   + "JOIN employees e ON p.emp_id=e.emp_id "
                   + "WHERE p.pay_month=? AND p.pay_year=? ORDER BY e.name";
        List<Payroll> list = new ArrayList<>();
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, month);
            ps.setInt(2, year);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Payroll p = new Payroll();
                    p.id = rs.getInt("id");
                    p.empId = rs.getInt("emp_id");
                    p.month = rs.getInt("pay_month");
                    p.year = rs.getInt("pay_year");
                    p.empName = rs.getString("name");
                    p.department = rs.getString("department");
                    p.designation = rs.getString("designation");
                    p.basic = rs.getDouble("basic");
                    p.hra = rs.getDouble("hra");
                    p.da = rs.getDouble("da");
                    p.bonus = rs.getDouble("bonus");
                    p.gross = rs.getDouble("gross");
                    p.pf = rs.getDouble("pf");
                    p.tax = rs.getDouble("tax");
                    p.otherDeduction = rs.getDouble("other_deduction");
                    p.net = rs.getDouble("net_salary");
                    list.add(p);
                }
            }
        }
        return list;
    }

    public double totalNet(int month, int year) throws SQLException {
        String sql = "SELECT COALESCE(SUM(net_salary),0) FROM payroll WHERE pay_month=? AND pay_year=?";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, month);
            ps.setInt(2, year);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0;
            }
        }
    }
}