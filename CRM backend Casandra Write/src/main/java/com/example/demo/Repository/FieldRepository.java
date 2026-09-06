package com.example.demo.Repository;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.example.demo.Model.Field;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class FieldRepository {

    private final CqlSession cqlSession;

    // Pre-compile queries for maximum performance
    private final PreparedStatement psInsertOutbox;
    private final PreparedStatement psInsertField;
    private final PreparedStatement psUpdateField;
    private final PreparedStatement psDeleteField;
    private final PreparedStatement psGetField;
    private final PreparedStatement psGetAllFields;

    public FieldRepository(CqlSession cqlSession) {
        this.cqlSession = cqlSession;

        this.psInsertOutbox = cqlSession.prepare(
                "INSERT INTO module_management.outbox (aggregate, event_id, event_type, payload) VALUES (?, ?, ?, ?)"
        );
        this.psInsertField = cqlSession.prepare(
                "INSERT INTO module_management.module_fields (api_name, field_api_name, field_display_name, field_data_type) VALUES (?, ?, ?, ?)"
        );
        this.psUpdateField = cqlSession.prepare(
                "UPDATE module_management.module_fields SET field_display_name = ?, field_data_type = ? WHERE api_name = ? AND field_api_name = ?"
        );
        this.psDeleteField = cqlSession.prepare(
                "DELETE FROM module_management.module_fields WHERE api_name = ? AND field_api_name = ?"
        );
        this.psGetField = cqlSession.prepare(
                "SELECT * FROM module_management.module_fields WHERE api_name = ? AND field_api_name = ?"
        );
        this.psGetAllFields = cqlSession.prepare(
                "SELECT * FROM module_management.module_fields WHERE api_name = ?"
        );
    }

    // Helper method to map Cassandra Row to Field Model
    private Field mapRow(Row row) {
        String fieldApiName = row.getString("field_api_name");
        String fieldDisplayName = row.getString("field_display_name");
        String fieldDataType = row.getString("field_data_type");

        // Note: FieldID is deprecated in Cassandra schema. Passing 0L as a placeholder.
        return new Field(0L, fieldApiName, fieldDisplayName, fieldDataType);
    }

    public Map<String, String> createModuleField(String moduleApiName, Map<String, String> fieldData) {
        try {
            UUID eventId = Uuids.timeBased();
            String fieldApiName = fieldData.get("fieldApiName");
            String fieldDisplayName = fieldData.get("fieldDisplayName");
            String fieldDataType = fieldData.get("fieldDataType");

            // 1. Insert into Outbox
            StringBuilder builder = new StringBuilder();
            builder.append("{\"FieldApiName\":\"").append(fieldApiName)
                    .append("\",\"FieldDisplayName\":\"").append(fieldDisplayName)
                    .append("\",\"FieldDataType\":\"").append(fieldDataType).append("\"}");

            BoundStatement boundOutbox = psInsertOutbox.bind(
                    moduleApiName, eventId, "CREATE_FIELD", builder.toString()
            );
            cqlSession.execute(boundOutbox);

            // 2. Insert Field Metadata
            // No ALTER TABLE needed because module records use a flexible map<text, text> in Cassandra
            BoundStatement boundField = psInsertField.bind(
                    moduleApiName, fieldApiName, fieldDisplayName, fieldDataType
            );
            cqlSession.execute(boundField);

            Map<String, String> response = new HashMap<>();
            response.put("Code", "201");
            response.put("Message", "Success");
            return response;

        } catch (Exception e) {
            throw new RuntimeException("Error on field creation: " + e.getMessage(), e);
        }
    }

    public Field updateModuleField(String moduleApiName, Field fieldData) {
        if (!moduleApiName.matches("^[a-zA-Z0-9_]{1,50}$")) {
            throw new IllegalArgumentException("Invalid module API name for security.");
        }
        if (!fieldData.getFieldApiName().matches("^[a-zA-Z0-9_]{1,20}$")) {
            throw new IllegalArgumentException("Invalid field API name.");
        }

        try {
            // Update the display name and data type for the specific module and field API name
            BoundStatement boundUpdate = psUpdateField.bind(
                    fieldData.getFieldDisplayName(),
                    fieldData.getFieldDataType(),
                    moduleApiName,
                    fieldData.getFieldApiName()
            );
            cqlSession.execute(boundUpdate);

            return fieldData;

        } catch (Exception e) {
            throw new RuntimeException("Error updating field: " + e.getMessage(), e);
        }
    }

    // Swapped FieldID (Long) for fieldApiName (String) to align with Cassandra Primary Key
    public String deleteModuleField(String moduleApiName, String fieldApiName) {
        try {
            // No ALTER TABLE DROP COLUMN needed. Just remove the metadata.
            BoundStatement boundDelete = psDeleteField.bind(moduleApiName, fieldApiName);
            cqlSession.execute(boundDelete);

            return "success";

        } catch (Exception e) {
            throw new RuntimeException("Error deleting field: " + e.getMessage(), e);
        }
    }

    // Swapped FieldID (Long) for fieldApiName (String) to align with Cassandra Primary Key
    public Field getModuleFieldByID(String moduleApiName, String fieldApiName) {
        try {
            BoundStatement boundGet = psGetField.bind(moduleApiName, fieldApiName);
            ResultSet rs = cqlSession.execute(boundGet);

            Row row = rs.one();
            if (row != null) {
                return mapRow(row);
            }
            throw new IllegalArgumentException("Field not found: " + fieldApiName);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Field> getModuleField(String moduleApiName) {
        List<Field> fields = new ArrayList<>();
        System.out.println("SUCCESSFULLY ENTERED THE CONSOLE MODULE API NAME [ inside repository layer ] - " + moduleApiName);

        try {
            BoundStatement boundGetAll = psGetAllFields.bind(moduleApiName);
            ResultSet rs = cqlSession.execute(boundGetAll);

            for (Row row : rs) {
                Field field = mapRow(row);
                fields.add(field);
                System.out.println(field);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return fields;
    }
}


//package com.example.demo.Repository;
//import javax.sql.DataSource;
//import com.example.demo.Model.Field;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Repository;
//
//import java.sql.*;
//import java.util.*;
//
//@Repository
//public class FieldRepository {
////    private static final String URL = "jdbc:sqlserver://DESKTOP-8K9QAPT;databaseName=MADHANDatabase;encrypt=true;trustServerCertificate=true;";
////    private static final String USER = "SpringUser";
////    private static final String PASSWORD = "Sandbox@123#";
////    public Connection getConnection() throws ClassNotFoundException, SQLException {
////        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
////
////        try {
////            return DriverManager.getConnection(URL,USER,PASSWORD);
////        } catch (Exception e) {
////            throw new RuntimeException(e);
////        }
////    }
//@Autowired
//private DataSource dataSource;
//    // Delete the hardcoded URL, USER, and PASSWORD variables
//    public Connection getConnection() throws SQLException {
//        // Now it uses your application.properties settings!
//        return dataSource.getConnection();
//    }
//    private Field mapRow(ResultSet rs) throws SQLException {
//        String FieldApiName = rs.getString("FieldApiName");
//        String FieldDisplayName = rs.getString("FieldDisplayName");
//        String FieldDataType = rs.getString("FieldDataType");
//        Long FieldID = rs.getLong("FieldID");
//        return new Field(FieldID,FieldApiName,FieldDisplayName,FieldDataType);
//    }
//    public Map<String,String> createModuleField(String moduleApiName, Map<String,String> fieldData) {
//        String type = fieldData.get("fieldDataType");
//        if (type.equals("String"))
//            type="VARCHAR(20)";
//        String InsertColumn ="ALTER TABLE " + moduleApiName + "records ADD "
//                + fieldData.get("fieldApiName") + " " + type + " NULL";
//        int rs;
//        try (Connection con = getConnection()){
//            con.setAutoCommit(false);
//            String outboxQuery = "INSERT INTO outbox (aggregate, event_type, payload) VALUES (?, ?, ?)";
//            try {
//
//                String InsertQuery = "INSERT INTO " + moduleApiName + " (FieldApiName,FieldDisplayName,FieldDataType) VALUES (?,?,?)";
//                try (PreparedStatement st1 =  con.prepareStatement(outboxQuery)) {
//                    st1.setString(1, moduleApiName);
//                    st1.setString(2, fieldData.get("fieldDisplayName"));
//                    StringBuilder Builder = new StringBuilder();
//                    Builder.append("{\"FieldApiName\":\"").append(fieldData.get("fieldApiName"))
//                            .append("\",\"FieldDisplayName\":\"").append(fieldData.get("FieldDisplayName"))
//                            .append("\",\"FieldDataType\":\"").append(fieldData.get("FieldDataType")).append("\"}");
//                    st1.setString(3, Builder.toString());
//                    if(st1.executeUpdate()<=0){
//                        throw new RuntimeException("Error inserting outbox record");
//                    }
//                }
//                try (PreparedStatement st = con.prepareStatement(InsertQuery)) {
//
//                    st.setString(1, fieldData.get("fieldApiName"));
//                    st.setString(2, fieldData.get("fieldDisplayName"));
//                    st.setString(3, fieldData.get("fieldDataType"));
//                    rs = st.executeUpdate();
//                    if (rs > 0) {
//                        try (Statement st2 = con.createStatement()) {
//                            if( st2.executeUpdate(InsertColumn) !=0) {
//                                con.commit();
//                                Map<String, String> map = new HashMap<>();
//                                map.put("Code","201");
//                                map.put("Message","Success");
//                                return map;
//                            }
//                            else{
//                                throw new RuntimeException("Error on field creation");
//                            }
//                        }
//                    } else {
//                        throw new RuntimeException("Error on field creation");
//                    }
//                }
//            }
//            catch (Exception e) {
//                con.rollback();
//                throw new RuntimeException(e);
//            }
//            finally {
//                con.setAutoCommit(true);
//            }
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
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
//                st.setString(1, fieldData.getFieldApiName());
//                st.setString(2, fieldData.getFieldDisplayName());
//                st.setString(3, fieldData.getFieldDataType());
//                st.setLong(4, fieldData.getFieldID());
//                rs = st.executeUpdate();
//                if (rs > 0) {
//                    try(Statement st2 = con.createStatement()) {
//                        rs = st2.executeUpdate(InsertColumn);
//                        con.commit();
//                        return fieldData;
//                    }
//                }
//
//                con.rollback();
//                return null;
//
//            }
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public String deleteModuleField(String moduleApiName, Long fieldID) {
//        int rs1,rs2;
//        String DeleteQuery = "DELETE FROM "+moduleApiName+" WHERE FieldID = ?";
//        Field field = getModuleFieldByID(fieldID,moduleApiName);
//        String DeleteColumn = "ALTER TABLE "+moduleApiName+"records DROP COLUMN "+field.getFieldApiName();
//        try (Connection con = getConnection()){
//            con.setAutoCommit(false);
//            try {
//                int rowsDeletedDML;
//                try (PreparedStatement st = con.prepareStatement(DeleteQuery)){
//                    st.setLong(1,fieldID);
//                    rowsDeletedDML = st.executeUpdate();
//                }
//                if(rowsDeletedDML ==0 ){
//                    con.rollback();
//                    return null;
//                }
//                try (Statement st2 = con.createStatement()){
//                    st2.executeUpdate(DeleteColumn);
//                }
//                con.commit();
//                return "success";
//            }
//            catch (Exception e) {
//                con.rollback();
//                throw new RuntimeException(e);
//            }
//
//        } catch (Exception e) {
//
//            throw new RuntimeException(e);
//        }
//
//    }
//    public Field getModuleFieldByID(long FieldID,String ModuleApiName){
//        String Query = "SELECT * FROM "+ModuleApiName+" WHERE FieldId = ?"+FieldID;
//        try (Connection con = getConnection()){
//            Statement st = con.createStatement();
//            ResultSet rs = st.executeQuery(Query);
//            if (rs.next()) {
//                return mapRow(rs);
//            }
//            throw new IllegalArgumentException("Field not found: " + FieldID);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//    public List<Field> getModuleField(String ModuleApiName) {
//        List<Field> arr = new ArrayList<>();
//        System.out.println("SUCCESS FULLY ENTERED THE CONSOLE MODULE API NAME [ inside repository layer ] - "+ModuleApiName);
//        String Query = "SELECT * FROM "+ModuleApiName;
//        try (Connection con = getConnection()){
//            Statement st = con.createStatement();
//            ResultSet rs = st.executeQuery(Query);
//            while (rs.next()){
//                arr.add(mapRow(rs));
//                System.out.println(mapRow(rs));
//            }
//
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//        return arr;
//    }
//
//}
//
//
