public class Payroll {
    public int id;
    public int empId;
    public int month;
    public int year;
    public String empName;
    public String department;
    public String designation;
    
    public double basic;
    public double hra;
    public double da;
    public double bonus;
    public double gross;
    public double pf;
    public double tax;
    public double otherDeduction;
    public double net;

    // Calculation method called by PayrollPanel
    public static Payroll calculate(Employee e, int month, int year, double bonus, double otherDeduction) {
        Payroll p = new Payroll();
        p.empId = e.id;
        p.empName = e.name;
        p.department = e.department;
        p.designation = e.designation;
        p.month = month;
        p.year = year;
        
        p.basic = e.basicSalary;
        p.hra = p.basic * 0.20;        // 20% HRA
        p.da = p.basic * 0.10;         // 10% DA
        p.bonus = bonus;
        
        p.gross = p.basic + p.hra + p.da + p.bonus;
        
        p.pf = p.basic * 0.12;         // 12% PF
        p.tax = p.gross * 0.05;        // 5% Income Tax estimation
        p.otherDeduction = otherDeduction;
        
        double totalDeductions = p.pf + p.tax + p.otherDeduction;
        p.net = p.gross - totalDeductions;
        
        return p;
    }
}