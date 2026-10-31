
import java.time.LocalDate;

public class Employee {
    public static final String[] DEPARTMENTS =
        {"IT", "HR", "Finance", "Sales", "Marketing", "Operations", "Admin"};

    public int id;
    public String name, email, phone, department, designation, status;
    public LocalDate joinDate;
    public double basicSalary;

    @Override
    public String toString() { return id + " - " + name; }
}