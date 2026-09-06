package com.example.demo.Repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.demo.Model.Field;

@Repository
public class RecordRepository {
//
//    private static final String URL = "jdbc:sqlserver://DESKTOP-8K9QAPT;databaseName=MADHANDatabase;encrypt=true;trustServerCertificate=true;";
//    private static final String USER = "SpringUser";
//
//    private static final String PASSWORD = "Sandbox@123#";
//
//    // 1. Let Spring inject the configured JdbcTemplate
//    private Connection getConnection(){
//        try {
//            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//            return DriverManager.getConnection(URL,USER,PASSWORD);
//        }catch (Exception e){
//            throw new RuntimeException("failed");
//        }
//    }

    @Autowired
    private FieldRepository fieldRepository;


    @Autowired
    private DataSource dataSource;

    // Delete the hardcoded URL, USER, and PASSWORD variables

    public Connection getConnection() throws SQLException {
        // Now it uses your application.properties settings!
        return dataSource.getConnection();
    }
    public List<Map<String, Object>> getModuleRecords(String ModuleApiName) throws SQLException {
        if (!ModuleApiName.matches("^[a-zA-Z0-9_]{1,50}$"))
            throw new IllegalArgumentException("Invalid module API name");
        List<Map<String, Object>> records = new ArrayList<>();
        String RecordQuery = "SELECT * FROM "+ModuleApiName+"records";
        try(Connection con = getConnection()){
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(RecordQuery);
            ResultSetMetaData RsMeta = rs.getMetaData();
            int columnCount = RsMeta.getColumnCount();
            while (rs.next()){
                Map<String, Object> record = new HashMap<>();
                for (int i = 1;i<=columnCount;i++){
                    String columnName = RsMeta.getColumnLabel(i);
                    Object value = rs.getObject(i);
                    record.put(columnName,value);
                }
                records.add(record);
            }
        }
        return records;
    }

