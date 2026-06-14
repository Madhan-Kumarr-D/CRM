package com.example.demo.Repository;
import com.example.demo.Model.Field;
import com.example.demo.Model.Module;
import org.springframework.stereotype.Repository;
import com.example.demo.Repository.FieldRepository;

import java.sql.Date;
import java.time.LocalDate;

import java.sql.*;
import java.util.*;

import com.example.demo.Model.Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate; // 💡 Import JdbcTemplate
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
//import java.util.Date;
import java.util.List;
//import java.util.Date; // java.util.Date is fine for the model

@Repository
public class ModuleRepository {
    // Update this line in ModuleRepository.java
//    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=MADHANDatabase;encrypt=true;trustServerCertificate=true;";
//
//    private static final String USER = "SpringUser";
//
//    private static final String PASSWORD = "Sandbox@123#";
//
//    // 1. Let Spring inject the configured JdbcTemplate
//   private Connection getConnection(){
//       try {
//           Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//           return DriverManager.getConnection(URL,USER,PASSWORD);
//       }catch (Exception e){
//           throw new RuntimeException(e);
//       }
//   }
    @Autowired
    private DataSource dataSource;

    // Delete the hardcoded URL, USER, and PASSWORD variables

    public Connection getConnection() throws SQLException {
        // Now it uses your application.properties settings!
        return dataSource.getConnection();
    }

   private Module mapRow(ResultSet rs) throws  SQLException{
       long ModuleID = rs.getLong("RecordID");
       String ModuleName = rs.getString("ModuleName");
       String ApiName = rs.getString("ApiName");
       Date CreatedDate = rs.getDate("CreatedDate");
       return new Module(ModuleName,ModuleID,ApiName,CreatedDate);
   }
    private Field mapRowField(ResultSet rs) throws SQLException {
        String FieldApiName = rs.getString("FieldApiName");
        String FieldDisplayName = rs.getString("FieldDisplayName");
        String FieldDataType = rs.getString("FieldDataType");
        Long FieldID = rs.getLong("FieldID");
        return new Field(FieldID,FieldApiName,FieldDisplayName,FieldDataType);
    }

public  List<Module> getModules(){
       String selectQuery = "SELECT * FROM Module";
       List<Module> modules = new ArrayList<>();
       try (Connection con = getConnection()){
           Statement stmt = con.createStatement();
           ResultSet rs = stmt.executeQuery(selectQuery);
           while (rs.next()){
               modules.add(mapRow(rs));

           }
       } catch (SQLException e) {
           throw new RuntimeException(e);
       }
       return modules;
}
public Module createModule(Module modulePayload) throws SQLException {
       String insertQuery = "INSERT Module (ModuleName,ApiName,CreatedDate) VALUES (?,?,?)";
       String apiname = modulePayload.getApiName();
    if (!apiname.matches("^[a-zA-Z0-9_]{1,20}$")) {
        System.out.println("Invalid API Name detected: " + apiname);
        return null;
    }
    String CreateTableQuery = "CREATE TABLE "+modulePayload.getApiName()+" (   FieldID BIGINT NOT NULL IDENTITY(1,1) PRIMARY KEY, FieldApiName VARCHAR(20) NOT NULL,  FieldDisplayName VARCHAR(20) NOT NULL,  FieldDataType VARCHAR(20) NOT NULL ) ";
    String InsertFirstRow = "INSERT INTO "+modulePayload.getApiName()+" ( FieldApiName , FieldDisplayName , FieldDataType) VALUES ( 'Name' , 'Name' , 'String' ) ";
    String InsertSecondRow = "INSERT INTO "+modulePayload.getApiName()+" ( FieldApiName , FieldDisplayName , FieldDataType) VALUES ( 'Number' , 'Number' , 'Number' ) ";
    String CreateRecordQuery = "CREATE TABLE "+modulePayload.getApiName()+"records  ( RecordID BIGINT NOT NULL IDENTITY(1,1) PRIMARY KEY, Name VARCHAR(20) NOT NULL , Number BIGINT)";
    System.out.println(modulePayload);
       try (Connection con = getConnection()){
           con.setAutoCommit(false);

           try (PreparedStatement pst = con.prepareStatement(insertQuery,Statement.RETURN_GENERATED_KEYS);){
               pst.setString(1,modulePayload.getModuleName());
               pst.setString(2,modulePayload.getApiName());
               java.time.LocalDate today = java.time.LocalDate.now();
               pst.setDate(3,java.sql.Date.valueOf(today));
               System.out.println(pst);
               if (pst.executeUpdate() > 0){
                   try (ResultSet rs = pst.getGeneratedKeys()){
                       if (rs.next()){
                           long generatedID = rs.getLong(1);
                           modulePayload.setRecordID(generatedID);
                       }
                   }
                   try (Statement st = con.createStatement()){
                       st.executeUpdate(CreateTableQuery);
                       st.executeUpdate(InsertFirstRow);
                       st.executeUpdate(InsertSecondRow);
                       st.executeUpdate(CreateRecordQuery);
                       con.commit();
                       System.out.println("updation completed");
                       return modulePayload;
                   }catch (Exception e){
                       con.rollback();
                       System.out.println(e);
                   }

               }
               else {
                   con.rollback();
                   System.out.println("rolled back");
               }
           }
           catch (Exception e){
               con.rollback();
               System.out.println("failed");
           }

       } catch (Exception e) {

           System.out.println(e);
       }

       return null;
}

public List<Map<String, Object>> getModulesByID(long ModuleID) throws SQLException {
       Module module = null;
    List<Map<String, Object>> recordList = new ArrayList<>();
       try (Connection con = getConnection()){
           String query = "SELECT * FROM Module WHERE RecordID = ?";
           String ModuleRecordField = "SELECT * FROM ?";
           List<Field> fieldList = new ArrayList<>();
           String ModuleRecord = "SELECT * FROM ?";
           try (PreparedStatement pt = con.prepareStatement(query)){
               pt.setLong(1,ModuleID);
               ResultSet rs = pt.executeQuery();
               if (rs.next()){
                   module = mapRow(rs);
               }
               try (PreparedStatement pt1 = con.prepareStatement(ModuleRecordField)){

                   pt1.setString(1,module.getApiName());
                   ResultSet rs1 = pt1.executeQuery();
                   while (rs1.next()){
                       fieldList.add(mapRowField(rs1));
                   }
               }
               catch (NullPointerException  e){
                   System.out.println(e);
               }

               try (PreparedStatement pt2 = con.prepareStatement(ModuleRecord)){
                   pt2.setString(1,module.getApiName()+"records");
                   ResultSet rs2 = pt2.executeQuery();
                   ResultSetMetaData rsdata = rs2.getMetaData();
                   while (rs2.next()){
                       Map<String,Object> objList = new HashMap<>();

                       int ColumnCount = rsdata.getColumnCount();
                       for (int i =0;i<ColumnCount;i++){
                           objList.put(rsdata.getColumnName(i),rs2.getObject(i));
                       }
                       recordList.add(objList);
                   }
               }

           }
       }catch (Exception e){
           throw new RuntimeException("none");
       }
       return recordList;
}

