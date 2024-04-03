package com.example.cs5031p3.demo.backend.controller;

import com.example.cs5031p3.demo.backend.service.RegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;


/**
 * Controller class for handling HTTP requests related to project registrations.
 */
@RestController
@CrossOrigin
@RequestMapping("/registration")
public class RegistrationController {
    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    /**
     * Endpoint for creating a project registration.
     *
     * @param requestBody The request body containing project ID and student ID
     * @return ResponseEntity indicating success or failure of the operation
     */
    @PostMapping("/create")
    public ResponseEntity<String> createProjectRegistration(@RequestBody Map<String, Integer> requestBody) {
        int projectId = requestBody.get("projectId");
        int studentId = requestBody.get("studentId");
        try {
            registrationService.createProjectRegistration(projectId, studentId);
            return ResponseEntity.ok("Project registration created successfully");
        } catch (SQLException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Endpoint for retrieving project registrations by student ID.
     *
     * @param studentId The ID of the student
     * @return ResponseEntity containing project registrations if available, or an error message if not
     */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<Object> getProjectRegistrationsByStudentId(@PathVariable int studentId){
        try {
            return ResponseEntity.ok(
                    registrationService.getProjectRegistrationsByStudentId(studentId));
        } catch (SQLException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Endpoint for retrieving registration students by staff ID.
     *
     * @param staffId The ID of the staff member
     * @return ResponseEntity containing registration students if available, or an error message if not
     */
    @GetMapping("/students-registration/{staffId}")
    public ResponseEntity<Object> getRegistrationStudentsByStaffId(@PathVariable int staffId){
        try {
            return ResponseEntity.ok(registrationService.findRegistrationStudentsByStaffId(staffId));
        } catch (SQLException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Endpoint for assigning a project registration.
     *
     * @param registrationId The ID of the registration to assign
     * @return ResponseEntity indicating success or failure of the operation
     */
    @PutMapping("/assign/{registrationId}")
    public ResponseEntity<String> assignRegistration(@PathVariable int registrationId) {
        try {
            boolean approved = registrationService.assignRegistration(registrationId);
            if (approved) {
                return ResponseEntity.ok("Registration approved successfully");
            } else {
                return ResponseEntity.badRequest().body("Failed to approve registration");
            }
        } catch (SQLException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Endpoint for checking if a student is assigned to any project.
     *
     * @param studentId The ID of the student
     * @return ResponseEntity indicating whether the student is assigned to a project or not
     */
    @GetMapping("/student/{studentId}/assigned")
    public ResponseEntity<Boolean> isStudentAssignedToProject(@PathVariable int studentId) {
        try {
            boolean isAssigned = registrationService.isStudentAssignedToProject(studentId);
            return ResponseEntity.ok(isAssigned);
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
