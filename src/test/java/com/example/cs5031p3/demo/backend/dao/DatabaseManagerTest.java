package com.example.cs5031p3.demo.backend.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * The DatabaseManagerTest class contains unit tests for the DatabaseManager class.
 */
class DatabaseManagerTest {

    private EmbeddedDatabase dataSource;
    private DatabaseManager databaseManager;

    /**
     * Sets up the test environment before each test method.
     */
    @BeforeEach
    void setUp() {
        dataSource = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .build();

        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement()) {
            stmt.execute("DROP ALL OBJECTS DELETE FILES");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        databaseManager = new DatabaseManager(dataSource);
    }

    /**
     * Tests whether the required tables are created in the database.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void testTablesCreated() throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SHOW TABLES;");
            List<String> expectedTables = Arrays.asList("users", "projects", "project_registrations");
            List<String> actualTables = new ArrayList<>();

            while (rs.next()) {
                actualTables.add(rs.getString(1).toLowerCase());
            }

            assertTrue(actualTables.containsAll(expectedTables),
                    "All expected tables should exist: " + expectedTables);
            assertEquals(expectedTables.size(), actualTables.size(),
                    "There should be exactly " + expectedTables.size() + " tables");
        }
    }

    /**
     * Tests whether the sample data is inserted into the database correctly.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void testSampleDataInserted() throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users;");
            assertTrue(rs.next(), "There should be user records");
            assertEquals(4, rs.getInt(1), "There should be 4 user records");

            rs = stmt.executeQuery("SELECT COUNT(*) FROM projects;");
            assertTrue(rs.next(), "There should be project records");
            assertEquals(4, rs.getInt(1), "There should be 4 project records");

            rs = stmt.executeQuery("SELECT COUNT(*) FROM project_registrations;");
            assertTrue(rs.next(), "There should be project registration records");
            assertEquals(3, rs.getInt(1), "There should be 3 project registration records");
        }
    }

    /**
     * Tests the getConnection() method to ensure that it returns a valid connection.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void testGetConnection() throws SQLException {
        Connection connection = databaseManager.getConnection();
        assertNotNull(connection, "Connection should not be null");
        assertFalse(connection.isClosed(), "Connection should be open");
    }

    /**
     * Tests whether SQLException is handled properly in the createTable() method.
     *
     * @throws SQLException if a database access error occurs
     */
    @Test
    void testCreateTableSQLExceptionHandling() throws SQLException {
        // Mock DataSource and Connection objects
        DataSource dataSource = mock(DataSource.class);
        Connection connection = mock(Connection.class);

        // Mocking Statement object
        Statement statement = mock(Statement.class);
        doThrow(new SQLException("Mocked SQLException")).when(statement).execute(any());

        // Mock getConnection() method of DataSource to return the mocked Connection object
        Mockito.when(dataSource.getConnection()).thenReturn(connection);

        // Mock createStatement() method of Connection to return the mocked Statement object
        when(connection.createStatement()).thenReturn(statement);

        // Create DatabaseManager instance with mocked DataSource
        DatabaseManager databaseManager = new DatabaseManager(dataSource);

        // Call createTable() method and verify that SQLException is caught and handled
        databaseManager.createTable();
    }
}