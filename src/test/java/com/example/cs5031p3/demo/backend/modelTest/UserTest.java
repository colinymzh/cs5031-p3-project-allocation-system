package com.example.cs5031p3.demo.backend.modelTest;

import com.example.cs5031p3.demo.backend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the User class.
 */
class UserTest {
    User user1;
    User user2;
    User differentUser;
    User sameAttributesUser;

    /**
     * Sets up the test environment by initializing user objects with different attributes.
     */
    @BeforeEach
    void setUp() {
        // Initialize two identical User objects
        int id = 1;
        String name = "Student John Doe";
        String username = "20240001";
        String password = "password";
        int typeId = 1;
        user1 = new User(id, name, username, password, typeId);
        user2 = new User(id, name, username, password, typeId);

        // Initialize a User with different attributes
        differentUser = new User(2, "Staff Jane Doe", "20240002", "diffPassword", 2);

        // Initialize a User with same ID, username, and typeId, but different name and password
        sameAttributesUser = new User(id, "Different Name", username, "Different Password", typeId);
    }

    /**
     * Test case to verify equality with itself.
     */
    @Test
    void testEqualsWithSelf() {
        // A user should be equal to itself
        assertTrue(user1.equals(user1));
    }

    /**
     * Test case to verify inequality with null.
     */
    @Test
    void testEqualsWithNull() {
        // A user should not be equal to null
        assertFalse(user1.equals(null));
    }

    /**
     * Test case to verify inequality with an object of a different class.
     */
    @Test
    void testEqualsWithDifferentClass() {
        // A user should not be equal to an object of a different class
        assertFalse(user1.equals(new Object()));
    }

    /**
     * Test case to verify inequality with a user with different attributes.
     */
    @Test
    void testEqualsWithDifferentUser() {
        // Two users with different attributes should not be considered equal
        assertFalse(user1.equals(differentUser));
    }

    /**
     * Test case to verify equality with a user having the same attributes.
     */
    @Test
    void testEqualsWithSameAttributes() {
        // Two users with the same attributes should be considered equal
        assertTrue(user1.equals(user2));
    }

    /**
     * Test case to verify equality with a user having the same relevant attributes.
     */
    @Test
    void testEqualsIgnoringNonRelevantFields() {
        // Only id, username, and typeId fields are considered in equals method
        assertTrue(user1.equals(sameAttributesUser));
    }

    /**
     * Test case to verify the hash code of users.
     */
    @Test
    void testHashCode() {
        // Hash code should be equal for two objects that are considered equal
        assertEquals(user1.hashCode(), user2.hashCode());
        // Considering the equals implementation, user1 and sameAttributesUser are considered equal, thus should have the same hashCode
        assertEquals(user1.hashCode(), sameAttributesUser.hashCode());
    }


}