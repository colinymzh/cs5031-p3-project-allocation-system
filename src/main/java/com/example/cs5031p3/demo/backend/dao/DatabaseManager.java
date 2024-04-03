package com.example.cs5031p3.demo.backend.dao;

import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * The DatabaseManager class is responsible for managing the database, including table creation and sample data insertion.
 */
@Component
public class DatabaseManager {

    private final DataSource dataSource;

    /**
     * Constructs a DatabaseManager with the provided DataSource and initializes the database by creating tables and inserting sample data.
     * @param dataSource The DataSource for connecting to the database
     */
    public DatabaseManager(DataSource dataSource) {
        this.dataSource = dataSource;
        createTable();
        insertSampleData();
    }

    /**
     * Creates tables in the database if they do not exist already.
     */
    public void createTable() {
        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "    user_id INT AUTO_INCREMENT PRIMARY KEY," +
                    "    name VARCHAR(255)," +
                    "    username VARCHAR(255) NOT NULL," +
                    "    password VARCHAR(255) NOT NULL," +
                    "    type_id INT" +
                    ");");
            stmt.execute("CREATE TABLE IF NOT EXISTS projects (" +
                    "    project_id INT AUTO_INCREMENT PRIMARY KEY," +
                    "    title VARCHAR(255) NOT NULL," +
                    "    description TEXT," +
                    "    staff_id INT," +
                    "    available INT," +
                    "    FOREIGN KEY (staff_id) REFERENCES users(user_id)" +
                    ");");
             stmt.execute("CREATE TABLE IF NOT EXISTS project_registrations (" +
                          "registration_id INT AUTO_INCREMENT PRIMARY KEY," +
                          "project_id INT," +
                          "student_id INT," +
                          "registration_state INT NOT NULL," +//这里我先用1和2代表interested和assigned
                          "FOREIGN KEY (project_id) REFERENCES projects(project_id)," +
                          "FOREIGN KEY (student_id) REFERENCES users(user_id));");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves a connection to the database.
     * @return Connection to the database
     * @throws SQLException if a database access error occurs
     */
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Inserts sample data into the database tables.
     */
    public void insertSampleData() {
        try (Connection connection = dataSource.getConnection();
             Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO users (name, username, password, type_id) VALUES ('Student John Doe', '20240001', 'password', 1);");
            stmt.execute("INSERT INTO users (name, username, password, type_id) VALUES ('Staff Jim Beam', '20240002', 'password', 2);");
            stmt.execute("INSERT INTO users (name, username, password, type_id) VALUES ('Student Jill Hill', '20240003', 'password', 1);");
            stmt.execute("INSERT INTO users (name, username, password, type_id) VALUES ('Staff Jack Black', '20240004', 'password', 2);");
            stmt.execute("INSERT INTO projects (title, description, staff_id, available) VALUES ('Responsive Website Development', 'Develop a responsive website for a local charity, focusing on mobile and desktop compatibility.', 2, 1);");
            stmt.execute("INSERT INTO projects (title, description, staff_id, available) VALUES ('Machine Learning for Predictive Analysis', 'Create a machine learning model to predict customer churn based on historical data.', 2, 1);");
            stmt.execute("INSERT INTO projects (title, description, staff_id, available) VALUES ('IoT Home Automation System', 'Develop an IoT system that allows users to control home appliances remotely via a web interface.', 4, 1);");
            stmt.execute("INSERT INTO projects (title, description, staff_id, available) VALUES ('Health Monitoring Mobile App', 'Develop a mobile application that tracks and provides insights on users'' health metrics.', 4, 1);");
            stmt.execute("INSERT INTO project_registrations (project_id, student_id, registration_state) VALUES (1, 1, 1);");
            stmt.execute("INSERT INTO project_registrations (project_id, student_id, registration_state) VALUES (2, 1, 1);");
            stmt.execute("INSERT INTO project_registrations (project_id, student_id, registration_state) VALUES (2, 3, 1);");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
