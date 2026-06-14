package com.users.administration.Repository;

import com.users.administration.Model.User;
import org.springframework.stereotype.Repository;

import javax.sql.rowset.RowSetWarning;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


@Repository
public class UserRepository {

    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=MADHANDatabase;encrypt=true;trustServerCertificate=true;";

    private static final String USER = "SpringUser";

    private static final String PASSWORD = "Sandbox@123#";

    private Connection getConnection(){
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            return DriverManager.getConnection(URL,USER,PASSWORD);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private User UserMapRow(ResultSet rs) throws SQLException {
        long userID = rs.getLong("userID");
        String UserName = rs.getString("UserName");
        String role = rs.getString("role");
        String UserEmail = rs.getString("UserEmail");
        return new User(UserName,userID,role,UserEmail);
    }

    public List<User> getUsers() throws SQLException {
        String selectQuery = "SELECT * FROM User";
        List<User> users = new ArrayList<>();
        try(Connection con = getConnection()){
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(selectQuery);
            while (rs.next()){
                users.add(UserMapRow(rs));
            }
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
        return users;
    }

    public User getUserByID(Long userID) throws SQLException {
        User user = null;
        try (Connection con = getConnection()){
            String query = "SELECT * FROM User WHERE UserID";
            try (PreparedStatement pt = con.prepareStatement(query)){
                pt.setLong(1,userID);
                ResultSet rs = pt.executeQuery();
                if (rs.next()){
                    user = UserMapRow(rs);
                }
            }
        }
        return user;
    }


    public User createUser(User userpayload) throws SQLException {
        String insertQuery = "INSERT User (UserName,Role,UserEmail) VALUES (?,?,?)";
        try (Connection con = getConnection()){
            con.setAutoCommit(false);
            try (PreparedStatement pst = con.prepareStatement(insertQuery,Statement.RETURN_GENERATED_KEYS)){
                pst.setString(1,userpayload.getUserName());
                pst.setString(2,userpayload.getRole());
                pst.setString(3,userpayload.getUserEmail());
                if (pst.executeUpdate()>0){
                    try (ResultSet rs = pst.getGeneratedKeys()) {
                        if (rs.next()){
                            long generatedID = rs.getLong(1);
                            userpayload.setUserID(generatedID);
                        }
                    }
                }
            }
        }
        return userpayload;
    }


    public User deleteUser(Long userID) throws SQLException {
        try (Connection con = getConnection()){
            con.setAutoCommit(false);
            String DeleteQuery = "DELETE FROM User WHERE UserID = ?";
            try (PreparedStatement stMod = con.prepareStatement(DeleteQuery)){
                stMod.setLong(1,userID);
                int rs = stMod.executeUpdate();
                if (rs != 0){
                    System.out.println("deletion not possible");
                }
            }
            catch (Exception e){
                throw new RuntimeException(e);
            }
        }
        catch (Exception e){
            throw new RuntimeException(e);        }
        return null;

    }
}
