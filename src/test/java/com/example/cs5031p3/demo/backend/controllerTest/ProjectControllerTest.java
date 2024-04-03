package com.example.cs5031p3.demo.backend.controllerTest;

import com.example.cs5031p3.demo.MockObject;
import com.example.cs5031p3.demo.backend.controller.ProjectController;
import com.example.cs5031p3.demo.backend.model.Project;
import com.example.cs5031p3.demo.backend.service.ProjectService;
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
 * Test class for {@link ProjectController}.
 */
@WebMvcTest(ProjectController.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProjectService projectService;

    /**
     * Test case for creating a new project.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void createProject() throws Exception {
        Project mockProject=new Project(212,"mock","mock",187);
        doNothing().when(projectService).createProject(mockProject);
        mvc.perform(post("/project/create").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockProject))
                )
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("The new project is created")));
        verify(projectService,times(1)).createProject(mockProject);
    }

    /**
     * Test case for handling exception during project creation.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void createProjectException() throws Exception {
        Project mockProject=new Project(212,"mock","mock",187);
        doThrow(new SQLException()).when(projectService).createProject(mockProject);
        mvc.perform(post("/project/create").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockProject))
                )
                .andExpect(status().isInternalServerError());
    }

    /**
     * Test case for getting an existing project.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void getExistedProject() throws Exception {
        when(projectService.getProjectById(1))
                .thenReturn(new Project(1,"1","1",1));
        mvc.perform(get("/project/" + "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(equalTo(
                        "{\"id\":1," +
                                "\"title\":\"1\"," +
                                "\"description\":\"1\"," +
                                "\"staffId\":1," +
                                "\"available\":1," +
                                "\"staffName\":null}")))// The staffName is null in test since we didn't set it
        ;
        verify(projectService, times(1)).getProjectById(1);
    }

    /**
     * Test case for getting a non-existent project.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void getNonExistedProject() throws Exception {
        int projectId=5031;
        when(projectService.getProjectById(projectId))
                .thenReturn(null);
        mvc.perform(get("/project/" + projectId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(equalTo("No such project")))
        ;
        verify(projectService, times(1)).getProjectById(projectId);
    }

    /**
     * Test case for getting a project with a negative ID.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void getNegativeIdProject() throws Exception {
        int projectId=-31;
        when(projectService.getProjectById(projectId))
                .thenReturn(null);
        mvc.perform(get("/project/" + projectId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(equalTo("No such project")))
        ;
        verify(projectService, times(1)).getProjectById(projectId);
    }

    /**
     * Test case for updating a project successfully.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void updateProjectSuccess() throws Exception {
        Project projectToUpdate = new Project(1, "Project Title", "Project Description", 10);
        doNothing().when(projectService).updateProject(any(Project.class));

        mvc.perform(put("/project/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectToUpdate)))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("Project updated successfully.")));

        verify(projectService, times(1)).updateProject(any(Project.class));
    }

    /**
     * Test case for handling failure during project update.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void updateProjectFailure() throws Exception {
        Project projectToUpdate = new Project(1, "Project Title", "Project Description", 10);
        doThrow(new SQLException("Error updating project.")).when(projectService).updateProject(any(Project.class));

        mvc.perform(put("/project/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(projectToUpdate)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(equalTo("Error updating project.")));

        verify(projectService, times(1)).updateProject(any(Project.class));
    }

    /**
     * Test case for getting projects by staff ID.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void getProjectsByStaffId() throws Exception
    {
        int staffId=221;
        List<Project> projectList= MockObject.projectList(staffId);
        when(projectService.getProjectsByStaffId(staffId)).thenReturn(projectList);
        ObjectMapper objectMapper=new ObjectMapper();
        mvc.perform(get("/project/staff/"+staffId))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(projectList)));
    }

    /**
     * Test case for getting projects by staff ID with empty project list.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void getProjectsByStaffIdEmptyProject() throws Exception
    {
        int staffId=221;
        List<Project> projectList= MockObject.projectList(staffId);
        // It's ok to have empty project list for staff
        when(projectService.getProjectsByStaffId(staffId)).thenReturn(null);
        mvc.perform(get("/project/staff/"+staffId))
                .andExpect(status().isOk());
    }

    /**
     * Test case for handling non-existent staff ID.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void getProjectsByStaffIdNotExist() throws Exception {
        int staffId=221;
        when(projectService.getProjectsByStaffId(staffId)).thenThrow(new SQLException("The user doesn't exist"));
        mvc.perform(get("/project/staff/"+staffId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(equalTo("The user doesn't exist")));
    }

    /**
     * Test case for handling non-staff user.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void getProjectsByStaffIdNotAStaff() throws Exception {
        int staffId=221;
        when(projectService.getProjectsByStaffId(staffId)).thenThrow(new SQLException("The user is not a staff"));
        mvc.perform(get("/project/staff/"+staffId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(equalTo("The user is not a staff")));
    }

    /**
     * Test case for handling internal server error while getting projects by staff ID.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void getProjectsByStaffId500ISE() throws Exception {
        int staffId=221;
        when(projectService.getProjectsByStaffId(staffId)).thenThrow(new SQLException());
        mvc.perform(get("/project/staff/"+staffId))
                .andExpect(status().isInternalServerError())
                ;
    }

    /**
     * Test case for making a project unavailable.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void makeProjectUnavailable() throws Exception
    {
        int projectId=331;
        doNothing().when(projectService).makeProjectUnavailable(projectId);
        mvc.perform(put("/project/make-unavailable/"+projectId))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("Project marked as unavailable")))
        ;
    }

    /**
     * Test case for handling exception while making project unavailable.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void makeProjectUnavailableException() throws Exception
    {
        int projectId=331;
        doThrow(new SQLException()).when(projectService).makeProjectUnavailable(projectId);
        mvc.perform(put("/project/make-unavailable/"+projectId))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(equalTo("Failed to make project unavailable")))
        ;
    }

    /**
     * Test case for deleting a project successfully.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void deleteProjectSuccess() throws Exception {
        int projectId = 1;
        doNothing().when(projectService).deleteProject(projectId);

        mvc.perform(delete("/project/{id}", projectId))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("Project deleted successfully.")));
    }

    /**
     * Test case for handling failure during project deletion.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void deleteProjectFailure() throws Exception {
        int projectId = 1;
        doThrow(new SQLException("Error deleting project.")).when(projectService).deleteProject(projectId);

        mvc.perform(delete("/project/{id}", projectId))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(equalTo("Error deleting project.")));
    }

    /**
     * Test case for getting all projects successfully.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void getAllProjectsSuccess() throws Exception {
        List<Project> projects = List.of(new Project(1, "Project 1", "Description 1", 1));
        when(projectService.getAllProjects()).thenReturn(projects);

        mvc.perform(get("/project/all"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(projects)));
    }

    /**
     * Test case for handling no content while getting all projects.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void getAllProjectsNoContent() throws Exception {
        when(projectService.getAllProjects()).thenReturn(List.of());

        mvc.perform(get("/project/all"))
                .andExpect(status().isNoContent());
    }

    /**
     * Test case for handling failure while getting all projects.
     *
     * @throws Exception if an error occurs during the test
     */
    @Test
    void getAllProjectsFailure() throws Exception {
        when(projectService.getAllProjects()).thenThrow(new SQLException("Internal server error."));

        mvc.perform(get("/project/all"))
                .andExpect(status().isInternalServerError());
    }

}