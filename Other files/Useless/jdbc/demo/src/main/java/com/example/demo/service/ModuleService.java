package com.example.demo.service;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.springframework.stereotype.Service;

public class ModuleService {
    ModuleService() {
        // SQL Server connection details
        String url = "jdbc:sqlserver://DESKTOP-8K9QAPT;databaseName=MADHANDatabase;encrypt=true;trustServerCertificate=true;";
        String user = "SpringUser";          // SQL Server username
        String password = "Sandbox@123#";  // SQL Server password

        try {
            // 1. Load SQL Server JDBC Driver
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            // 2. Connect to SQL Server
            Connection con = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Connected to SQL Server!");

            // 3. Insert Data
//            String insertQuery = "INSERT INTO Students (id, name, age) VALUES (?, ?, ?)";
//            PreparedStatement pst = con.prepareStatement(insertQuery);
//            pst.setInt(1, 1);
//            pst.setString(2, "Alice");
//            pst.setInt(3, 22);
//            pst.executeUpdate();
//            System.out.println("✅ Data inserted");

            // 4. Fetch Data
            String selectQuery = "SELECT * FROM dbo.Employees";
            ResultSet rs = con.createStatement().executeQuery(selectQuery);

            while (rs.next()) {
                int id = rs.getInt("EmployeeID");
                String name = rs.getString("FirstName");
                String age = rs.getString("LastName");
                System.out.println(id + " | " + name + " | " + age);
            }

            // 5. Close connection
            con.close();
            System.out.println("✅ Connection closed");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
