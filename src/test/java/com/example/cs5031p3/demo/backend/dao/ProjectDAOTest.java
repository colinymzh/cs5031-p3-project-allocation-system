package com.example.cs5031p3.demo.backend.dao;

import com.example.cs5031p3.demo.backend.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * The ProjectDAOTest class contains unit tests for the ProjectDAO class.
 */
public class ProjectDAOTest {
    @Mock
    private DatabaseManager databaseManager;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private ProjectDAO projectDAO;

    /**
     * Sets up the test environment before each test method.
     *
     * @throws SQLException if a database access error occurs
     */
    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        when(databaseManager.getConnection()).thenReturn(connection);
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(connection.prepareStatement(anyString(), anyInt())).thenReturn(preparedStatement);
    }

    /**
     * Tests the createProject() method of ProjectDAO.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void createProject() throws SQLException {
        // Arrange
        Project project = new Project();
        project.setTitle("Test Project");
        project.setDescription("Test Description");
        project.setStaffId(1);
        project.setAvailable(1);

        when(preparedStatement.getGeneratedKeys()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(1);

        // Act
        projectDAO.createProject(project);

        // Assert
        verify(preparedStatement, times(1)).executeUpdate();
        assertEquals(1, project.getId());
    }

    /**
     * Tests the getProjectById() method of ProjectDAO.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void getProjectById() throws SQLException {
        // Arrange
        int existingProjectId = 1;
        int nonExistingProjectId = 2;

        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true).thenReturn(false); // 第一次调用返回true,第二次调用返回false
        when(resultSet.getInt("project_id")).thenReturn(existingProjectId);
        when(resultSet.getString("title")).thenReturn("Test Project");
        when(resultSet.getString("description")).thenReturn("Test Description");
        when(resultSet.getInt("staff_id")).thenReturn(1);
        when(resultSet.getInt("available")).thenReturn(1);
        when(resultSet.getString("staff_name")).thenReturn("John Doe");

        // Act
        Project existingProject = projectDAO.getProjectById(existingProjectId);
        Project nonExistingProject = projectDAO.getProjectById(nonExistingProjectId);

        // Assert
        assertNotNull(existingProject);
        assertEquals(existingProjectId, existingProject.getId());
        assertEquals("Test Project", existingProject.getTitle());
        assertEquals("Test Description", existingProject.getDescription());
        assertEquals(1, existingProject.getStaffId());
        assertEquals(1, existingProject.getAvailable());
        assertEquals("John Doe", existingProject.getStaffName());

        assertNull(nonExistingProject);
    }

    /**
     * Tests the updateProject() method of ProjectDAO.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void updateProject() throws SQLException {
        // Arrange
        Project project = new Project();
        project.setId(1);
        project.setTitle("Updated Project");
        project.setDescription("Updated Description");
        project.setStaffId(2);
        project.setAvailable(0);

        // Act
        projectDAO.updateProject(project);

        // Assert
        verify(preparedStatement, times(1)).executeUpdate();
    }

    /**
     * Tests the deleteProject() method of ProjectDAO.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void deleteProject() throws SQLException {
        // Arrange
        int projectId = 1;

        // Act
        projectDAO.deleteProject(projectId);

        // Assert
        verify(preparedStatement, times(1)).executeUpdate();
    }

    /**
     * Tests the getAllProjects() method of ProjectDAO.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void getAllProjects() throws SQLException {
        // Arrange
        List<Project> expectedProjects = new ArrayList<>();
        Project project1 = new Project();
        project1.setId(1);
        project1.setTitle("Project 1");
        project1.setDescription("Description 1");
        project1.setStaffId(1);
        project1.setStaffName("John Doe");
        project1.setAvailable(1);
        expectedProjects.add(project1);

        Project project2 = new Project();
        project2.setId(2);
        project2.setTitle("Project 2");
        project2.setDescription("Description 2");
        project2.setStaffId(2);
        project2.setStaffName("Jane Smith");
        project2.setAvailable(0);
        expectedProjects.add(project2);

        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getInt("project_id")).thenReturn(1, 2);
        when(resultSet.getString("title")).thenReturn("Project 1", "Project 2");
        when(resultSet.getString("description")).thenReturn("Description 1", "Description 2");
        when(resultSet.getInt("staff_id")).thenReturn(1, 2);
        when(resultSet.getString("staff_name")).thenReturn("John Doe", "Jane Smith");
        when(resultSet.getInt("available")).thenReturn(1, 0);

        // Act
        List<Project> actualProjects = projectDAO.getAllProjects();

        // Assert
        assertEquals(expectedProjects.size(), actualProjects.size());
        for (int i = 0; i < expectedProjects.size(); i++) {
            Project expectedProject = expectedProjects.get(i);
            Project actualProject = actualProjects.get(i);
            assertEquals(expectedProject.getId(), actualProject.getId());
            assertEquals(expectedProject.getTitle(), actualProject.getTitle());
            assertEquals(expectedProject.getDescription(), actualProject.getDescription());
            assertEquals(expectedProject.getStaffId(), actualProject.getStaffId());
            assertEquals(expectedProject.getStaffName(), actualProject.getStaffName());
            assertEquals(expectedProject.getAvailable(), actualProject.getAvailable());
        }
    }

    /**
     * Tests the findProjectsByStaffId() method of ProjectDAO.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void findProjectsByStaffId() throws SQLException {
        // Arrange
        int staffId = 1;
        List<Project> expectedProjects = new ArrayList<>();
        Project project1 = new Project();
        project1.setId(1);
        project1.setTitle("Project 1");
        project1.setDescription("Description 1");
        project1.setStaffId(staffId);
        project1.setStaffName("John Doe");
        project1.setAvailable(1);
        expectedProjects.add(project1);

        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getInt("project_id")).thenReturn(1);
        when(resultSet.getString("title")).thenReturn("Project 1");
        when(resultSet.getString("description")).thenReturn("Description 1");
        when(resultSet.getInt("staff_id")).thenReturn(staffId);
        when(resultSet.getString("staff_name")).thenReturn("John Doe");
        when(resultSet.getInt("available")).thenReturn(1);

        // Act
        List<Project> actualProjects = projectDAO.findProjectsByStaffId(staffId);

        // Assert
        assertEquals(expectedProjects.size(), actualProjects.size());
        for (int i = 0; i < expectedProjects.size(); i++) {
            Project expectedProject = expectedProjects.get(i);
            Project actualProject = actualProjects.get(i++);
            assertEquals(expectedProject.getId(), actualProject.getId());
            assertEquals(expectedProject.getTitle(), actualProject.getTitle());
            assertEquals(expectedProject.getDescription(), actualProject.getDescription());
            assertEquals(expectedProject.getStaffId(), actualProject.getStaffId());
            assertEquals(expectedProject.getStaffName(), actualProject.getStaffName());
            assertEquals(expectedProject.getAvailable(), actualProject.getAvailable());
        }
    }

    /**
     * Tests the makeProjectUnavailable() method of ProjectDAO.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void makeProjectUnavailable() throws SQLException {
        // Arrange
        int projectId = 1;

        // Act
        projectDAO.makeProjectUnavailable(projectId);

        // Assert
        verify(preparedStatement, times(1)).executeUpdate();
    }
}
