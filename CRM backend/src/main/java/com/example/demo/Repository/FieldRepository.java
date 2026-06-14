package com.example.demo.Repository;
import javax.sql.DataSource;
import com.example.demo.Model.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        String Query = "SELECT * FROM "+ModuleApiName+" WHERE FieldId ="+FieldID;
        try (Connection con = getConnection()){
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(Query);
            return mapRow(rs);
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
            throw new IllegalArgumentException("Invalid module API name for security.");
        }
        // Validate new column name: Must be safe for column names
        if (!fieldData.get("fieldApiName").matches("^[a-zA-Z0-9_]{1,20}$")) {
            throw new IllegalArgumentException("Invalid field API name.");
        }
    String InsertQuery = "INSERT INTO "+moduleApiName+" (FieldApiName,FieldDisplayName,FieldDataType) VALUES (?,?,?)";
        String type = fieldData.get("fieldDataType");
        if (type.equals("String"))
            type="VARCHAR(20)";
        String InsertColumn = "ALTER TABLE "+ moduleApiName + "records ADD "+fieldData.get("fieldApiName")+" "+type;
        int rs;
    try (Connection con = getConnection()){
        con.setAutoCommit(false);
        try (PreparedStatement st = con.prepareStatement(InsertQuery)){

            st.setString(1, fieldData.get("fieldApiName"));
            st.setString(2, fieldData.get("fieldDisplayName") );
            st.setString(3, fieldData.get("fieldDataType"));
            System.out.println(InsertColumn);
            rs = st.executeUpdate();
            if (rs>0) {
                try (Statement st2 = con.createStatement();) {
                    rs = st2.executeUpdate(InsertColumn);
                    con.commit();
                    return fieldData;
                }
                catch (Exception e){
                    con.rollback();
                    throw new RuntimeException(e);
                }
            }
            else {
                con.rollback();
                throw new RuntimeException("failure ...");
            }
        }
        catch (Exception e){
            con.rollback();
            throw new RuntimeException(e);
        }


    } catch (Exception e) {
        throw new RuntimeException(e);
    }
    }

    public Field updateModuleField(String moduleApiName, Field fieldData) {
        if (!moduleApiName.matches("^[a-zA-Z0-9_]{1,50}$")) {
            throw new IllegalArgumentException("Invalid module API name for security.");
        }
        // Validate new column name: Must be safe for column names
        if (!fieldData.getFieldApiName().matches("^[a-zA-Z0-9_]{1,20}$")) {
            throw new IllegalArgumentException("Invalid field API name.");
        }
        String UpdateQuery = "UPDATE "+moduleApiName+" SET FieldApiName = ? , FieldDisplayName = ? , FieldDataType = ? WHERE FieldID = ? ";
        String type = fieldData.getFieldDataType();
        if (type.equals("String"))
            type="VARCHAR(20)";
        String InsertColumn = "ALTER TABLE "+ moduleApiName + "records ADD COLUMN "+fieldData.getFieldApiName()+" "+type+" NOT NULL";
        int rs;
        try (Connection con = getConnection()){
            con.setAutoCommit(false);
            PreparedStatement st = con.prepareStatement(UpdateQuery);
            st.setString(1,fieldData.getFieldApiName());
            st.setString(2,fieldData.getFieldDisplayName());
            st.setString(3,fieldData.getFieldDataType());
            st.setLong(4,fieldData.getFieldID());
            rs = st.executeUpdate();
            if (rs>0) {
                Statement st2 = con.createStatement();
                rs = st2.executeUpdate(InsertColumn);
                    con.commit();
                    return fieldData;
            }
            else {
                con.rollback();
                return null;
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String deleteModuleField(String moduleApiName, Long fieldID) {
        int rs1,rs2;
        String DeleteQuery = "DELETE FROM "+moduleApiName+" WHERE FieldID = ?";
        Field field = getModuleFieldByID(fieldID,moduleApiName);
        String DeleteColumn = "ALTER TABLE "+moduleApiName+"records DROP COLUMN "+field.getFieldApiName();
        try (Connection con = getConnection()){
            con.setAutoCommit(false);
            try {
                int rowsDeletedDML;
                try (PreparedStatement st = con.prepareStatement(DeleteQuery)){
                    st.setLong(1,fieldID);
                    rowsDeletedDML = st.executeUpdate();
                }
                if(rowsDeletedDML ==0 ){
                    con.rollback();
                    return null;
                }
                try (Statement st2 = con.createStatement()){
                    st2.executeUpdate(DeleteColumn);
                }
                con.commit();
                return "success";
            }
            catch (Exception e) {
                con.rollback();
                throw new RuntimeException(e);
            }

        } catch (Exception e) {

            throw new RuntimeException(e);
        }

    }
}


