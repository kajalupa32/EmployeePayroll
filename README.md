# Payroll System (Java Swing + MySQL)

A desktop payroll management application built with Java Swing and MySQL.

## Features
- Secure login screen
- Dashboard with employee counts and department overview
- Add, update, delete and search employees
- Generate monthly payroll (HRA 20%, DA 10%, PF 12%, tax 5%, bonus and other deductions)
- View and delete payroll records by month/year

## Tech Stack
- Java (JDK 16 or higher)
- Java Swing (GUI)
- MySQL + JDBC (MySQL Connector/J)

## Setup

1. **Install** JDK 16+ and MySQL.
2. **Download** MySQL Connector/J (the `.jar`) from https://dev.mysql.com/downloads/connector/j/
3. **Create the database:**
   ```
   mysql -u root -p < schema.sql
   ```
4. **Set your MySQL password** (the app reads it from environment variables):
   - Windows (CMD): `set DB_PASSWORD=your_password`
   - Mac/Linux: `export DB_PASSWORD=your_password`
   - Optional: `DB_USER` (defaults to `root`)
5. **Compile and run** (replace the jar name with yours):
   ```
   cd PayrollSystem
   javac -cp ".;mysql-connector-j.jar" -d bin src/*.java
   java -cp "bin;mysql-connector-j.jar" Main
   ```
   On Mac/Linux use `:` instead of `;`.

   Or import the project into Eclipse/IntelliJ and add the jar to the build path.

## Default Login
`admin` / `admin123` — for demo purposes only. Change it before any real use.

## Known Limitations
- Passwords are hashed with plain SHA-256 (no salt). Use BCrypt for real-world use.
- Single admin user, no roles.

## Note
Built with AI assistance (Claude) as a learning project.
