package com.example.cs5031p3.demo.backend.controllerTest;

import com.example.cs5031p3.demo.MockObject;
import com.example.cs5031p3.demo.backend.controller.RegistrationController;
import com.example.cs5031p3.demo.backend.service.RegistrationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for the RegistrationController class.
 */
@WebMvcTest(RegistrationController.class)
class RegistrationControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RegistrationService registrationService;

    /**
     * Test case for creating a new project registration.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void createNewProjectRegistration() throws Exception{
        int projectId = 1;
        int studentId = 3;
        Map<String, String> createInfo = new HashMap<>();
        createInfo.put("projectId", String.valueOf(projectId));
        createInfo.put("studentId", String.valueOf(studentId));
        //ref:https://www.javacodegeeks.com/2020/06/mock-void-method-with-mockito.html
        doNothing().when(registrationService).createProjectRegistration(projectId,studentId);
        mvc.perform(post("/registration/create").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createInfo))
                )
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("Project registration created successfully")))
        ;
    }

    /**
     * Test case for creating a project registration when student is already assigned to a project.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void createAssignedProjectRegistration() throws Exception{
        int projectId = 1;
        int studentId = 3;
        Map<String, String> createInfo = new HashMap<>();
        createInfo.put("projectId", String.valueOf(projectId));
        createInfo.put("studentId", String.valueOf(studentId));
        doThrow(new SQLException("Student is already assigned to a project"))
                .when(registrationService).createProjectRegistration(projectId,studentId);
        mvc.perform(post("/registration/create").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createInfo))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string(equalTo("Student is already assigned to a project")))
        ;
    }

    /**
     * Test case for creating a project registration when student is already interested in the project.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void createInterestedProjectRegistration() throws Exception{
        int projectId = 1;
        int studentId = 3;
        Map<String, String> createInfo = new HashMap<>();
        createInfo.put("projectId", String.valueOf(projectId));
        createInfo.put("studentId", String.valueOf(studentId));
        doThrow(new SQLException("Student is already interested in this project"))
                .when(registrationService).createProjectRegistration(projectId,studentId);
        mvc.perform(post("/registration/create").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createInfo))
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string(equalTo("Student is already interested in this project")))
        ;
    }

    /**
     * Test case for getting project registrations by an existed student ID.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void getProjectRegistrationsByExistedStudentId() throws Exception {
        int studentId = 2;
        List<Map<String, Object>> registrationList= MockObject.mockRegistration(studentId);
        when(registrationService.getProjectRegistrationsByStudentId(studentId)).thenReturn(registrationList);
        mvc.perform(get("/registration/student/"+studentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(equalTo(objectMapper.writeValueAsString(registrationList))));
    }

    /**
     * Test case for getting project registrations when the student ID is not found.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void getProjectRegistrationsNotFound() throws Exception {
        int studentId = 2222;
        doThrow(new SQLException("The user doesn't exist"))
                .when(registrationService).getProjectRegistrationsByStudentId(studentId);
        mvc.perform(get("/registration/student/"+studentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(equalTo("The user doesn't exist")))
        ;
    }

    /**
     * Test case for getting project registrations when the user is not a student.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void getProjectRegistrationsNotAStudent() throws Exception {
        int studentId = 2222;
        doThrow(new SQLException("The user is not a student"))
                .when(registrationService).getProjectRegistrationsByStudentId(studentId);
        mvc.perform(get("/registration/student/"+studentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(equalTo("The user is not a student")))
        ;
    }

    /**
     * Test case for getting project registrations by an existed staff ID.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void getRegistrationStudentsByExistedStaffId() throws Exception{
        int staffId = 2;
        List<Map<String, Object>> registrationList= MockObject.mockRegistration();
        when(registrationService.findRegistrationStudentsByStaffId(staffId)).thenReturn(registrationList);
        mvc.perform(get("/registration/students-registration/"+staffId)
                )
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo(objectMapper.writeValueAsString(registrationList))));
    }

    /**
     * Test case for getting project registrations when the staff ID is not found.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void getRegistrationStudentsByStaffIdNotFound() throws Exception {
        int staffId = 5555;
        doThrow(new SQLException())
                .when(registrationService).findRegistrationStudentsByStaffId(staffId);
        mvc.perform(get("/registration/students-registration/"+staffId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test case for assigning a project registration successfully.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void assignRegistrationSuccessful() throws Exception {
        int registrationId=2;
        when(registrationService.assignRegistration(registrationId)).thenReturn(true);
        mvc.perform(put("/registration/assign/"+registrationId))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("Registration approved successfully")));
    }

    /**
     * Test case for failing to assign a project registration.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void assignRegistrationFail() throws Exception {
        int registrationId=6;
        when(registrationService.assignRegistration(registrationId)).thenReturn(false);
        mvc.perform(put("/registration/assign/"+registrationId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(equalTo("Failed to approve registration")));
    }

    /**
     * Test case for handling an exception while assigning a project registration.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void assignRegistrationException() throws Exception {
        int registrationId=6;
        when(registrationService.assignRegistration(registrationId)).thenThrow(new SQLException("msg"));
        mvc.perform(put("/registration/assign/"+registrationId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(equalTo("msg")))
                ;
    }

    /**
     * Test case for checking if a student is assigned to a project (returns true).
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void isStudentAssignedToProjectTrue() throws Exception {
        int studentId=2;
        when((registrationService.isStudentAssignedToProject(studentId))).thenReturn(true);
        mvc.perform(get("/registration/student/"+studentId+"/assigned"))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("true")));
    }

    /**
     * Test case for checking if a student is assigned to a project (returns false).
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void isStudentAssignedToProjectFalse() throws Exception {
        int studentId=2;
        when((registrationService.isStudentAssignedToProject(studentId))).thenReturn(false);
        mvc.perform(get("/registration/student/"+studentId+"/assigned"))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("false")));
    }

    /**
     * Test case for handling an exception while checking if a student is assigned to a project.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void isStudentAssignedToProject_Exception() throws Exception {
        int studentId = 2;
        when(registrationService.isStudentAssignedToProject(studentId)).thenThrow(new SQLException("Internal server error"));

        mvc.perform(get("/registration/student/{studentId}/assigned", studentId))
                .andExpect(status().isInternalServerError());

        verify(registrationService, times(1)).isStudentAssignedToProject(studentId);
    }
}