package com.example.cs5031p3.demo.backend.serviceTest;

import com.example.cs5031p3.demo.MockObject;
import com.example.cs5031p3.demo.backend.dao.ProjectRegistrationsDAO;
import com.example.cs5031p3.demo.backend.dao.UserDAO;
import com.example.cs5031p3.demo.backend.enums.RegistrationStateEnum;
import com.example.cs5031p3.demo.backend.enums.TypeEnum;
import com.example.cs5031p3.demo.backend.model.User;
import com.example.cs5031p3.demo.backend.service.RegistrationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the RegistrationService class.
 */
class RegistrationServiceTest {
    @InjectMocks
    RegistrationService registrationService;

    @Mock
    ProjectRegistrationsDAO projectRegistrationsDAO;

    @Mock
    UserDAO userDAO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Test case for creating a project registration when it's successful.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    void createProjectRegistration() throws Exception {
        int projectId=2;
        int studentId=3;
        when(projectRegistrationsDAO.isStudentAssignedToProject(studentId)).thenReturn(false);
        when(projectRegistrationsDAO.isStudentInterestedInProject(projectId, studentId)).thenReturn(false);
        //ref: https://stackoverflow.com/questions/17731234/how-to-test-that-no-exception-is-thrown
        assertDoesNotThrow(() -> registrationService.createProjectRegistration(projectId,studentId));
        verify(projectRegistrationsDAO,times(1))
                .insertProjectRegistration(projectId,studentId);
    }

    /**
     * Test case for creating a project registration when the student is already assigned to a project.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    void createProjectRegistrationAssigned() throws Exception {
        int projectId=2;
        int studentId=3;
        when(projectRegistrationsDAO.isStudentAssignedToProject(studentId)).thenReturn(true);
        //ref: https://stackoverflow.com/questions/40268446/junit-5-how-to-assert-an-exception-is-thrown
        SQLException e=assertThrows(SQLException.class,
                () -> registrationService.createProjectRegistration(projectId,studentId));
        assertEquals("Student is already assigned to a project",e.getMessage());
    }

    /**
     * Test case for creating a project registration when the student is already interested in the project.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    void createProjectRegistrationInterested() throws Exception {
        int projectId=2;
        int studentId=3;
        when(projectRegistrationsDAO.isStudentAssignedToProject(studentId)).thenReturn(false);
        when(projectRegistrationsDAO.isStudentInterestedInProject(projectId, studentId)).thenReturn(true);
        SQLException e=assertThrows(SQLException.class,
                () -> registrationService.createProjectRegistration(projectId,studentId));
        assertEquals("Student is already interested in this project",e.getMessage());
    }

    /**
     * Test case for getting project registrations by student ID.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    void getProjectRegistrationsByStudentId() throws Exception{
        int studentId=3;
        List<Map<String, Object>> registrationList= MockObject.mockRegistration(studentId);
        when(userDAO.getUserById(studentId)).thenReturn(new User(studentId,
                "mock","mock","mock", TypeEnum.STUDENT.getCode()));
        when(projectRegistrationsDAO.findByStudentId(studentId)).thenReturn(registrationList);
        assertEquals(registrationList,registrationService.getProjectRegistrationsByStudentId(studentId));
    }

    /**
     * Test case for getting project registrations by student ID when the user doesn't exist.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    void getProjectRegistrationsByStudentIdNotExisted() throws Exception{
        int studentId=anyInt();
        when(userDAO.getUserById(studentId)).thenReturn(null);
        SQLException e=assertThrows(SQLException.class,
                () ->registrationService.getProjectRegistrationsByStudentId(studentId));
        assertEquals("The user doesn't exist",e.getMessage());
    }

    /**
     * Test case for getting project registrations by student ID when the user is not a student.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    void getProjectRegistrationsByStudentIdNotAStudent() throws Exception{
        User notStudent=MockObject.mockStaff();
        int studentId=notStudent.getId();
        when(userDAO.getUserById(studentId)).thenReturn(notStudent);
        assertFalse(registrationService.isStudent(studentId));
        SQLException e=assertThrows(SQLException.class,
                () ->registrationService.getProjectRegistrationsByStudentId(studentId));
        assertEquals("The user is not a student",e.getMessage());
    }

    /**
     * Test case for finding registration students by staff ID.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    void findRegistrationStudentsByStaffId() throws Exception{
        int staffId=32;
        List<Map<String, Object>> registrationList= MockObject.mockRegistration();
        when(userDAO.getUserById(staffId)).thenReturn
                (new User(staffId,"mock","mock","mock",TypeEnum.STAFF.getCode()));
        when(projectRegistrationsDAO.findRegistrationStudentsByStaffId(staffId)).thenReturn(registrationList);
        assertEquals(registrationList,registrationService.findRegistrationStudentsByStaffId(staffId));
    }

    /**
     * Test case for finding registration students by staff ID when the user doesn't exist.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    void findRegistrationStudentsByStaffIdNotExisted() throws Exception{
        int staffId=anyInt();
        when(userDAO.getUserById(staffId)).thenReturn(null);
        SQLException e=assertThrows(SQLException.class,
                () ->registrationService.findRegistrationStudentsByStaffId(staffId));
        assertEquals("The user doesn't exist",e.getMessage());
    }

    /**
     * Test case for finding registration students by staff ID when the user is not a staff.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    void findRegistrationStudentsByStaffIdNotAStaff() throws Exception{
        int staffId=anyInt();
        when(userDAO.getUserById(staffId)).thenReturn(new User(staffId,"mock","mock","mock",1));
        assertFalse(registrationService.isStaff(staffId));
        SQLException e=assertThrows(SQLException.class,
                () ->registrationService.findRegistrationStudentsByStaffId(staffId));
        assertEquals("The user is not a staff",e.getMessage());
    }

    /**
     * Test case for assigning a project registration to a student.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    void assignRegistration() throws Exception{
        int registrationId=1;
        int studentId=3;
        Map<String, Object> registration = new HashMap<>();
        registration.put("registrationId", registrationId);
        registration.put("projectId", 2);
        registration.put("studentId", studentId);
        registration.put("registrationState", 3);
        when(projectRegistrationsDAO.getRegistrationById(registrationId)).thenReturn(registration);
        assertTrue(registrationService.assignRegistration(registrationId));
        verify(projectRegistrationsDAO,times(1))
                .deleteInterestedRegistrationsByStudentId(studentId,registrationId);
        verify(projectRegistrationsDAO,times(1))
                .updateRegistrationState(registrationId, 2);
    }

    /**
     * Test case for assigning a project registration when the registration is not found.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    void assignRegistrationNotFound() throws Exception{
        int registrationId=anyInt();
        when(projectRegistrationsDAO.getRegistrationById(registrationId)).thenReturn(null);
        assertFalse(registrationService.assignRegistration(registrationId));
    }

    /**
     * Test case for assigning a project registration when the student is not found or an exception occurs in delete operation.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    void assignRegistrationStudentNotFoundOrOtherExceptionInDelete() throws Exception{
        int registrationId=1;
        int studentId=3;
        Map<String, Object> registration = new HashMap<>();
        registration.put("registrationId", registrationId);
        registration.put("projectId", 2);
        registration.put("studentId", studentId);
        registration.put("registrationState", 3);
        when(projectRegistrationsDAO.getRegistrationById(registrationId)).thenReturn(registration);
        doThrow(new SQLException())
                .when(projectRegistrationsDAO)
                .deleteInterestedRegistrationsByStudentId(studentId,registrationId);
        assertThrows(SQLException.class,()->registrationService.assignRegistration(registrationId));
    }

    /**
     * Test case for assigning a project registration when an exception occurs in the update operation.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    void assignRegistrationExceptionInUpdate() throws Exception{
        int registrationId=1;
        int studentId=3;
        Map<String, Object> registration = new HashMap<>();
        registration.put("registrationId", registrationId);
        registration.put("projectId", 2);
        registration.put("studentId", studentId);
        registration.put("registrationState", 3);
        when(projectRegistrationsDAO.getRegistrationById(registrationId)).thenReturn(registration);
        doThrow(new SQLException())
                .when(projectRegistrationsDAO)
                .updateRegistrationState(registrationId,RegistrationStateEnum.SUCCESS.getCode());
        assertThrows(SQLException.class,()->registrationService.assignRegistration(registrationId));
    }

    /**
     * Test case for checking if a student is assigned to a project when it's true.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    void isStudentAssignedToProjectTrue() throws Exception{
        int studentId=anyInt();
        when(projectRegistrationsDAO.isStudentAssignedToProject(studentId)).thenReturn(true);
        assertTrue(registrationService.isStudentAssignedToProject(studentId));
    }

    /**
     * Test case for checking if a student is assigned to a project when it's false.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    void isStudentAssignedToProjectFalse() throws Exception{
        int studentId=anyInt();
        when(projectRegistrationsDAO.isStudentAssignedToProject(studentId)).thenReturn(false);
        assertFalse(registrationService.isStudentAssignedToProject(studentId));
    }
}