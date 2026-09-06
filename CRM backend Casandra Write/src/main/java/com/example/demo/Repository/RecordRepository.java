package com.example.demo.Repository;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.example.demo.Model.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class RecordRepository {

    @Autowired
    private FieldRepository fieldRepository;

    private final CqlSession cqlSession;

    private final PreparedStatement psInsertRecord;
    private final PreparedStatement psGetRecords;
    private final PreparedStatement psGetRecordById;
    private final PreparedStatement psUpdateRecord;

    public RecordRepository(CqlSession cqlSession) {
        this.cqlSession = cqlSession;

        // Prepare queries once for performance
        this.psInsertRecord = cqlSession.prepare(
                "INSERT INTO module_management.module_records (api_name, record_id, field_values) VALUES (?, ?, ?)"
        );
        this.psGetRecords = cqlSession.prepare(
                "SELECT * FROM module_management.module_records WHERE api_name = ?"
        );
        this.psGetRecordById = cqlSession.prepare(
                "SELECT * FROM module_management.module_records WHERE api_name = ? AND record_id = ?"
        );
        // The '+' operator on a map allows partial updates/inserts of specific keys without overwriting the whole map
        this.psUpdateRecord = cqlSession.prepare(
                "UPDATE module_management.module_records SET field_values = field_values + ? WHERE api_name = ? AND record_id = ?"
        );
    }

    public List<Map<String, Object>> getModuleRecords(String moduleApiName) {
        if (!moduleApiName.matches("^[a-zA-Z0-9_]{1,50}$")) {
            throw new IllegalArgumentException("Invalid module API name");
        }

        List<Map<String, Object>> recordsList = new ArrayList<>();

        try {
            BoundStatement boundGet = psGetRecords.bind(moduleApiName);
            ResultSet rs = cqlSession.execute(boundGet);

            for (Row row : rs) {
                Map<String, Object> recordData = new HashMap<>();
                recordData.put("recordID", row.getUuid("record_id").toString());

                // Extract the dynamic map of fields
                Map<String, String> fieldValues = row.getMap("field_values", String.class, String.class);
                if (fieldValues != null) {
                    recordData.putAll(fieldValues);
                }

                recordsList.add(recordData);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error fetching records: " + e.getMessage(), e);
        }

        return recordsList;
    }

    public void CreateRecord(String moduleApi, Map<String, String> data) {
        if (data == null || data.isEmpty()) return;

        if (!moduleApi.matches("^[a-zA-Z0-9_]{1,50}$")) {
            throw new IllegalArgumentException("Invalid module API name");
        }

        // Validate fields against FieldRepository
        List<String> validColumns = fieldRepository.getModuleField(moduleApi)
                .stream()
                .map(Field::getFieldApiName)
                .collect(Collectors.toList());

        Map<String, String> validDataToInsert = new HashMap<>();

        for (Map.Entry<String, String> entry : data.entrySet()) {
            if (!validColumns.contains(entry.getKey())) {
                throw new IllegalArgumentException("Unknown field: " + entry.getKey());
            }
            validDataToInsert.put(entry.getKey(), entry.getValue());
        }

        try {
            UUID recordId = Uuids.timeBased();

            BoundStatement boundInsert = psInsertRecord.bind(moduleApi, recordId, validDataToInsert);
            cqlSession.execute(boundInsert);

            System.out.println("Successfully created record with ID: " + recordId);
        } catch (Exception e) {
            throw new RuntimeException("Error creating record: " + e.getMessage(), e);
        }
    }

    public Map<String, String> GetRecordByID(String moduleApiName, String recordIDStr) {
        if (!moduleApiName.matches("^[a-zA-Z0-9_]{1,50}$")) {
            throw new IllegalArgumentException("Invalid module API name");
        }

        try {
            UUID recordId = UUID.fromString(recordIDStr);
            BoundStatement boundGetId = psGetRecordById.bind(moduleApiName, recordId);
            ResultSet rs = cqlSession.execute(boundGetId);

            Row row = rs.one();
            Map<String, String> result = new HashMap<>();

            if (row != null) {
                result.put("recordID", recordId.toString());

                Map<String, String> fieldValues = row.getMap("field_values", String.class, String.class);
                if (fieldValues != null) {
                    // Filter the stored map against what is currently valid in the FieldRepository
                    // to match your previous SQL behavior
                    List<String> validColumns = fieldRepository.getModuleField(moduleApiName)
                            .stream()
                            .map(Field::getFieldApiName)
                            .collect(Collectors.toList());

                    for (String col : validColumns) {
                        if (fieldValues.containsKey(col)) {
                            result.put(col, fieldValues.get(col));
                        }
                    }
                }
            }
            return result;

        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid UUID format for Record ID: " + recordIDStr, e);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching record by ID: " + e.getMessage(), e);
        }
    }

    public Map<String, String> UpdateRecord(String moduleApiName, String recordIDStr, Map<String, String> record) {
        if (record == null || record.isEmpty()) {
            return record;
        }

        if (!moduleApiName.matches("^[a-zA-Z0-9_]{1,50}$")) {
            throw new IllegalArgumentException("Invalid module API name");
        }

        // Validate fields against FieldRepository
        List<String> validColumns = fieldRepository.getModuleField(moduleApiName)
                .stream()
                .map(Field::getFieldApiName)
                .collect(Collectors.toList());

        for (String key : record.keySet()) {
            if (!validColumns.contains(key)) {
                throw new IllegalArgumentException("Unknown field: " + key);
            }
        }

        try {
            UUID recordId = UUID.fromString(recordIDStr);

            // Execute the update. The '+' operator in our prepared statement merges the new map entries
            // into the existing map, which perfectly mimics "UPDATE SET field = value"
            BoundStatement boundUpdate = psUpdateRecord.bind(record, moduleApiName, recordId);
            cqlSession.execute(boundUpdate);

            return record;

        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid UUID format for Record ID: " + recordIDStr, e);
        } catch (Exception e) {
            throw new RuntimeException("Error updating record: " + e.getMessage(), e);
        }
    }
}





