package com.example.cs5031p3.demo.backend.controller;

import com.example.cs5031p3.demo.backend.model.Project;
import com.example.cs5031p3.demo.backend.model.User;
import com.example.cs5031p3.demo.backend.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Controller class for handling HTTP requests related to projects.
 */
@RestController
@CrossOrigin
@RequestMapping("/project")
public class ProjectController {
    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * Endpoint for creating a new project.
     *
     * @param project The project to be created
     * @return ResponseEntity indicating success or failure of the operation
     */
    @PostMapping("/create")
    public ResponseEntity<String> createProject(@RequestBody Project project){
        try {
            projectService.createProject(project);
            return ResponseEntity.ok("The new project is created");
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    /**
     * Endpoint for retrieving a project by its ID.
     *
     * @param id The ID of the project to retrieve
     * @return ResponseEntity containing the project if found, or an error message if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getProject(@PathVariable int id){
        try {
            Project project=projectService.getProjectById(id);
            if(project==null) throw new SQLException("No such project");
            return ResponseEntity.ok(project);
        } catch (SQLException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Endpoint for updating an existing project.
     *
     * @param project The project with updated information
     * @return ResponseEntity indicating success or failure of the operation
     */
    @PutMapping("/")
    public ResponseEntity<?> updateProject(@RequestBody Project project) {
        try {
            projectService.updateProject(project);
            return ResponseEntity.ok().body("Project updated successfully.");
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body("Error updating project.");
        }
    }

    /**
     * Endpoint for deleting a project by its ID.
     *
     * @param id The ID of the project to delete
     * @return ResponseEntity indicating success or failure of the operation
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable int id) {
        try {
            projectService.deleteProject(id);
            return ResponseEntity.ok().body("Project deleted successfully.");
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().body("Error deleting project.");
        }
    }

    /**
     * Endpoint for retrieving all projects.
     *
     * @return ResponseEntity containing a list of projects if available, or an error message if not
     */
    @GetMapping("/all")
    public ResponseEntity<List<Project>> getAllProjects() {
        try {
            List<Project> projects = projectService.getAllProjects();
            if (projects.isEmpty()) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.ok(projects);
            }
        } catch (SQLException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Endpoint for retrieving projects by staff ID.
     *
     * @param staffId The ID of the staff member
     * @return ResponseEntity containing a list of projects if available, or an error message if not
     */
    @GetMapping("/staff/{staffId}")
    public ResponseEntity<Object> getProjectsByStaffId(@PathVariable int staffId) {
        try {
            List<Project> projects = projectService.getProjectsByStaffId(staffId);
            return ResponseEntity.ok(projects);
        } catch (Exception e) {
            if((Objects.equals(e.getMessage(), "The user is not a staff"))
                    ||(Objects.equals(e.getMessage(), "The user doesn't exist")))
                return ResponseEntity.badRequest().body(e.getMessage());
            else return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Endpoint for marking a project as unavailable.
     *
     * @param projectId The ID of the project to mark as unavailable
     * @return ResponseEntity indicating success or failure of the operation
     */
    @PutMapping("/make-unavailable/{projectId}")
    public ResponseEntity<String> makeProjectUnavailable(@PathVariable int projectId) {
        try {
            projectService.makeProjectUnavailable(projectId);
            return ResponseEntity.ok("Project marked as unavailable");
        } catch (SQLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to make project unavailable");
        }
    }
}
