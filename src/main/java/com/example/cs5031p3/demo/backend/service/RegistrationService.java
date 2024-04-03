package com.example.cs5031p3.demo.backend.service;

import com.example.cs5031p3.demo.backend.dao.UserDAO;
import com.example.cs5031p3.demo.backend.enums.RegistrationStateEnum;
import com.example.cs5031p3.demo.backend.enums.TypeEnum;
import com.example.cs5031p3.demo.backend.model.User;
import org.springframework.stereotype.Service;
import com.example.cs5031p3.demo.backend.dao.ProjectRegistrationsDAO;

import java.sql.SQLException;
import java.util.Map;
import java.util.List;

/**
 * Service class for handling project registration-related operations.
 */
@Service
public class RegistrationService {
    private final ProjectRegistrationsDAO projectRegistrationsDAO;
    private final UserDAO userDAO;

    /**
     * Constructs a RegistrationService with the specified ProjectRegistrationsDAO and UserDAO.
     * @param projectRegistrationsDAO The Data Access Object (DAO) for project registrations.
     * @param userDAO The Data Access Object (DAO) for user entities.
     */
    public RegistrationService(ProjectRegistrationsDAO projectRegistrationsDAO, UserDAO userDAO) {
        this.projectRegistrationsDAO = projectRegistrationsDAO;
        this.userDAO = userDAO;
    }

    /**
     * Creates a project registration for a student.
     * @param projectId The ID of the project to register for.
     * @param studentId The ID of the student registering for the project.
     * @throws SQLException if a database access error occurs.
     */
    public void createProjectRegistration(int projectId, int studentId) throws SQLException {
        if (projectRegistrationsDAO.isStudentAssignedToProject(studentId)) {
            throw new SQLException("Student is already assigned to a project");
        }

        if (!projectRegistrationsDAO.isStudentInterestedInProject(projectId, studentId)) {
            projectRegistrationsDAO.insertProjectRegistration(projectId, studentId);
        } else {
            throw new SQLException("Student is already interested in this project");
        }
    }

    /**
     * Retrieves project registrations for a student.
     * @param studentId The ID of the student.
     * @return A list of project registrations for the student.
     * @throws SQLException if a database access error occurs or the user is not a student.
     */
    public List<Map<String, Object>> getProjectRegistrationsByStudentId(int studentId) throws SQLException {
        if(isStudent(studentId)) {
            return projectRegistrationsDAO.findByStudentId(studentId);
        }
        else {
            throw new SQLException("The user is not a student");
        }
    }

    /**
     * Finds project registrations associated with a staff member.
     * @param staffId The ID of the staff member.
     * @return A list of project registrations associated with the staff member.
     * @throws SQLException if a database access error occurs or the user is not a staff member.
     */
    public List<Map<String, Object>> findRegistrationStudentsByStaffId(int staffId) throws SQLException {
        if (isStaff(staffId)) {
            return projectRegistrationsDAO.findRegistrationStudentsByStaffId(staffId);
        }
        else {
            throw new SQLException("The user is not a staff");
        }
    }

    /**
     * Assigns a project registration to a student.
     * @param registrationId The ID of the project registration to assign.
     * @return true if the assignment is successful, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    public boolean assignRegistration(int registrationId) throws SQLException {
        Map<String, Object> registration = projectRegistrationsDAO.getRegistrationById(registrationId);

        if (registration == null) {
            return false;
        }

        int studentId = (int) registration.get("studentId");

        projectRegistrationsDAO.deleteInterestedRegistrationsByStudentId(studentId, registrationId);

        projectRegistrationsDAO.updateRegistrationState(registrationId, RegistrationStateEnum.SUCCESS.getCode());

        return true;
    }

    /**
     * Checks if a student is already assigned to a project.
     * @param studentId The ID of the student.
     * @return true if the student is already assigned to a project, false otherwise.
     * @throws SQLException if a database access error occurs.
     */
    public boolean isStudentAssignedToProject(int studentId) throws SQLException {
        return projectRegistrationsDAO.isStudentAssignedToProject(studentId);
    }

    /**
     * Checks if a user is a student.
     * @param id The ID of the user.
     * @return true if the user is a student, false otherwise.
     * @throws SQLException if a database access error occurs or the user does not exist.
     */
    public boolean isStudent(int id) throws SQLException {
        User user=userDAO.getUserById(id);
        if(user==null) throw new SQLException("The user doesn't exist");
        return user.getTypeId().equals(TypeEnum.STUDENT.getCode());
    }

    /**
     * Checks if a user is a staff member.
     * @param id The ID of the user.
     * @return true if the user is a staff member, false otherwise.
     * @throws SQLException if a database access error occurs or the user does not exist.
     */
    public boolean isStaff(int id) throws SQLException {
        User user=userDAO.getUserById(id);
        if(user==null) throw new SQLException("The user doesn't exist");
        return user.getTypeId().equals(TypeEnum.STAFF.getCode());
    }
}
