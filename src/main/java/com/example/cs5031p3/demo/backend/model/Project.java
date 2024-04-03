package com.example.cs5031p3.demo.backend.model;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Represents a project entity.
 */
public class Project {


    private Integer id;
    private String title;
    private String description;
    private Integer staffId;
    private Integer available = 1;
    private String staffName;

    /**
     * Default constructor for Project.
     */
    public Project() {
    }

    /**
     * Parameterized constructor for Project.
     * @param projectId The ID of the project.
     * @param title The title of the project.
     * @param description The description of the project.
     * @param staffId The ID of the staff associated with the project.
     */
    public Project(int projectId, String title, String description, int staffId) {
        this.id = projectId;
        this.title = title;
        this.description = description;
        this.staffId = staffId;
    }

    /**
     * Compares this project with the specified object for equality.
     * Two projects are considered equal if they have the same ID and staff ID.
     * @param other The object to compare with this project.
     * @return true if the specified object is equal to this project, false otherwise.
     */
    @Override
    public boolean equals(Object other) {
        if(this == other) {
            return true;
        }
        if(null == other) {
            return false;
        }
        if (!(other instanceof Project otherProject)) return false;
        else
        {
            return (otherProject.id.equals(this.id)
                    && otherProject.staffId.equals(this.staffId)
            );
        }
    }

    /**
     * Generates a hash code for this project based on its ID and staff ID.
     * @return The hash code value for this project.
     */
    //ref: https://www.baeldung.com/java-override-hashcode-equals-records
    @Override
    public int hashCode() {
        return Objects.hash(id,staffId);
    }

    //Getter and Setter
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStaffId() {
        return staffId;
    }

    public void setStaffId(Integer staffId) {
        this.staffId = staffId;
    }


    public Integer getAvailable() {
        return available;
    }

    public void setAvailable(Integer available) {
        this.available = available;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }
}