//package com.example.demo.Repository;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Repository;
//
//import javax.sql.DataSource;
//import java.sql.*;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//import com.example.demo.Model.Field;
//
//@Repository
//public class RecordRepository {
////
////    private static final String URL = "jdbc:sqlserver://DESKTOP-8K9QAPT;databaseName=MADHANDatabase;encrypt=true;trustServerCertificate=true;";
////    private static final String USER = "SpringUser";
////
////    private static final String PASSWORD = "Sandbox@123#";
////
////    // 1. Let Spring inject the configured JdbcTemplate
////    private Connection getConnection(){
////        try {
////            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
////            return DriverManager.getConnection(URL,USER,PASSWORD);
////        }catch (Exception e){
////            throw new RuntimeException("failed");
////        }
////    }
//
//    @Autowired
//    private FieldRepository fieldRepository;
//
//
//    @Autowired
//    private DataSource dataSource;
//
//    // Delete the hardcoded URL, USER, and PASSWORD variables
//
//    public Connection getConnection() throws SQLException {
//        // Now it uses your application.properties settings!
//        return dataSource.getConnection();
//    }
//    public List<Map<String, Object>> getModuleRecords(String ModuleApiName) throws SQLException {
//        if (!ModuleApiName.matches("^[a-zA-Z0-9_]{1,50}$"))
//            throw new IllegalArgumentException("Invalid module API name");
//        List<Map<String, Object>> records = new ArrayList<>();
//        String RecordQuery = "SELECT * FROM "+ModuleApiName+"records";
//        try(Connection con = getConnection()){
//            Statement st = con.createStatement();
//            ResultSet rs = st.executeQuery(RecordQuery);
//            ResultSetMetaData RsMeta = rs.getMetaData();
//            int columnCount = RsMeta.getColumnCount();
//            while (rs.next()){
//                Map<String, Object> record = new HashMap<>();
//                for (int i = 1;i<=columnCount;i++){
//                    String columnName = RsMeta.getColumnLabel(i);
//                    Object value = rs.getObject(i);
//                    record.put(columnName,value);
//                }
//                records.add(record);
//            }
//        }
//        return records;
//    }
//
//    public void CreateRecord(String moduleApi, Map<String, String> data) throws SQLException {
//        if (data == null || data.isEmpty()) return;
//
////        StringBuilder columns = new StringBuilder();
////        StringBuilder placeHolders = new StringBuilder();
////        List<Object> values = new ArrayList<>();
////        for ( Map.Entry<String, String> entry : data.entrySet()){
////
////            columns.append(entry.getKey()).append(",");
////            placeHolders.append("?,"); // Create a '?' for every single value
////            values.add(entry.getValue());
////        }
//        if (!moduleApi.matches("^[a-zA-Z0-9_]{1,50}$"))
//            throw new IllegalArgumentException("Invalid module API name");
//        List<String> validColumns = fieldRepository.getModuleField(moduleApi)
//                .stream()
//                .map(Field::getFieldApiName)
//                .collect(Collectors.toList());
//
//        for (String key : data.keySet()) {
//            if (!validColumns.contains(key))
//                throw new IllegalArgumentException("Unknown field: " + key);
//        }
//
//
//        StringBuilder columns = new StringBuilder();
//        StringBuilder placeHolders = new StringBuilder();
//        List<Object> values = new ArrayList<>();
//        for (String col : validColumns) {
//            if (data.containsKey(col)) {
//                columns.append(col).append(",");
//                placeHolders.append("?,");
//                values.add(data.get(col));
//            }
//        }
//
//        columns.setLength(columns.length() - 1);
//        placeHolders.setLength(placeHolders.length() - 1);
//        String Query = "INSERT INTO "+moduleApi+"records ("+columns+") VALUES ("+placeHolders+")";
//        try(Connection con = getConnection()){
//            PreparedStatement pst = con.prepareStatement(Query,Statement.RETURN_GENERATED_KEYS);
////            pst.setString(1,moduleApi);
//            for (int i = 0; i < values.size(); i++) {
//                pst.setObject(i + 1, values.get(i)); // JDBC index starts at 1
//            }
//            System.out.println(Query);
//            if (pst.executeUpdate() > 0){
//                try (ResultSet rs = pst.getGeneratedKeys()){
//                    if (rs.next()){
//                        long generatedID = rs.getLong(1);
//                        System.out.println("Successfully created record with ID: " + generatedID);
//                    }
//                }
//            }
//        }
//
//    }
//
//
//    public Map<String,String> GetRecordByID(String moduleApiName, String recordID) throws SQLException {
//        if (!moduleApiName.matches("^[a-zA-Z0-9_]{1,50}$"))
//            throw new IllegalArgumentException("Invalid module API name");
//        String Query = "SELECT * FROM "+moduleApiName+"records WHERE RecordID = ?";
//        Map<String,String> data = new HashMap<>();
//        List<Field> array;
//        try (Connection con = getConnection()) {
//            PreparedStatement pst = con.prepareStatement(Query);
//            pst.setString(1,recordID);
//            ResultSet rs = pst.executeQuery();
//            array = fieldRepository.getModuleField(moduleApiName);
//            rs.next();
//            for (Field field :array){
//
//                String  value =String.valueOf( rs.getObject(field.getFieldApiName()));
//                field.setFieldValue(value);
//                data.put(field.getFieldApiName(),field.getFieldValue());
//            }
//        }
//        return data;
//    }
//
//    public Map<String,String> UpdateRecord(String moduleApiName, String recordID, Map<String,String> record) throws SQLException {
//        if (record == null || record.isEmpty()) {
//            return record;
//        }
//        if (!moduleApiName.matches("^[a-zA-Z0-9_]{1,50}$"))
//            throw new IllegalArgumentException("Invalid module API name");
//
//        List<String> validColumns = fieldRepository.getModuleField(moduleApiName)
//                .stream()
//                .map(Field::getFieldApiName)
//                .collect(Collectors.toList());
//        for (String key : record.keySet()) {
//            if (!validColumns.contains(key))
//                throw new IllegalArgumentException("Unknown field: " + key);
//        }
//        String Query = "UPDATE "+moduleApiName+"records SET  ",columns="";
//        List<String> arr = new ArrayList<>();
//        for( String key : record.keySet()){
//            columns+=key+"=?,";
//            arr.add(record.get(key));
//        }
//        columns=columns.substring(0,columns.length()-1);
//        Query=Query+columns+" WHERE RecordID = ?";
//        try (Connection con = getConnection()){
//           try( PreparedStatement pst = con.prepareStatement(Query)) {
//               con.setAutoCommit(false);
//               int i = 1;
//               for (String str : arr) {
//                   pst.setString(i, str);
//                   i++;
//               }
//               pst.setString(i, recordID);
//               int rs = pst.executeUpdate();
//               con.commit();
//           }
//           catch (SQLException e){
//               con.rollback();
//               throw new RuntimeException(e);
//           }
//        }
//        return record;
//    }
//}
