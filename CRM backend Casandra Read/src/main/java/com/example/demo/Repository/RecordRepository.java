package com.example.demo.Repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
}
