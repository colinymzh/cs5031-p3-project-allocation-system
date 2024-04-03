package com.example.cs5031p3.demo.backend.serviceTest;

import com.example.cs5031p3.demo.MockObject;
import com.example.cs5031p3.demo.backend.dao.ProjectDAO;
import com.example.cs5031p3.demo.backend.dao.UserDAO;
import com.example.cs5031p3.demo.backend.model.Project;
import com.example.cs5031p3.demo.backend.model.User;
import com.example.cs5031p3.demo.backend.service.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the ProjectService class.
 */
class ProjectServiceTest {

    @InjectMocks
    ProjectService projectService;

    @Mock
    ProjectDAO projectDAO;
    @Mock
    UserDAO userDAO;

    /**
     * Sets up the test environment.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Test case for getting a project by ID.
     *
     * @throws Exception if an error occurs
     */
    @Test
    void getProjectById() throws Exception {
        int projectId=2;
        Project project=new Project(projectId,"mockTitle","MockDescription",9);
        when(projectDAO.getProjectById(projectId)).thenReturn(project);
        assertEquals(project,projectService.getProjectById(projectId));
    }

    /**
     * Test case for getting a project by ID when not found.
     *
     * @throws Exception if an error occurs
     */
    @Test
    void getProjectByIdNotFound() throws Exception {
        int projectId=2;
        when(projectDAO.getProjectById(projectId)).thenReturn(null);
        assertNull(projectService.getProjectById(projectId));
    }

    /**
     * Test case for creating a project.
     *
     * @throws Exception if an error occurs
     */
    @Test
    void testCreateProject() throws Exception {
        int projectId=2;
        Project project=new Project(projectId,"mockTitle","MockDescription",9);
        doNothing().when(projectDAO).createProject(project);
        assertDoesNotThrow(() -> projectService.createProject(project));
    }

    /**
     * Test case for updating a project.
     *
     * @throws SQLException if an SQL error occurs
     */
    @Test
    void testUpdateProject() throws SQLException {
        Project project = new Project(1, "Project Name", "Project Description", 1);
        doNothing().when(projectDAO).updateProject(project);
        assertDoesNotThrow(() -> projectService.updateProject(project));
        verify(projectDAO, times(1)).updateProject(project);
    }

    /**
     * Test case for deleting a project.
     *
     * @throws SQLException if an SQL error occurs
     */
    @Test
    void testDeleteProject() throws SQLException {
        int projectId = 1;
        doNothing().when(projectDAO).deleteProject(projectId);
        assertDoesNotThrow(() -> projectService.deleteProject(projectId));
        verify(projectDAO, times(1)).deleteProject(projectId);
    }

    /**
     * Test case for retrieving all projects.
     *
     * @throws SQLException if an SQL error occurs
     */
    @Test
    void testGetAllProjects() throws SQLException {
        List<Project> projectList = new ArrayList<>();
        projectList.add(new Project(1, "Project 1", "Description 1", 1));
        projectList.add(new Project(2, "Project 2", "Description 2", 2));

        when(projectDAO.getAllProjects()).thenReturn(projectList);

        List<Project> result = projectService.getAllProjects();

        assertEquals(projectList.size(), result.size());
        assertEquals(projectList.get(0).getId(), result.get(0).getId());
        assertEquals(projectList.get(1).getId(), result.get(1).getId());

        verify(projectDAO, times(1)).getAllProjects();
    }

    /**
     * Test case for retrieving projects by staff ID.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    void getProjectsByStaffId() throws Exception{
        User staff=MockObject.mockStaff();
        int staffId=staff.getId();
        when(userDAO.getUserById(staffId)).thenReturn
                (staff);
        List<Project> projectList= MockObject.projectList(staffId);
        when(projectDAO.findProjectsByStaffId(staffId)).thenReturn(projectList);
        assertEquals(projectList,projectService.getProjectsByStaffId(staffId));
    }

    /**
     * Test case for retrieving projects by staff ID when the list is empty.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    void getProjectsByStaffIdEmptyList() throws Exception{
        User staff=MockObject.mockStaff();
        int staffId=staff.getId();
        when(userDAO.getUserById(staffId)).thenReturn(staff);
        when(projectDAO.findProjectsByStaffId(staffId)).thenReturn(null);
        // It's ok to have empty project list for staff
        assertDoesNotThrow(()->projectService.getProjectsByStaffId(staffId));
    }

    /**
     * Test case for retrieving projects by staff ID when the staff ID does not exist.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    void getProjectsByStaffIdNotExist() throws Exception{
        int staffId=2;
        when(userDAO.getUserById(staffId)).thenReturn(null);
        SQLException e=assertThrows(SQLException.class,
                () ->projectService.getProjectsByStaffId(staffId));
        assertEquals("The user doesn't exist",e.getMessage());
    }

    /**
     * Test case for retrieving projects by staff ID when the user is not a staff member.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    void getProjectsByStaffIdNotAStaff() throws Exception{
        User student=MockObject.mockStudent();
        int staffId=student.getId();
        when(userDAO.getUserById(staffId)).thenReturn(student);
        assertFalse(projectService.isStaff(staffId));
        SQLException e=assertThrows(SQLException.class,
                () ->projectService.getProjectsByStaffId(staffId));
        assertEquals("The user is not a staff",e.getMessage());
    }

    /**
     * Test case for making a project unavailable.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    void makeProjectUnavailable() throws Exception{
        int projectId=443;
        doNothing().when(projectDAO).makeProjectUnavailable(projectId);
        assertDoesNotThrow(()->projectService.makeProjectUnavailable(projectId));
    }

    /**
     * Test case for making a project unavailable when an exception occurs.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    void makeProjectUnavailableException() throws Exception{
        int projectId=443;
        doThrow(new SQLException()).when(projectDAO).makeProjectUnavailable(projectId);
        assertThrows(SQLException.class,()->projectService.makeProjectUnavailable(projectId));
    }
}