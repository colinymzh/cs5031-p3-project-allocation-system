package com.example.cs5031p3.demo.backend.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object (DAO) class for managing project registrations in the database.
 */
@Repository
public class ProjectRegistrationsDAO {
    private final DatabaseManager databaseManager;

    @Autowired
    public ProjectRegistrationsDAO(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    /**
     * Inserts a new project registration into the database.
     *
     * @param projectId The ID of the project being registered for
     * @param studentId The ID of the student registering for the project
     * @throws SQLException if a database access error occurs
     */
    public void insertProjectRegistration(int projectId, int studentId) throws SQLException {
        String sql = "INSERT INTO project_registrations (project_id, student_id, registration_state) VALUES (?, ?, ?)";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, projectId);
            pstmt.setInt(2, studentId);
            pstmt.setInt(3, 1); // Assuming 1 represents "interested" state
            pstmt.executeUpdate();
        }
    }

    /**
     * Retrieves project registrations by student ID from the database.
     *
     * @param studentId The ID of the student
     * @return A list of project registrations associated with the student
     * @throws SQLException if a database access error occurs
     */
    public List<Map<String, Object>> findByStudentId(int studentId) throws SQLException {
        List<Map<String, Object>> registrations = new ArrayList<>();
        String sql = "SELECT pr.registration_id, pr.project_id, pr.student_id, pr.registration_state, " +
                "u.name AS student_name, p.title AS project_title, uf.name AS staff_name " +
                "FROM project_registrations pr " +
                "JOIN users u ON pr.student_id = u.user_id " +
                "JOIN projects p ON pr.project_id = p.project_id " +
                "JOIN users uf ON p.staff_id = uf.user_id " +
                "WHERE pr.student_id = ?";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> registration = new HashMap<>();
                    registration.put("registrationId", rs.getInt("registration_id"));
                    registration.put("projectId", rs.getInt("project_id"));
                    registration.put("studentId", rs.getInt("student_id"));
                    registration.put("registrationState", rs.getInt("registration_state"));
                    registration.put("studentName", rs.getString("student_name"));
                    registration.put("projectTitle", rs.getString("project_title"));
                    registration.put("staffName", rs.getString("staff_name"));
                    registrations.add(registration);
                }
            }
        }
        return registrations;
    }

    /**
     * Checks if a student is interested in a particular project.
     *
     * @param projectId The ID of the project
     * @param studentId The ID of the student
     * @return true if the student is interested in the project, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean isStudentInterestedInProject(int projectId, int studentId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM project_registrations WHERE project_id = ? AND student_id = ?";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, projectId);
            pstmt.setInt(2, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
        }
        return false;
    }

    /**
     * Finds project registrations associated with a staff member.
     *
     * @param staffId The ID of the staff member
     * @return A list of project registrations associated with the staff member
     * @throws SQLException if a database access error occurs
     */
    public List<Map<String, Object>> findRegistrationStudentsByStaffId(int staffId) throws SQLException {
        List<Map<String, Object>> interestedStudents = new ArrayList<>();
        String sql = "SELECT pr.registration_id, pr.project_id, pr.student_id, pr.registration_state, " +
                "u.name AS student_name, p.title AS project_title, uf.name AS staff_name " +
                "FROM project_registrations pr " +
                "JOIN users u ON pr.student_id = u.user_id " +
                "JOIN projects p ON pr.project_id = p.project_id " +
                "JOIN users uf ON p.staff_id = uf.user_id " +
                "WHERE p.staff_id = ?";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, staffId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> interestedStudent = new HashMap<>();
                    interestedStudent.put("registrationId", rs.getInt("registration_id"));
                    interestedStudent.put("projectId", rs.getInt("project_id"));
                    interestedStudent.put("studentId", rs.getInt("student_id"));
                    interestedStudent.put("studentName", rs.getString("student_name"));
                    interestedStudent.put("projectTitle", rs.getString("project_title"));
                    interestedStudent.put("staffName", rs.getString("staff_name"));
                    interestedStudent.put("registrationState", rs.getInt("registration_state"));
                    interestedStudents.add(interestedStudent);
                }
            }
        }
        return interestedStudents;
    }

    /**
     * Checks if a student is assigned to any project.
     *
     * @param studentId The ID of the student
     * @return true if the student is assigned to a project, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean isStudentAssignedToProject(int studentId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM project_registrations WHERE student_id = ? AND registration_state = 2";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count > 0;
                }
            }
        }
        return false;
    }

    /**
     * Deletes interested project registrations for a student, excluding the current registration.
     *
     * @param studentId           The ID of the student
     * @param currentRegistrationId The ID of the current registration to exclude
     * @throws SQLException if a database access error occurs
     */
    public void deleteInterestedRegistrationsByStudentId(int studentId, int currentRegistrationId) throws SQLException {
        String sql = "DELETE FROM project_registrations " +
                "WHERE student_id = ? AND registration_state = 1 AND registration_id != ?";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, studentId);
            pstmt.setInt(2, currentRegistrationId);
            pstmt.executeUpdate();
        }
    }

    /**
     * Retrieves a project registration by its ID.
     *
     * @param registrationId The ID of the registration
     * @return A map containing registration details if found, otherwise null
     * @throws SQLException if a database access error occurs
     */
    public Map<String, Object> getRegistrationById(int registrationId) throws SQLException {
        String sql = "SELECT * FROM project_registrations WHERE registration_id = ?";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, registrationId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> registration = new HashMap<>();
                    registration.put("registrationId", rs.getInt("registration_id"));
                    registration.put("projectId", rs.getInt("project_id"));
                    registration.put("studentId", rs.getInt("student_id"));
                    registration.put("registrationState", rs.getInt("registration_state"));
                    return registration;
                }
            }
        }
        return null;
    }

    /**
     * Updates the state of a project registration.
     *
     * @param registrationId The ID of the registration to update
     * @param state          The new state to set for the registration
     * @throws SQLException if a database access error occurs
     */
    public void updateRegistrationState(int registrationId, int state) throws SQLException {
        String sql = "UPDATE project_registrations SET registration_state = ? WHERE registration_id = ?";
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, state);
            pstmt.setInt(2, registrationId);
            pstmt.executeUpdate();
        }
    }
}
