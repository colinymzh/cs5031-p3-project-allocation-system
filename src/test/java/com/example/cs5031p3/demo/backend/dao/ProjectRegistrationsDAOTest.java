package com.example.cs5031p3.demo.backend.dao;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.sql.*;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * The ProjectRegistrationsDAOTest class contains unit tests for the ProjectRegistrationsDAO class.
 */
@ExtendWith(MockitoExtension.class)
class ProjectRegistrationsDAOTest {

    @Mock
    private DatabaseManager databaseManager;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private ProjectRegistrationsDAO projectRegistrationsDAO;

    /**
     * Sets up the test environment before each test method.
     *
     * @throws SQLException if a database access error occurs
     */
    @BeforeEach
    void setUp() throws SQLException {
        when(databaseManager.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
    }

    /**
     * Tests the insertProjectRegistration() method of ProjectRegistrationsDAO.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void insertProjectRegistrationTest() throws SQLException {
        // Arrange
        int projectId = 1;
        int studentId = 1;

        // Act
        projectRegistrationsDAO.insertProjectRegistration(projectId, studentId);

        // Assert
        verify(preparedStatement, times(1)).executeUpdate();
    }

    /**
     * Tests the findByStudentId() method of ProjectRegistrationsDAO.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void findByStudentIdTest() throws SQLException {
        // Arrange
        int studentId = 1;
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true).thenReturn(false); // Simulate one result
        when(resultSet.getInt(anyString())).thenReturn(1);
        when(resultSet.getString(anyString())).thenReturn("Sample Data");

        // Act
        List<Map<String, Object>> results = projectRegistrationsDAO.findByStudentId(studentId);

        // Assert
        assertFalse(results.isEmpty());
        verify(preparedStatement, times(1)).executeQuery();
    }

    /**
     * Tests the isStudentInterestedInProject() method of ProjectRegistrationsDAO when the student is interested.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void isStudentInterestedInProjectTest() throws SQLException {
        // Arrange
        int projectId = 1;
        int studentId = 1;
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(1); // Simulate the student has shown interest

        // Act
        boolean isInterested = projectRegistrationsDAO.isStudentInterestedInProject(projectId, studentId);

        // Assert
        assertTrue(isInterested);
        verify(preparedStatement, times(1)).executeQuery();
    }

    /**
     * Tests the updateRegistrationState() method of ProjectRegistrationsDAO.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void updateRegistrationStateTest() throws SQLException {
        // Given
        int registrationId = 1;
        int newState = 2; // Assume 2 represents a new state, like "Assigned"

        // When
        projectRegistrationsDAO.updateRegistrationState(registrationId, newState);

        // Then
        verify(preparedStatement).setInt(1, newState);
        verify(preparedStatement).setInt(2, registrationId);
        verify(preparedStatement).executeUpdate();
    }

    /**
     * Tests the deleteInterestedRegistrationsByStudentId() method of ProjectRegistrationsDAO.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void deleteInterestedRegistrationsByStudentIdTest() throws SQLException {
        // Given
        int studentId = 100; // Example student ID
        int currentRegistrationId = 1; // Example current registration ID not to be deleted

        // When
        projectRegistrationsDAO.deleteInterestedRegistrationsByStudentId(studentId, currentRegistrationId);

        // Then
        verify(preparedStatement).setInt(1, studentId);
        verify(preparedStatement).setInt(2, currentRegistrationId);
        verify(preparedStatement).executeUpdate();

    }

    /**
     * Tests the findRegistrationStudentsByStaffId() method of ProjectRegistrationsDAO.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void findRegistrationStudentsByStaffIdTest() throws SQLException {
        // Arrange
        int staffId = 1;
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true).thenReturn(false); // Simulate one result
        when(resultSet.getInt(anyString())).thenReturn(1);
        when(resultSet.getString(anyString())).thenReturn("Sample Data");

        // Act
        List<Map<String, Object>> results = projectRegistrationsDAO.findRegistrationStudentsByStaffId(staffId);

        // Assert
        assertFalse(results.isEmpty());
        verify(preparedStatement, times(1)).executeQuery();
    }

    /**
     * Tests the isStudentAssignedToProject() method of ProjectRegistrationsDAO when the student is assigned to a project.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void isStudentAssignedToProjectTestTrue() throws SQLException {
        // Arrange
        int studentId = 1;
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(1); // Simulate the student is assigned to a project

        // Act
        boolean isAssigned = projectRegistrationsDAO.isStudentAssignedToProject(studentId);

        // Assert
        assertTrue(isAssigned);
        verify(preparedStatement, times(1)).executeQuery();
    }

    /**
     * Tests the isStudentAssignedToProject() method of ProjectRegistrationsDAO when the student is not assigned to any project.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void isStudentAssignedToProjectTestFalse() throws SQLException {
        // Arrange
        int studentId = 1;
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false); // Simulate the student is not assigned to any project

        // Act
        boolean isAssigned = projectRegistrationsDAO.isStudentAssignedToProject(studentId);

        // Assert
        assertFalse(isAssigned);
        verify(preparedStatement, times(1)).executeQuery();
    }

    /**
     * Tests the getRegistrationById() method of ProjectRegistrationsDAO when a registration is found.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void getRegistrationByIdTestFound() throws SQLException {
        // Arrange
        int registrationId = 1;
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(anyString())).thenReturn(1);

        // Act
        Map<String, Object> registration = projectRegistrationsDAO.getRegistrationById(registrationId);

        // Assert
        assertNotNull(registration);
        verify(preparedStatement, times(1)).executeQuery();
    }

    /**
     * Tests the getRegistrationById() method of ProjectRegistrationsDAO when a registration is not found.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void getRegistrationByIdTestNotFound() throws SQLException {
        // Arrange
        int registrationId = 1;
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        // Act
        Map<String, Object> registration = projectRegistrationsDAO.getRegistrationById(registrationId);

        // Assert
        assertNull(registration);
        verify(preparedStatement, times(1)).executeQuery();
    }

    /**
     * Tests the isStudentInterestedInProject() method of ProjectRegistrationsDAO when the student is not interested in the project.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void isStudentInterestedInProjectTestNotInterested() throws SQLException {
        // Arrange
        int projectId = 1;
        int studentId = 1;
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(0); // Simulate the student has not shown interest

        // Act
        boolean isInterested = projectRegistrationsDAO.isStudentInterestedInProject(projectId, studentId);

        // Assert
        assertFalse(isInterested);
        verify(preparedStatement, times(1)).executeQuery();
    }
}
