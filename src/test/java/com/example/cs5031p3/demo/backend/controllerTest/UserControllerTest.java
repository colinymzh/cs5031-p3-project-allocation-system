package com.example.cs5031p3.demo.backend.controllerTest;

import com.example.cs5031p3.demo.MockObject;
import com.example.cs5031p3.demo.backend.bean.ResponseMessage;
import com.example.cs5031p3.demo.backend.controller.UserController;
import com.example.cs5031p3.demo.backend.dto.ResponseDTO;
import com.example.cs5031p3.demo.backend.model.User;
import com.example.cs5031p3.demo.backend.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.SQLException;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for the UserController class.
 */
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    /**
     * Test case for creating a user successfully.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void createUserNormal() throws Exception {
        User user=MockObject.mockGeneralUser();
        doNothing().when(userService).createUser(user);
        mvc.perform(post("/user/").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user))
                )
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("The new user is created")))
        ;
        verify(userService,times(1)).createUser(user);
    }

    /**
     * Test case for creating a user with an existing username.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test//create user should be aware if there is any user with the same username
    void createUserWithExistedUsername() throws Exception {
        User user=MockObject.mockGeneralUser();
        doThrow(new SQLException("The username has already existed"))
                .when(userService).createUser(user);
        // The test fail because user in userService and the one constructed from Json are not the same object
        mvc.perform(post("/user/").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string(equalTo("The username has already existed")))
        ;
        verify(userService,times(1)).createUser(user);
    }

    /**
     * Test case for getting a user by ID when the user exists.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void getUserByIdFound() throws Exception {
        User user = MockObject.mockGeneralUser();
        when(userService.getUserById(user.getId())).thenReturn(user);

        mvc.perform(get("/user/id/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));

        verify(userService, times(1)).getUserById(user.getId());
    }

    /**
     * Test case for getting a user by ID when the user does not exist.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void getUserByIdNotFound() throws Exception {
        int userId = 999;
        when(userService.getUserById(userId)).thenReturn(null);

        mvc.perform(get("/user/id/{id}", userId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(equalTo("No such user")));

        verify(userService, times(1)).getUserById(userId);
    }

    /**
     * Test case for getting a user by ID when an exception occurs.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void getUserByIdException() throws Exception {
        int userId = 1;
        when(userService.getUserById(userId)).thenThrow(new SQLException("Database error"));

        mvc.perform(get("/user/id/{id}", userId))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(equalTo("Database error")));

        verify(userService, times(1)).getUserById(userId);
    }

    /**
     * Test case for getting a user by username when the user exists.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void getUserByUsernameFound() throws Exception {
        User user = MockObject.mockGeneralUser();
        when(userService.getUserByUsername(user.getUsername())).thenReturn(user);

        mvc.perform(get("/user/username/{username}", user.getUsername()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));

        verify(userService, times(1)).getUserByUsername(user.getUsername());
    }

    /**
     * Test case for getting a user by username when the user does not exist.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void getUserByUsernameNotFound() throws Exception {
        String username = "nonexistent";
        when(userService.getUserByUsername(username)).thenReturn(null);

        mvc.perform(get("/user/username/{username}", username))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(equalTo("No such user")));

        verify(userService, times(1)).getUserByUsername(username);
    }

    /**
     * Test case for getting a user by username when an exception occurs.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void getUserByUsernameException() throws Exception {
        String username = "testUser";
        when(userService.getUserByUsername(username)).thenThrow(new RuntimeException("Unexpected error"));

        mvc.perform(get("/user/username/{username}", username))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(equalTo("Unexpected error")));

        verify(userService, times(1)).getUserByUsername(username);
    }

    /**
     * Test case for updating a user successfully.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void updateUserNormal() throws Exception {
        User user = MockObject.mockGeneralUser();
        doNothing().when(userService).updateUser(any(User.class));

        mvc.perform(put("/user/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("User updated successfully.")));

        verify(userService, times(1)).updateUser(any(User.class));
    }

    /**
     * Test case for updating a user when an exception occurs.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void updateUserException() throws Exception {
        User user = MockObject.mockGeneralUser();
        doThrow(new SQLException("Error updating user.")).when(userService).updateUser(any(User.class));

        mvc.perform(put("/user/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(equalTo("Error updating user.")));

        verify(userService, times(1)).updateUser(any(User.class));
    }

    /**
     * Test case for deleting an existing user successfully.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void deleteExistingUser() throws Exception {
        int userId = 1;
        doNothing().when(userService).deleteUser(userId);

        mvc.perform(delete("/user/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("User deleted successfully.")));

        verify(userService, times(1)).deleteUser(userId);
    }

    /**
     * Test case for deleting a non-existing user.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void deleteNonExistedUser() throws Exception {
        int userId = -5039;
        doThrow(new SQLException("User does not exist")).when(userService).deleteUser(userId);

        mvc.perform(delete("/user/{id}", userId))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(equalTo("Error deleting user.")));

        verify(userService, times(1)).deleteUser(userId);
    }

    /**
     * Test case for deleting a user with a string ID.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void deleteStringIdUser() throws Exception {
        String userid="@SE5031";
        mvc.perform(delete("/user/"+userid)
                )
                .andExpect(status().isBadRequest())
        ;
    }

    /**
     * Test case for getting all users.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void getAllUsers() throws Exception{
        List<User> userList= MockObject.mockUserList();
        ObjectMapper objectMapper=new ObjectMapper();
        when(userService.getAllUsers()).thenReturn(userList);
        mvc.perform(get("/user/all"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(userList)));
    }

    /**
     * Test case for getting all users when an exception occurs.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void getAllUsersException() throws Exception{
        doThrow(new SQLException()).when(userService).getAllUsers();
        mvc.perform(get("/user/all"))
                .andExpect(status().isInternalServerError())
        ;
    }

    /**
     * Test case for updating a user's password.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void updatePassword() throws Exception{
        User user=MockObject.mockGeneralUser();
        doNothing().when(userService).updatePassword(user);
        mvc.perform(put("/user/password").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
        ;
    }

    /**
     * Test case for updating a user's password when an exception occurs.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void updatePasswordException() throws Exception{
        User user=MockObject.mockGeneralUser();
        doThrow(new SQLException()).when(userService).updatePassword(user);
        mvc.perform(put("/user/password").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isInternalServerError())
        ;
    }

    /**
     * Test case for logging in a user successfully.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void loginUserSuccess() throws Exception {
        User loginUser = MockObject.mockGeneralUser();
        User returnedUser = new User(loginUser.getId(), loginUser.getName(), loginUser.getUsername(), loginUser.getPassword(), loginUser.getTypeId());

        when(userService.loginUser(loginUser.getUsername(), loginUser.getPassword(), loginUser.getTypeId()))
                .thenReturn(returnedUser);

        mvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginUser)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(ResponseDTO.success(returnedUser))));

        verify(userService, times(1))
                .loginUser(loginUser.getUsername(), loginUser.getPassword(), loginUser.getTypeId());
    }

    /**
     * Test case for failing to log in a user.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void loginUserFailure() throws Exception {
        User loginUser = MockObject.mockGeneralUser();

        when(userService.loginUser(loginUser.getUsername(), loginUser.getPassword(), loginUser.getTypeId()))
                .thenReturn(null);

        mvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginUser)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(ResponseDTO.errorByMsg(ResponseMessage.LOGIN_FAILED))));

        verify(userService, times(1))
                .loginUser(loginUser.getUsername(), loginUser.getPassword(), loginUser.getTypeId());
    }

    /**
     * Test case for an exception occurring while logging in a user.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void loginUserException() throws Exception {
        User loginUser = MockObject.mockGeneralUser();

        when(userService.loginUser(loginUser.getUsername(), loginUser.getPassword(), loginUser.getTypeId()))
                .thenThrow(new SQLException("Database error"));

        mvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginUser)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(ResponseDTO.errorByMsg(ResponseMessage.INTERNAL_SERVER_ERROR))));

        verify(userService, times(1))
                .loginUser(loginUser.getUsername(), loginUser.getPassword(), loginUser.getTypeId());
    }

    /**
     * Test case for verifying a user's password when it is correct.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void verifyPasswordCorrect() throws Exception {
        User user=MockObject.mockGeneralUser();
        when(userService
                .verifyPassword(user))
                .thenReturn(true);
        mvc.perform(post("/user/verify-password").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user))
                )
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("{\"code\":0,\"message\":null,\"data\":null}")))
        ;
    }

    /**
     * Test case for verifying a user's password when it is wrong.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void verifyPasswordWrong() throws Exception {
        User user=MockObject.mockGeneralUser();
        when(userService
                .verifyPassword(user))
                .thenReturn(false);
        mvc.perform(post("/user/verify-password").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user))
                )
//                .andExpect(status().isBadRequest())
                .andExpect(content().string(equalTo("{\"code\":-3,\"message\":\"Password error\",\"data\":null}")))
        ;
    }

    /**
     * Test case for retrieving the user ID by username when the user exists.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void getUserIdByUsernameFound() throws Exception {
        String username = "testUser";
        int userId = 1;
        when(userService.getUserIdByUsername(username)).thenReturn(userId);

        mvc.perform(get("/user/username/{username}/id", username))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(userId)));

        verify(userService, times(1)).getUserIdByUsername(username);
    }

    /**
     * Test case for retrieving the user ID by username when the user does not exist.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void getUserIdByUsernameNotFound() throws Exception {
        String username = "nonexistentUser";
        when(userService.getUserIdByUsername(username)).thenReturn(-1);

        mvc.perform(get("/user/username/{username}/id", username))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(equalTo("User not found")));

        verify(userService, times(1)).getUserIdByUsername(username);
    }

    /**
     * Test case for retrieving the user ID by username when an exception occurs.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void getUserIdByUsernameException() throws Exception {
        String username = "testUser";
        when(userService.getUserIdByUsername(username)).thenThrow(new SQLException("Database error"));

        mvc.perform(get("/user/username/{username}/id", username))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(equalTo("Error retrieving user ID")));

        verify(userService, times(1)).getUserIdByUsername(username);
    }
}