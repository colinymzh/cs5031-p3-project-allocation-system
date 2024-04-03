package com.example.cs5031p3.demo.backend.modelTest;

import com.example.cs5031p3.demo.backend.model.Project;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Project class.
 */
class ProjectTest {
    Project project1;
    Project project2;
    Project projectDifferentId;
    Project projectDifferentStaffId;

    /**
     * Sets up the test environment by initializing project objects with different attributes.
     */
    @BeforeEach
    void setUp() {
        int projectId = 100;
        String title = "title";
        String description = "description";
        int staffId = 10;
        // Initialize two projects with the same ID and staffId, but different titles and descriptions
        project1 = new Project(projectId, title, description, staffId);
        project2 = new Project(projectId, title + "mod", description + "mod", staffId);

        // Initialize projects with different IDs and staffIds for additional tests
        projectDifferentId = new Project(101, title, description, staffId);
        projectDifferentStaffId = new Project(projectId, title, description, 11);
    }

    /**
     * Test case to verify equality between projects with the same attributes.
     */
    @Test
    void testEqualsWithSameAttributes() {
        // Tests that two projects with the same ID and staffId are considered equal, even if other attributes differ
        assertEquals(project1, project2, "Projects with the same ID and staffId should be equal");
    }

    /**
     * Test case to verify inequality between projects with different IDs.
     */
    @Test
    void testNotEqualsWithDifferentId() {
        // Tests that two projects are not considered equal if their IDs differ
        assertNotEquals(project1, projectDifferentId, "Projects with different IDs should not be equal");
    }

    /**
     * Test case to verify inequality between projects with different staff IDs.
     */
    @Test
    void testNotEqualsWithDifferentStaffId() {
        // Tests that two projects are not considered equal if their staffIds differ
        assertNotEquals(project1, projectDifferentStaffId, "Projects with different staffIds should not be equal");
    }

    /**
     * Test case to verify the hash code of projects.
     */
    @Test
    void testHashCode() {
        // Tests that two projects considered equal have the same hash code
        assertEquals(project1.hashCode(), project2.hashCode(), "Equal projects should have the same hash code");
    }

    /**
     * Test case to verify inequality between a project and null.
     */
    @Test
    void testEqualsWithNull() {
        // Tests that a project is not considered equal to null
        assertNotNull(project1, "A project should not be equal to null.");
        assertFalse(project1.equals(null), "A project compared to null should return false.");
    }

    /**
     * Test case to verify inequality between a project and an object of a different class.
     */
    @Test
    void testEqualsWithDifferentClassObject() {
        // Tests that a project is not considered equal to an object of a different class
        Object otherObject = new Object();
        assertFalse(project1.equals(otherObject), "A project compared to an object of a different class should return false.");
    }
}