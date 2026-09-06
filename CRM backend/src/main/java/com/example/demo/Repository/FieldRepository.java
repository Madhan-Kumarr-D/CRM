package com.example.demo.Repository;
import javax.sql.DataSource;
import com.example.demo.Model.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.*;

@Repository
public class FieldRepository {
//    private static final String URL = "jdbc:sqlserver://DESKTOP-8K9QAPT;databaseName=MADHANDatabase;encrypt=true;trustServerCertificate=true;";
//    private static final String USER = "SpringUser";
//    private static final String PASSWORD = "Sandbox@123#";
//    public Connection getConnection() throws ClassNotFoundException, SQLException {
//        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//
//        try {
//            return DriverManager.getConnection(URL,USER,PASSWORD);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
@Autowired
private DataSource dataSource;
    // Delete the hardcoded URL, USER, and PASSWORD variables
    public Connection getConnection() throws SQLException {
        // Now it uses your application.properties settings!
        return dataSource.getConnection();
    }
    private Field mapRow(ResultSet rs) throws SQLException {
        String FieldApiName = rs.getString("FieldApiName");
        String FieldDisplayName = rs.getString("FieldDisplayName");
        String FieldDataType = rs.getString("FieldDataType");
        Long FieldID = rs.getLong("FieldID");
        return new Field(FieldID,FieldApiName,FieldDisplayName,FieldDataType);
    }
    public Field getModuleFieldByID(long FieldID,String ModuleApiName){
        String Query = "SELECT * FROM "+ModuleApiName+" WHERE FieldId = ?"+FieldID;
        try (Connection con = getConnection()){
            Statement st = con.createStatement();
//            ResultSet rs = st.executeQuery(Query);
            try (PreparedStatement pr = con.prepareStatement(Query)){
                pr.setLong(1,FieldID);
                try (ResultSet rs = pr.executeQuery()){
                    if (rs.next()) {
                        return mapRow(rs);
                    }
//                    throw new IllegalArgumentException("Field not found: " + FieldID);
                }
            }
            throw new IllegalArgumentException("Field not found: " + FieldID);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public List<Field> getModuleField(String ModuleApiName) {
        List<Field> arr = new ArrayList<>();
        System.out.println("SUCCESS FULLY ENTERED THE CONSOLE MODULE API NAME [ inside repository layer ] - "+ModuleApiName);
        String Query = "SELECT * FROM "+ModuleApiName;
        try (Connection con = getConnection()){
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(Query);
            while (rs.next()){
                arr.add(mapRow(rs));
                System.out.println(mapRow(rs));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return arr;
    }
    public Map<String,String> createModuleField(String moduleApiName, Map<String,String> fieldData) {
        if (!moduleApiName.matches("^[a-zA-Z0-9_]{1,50}$")) {
            throw new IllegalArgumentException("Invalid module API name.");
        }
        String fieldApiName = fieldData.get("fieldApiName");
        if (!fieldApiName.matches("^[a-zA-Z0-9_]{1,20}$")) {
            throw new IllegalArgumentException("Invalid field API name.");
        }

        String type = fieldData.get("fieldDataType");
        if (type.equals("String"))
            type="VARCHAR(20)";
        String InsertColumn ="ALTER TABLE " + moduleApiName + "records ADD "
                + fieldData.get("fieldApiName") + " " + type + " NULL";
        int rs;
    try (Connection con = getConnection()){
        con.setAutoCommit(false);
        String outboxQuery = "INSERT INTO outbox (aggregate, event_type, payload) VALUES (?, ?, ?)";
        try {

            String InsertQuery = "INSERT INTO " + moduleApiName + " (FieldApiName,FieldDisplayName,FieldDataType) VALUES (?,?,?)";
            try (PreparedStatement st1 =  con.prepareStatement(outboxQuery)) {
                st1.setString(1, moduleApiName);
                st1.setString(2, fieldData.get("fieldDisplayName"));
                StringBuilder Builder = new StringBuilder();
                Builder.append("{\"FieldApiName\":\"").append(fieldData.get("fieldApiName"))
                        .append("\",\"FieldDisplayName\":\"").append(fieldData.get("FieldDisplayName"))
                        .append("\",\"FieldDataType\":\"").append(fieldData.get("FieldDataType")).append("\"}");
                st1.setString(3, Builder.toString());
                if(st1.executeUpdate()<=0){
                    throw new RuntimeException("Error inserting outbox record");
                }
            }
            try (PreparedStatement st = con.prepareStatement(InsertQuery)) {

                st.setString(1, fieldData.get("fieldApiName"));
                st.setString(2, fieldData.get("fieldDisplayName"));
                st.setString(3, fieldData.get("fieldDataType"));
                rs = st.executeUpdate();
                if (rs > 0) {
                    try (Statement st2 = con.createStatement()) {
                       if( st2.executeUpdate(InsertColumn) ==0) {
                           con.commit();
                           Map<String, String> map = new HashMap<>();
                           map.put("Code","201");
                           map.put("Message","Success");
                           return map;
                       }
                       else{
                           throw new RuntimeException("Error on field creation");
                       }
                    }
                } else {
                    throw new RuntimeException("Error on field creation");
                }
            }
        }
        catch (Exception e) {
            con.rollback();
            throw new RuntimeException(e);
        }
        finally {
            con.setAutoCommit(true);
        }
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
    }

//    public Field updateModuleField(String moduleApiName, Field fieldData) {
//        if (!moduleApiName.matches("^[a-zA-Z0-9_]{1,50}$")) {
//            throw new IllegalArgumentException("Invalid module API name for security.");
//        }
//        if (!fieldData.getFieldApiName().matches("^[a-zA-Z0-9_]{1,20}$")) {
//            throw new IllegalArgumentException("Invalid field API name.");
//        }
//        String UpdateQuery = "UPDATE "+moduleApiName+" SET FieldApiName = ? , FieldDisplayName = ? , FieldDataType = ? WHERE FieldID = ? ";
//        String type = fieldData.getFieldDataType();
//        if (type.equals("String"))
//            type="VARCHAR(20)";
//        String InsertColumn = "ALTER TABLE "+ moduleApiName + "records ADD "+fieldData.getFieldApiName()+" "+type+" NOT NULL";
//        int rs;
//        try (Connection con = getConnection()) {
//            con.setAutoCommit(false);
//            try(PreparedStatement st = con.prepareStatement(UpdateQuery)){
//            st.setString(1, fieldData.getFieldApiName());
//            st.setString(2, fieldData.getFieldDisplayName());
//            st.setString(3, fieldData.getFieldDataType());
//            st.setLong(4, fieldData.getFieldID());
//            rs = st.executeUpdate();
//            if (rs > 0) {
//                try(Statement st2 = con.createStatement()) {
//                    rs = st2.executeUpdate(InsertColumn);
//                    con.commit();
//                    return fieldData;
//                }
//            }
//
//            con.rollback();
//            return null;
//
//        }
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }

    public String deleteModuleField(String moduleApiName, Long fieldID) {
        if (!moduleApiName.matches("^[a-zA-Z0-9_]{1,50}$")) {
            throw new IllegalArgumentException("Invalid module API name for security.");
        }
        Field field = getModuleFieldByID(fieldID, moduleApiName);
        if (field == null || field.getFieldApiName() == null) {
            throw new IllegalArgumentException("Field does not exist.");
        }

        if (!field.getFieldApiName().matches("^[a-zA-Z0-9_]{1,20}$")) {
            throw new IllegalStateException("Corrupted field API name in database.");
        }

        String DeleteQuery = "DELETE FROM " + moduleApiName + " WHERE FieldID = ?";
        String DeleteColumn = "ALTER TABLE " + moduleApiName + "records DROP COLUMN " + field.getFieldApiName();
        String OutboxQuery = "INSERT INTO outbox (aggregate, event_type, payload) VALUES (?, ?, ?)";

        try (Connection con = getConnection()) {
            con.setAutoCommit(false);
            try {

                try (PreparedStatement st = con.prepareStatement(DeleteQuery)) {
                    st.setLong(1, fieldID);
                    int rowsDeleted = st.executeUpdate();
                    if (rowsDeleted == 0) {
                        con.rollback();
                        return null;
                    }
                }


                try (PreparedStatement pstOutbox = con.prepareStatement(OutboxQuery)) {
                    pstOutbox.setString(1, moduleApiName);
                    pstOutbox.setString(2, "FIELD_DELETE");


                    String payload = "{\"FieldID\":" + fieldID + ",\"FieldApiName\":\"" + field.getFieldApiName() + "\"}";
                    pstOutbox.setString(3, payload);

                    if (pstOutbox.executeUpdate() <= 0) {
                        throw new RuntimeException("Failed to insert outbox event.");
                    }
                }


                try (Statement st2 = con.createStatement()) {
                    st2.executeUpdate(DeleteColumn);
                }

                con.commit();
                return "success";

            } catch (Exception e) {
                con.rollback();
                throw new RuntimeException(e);
            } finally {
                con.setAutoCommit(true);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}


