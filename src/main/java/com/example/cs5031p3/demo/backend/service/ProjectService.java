package com.example.cs5031p3.demo.backend.service;

import com.example.cs5031p3.demo.backend.dao.ProjectDAO;
import com.example.cs5031p3.demo.backend.dao.UserDAO;
import com.example.cs5031p3.demo.backend.enums.TypeEnum;
import com.example.cs5031p3.demo.backend.model.Project;
import com.example.cs5031p3.demo.backend.model.User;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

/**
 * Service class for handling project-related operations.
 */
@Service
public class ProjectService {
    private final ProjectDAO projectDAO;
    private final UserDAO userDAO;

    /**
     * Constructs a ProjectService with the specified ProjectDAO and UserDAO.
     * @param projectDAO The Data Access Object (DAO) for project entities.
     * @param userDAO The Data Access Object (DAO) for user entities.
     */
    public ProjectService(ProjectDAO projectDAO, UserDAO userDAO) {
        this.projectDAO = projectDAO;
        this.userDAO = userDAO;
    }

    /**
     * Retrieves a project by its ID.
     * @param id The ID of the project to retrieve.
     * @return The project with the specified ID.
     * @throws SQLException if a database access error occurs.
     */
    public Project getProjectById(int id) throws SQLException {
        return projectDAO.getProjectById(id);
    }

    /**
     * Creates a new project.
     * @param project The project to create.
     * @throws SQLException if a database access error occurs.
     */
    public void createProject(Project project) throws SQLException {
        projectDAO.createProject(project);
    }

    /**
     * Updates an existing project.
     * @param project The project to update.
     * @throws SQLException if a database access error occurs.
     */
    public void updateProject(Project project) throws SQLException {
        projectDAO.updateProject(project);
    }

    /**
     * Deletes a project by its ID.
     * @param id The ID of the project to delete.
     * @throws SQLException if a database access error occurs.
     */
    public void deleteProject(int id) throws SQLException {
        projectDAO.deleteProject(id);
    }

    /**
     * Retrieves all projects.
     * @return A list of all projects.
     * @throws SQLException if a database access error occurs.
     */
    public List<Project> getAllProjects() throws SQLException {
        return projectDAO.getAllProjects();
    }

    /**
     * Retrieves projects associated with a staff member.
     * @param staffId The ID of the staff member.
     * @return A list of projects associated with the staff member.
     * @throws SQLException if a database access error occurs or the user is not a staff member.
     */
    public List<Project> getProjectsByStaffId(int staffId) throws SQLException {
        if(isStaff(staffId))
        {
            return projectDAO.findProjectsByStaffId(staffId);
        }
        else throw new SQLException("The user is not a staff");
    }

    /**
     * Marks a project as unavailable.
     * @param projectId The ID of the project to make unavailable.
     * @throws SQLException if a database access error occurs.
     */
    public void makeProjectUnavailable(int projectId) throws SQLException {
        projectDAO.makeProjectUnavailable(projectId);
    }

    /**
     * Checks if a user is a staff member.
     * @param id The ID of the user to check.
     * @return true if the user is a staff member, false otherwise.
     * @throws SQLException if a database access error occurs or the user does not exist.
     */
    public boolean isStaff(int id) throws SQLException {
        User user=userDAO.getUserById(id);
        if(user==null) throw new SQLException("The user doesn't exist");
        return user.getTypeId().equals(TypeEnum.STAFF.getCode());
    }
}
