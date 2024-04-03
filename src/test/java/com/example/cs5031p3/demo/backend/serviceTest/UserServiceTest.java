package com.example.cs5031p3.demo.backend.serviceTest;

import com.example.cs5031p3.demo.MockObject;
import com.example.cs5031p3.demo.backend.dao.UserDAO;
import com.example.cs5031p3.demo.backend.model.User;
import com.example.cs5031p3.demo.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the UserService class, covering various user-related operations.
 */
class UserServiceTest {
    @InjectMocks
    UserService userService;

    @Mock
    UserDAO userDAO;

    @BeforeEach
    void setUp()
    {
// ref:https://stackoverflow.com/questions/33076530/the-type-mockitoannotations-mock-is-deprecated
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Test case for retrieving a user by ID when the user does not exist.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    void getUserByIdNotFound() throws Exception{
        int userId=999;
        when(userDAO.getUserById(userId)).thenReturn(null);
        assertNull(userService.getUserById(userId));
    }

    /**
     * Test case for retrieving a user by ID when the user exists.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    void getUserByIdFound() throws Exception{
        int userId=1;
        when(userDAO.getUserById(userId))
                .thenReturn(new User(userId,"name","username","password",2));
        assertNotNull(userService.getUserById(userId));
    }

    /**
     * Test case for retrieving a user by username when the user exists.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    void getUserByUsernameFound() throws Exception{
        String username="mockUsername";
        when(userDAO.getUserByUsername(username))
                .thenReturn(new User(3,"name","username","password",1));
        assertNotNull(userService.getUserByUsername(username));
    }

    /**
     * Test case for retrieving a user by username when the user does not exist.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    void getUserByUsernameNotFound() throws Exception{
        String username="mockUsername404";
        when(userDAO.getUserByUsername(username))
                .thenReturn(null);
        assertNull(userService.getUserByUsername(username));
    }

    /**
     * Test case for creating a new user with a unique username.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    void createNewUser() throws Exception{
        User user=MockObject.mockGeneralUser();
        when(userDAO.getUserByUsername(user.getUsername())).thenReturn(null);
        assertDoesNotThrow(() ->userService.createUser(user));
    }

    /**
     * Test case for creating a user with an existing username.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    void createUserWithExistedUsername() throws Exception{
        User user=MockObject.mockGeneralUser();
        when(userDAO.getUserByUsername(user.getUsername())).thenReturn(user);
        SQLException e=assertThrows(SQLException.class,
                () ->userService.createUser(user));
        assertEquals("The username has already existed",e.getMessage());
    }

    /**
     * Test case for updating a user successfully.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    void updateUserSuccess() throws Exception {
        User user = MockObject.mockGeneralUser();
        doNothing().when(userDAO).updateUser(user);

        assertDoesNotThrow(() -> userService.updateUser(user));
        verify(userDAO, times(1)).updateUser(user);
    }

    /**
     * Test case for failing to update a user due to a database error.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    void updateUserFailure() throws Exception {
        User user = MockObject.mockGeneralUser();
        doThrow(new SQLException("Update failed")).when(userDAO).updateUser(user);

        SQLException exception = assertThrows(SQLException.class, () -> userService.updateUser(user));
        assertEquals("Update failed", exception.getMessage());
        verify(userDAO, times(1)).updateUser(user);
    }

    /**
     * Test case for successfully deleting a user.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    void deleteUserSuccess() throws Exception {
        int userId = 1;
        doNothing().when(userDAO).deleteUser(userId);

        assertDoesNotThrow(() -> userService.deleteUser(userId));
        verify(userDAO, times(1)).deleteUser(userId);
    }

    /**
     * Test case for failing to delete a user due to a database error.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    void deleteUserFailure() throws Exception {
        int userId = 1;
        doThrow(new SQLException("Deletion failed")).when(userDAO).deleteUser(userId);

        SQLException exception = assertThrows(SQLException.class, () -> userService.deleteUser(userId));
        assertEquals("Deletion failed", exception.getMessage());
        verify(userDAO, times(1)).deleteUser(userId);
    }

    /**
     * Test case for retrieving all users successfully.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    void getAllUsers() throws Exception {
        List<User> userList=MockObject.mockUserList();
        when(userDAO.getAllUsers()).thenReturn(userList);
        assertEquals(userList,userService.getAllUsers());
    }

    /**
     * Test case for updating a user password successfully.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    void updatePassword() throws Exception{
        User user=MockObject.mockGeneralUser();
        doNothing().when(userDAO).updatePassword(user);
        assertDoesNotThrow(() -> userService.updatePassword(user));
    }

    /**
     * Test case for failing to update a user password due to a database error.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    void updatePasswordWithException() throws Exception{
        User user=MockObject.mockGeneralUser();
        doThrow(new SQLException()).when(userDAO).updatePassword(user);
        assertThrows(SQLException.class,() -> userService.updatePassword(user));
    }

    /**
     * Test case for successfully logging in a user.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    void loginUserFound() throws Exception{
        User user=MockObject.mockGeneralUser();
        when(userDAO.getUserByUsername(user.getUsername())).thenReturn(user);
        User userReturnedFromService=userService.loginUser(user.getUsername(),user.getPassword(),user.getTypeId());
        assertEquals(user,userReturnedFromService);
        assertEquals(user.getPassword(),userReturnedFromService.getPassword());
        assertEquals(user.getTypeId(),userReturnedFromService.getTypeId());
    }

    /**
     * Test case for attempting to log in a user who does not exist.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    void loginUserNotFound() throws Exception{
        String username = "20260009";
        String password = "password";
        int typeId = 2;
        when(userDAO.getUserByUsername(username)).thenReturn(null);
        assertNull(userService.loginUser(username,password,typeId));
    }

    /**
     * Test case for attempting to log in a user with the wrong password.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    void loginUserWrongPassword() throws Exception{
        String wrongPassword = "wrongPassword";
        User user=MockObject.mockGeneralUser();
        when(userDAO.getUserByUsername(user.getUsername())).thenReturn(user);
        User userReturnedFromService=userService.loginUser(user.getUsername(),wrongPassword,user.getTypeId());
        assertNull(userReturnedFromService);
    }

    /**
     * Test case for attempting to log in a user with the wrong user type.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    void loginUserWrongType() throws Exception{
        User user=MockObject.mockGeneralUser();
        int wrongTypeId = -1;
        when(userDAO.getUserByUsername(user.getUsername())).thenReturn(user);
        User userReturnedFromService=userService.loginUser(user.getUsername(),user.getPassword(),wrongTypeId);
        assertNull(userReturnedFromService);
    }

    /**
     * Test case for verifying a user's password when it is correct.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    void verifyPasswordCorrect() throws Exception {
        User user=MockObject.mockGeneralUser();
        when(userDAO.verifyPassword(user)).thenReturn(true);
        assertTrue(userService.verifyPassword(user));
    }

    /**
     * Test case for verifying a user's password when it is wrong.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    void verifyPasswordWrong() throws Exception{
        User user=MockObject.mockGeneralUser();
        when(userDAO.verifyPassword(user)).thenReturn(false);
        assertFalse(userService.verifyPassword(user));
    }

    /**
     * Test case for verifying a user's password when an exception occurs.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    void verifyPasswordException() throws Exception {
        User user = MockObject.mockGeneralUser();
        when(userDAO.verifyPassword(user)).thenThrow(new SQLException("Database error"));

        assertFalse(userService.verifyPassword(user));

        verify(userDAO, times(1)).verifyPassword(user);
    }

    /**
     * Test case for retrieving the user ID by username when the user exists.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    void getUserIdByUsernameFound() throws Exception {
        String username = "existingUser";
        int expectedUserId = 1;
        when(userDAO.getUserIdByUsername(username)).thenReturn(expectedUserId);
        int actualUserId = userService.getUserIdByUsername(username);
        assertEquals(expectedUserId, actualUserId);
    }

    /**
     * Test case for retrieving the user ID by username when the user does not exist.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    void getUserIdByUsernameNotFound() throws Exception {
        String username = "nonExistingUser";
        when(userDAO.getUserIdByUsername(username)).thenReturn(-1);
        int actualUserId = userService.getUserIdByUsername(username);
        assertEquals(-1, actualUserId);
    }

    /**
     * Test case for retrieving the user ID by username when a database error occurs.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    void getUserIdByUsernameException() throws Exception {
        String username = "errorUser";
        when(userDAO.getUserIdByUsername(username)).thenThrow(new SQLException("Database error"));
        assertThrows(SQLException.class, () -> userService.getUserIdByUsername(username));
    }
}