    public void CreateRecord(String moduleApi, Map<String, String> data) throws SQLException {
        if (data == null || data.isEmpty()) return;

        if (!moduleApi.matches("^[a-zA-Z0-9_]{1,50}$"))
            throw new IllegalArgumentException("Invalid module API name");

        List<String> validColumns = fieldRepository.getModuleField(moduleApi)
                .stream()
                .map(Field::getFieldApiName)
                .collect(Collectors.toList());

        for (String key : data.keySet()) {
            if (!validColumns.contains(key))
                throw new IllegalArgumentException("Unknown field: " + key);
        }

        StringBuilder columns = new StringBuilder();
        StringBuilder placeHolders = new StringBuilder();
        List<Object> values = new ArrayList<>();

        for (String col : validColumns) {
            if (data.containsKey(col)) {
                columns.append(col).append(",");
                placeHolders.append("?,");
                values.add(data.get(col));
            }
        }

        // Bug Fix: Prevent exception if no values matched
        if (values.isEmpty()) return;

        columns.setLength(columns.length() - 1);
        placeHolders.setLength(placeHolders.length() - 1);

        String Query = "INSERT INTO " + moduleApi + "records (" + columns + ") VALUES (" + placeHolders + ")";
        String outboxQuery = "INSERT INTO outbox (aggregate, event_type, payload) VALUES (?, ?, ?)";

        try (Connection con = getConnection()) {
            con.setAutoCommit(false);
            try {
                long generatedID = -1;

                // 1. Insert the Record
                try (PreparedStatement pst = con.prepareStatement(Query, Statement.RETURN_GENERATED_KEYS)) {
                    for (int i = 0; i < values.size(); i++) {
                        pst.setObject(i + 1, values.get(i));
                    }

                    if (pst.executeUpdate() > 0) {
                        try (ResultSet rs = pst.getGeneratedKeys()) {
                            if (rs.next()) {
                                generatedID = rs.getLong(1);
                            }
                        }
                    } else {
                        throw new RuntimeException("Failed to create record in database.");
                    }
                }

                // 2. Insert the Outbox Event (CDC Pipeline)
                try (PreparedStatement pstOutbox = con.prepareStatement(outboxQuery)) {
                    pstOutbox.setString(1, moduleApi);
                    pstOutbox.setString(2, "RECORD_CREATE");

                    // Prepare the payload by injecting the newly generated ID into the data map
                    Map<String, Object> payloadMap = new HashMap<>(data);
                    payloadMap.put("RecordID", generatedID);

                    // Bug Fix: Use Jackson ObjectMapper to safely serialize arbitrary user input to JSON
                    ObjectMapper mapper = new ObjectMapper();
                    String jsonPayload = mapper.writeValueAsString(payloadMap);

                    pstOutbox.setString(3, jsonPayload);

                    if (pstOutbox.executeUpdate() <= 0) {
                        throw new RuntimeException("Failed to insert outbox event.");
                    }
                }

                // 3. Commit the transaction
                con.commit();
                System.out.println("Successfully created record with ID: " + generatedID);

            } catch (Exception e) {
                con.rollback();
                throw new RuntimeException(e);
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    public Map<String,String> GetRecordByID(String moduleApiName, String recordID) throws SQLException {
        if (!moduleApiName.matches("^[a-zA-Z0-9_]{1,50}$"))
            throw new IllegalArgumentException("Invalid module API name");
        String Query = "SELECT * FROM "+moduleApiName+"records WHERE RecordID = ?";
        Map<String,String> data = new HashMap<>();
        List<Field> array;
        try (Connection con = getConnection()) {
            PreparedStatement pst = con.prepareStatement(Query);
            pst.setString(1,recordID);
            ResultSet rs = pst.executeQuery();
            array = fieldRepository.getModuleField(moduleApiName);
            rs.next();
            for (Field field :array){

                String  value =String.valueOf( rs.getObject(field.getFieldApiName()));
                field.setFieldValue(value);
                data.put(field.getFieldApiName(),field.getFieldValue());
            }
        }
        return data;
    }

    public Map<String,String> UpdateRecord(String moduleApiName, String recordID, Map<String,String> record) throws SQLException {
        if (record == null || record.isEmpty()) {
            return record;
        }
        // 1. Security Validation
        if (!moduleApiName.matches("^[a-zA-Z0-9_]{1,50}$")) {
            throw new IllegalArgumentException("Invalid module API name");
        }

        // Validate Columns
        List<String> validColumns = fieldRepository.getModuleField(moduleApiName)
                .stream()
                .map(Field::getFieldApiName)
                .collect(Collectors.toList());

        for (String key : record.keySet()) {
            if (!validColumns.contains(key)) {
                throw new IllegalArgumentException("Unknown field: " + key);
            }
        }

        // 2. Efficient Query Construction
        StringBuilder queryBuilder = new StringBuilder("UPDATE ").append(moduleApiName).append("records SET ");
        List<String> values = new ArrayList<>();

        for (Map.Entry<String, String> entry : record.entrySet()) {
            queryBuilder.append(entry.getKey()).append("=?,");
            values.add(entry.getValue());
        }

        // Remove the trailing comma and append the WHERE clause
        queryBuilder.setLength(queryBuilder.length() - 1);
        queryBuilder.append(" WHERE RecordID = ?");

        String updateQuery = queryBuilder.toString();
        String outboxQuery = "INSERT INTO outbox (aggregate, event_type, payload) VALUES (?, ?, ?)";

        try (Connection con = getConnection()) {
            // Start Transaction immediately
            con.setAutoCommit(false);

            try {
                // 3. Update the Physical Record
                try (PreparedStatement pst = con.prepareStatement(updateQuery)) {
                    int i = 1;
                    for (String val : values) {
                        pst.setString(i++, val);
                    }
                    pst.setString(i, recordID);

                    int rowsAffected = pst.executeUpdate();
                    if (rowsAffected == 0) {
                        throw new RuntimeException("Record ID " + recordID + " not found.");
                    }
                }

                // 4. Insert Outbox Event (CDC Pipeline)
                try (PreparedStatement pstOutbox = con.prepareStatement(outboxQuery)) {
                    pstOutbox.setString(1, moduleApiName);
                    pstOutbox.setString(2, "RECORD_UPDATE");

                    // Construct the JSON payload containing the ID and all updated fields
                    StringBuilder payload = new StringBuilder();
                    payload.append("{\"RecordID\":\"").append(recordID).append("\"");
                    for (Map.Entry<String, String> entry : record.entrySet()) {
                        payload.append(",\"").append(entry.getKey()).append("\":\"").append(entry.getValue()).append("\"");
                    }
                    payload.append("}");

                    pstOutbox.setString(3, payload.toString());

                    if (pstOutbox.executeUpdate() <= 0) {
                        throw new RuntimeException("Failed to insert outbox event.");
                    }
                }

                // Commit only if both succeed
                con.commit();
                return record;

            } catch (Exception e) {
                con.rollback();
                throw new RuntimeException(e);
            } finally {
                // BUG FIX: Always restore auto-commit state before releasing the connection
                con.setAutoCommit(true);
            }
        }
    }}
