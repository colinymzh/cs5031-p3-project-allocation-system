package com.example.cs5031p3.demo.backend.service;

import com.example.cs5031p3.demo.backend.dao.UserDAO;
import com.example.cs5031p3.demo.backend.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

/**
 * Service class for handling user-related operations.
 */
@Service
public class UserService {
    private final UserDAO userDAO;

    /**
     * Constructs a UserService with the specified UserDAO.
     * @param userDAO The Data Access Object (DAO) for user entities.
     */
    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Retrieves a user by their ID.
     * @param id The ID of the user to retrieve.
     * @return The user with the specified ID.
     * @throws SQLException if a database access error occurs.
     */
    public User getUserById(int id) throws SQLException {
        return userDAO.getUserById(id);
    }

    /**
     * Retrieves a user by their username.
     * @param username The username of the user to retrieve.
     * @return The user with the specified username.
     * @throws SQLException if a database access error occurs.
     */
    public User getUserByUsername(String username) throws SQLException {
        return userDAO.getUserByUsername(username);
    }

    /**
     * Checks if a username already exists.
     * @param username The username to check.
     * @return true if the username exists, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    public boolean isUsernameExisted(String username) throws SQLException
    {
        return (userDAO.getUserByUsername(username)!=null);
    }

    /**
     * Creates a new user.
     * @param user The user to create.
     * @throws SQLException if a database access error occurs or the username already exists.
     */
    public void createUser(User user) throws SQLException {
        if(isUsernameExisted(user.getUsername())) throw new SQLException("The username has already existed");
        else userDAO.createUser(user);
    }

    /**
     * Updates an existing user.
     * @param user The user to update.
     * @throws SQLException if a database access error occurs.
     */
    public void updateUser(User user) throws SQLException {
        userDAO.updateUser(user);
    }

    /**
     * Deletes a user by their ID.
     * @param id The ID of the user to delete.
     * @throws SQLException if a database access error occurs.
     */
    public void deleteUser(int id) throws SQLException {
        userDAO.deleteUser(id);
    }

    /**
     * Retrieves all users.
     * @return A list of all users.
     * @throws SQLException if a database access error occurs.
     */
    public List<User> getAllUsers() throws SQLException {
        return userDAO.getAllUsers();
    }

    /**
     * Updates the password of a user.
     * @param user The user whose password to update.
     * @throws SQLException if a database access error occurs.
     */
    public void updatePassword(User user) throws SQLException {
        userDAO.updatePassword(user);
    }

    /**
     * Authenticates a user's login credentials.
     * @param username The username provided during login.
     * @param password The password provided during login.
     * @param typeId The type ID of the user provided during login.
     * @return The authenticated user if login is successful, null otherwise.
     * @throws SQLException if a database access error occurs.
     */
    public User loginUser(String username, String password, Integer typeId) throws SQLException {
        // get user info with username
        User user = userDAO.getUserByUsername(username);

        // check if user exists , if the password and userType are matched
        if (user != null && user.getPassword().equals(password) && user.getTypeId().equals(typeId)) {
            return user; // return user info to indicate success
        } else {
            return null; // return null for login failure
        }
    }

    /**
     * Verifies the password of a user.
     * @param user The user whose password to verify.
     * @return true if the password is correct, false otherwise.
     */
    public boolean verifyPassword(User user) {
        try {
            return userDAO.verifyPassword(user);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves the user ID by the given username.
     *
     * @param username The username of the user
     * @return The user ID if found, otherwise -1
     * @throws SQLException if a database access error occurs
     */
    public int getUserIdByUsername(String username) throws SQLException {
        return userDAO.getUserIdByUsername(username);
    }
}
