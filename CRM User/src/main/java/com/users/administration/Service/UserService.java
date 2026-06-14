package com.users.administration.Service;

import com.users.administration.Model.User;
import com.users.administration.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;



@Service
public class UserService {
    @Autowired
    UserRepository user;
    public List<User> getUsers() throws SQLException {
        return user.getUsers();
    }

    public User getUserByID(Long userID) throws SQLException {
        return user.getUserByID(userID);
    }

    public User createUser(User userpayload) throws SQLException {
        return user.createUser(userpayload);
    }

    public User deleteUser(Long userID) throws SQLException {
        return user.deleteUser(userID);
    }
}
