package com.example.cs5031p3.demo.backend.model;


import java.util.Objects;

/**
 * Represents a user entity.
 */
public class User {

    private Integer id;
    private String name;
    private String username;
    private String password;
    private Integer typeId;

    /**
     * Default constructor for User.
     */
    public User() {
    }

    /**
     * Parameterized constructor for User.
     * @param id The ID of the user.
     * @param name The name of the user.
     * @param username The username of the user.
     * @param password The password of the user.
     * @param typeId The type identifier of the user.
     */
    public User(Integer id, String name, String username, String password, Integer typeId) {
        this.id = id;
        this.username = username;
        this.name = name;
        this.password = password;
        this.typeId = typeId;
    }

    /**
     * Compares this user with the specified object for equality.
     * Two users are considered equal if they have the same ID, username, and type ID.
     * @param other The object to compare with this user.
     * @return true if the specified object is equal to this user, false otherwise.
     */
    @Override
    public boolean equals(Object other) {
        if(this == other) {
            return true;
        }
        if(null == other) {
            return false;
        }
        if (!(other instanceof User otherUser)) return false;
        else
        {
            return (otherUser.id.equals(this.id)
                    && otherUser.username.equals(this.username)
                    && otherUser.typeId.equals(this.typeId)
            );
        }
    }

    /**
     * Generates a hash code for this user based on its ID, username, and type ID.
     * @return The hash code value for this user.
     */
    //ref: https://www.baeldung.com/java-override-hashcode-equals-records
    @Override
    public int hashCode() {
        return Objects.hash(id, username, typeId);
    }

    //Getter and Setter
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }
}
