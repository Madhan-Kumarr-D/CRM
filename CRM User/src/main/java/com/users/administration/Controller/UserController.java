package com.users.administration.Controller;

import com.users.administration.Model.User;
import com.users.administration.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/v1")
public class UserController {
    @Autowired
    UserService user;
    @GetMapping("/users")
    public List<User> getUsers() throws SQLException{
        return user.getUsers();
    }
    @GetMapping("/users/{UserID}")
    public User getUserByID(@PathVariable Long UserID) throws SQLException {
        return user.getUserByID(UserID);
    }
    @PostMapping("/users")
    public User createUser(@RequestBody User userpayload) throws SQLException {
        System.out.println(userpayload);
        return user.createUser(userpayload);
    }

    @DeleteMapping("/users/{UserID}")
    public User deleteUser(@PathVariable Long UserID) throws SQLException {
        return user.deleteUser(UserID);
    }
}
