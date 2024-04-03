package com.example.cs5031p3.demo.backend.dao;

import com.example.cs5031p3.demo.backend.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import com.example.cs5031p3.demo.backend.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * The UserDAOTest class contains unit tests for the UserDAO class.
 */
@ExtendWith(MockitoExtension.class)
public class UserDAOTest {

    @Mock
    private DatabaseManager databaseManager;

    @InjectMocks
    private UserDAO userDAO;

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    /**
     * Tests the createUser() method of UserDAO.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void createUserTest() throws Exception {
        // Arrange
        User user = new User(1,"Harry Potter", "harry", "wicked", 1);
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        when(databaseManager.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString(), anyInt())).thenReturn(mockStatement);
        when(mockStatement.getGeneratedKeys()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(1); // Simulate generated user ID

        // Act
        userDAO.createUser(user);

        // Assert
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockConnection).prepareStatement(sqlCaptor.capture(), eq(Statement.RETURN_GENERATED_KEYS));

        assertTrue(sqlCaptor.getValue().contains("INSERT INTO users"));
        verify(mockStatement, times(1)).executeUpdate();
        verify(mockResultSet, times(1)).next();
        assertEquals(1, user.getId()); // Verify the user ID is set correctly after creation
    }

    /**
     * Tests the getUserById() method of UserDAO.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void getUserByIdTest() throws Exception {
        // Arrange
        int userId = 1; // Example user ID
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        when(databaseManager.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true); // Simulate finding the user
        // Simulate the user data returned from the database
        when(mockResultSet.getInt("user_id")).thenReturn(userId);
        when(mockResultSet.getString("name")).thenReturn("John Doe");
        when(mockResultSet.getString("username")).thenReturn("johndoe");
        when(mockResultSet.getString("password")).thenReturn("password123");
        when(mockResultSet.getInt("type_id")).thenReturn(2);

        // Act
        User result = userDAO.getUserById(userId);

        // Assert
        assertNotNull(result, "The result should not be null.");
        assertEquals(userId, result.getId(), "The user ID should match.");
        assertEquals("John Doe", result.getName(), "The names should match.");
        assertEquals("johndoe", result.getUsername(), "The usernames should match.");
        assertEquals("password123", result.getPassword(), "The passwords should match.");
        assertEquals(2, result.getTypeId(), "The type IDs should match.");

        // Verify the prepared statement was created with the correct SQL query
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockConnection).prepareStatement(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("SELECT * FROM users WHERE user_id = ?"), "The SQL query should select a user by ID.");

        // Verify the prepared statement was set with the correct ID
        ArgumentCaptor<Integer> idCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(mockPreparedStatement).setInt(eq(1), idCaptor.capture());
        assertEquals(userId, idCaptor.getValue(), "The prepared statement should be set with the correct user ID.");
    }

    /**
     * Tests the deleteUser() method of UserDAO.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void deleteUserTest() throws Exception {
        // Arrange
        int userIdToDelete = 1; // Example user ID to delete
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);

        when(databaseManager.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        // Act
        userDAO.deleteUser(userIdToDelete);

        // Assert
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockConnection).prepareStatement(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("DELETE FROM users WHERE user_id = ?"), "The SQL query should delete a user by ID.");

        // Verify the prepared statement was set with the correct user ID
        ArgumentCaptor<Integer> idCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(mockPreparedStatement).setInt(eq(1), idCaptor.capture());
        assertEquals(userIdToDelete, idCaptor.getValue().intValue(), "The prepared statement should be set with the correct user ID to delete.");

        // Verify that the update is executed
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    /**
     * Tests the getAllUsers() method of UserDAO.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void getAllUsersTest() throws Exception {
        // Arrange
        when(databaseManager.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        // Simulate two users in the result set
        when(mockResultSet.next()).thenReturn(true, true, false); // End after two users
        // Simulate user data for the first user
        when(mockResultSet.getInt("user_id")).thenReturn(1, 2); // Two user IDs
        when(mockResultSet.getString("name")).thenReturn("John Doe", "Jane Doe");
        when(mockResultSet.getString("username")).thenReturn("johndoe", "janedoe");
        when(mockResultSet.getString("password")).thenReturn("password1", "password2");
        when(mockResultSet.getInt("type_id")).thenReturn(1, 2); // Two type IDs

        // Act
        List<User> result = userDAO.getAllUsers();

        // Assert
        assertNotNull(result, "The result list should not be null.");
        assertEquals(2, result.size(), "The result list should contain two users.");

        // Verify first user
        User firstUser = result.get(0);
        assertEquals(1, firstUser.getId(), "The first user's ID should be 1.");
        assertEquals("John Doe", firstUser.getName(), "The first user's name should match.");
        assertEquals("johndoe", firstUser.getUsername(), "The first user's username should match.");
        assertEquals("password1", firstUser.getPassword(), "The first user's password should match.");
        assertEquals(1, firstUser.getTypeId(), "The first user's type ID should match.");

        // Verify second user
        User secondUser = result.get(1);
        assertEquals(2, secondUser.getId(), "The second user's ID should be 2.");
        assertEquals("Jane Doe", secondUser.getName(), "The second user's name should match.");
        assertEquals("janedoe", secondUser.getUsername(), "The second user's username should match.");
        assertEquals("password2", secondUser.getPassword(), "The second user's password should match.");
        assertEquals(2, secondUser.getTypeId(), "The second user's type ID should match.");

        // Verify the SQL query
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockConnection).prepareStatement(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("SELECT * FROM users"), "The SQL query should select all users.");
    }

    /**
     * Tests the updateUser() method of UserDAO.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void updateUserTest() throws Exception {
        // Arrange
        User userToUpdate = new User(1, "Updated Name", "UpdatedUsername", "UpdatedPassword", 2);
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);

        when(databaseManager.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        // Act
        userDAO.updateUser(userToUpdate);

        // Assert
        // Verify that the SQL statement is prepared correctly
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockConnection).prepareStatement(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("UPDATE users SET name = ?, username = ?, password = ?, type_id = ? WHERE user_id = ?"),
                "The SQL query should update a user's details.");

        // Verify that the parameters are set correctly on the PreparedStatement
        verify(mockPreparedStatement).setString(1, userToUpdate.getName());
        verify(mockPreparedStatement).setString(2, userToUpdate.getUsername());
        verify(mockPreparedStatement).setString(3, userToUpdate.getPassword());
        verify(mockPreparedStatement).setInt(4, userToUpdate.getTypeId());
        verify(mockPreparedStatement).setInt(5, userToUpdate.getId());

        // Verify that the update is executed
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    /**
     * Tests the getUserByUsername() method of UserDAO.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void getUserByUsernameTest() throws Exception {
        // Arrange
        String username = "johndoe";
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        when(databaseManager.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true); // Simulate finding the user
        // Simulate the user data returned from the database
        when(mockResultSet.getInt("user_id")).thenReturn(1);
        when(mockResultSet.getString("name")).thenReturn("John Doe");
        when(mockResultSet.getString("username")).thenReturn(username);
        when(mockResultSet.getString("password")).thenReturn("password123");
        when(mockResultSet.getInt("type_id")).thenReturn(2);

        // Act
        User result = userDAO.getUserByUsername(username);

        // Assert
        assertNotNull(result, "The result should not be null.");
        assertEquals(1, result.getId(), "The user ID should match.");
        assertEquals("John Doe", result.getName(), "The names should match.");
        assertEquals(username, result.getUsername(), "The usernames should match.");
        assertEquals("password123", result.getPassword(), "The passwords should match.");
        assertEquals(2, result.getTypeId(), "The type IDs should match.");

        // Verify the prepared statement was created with the correct SQL query
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockConnection).prepareStatement(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("SELECT * FROM users WHERE username = ?"), "The SQL query should select a user by username.");

        // Verify the prepared statement was set with the correct username
        ArgumentCaptor<String> usernameCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockPreparedStatement).setString(eq(1), usernameCaptor.capture());
        assertEquals(username, usernameCaptor.getValue(), "The prepared statement should be set with the correct username.");
    }

    /**
     * Tests the getUserById() method of UserDAO when the user is not found.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void getUserByIdNotFoundTest() throws Exception {
        // Arrange
        int userId = 1;
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        when(databaseManager.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false); // Simulate not finding the user

        // Act
        User result = userDAO.getUserById(userId);

        // Assert
        assertNull(result, "The result should be null when the user is not found.");
    }

    /**
     * Tests the getUserByUsername() method of UserDAO when the user is not found.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void getUserByUsernameNotFoundTest() throws Exception {
        // Arrange
        String username = "nonexistentuser";
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        when(databaseManager.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false); // Simulate not finding the user

        // Act
        User result = userDAO.getUserByUsername(username);

        // Assert
        assertNull(result, "The result should be null when the user is not found.");
    }

    /**
     * Tests the updatePassword() method of UserDAO.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void updatePasswordTest() throws Exception {
        // Arrange
        User userToUpdate = new User(1, "John Doe", "johndoe", "NewPassword", 1);
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);

        when(databaseManager.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);

        // Act
        userDAO.updatePassword(userToUpdate);

        // Assert
        // Verify that the SQL statement is prepared correctly
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockConnection).prepareStatement(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("UPDATE users SET password = ? WHERE user_id = ?"),
                "The SQL query should update the user's password.");

        // Verify that the parameters are set correctly on the PreparedStatement
        verify(mockPreparedStatement).setString(1, userToUpdate.getPassword());
        verify(mockPreparedStatement).setInt(2, userToUpdate.getId());

        // Verify that the update is executed
        verify(mockPreparedStatement, times(1)).executeUpdate();
    }

    /**
     * Tests the verifyPassword() method of UserDAO when the password is correct.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void verifyPasswordTest() throws Exception {
        // Arrange
        User userToVerify = new User(1, "John Doe", "johndoe", "password123", 1);
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        when(databaseManager.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(1); // Simulate password match

        // Act
        boolean result = userDAO.verifyPassword(userToVerify);

        // Assert
        assertTrue(result, "The password should be verified successfully.");

        // Verify that the SQL statement is prepared correctly
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockConnection).prepareStatement(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("SELECT COUNT(*) FROM users WHERE user_id = ? AND password = ?"),
                "The SQL query should verify the user's password.");

        // Verify that the parameters are set correctly on the PreparedStatement
        verify(mockPreparedStatement).setInt(1, userToVerify.getId());
        verify(mockPreparedStatement).setString(2, userToVerify.getPassword());
    }

    /**
     * Tests the verifyPassword() method of UserDAO when the password is incorrect.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void verifyPasswordNotFoundTest() throws Exception {
        // Arrange
        User userToVerify = new User(1, "John Doe", "johndoe", "wrongpassword", 1);
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        when(databaseManager.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false); // Simulate no records in the result set

        // Act
        boolean result = userDAO.verifyPassword(userToVerify);

        // Assert
        assertFalse(result, "The password should not be verified when no matching record is found.");

        // Verify that the SQL statement is prepared correctly
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockConnection).prepareStatement(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("SELECT COUNT(*) FROM users WHERE user_id = ? AND password = ?"),
                "The SQL query should verify the user's password.");

        // Verify that the parameters are set correctly on the PreparedStatement
        verify(mockPreparedStatement).setInt(1, userToVerify.getId());
        verify(mockPreparedStatement).setString(2, userToVerify.getPassword());
    }

    /**
     * Tests the getUserIdByUsername() method of UserDAO when the user is found.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void getUserIdByUsernameTest() throws Exception {
        // Arrange
        String username = "johndoe";
        int expectedUserId = 1;
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        when(databaseManager.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true); // Simulate finding the user
        when(mockResultSet.getInt("user_id")).thenReturn(expectedUserId);

        // Act
        int result = userDAO.getUserIdByUsername(username);

        // Assert
        assertEquals(expectedUserId, result, "The user ID should match the expected value.");

        // Verify the prepared statement was created with the correct SQL query
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockConnection).prepareStatement(sqlCaptor.capture());
        assertTrue(sqlCaptor.getValue().contains("SELECT user_id FROM users WHERE username = ?"),
                "The SQL query should select the user ID by username.");

        // Verify the prepared statement was set with the correct username
        ArgumentCaptor<String> usernameCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockPreparedStatement).setString(eq(1), usernameCaptor.capture());
        assertEquals(username, usernameCaptor.getValue(), "The prepared statement should be set with the correct username.");
    }

    /**
     * Tests the getUserIdByUsername() method of UserDAO when the user is not found.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    public void getUserIdByUsernameNotFoundTest() throws Exception {
        // Arrange
        String username = "nonexistentuser";
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        when(databaseManager.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false); // Simulate not finding the user

        // Act
        int result = userDAO.getUserIdByUsername(username);

        // Assert
        assertEquals(-1, result, "The result should be -1 when the user is not found.");
    }
}
