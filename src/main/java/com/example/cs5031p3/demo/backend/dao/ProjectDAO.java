package com.example.cs5031p3.demo.backend.dao;

import com.example.cs5031p3.demo.backend.model.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * The ProjectDAO class provides methods to interact with the database for Project-related operations.
 */
@Repository
public class ProjectDAO {
    private final DatabaseManager databaseManager;

    /**
     * Constructs a ProjectDAO with the provided DatabaseManager.
     * @param databaseManager The DatabaseManager for database interaction
     */
    @Autowired
    public ProjectDAO(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    /**
     * Creates a new project in the database.
     * @param project The project to be created
     * @throws SQLException if a database access error occurs
     */
    public void createProject(Project project) throws SQLException {
        String sql = "INSERT INTO projects (title, description, staff_id, available) VALUES (?, ?, ?, ?)";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, project.getTitle());
            pstmt.setString(2, project.getDescription());
            pstmt.setInt(3, project.getStaffId());
            pstmt.setInt(4, project.getAvailable());
            pstmt.executeUpdate();
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    project.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    /**
     * Retrieves a project from the database by its ID.
     * @param projectId The ID of the project to retrieve
     * @return The retrieved project, or null if not found
     * @throws SQLException if a database access error occurs
     */
    public Project getProjectById(int projectId) throws SQLException {
        String sql = "SELECT p.project_id, p.title, p.description, p.staff_id, p.available, u.name as staff_name " +
                "FROM projects p " +
                "INNER JOIN users u ON p.staff_id = u.user_id " +
                "WHERE project_id = ?";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, projectId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Project project = new Project();
                    project.setId(rs.getInt("project_id"));
                    project.setTitle(rs.getString("title"));
                    project.setDescription(rs.getString("description"));
                    project.setStaffId(rs.getInt("staff_id"));
                    project.setAvailable(rs.getInt("available"));
                    project.setStaffName(rs.getString("staff_name"));
                    return project;
                }
            }
        }
        return null;
    }

    /**
     * Updates the details of a project in the database.
     *
     * @param project The Project object containing updated information
     * @throws SQLException if a database access error occurs
     */
    public void updateProject(Project project) throws SQLException {
        String sql = "UPDATE projects SET title = ?, description = ?, staff_id = ?, available = ? WHERE project_id = ?";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, project.getTitle());
            pstmt.setString(2, project.getDescription());
            pstmt.setInt(3, project.getStaffId());
            pstmt.setInt(4, project.getAvailable());
            pstmt.setInt(5, project.getId());
            pstmt.executeUpdate();
        }
    }

    /**
     * Deletes a project from the database by its ID.
     *
     * @param projectId The ID of the project to delete
     * @throws SQLException if a database access error occurs
     */
    public void deleteProject(int projectId) throws SQLException {
        String sql = "DELETE FROM projects WHERE project_id = ?";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, projectId);
            pstmt.executeUpdate();
        }
    }

    /**
     * Retrieves all projects from the database.
     *
     * @return A list of all projects in the database
     * @throws SQLException if a database access error occurs
     */
    public List<Project> getAllProjects() throws SQLException {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT p.project_id, p.title, p.description, p.staff_id, u.name AS staff_name, p.available " +
                "FROM projects p " +
                "JOIN users u ON p.staff_id = u.user_id";

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Project project = new Project();
                    project.setId(rs.getInt("project_id"));
                    project.setTitle(rs.getString("title"));
                    project.setDescription(rs.getString("description"));
                    project.setStaffId(rs.getInt("staff_id"));
                    project.setStaffName(rs.getString("staff_name"));
                    project.setAvailable(rs.getInt("available"));
                    projects.add(project);
                }
            }
        }
        return projects;
    }

    /**
     * Retrieves all projects associated with a staff member from the database.
     *
     * @param staffId The ID of the staff member
     * @return A list of projects associated with the staff member
     * @throws SQLException if a database access error occurs
     */
    public List<Project> findProjectsByStaffId(int staffId) throws SQLException {
        List<Project> projects = new ArrayList<>();
        String sql = "SELECT p.project_id, p.title, p.description, p.staff_id, u.name AS staff_name, p.available " +
                "FROM projects p " +
                "JOIN users u ON p.staff_id = u.user_id " +
                "WHERE p.staff_id = ?";

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, staffId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Project project = new Project();
                    project.setId(rs.getInt("project_id"));
                    project.setTitle(rs.getString("title"));
                    project.setDescription(rs.getString("description"));
                    project.setStaffId(rs.getInt("staff_id"));
                    project.setStaffName(rs.getString("staff_name"));
                    project.setAvailable(rs.getInt("available"));
                    projects.add(project);
                }
            }
        }
        return projects;
    }

    /**
     * Marks a project as unavailable in the database.
     *
     * @param projectId The ID of the project to mark as unavailable
     * @throws SQLException if a database access error occurs
     */
    public void makeProjectUnavailable(int projectId) throws SQLException {
        String sql = "UPDATE projects SET available = 2 WHERE project_id = ?";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, projectId);
            pstmt.executeUpdate();
        }
    }
}

