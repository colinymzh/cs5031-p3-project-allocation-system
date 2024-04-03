package com.example.cs5031p3.demo.backend.controller;

import com.example.cs5031p3.demo.backend.bean.ResponseMessage;
import com.example.cs5031p3.demo.backend.dto.ResponseDTO;
import com.example.cs5031p3.demo.backend.model.User;
import com.example.cs5031p3.demo.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

/**
 * Controller class for handling HTTP requests related to user operations.
 */
@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Endpoint for creating a new user.
     *
     * @param user The user object to create
     * @return ResponseEntity indicating success or failure of the operation
     */
    @PostMapping("/")
    public ResponseEntity<String> createUser(@RequestBody User user) {
        try
        {
            userService.createUser(user);
            return ResponseEntity.ok("The new user is created");
        }catch (SQLException e)
        {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Endpoint for retrieving a user by ID.
     *
     * @param id The ID of the user to retrieve
     * @return ResponseEntity containing the user object if found, or an error message if not
     */
    @GetMapping("/id/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable int id) throws SQLException {
        try {
            User returnedUser=userService.getUserById(id);
            if(returnedUser==null) return ResponseEntity.badRequest().body("No such user");
            return ResponseEntity.ok(returnedUser);
        }catch (Exception e)
        {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    /**
     * Endpoint for retrieving a user by username.
     *
     * @param username The username of the user to retrieve
     * @return ResponseEntity containing the user object if found, or an error message if not
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<Object> getUserbyUsername(@PathVariable String username){
        try {
            User returnedUser=userService.getUserByUsername(username);
            if(returnedUser==null) return ResponseEntity.badRequest().body("No such user");
            return ResponseEntity.ok(returnedUser);
        }catch (Exception e)
        {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    /**
     * Endpoint for updating a user.
     *
     * @param user The user object with updated information
     * @return ResponseEntity indicating success or failure of the operation
     */
    @PutMapping("/")
    public ResponseEntity<?> updateUser(@RequestBody User user) {
        try {
            userService.updateUser(user);
            return ResponseEntity.ok().body("User updated successfully.");
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body("Error updating user.");
        }
    }

    /**
     * Endpoint for deleting a user by ID.
     *
     * @param id The ID of the user to delete
     * @return ResponseEntity indicating success or failure of the operation
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable int id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok().body("User deleted successfully.");
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body("Error deleting user.");
        }
    }

    /**
     * Endpoint for retrieving all users.
     *
     * @return ResponseEntity containing a list of all users
     */
    @GetMapping("/all")
    public ResponseEntity<Object> getAllUsers() {
        try {
            return ResponseEntity.ok(userService.getAllUsers());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    /**
     * Endpoint for updating a user's password.
     *
     * @param user The user object with updated password
     * @return ResponseEntity indicating success or failure of the operation
     * @throws SQLException if an SQL exception occurs
     */
    @PutMapping("/password")
    public ResponseEntity<String> updatePassword(@RequestBody User user) throws SQLException {
        try
        {
            userService.updatePassword(user);
            return ResponseEntity.ok("Password updated");
        }catch (SQLException e)
        {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    /**
     * Endpoint for user login.
     *
     * @param user The user object containing login credentials
     * @return ResponseDTO containing the logged-in user if successful, or an error message if not
     */
    @PostMapping("/login")
    public ResponseDTO<User> loginUser(@RequestBody User user) {
        try {
            User loggedInUser = userService.loginUser(user.getUsername(), user.getPassword(), user.getTypeId());
            if (loggedInUser != null) {
                return ResponseDTO.success(loggedInUser);
            } else {
                return ResponseDTO.errorByMsg(ResponseMessage.LOGIN_FAILED);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return ResponseDTO.errorByMsg(ResponseMessage.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint for verifying a user's password.
     *
     * @param user The user object containing the password to verify
     * @return ResponseDTO indicating whether the password is correct or not
     */
    @PostMapping("/verify-password")
    public ResponseDTO<Void> verifyPassword(@RequestBody User user) {
        boolean isPasswordCorrect = userService.verifyPassword(user);
        if (isPasswordCorrect) {
            return ResponseDTO.success(null);
        } else {
            return ResponseDTO.errorByMsg(ResponseMessage.PASSWORD_ERROR);
        }
    }

    /**
     * Endpoint for retrieving the user ID by username.
     *
     * @param username The username of the user
     * @return ResponseEntity containing the user ID if found, or an error message if not
     */
    @GetMapping("/username/{username}/id")
    public ResponseEntity<Object> getUserIdByUsername(@PathVariable String username) {
        try {
            int userId = userService.getUserIdByUsername(username);
            if (userId != -1) {
                return ResponseEntity.ok(userId);
            } else {
                return ResponseEntity.badRequest().body("User not found");
            }
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body("Error retrieving user ID");
        }
    }
}
