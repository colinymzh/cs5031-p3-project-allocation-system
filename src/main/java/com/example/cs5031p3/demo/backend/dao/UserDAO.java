package com.example.cs5031p3.demo.backend.dao;

import com.example.cs5031p3.demo.backend.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for managing user data in the database.
 */
@Repository
public class UserDAO {
    private final DatabaseManager databaseManager;

    @Autowired
    public UserDAO(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    /**
     * Creates a new user in the database.
     *
     * @param user The user object to be created
     * @throws SQLException if a database access error occurs
     */
    public void createUser(User user) throws SQLException {
        String sql = "INSERT INTO users (name, username, password, type_id) VALUES (?, ?, ?, ?)";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getUsername());
            pstmt.setString(3, user.getPassword());
            pstmt.setInt(4, user.getTypeId());
            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    /**
     * Retrieves a user from the database by its ID.
     *
     * @param userId The ID of the user to retrieve
     * @return The user object if found, otherwise null
     * @throws SQLException if a database access error occurs
     */
    public User getUserById(int userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("user_id"),
                            rs.getString("name"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getInt("type_id"));
                }
            }
        }
        return null;
    }

    /**
     * Retrieves a user from the database by its username.
     *
     * @param username The username of the user to retrieve
     * @return The user object if found, otherwise null
     * @throws SQLException if a database access error occurs
     */
    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("user_id"),
                            rs.getString("name"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getInt("type_id"));
                }
            }
        }
        return null;
    }

    /**
     * Updates an existing user in the database.
     *
     * @param user The user object containing updated information
     * @throws SQLException if a database access error occurs
     */
    public void updateUser(User user) throws SQLException {
        String sql = "UPDATE users SET name = ?, username = ?, password = ?, type_id = ? WHERE user_id = ?";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getUsername());
            pstmt.setString(3, user.getPassword());
            pstmt.setInt(4, user.getTypeId());
            pstmt.setInt(5, user.getId());
            pstmt.executeUpdate();
        }
    }

    /**
     * Deletes a user from the database.
     *
     * @param userId The ID of the user to delete
     * @throws SQLException if a database access error occurs
     */
    public void deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        }
    }

    /**
     * Retrieves all users from the database.
     *
     * @return A list of user objects
     * @throws SQLException if a database access error occurs
     */
    public List<User> getAllUsers() throws SQLException {
        List<User> userList = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User(
                            rs.getInt("user_id"),
                            rs.getString("name"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getInt("type_id"));
                    userList.add(user);
                }
            }
        }
        return userList;
    }

    /**
     * Updates the password of a user in the database.
     *
     * @param user The user object containing the new password and user ID
     * @throws SQLException if a database access error occurs
     */
    public void updatePassword(User user) throws SQLException {
        String sql = "UPDATE users SET password = ? WHERE user_id = ?";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user.getPassword());
            pstmt.setInt(2, user.getId());
            pstmt.executeUpdate();
        }
    }

    /**
     * Verifies the password of a user in the database.
     *
     * @param user The user object containing the user ID and password to verify
     * @return true if the password matches, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean verifyPassword(User user) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE user_id = ? AND password = ?";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, user.getId());
            pstmt.setString(2, user.getPassword());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
        }
        return false;
    }

    /**
     * Retrieves the user ID by the given username.
     *
     * @param username The username of the user
     * @return The user ID if found, otherwise -1
     * @throws SQLException if a database access error occurs
     */
    public int getUserIdByUsername(String username) throws SQLException {
        String sql = "SELECT user_id FROM users WHERE username = ?";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id");
                }
            }
        }
        return -1;
    }
}