    public Module deleteModule(Module moduleData) {
       try (Connection con = getConnection()) {

           con.setAutoCommit(false);
           String DeleteQuery = "DELETE FROM Module WHERE RecordID = ?";
           String DeleteTable = "DROP TABLE "+moduleData.getApiName()+"records";
           try (PreparedStatement stMod = con.prepareStatement(DeleteQuery)){
               stMod.setLong(1, moduleData.getRecordID());
               int rs = stMod.executeUpdate();
               if (rs != 0) {
                   DeleteQuery = "DROP TABLE " + moduleData.getApiName();
                   try (Statement st = con.createStatement()) {
                       int rsDel = st.executeUpdate(DeleteQuery);
                       int rsdel1 = st.executeUpdate(DeleteTable);
                       con.commit();
                   } catch (Exception e) {
                       throw new RuntimeException(e);
                   }
               }
               else {
                   con.rollback();

               }
           }
           catch (Exception e) {
               con.rollback();
               throw new RuntimeException(e);
           }

       } catch (Exception e) {
           throw new RuntimeException(e);
       }
       return moduleData;
   }

    public Module getModulesByIDAlone(Long moduleID) throws SQLException {
        Module module = null;
        try (Connection con = getConnection()) {
            String query = "SELECT * FROM Module WHERE RecordID = ?";
            try (PreparedStatement pt = con.prepareStatement(query)) {
                pt.setLong(1, moduleID);
                ResultSet rs = pt.executeQuery();
                if (rs.next()) {
                    module = mapRow(rs);
                }
            }
        }
        return module;
    }
    // 2. Define the RowMapper once to convert SQL row to Module object
//    private final class ModuleRowMapper implements RowMapper<Module> {
//        @Override
//        public Module mapRow(ResultSet rs, int rowNum) throws SQLException {
//            // Use rs.getLong() for BIGINT columns
//            long RecordID = rs.getLong("RecordID");
//            String ModuleName = rs.getString("ModuleName");
//            String ApiName = rs.getString("ApiName");
//            Date CreatedDate = rs.getDate("CreatedDate");
//
//            // Assuming your Module constructor order is (ModuleName, RecordID, ApiName, CreatedDate)
//            return new Module(ModuleName, RecordID, ApiName, CreatedDate);
//        }
//    }
//
//    // Constructor is now implicit (default) and does not need definition.
//
//    public List<Module> getModules() {
//        String selectQuery = "SELECT * FROM Module";
//        // 3. Use jdbcTemplate.query() - it handles connection pooling/closing
//        return jdbcTemplate.query(selectQuery, new ModuleRowMapper());
//    }
//
//    public Module createModule(Module modulePayload) {
//        String InsertQuery = "INSERT INTO table_name (column1, column2, column3) VALUES (value1, value2, value3)";
//    }
//
//    public Module getModulesByID(long moduleID) {
//        // Use '?' placeholder for security
//        String selectQuery = "SELECT * FROM Module WHERE RecordID = ?";
//
//        try {
//            // 4. Use queryForObject for a single result, passing ID securely
//            return jdbcTemplate.queryForObject(
//                    selectQuery,
//                    new ModuleRowMapper(),
//                    moduleID // Securely passed parameter
//            );
//        } catch (Exception e) {
//            // Return null if no module is found
//            return null;
//        }

}