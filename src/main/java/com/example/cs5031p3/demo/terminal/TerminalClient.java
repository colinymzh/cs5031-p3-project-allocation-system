/**
 * The TerminalClient class provides functionality for interacting with a project allocation system
 * through a terminal interface.
 */
package com.example.cs5031p3.demo.terminal;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.example.cs5031p3.demo.backend.dto.ResponseDTO;
import com.example.cs5031p3.demo.backend.model.Project;
import com.example.cs5031p3.demo.backend.model.User;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

public class TerminalClient {

    private final HttpClient httpClient = HttpClient.newBuilder().build();
    private final Scanner scanner = new Scanner(System.in);
    private Integer userType;
    private String loggedInUsername;
    private Integer loggedInUserId;

    /**
     * Attempts to log in the user by prompting for username, password, and type ID.
     * @return true if login is successful, false otherwise
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public boolean login() throws IOException, InterruptedException {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        loggedInUsername = username;
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        Integer typeId = null;
        while (typeId == null) {
            System.out.print("Enter type ID (1 for Student, 2 for Staff): ");
            String typeIdInput = scanner.nextLine();

            try {
                typeId = Integer.parseInt(typeIdInput);
                if (typeId != 1 && typeId != 2) {
                    System.out.println("Invalid type ID. Please enter 1 for Student or 2 for Staff.");
                    typeId = null;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid integer.");
            }
        }

        userType = typeId;

        String json = String.format("{\"username\":\"%s\", \"password\":\"%s\", \"typeId\":%d}", username, password, typeId);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/user/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        Gson gson = new Gson();
        ResponseDTO<User> responseDTO = gson.fromJson(response.body(), new TypeToken<ResponseDTO<User>>() {}.getType());

        if (responseDTO.getCode() == 0) {
            User loggedInUser = responseDTO.getData();
            loggedInUserId = loggedInUser.getId();
            System.out.println("Login successful.");
            System.out.println("Logged in user ID: " + loggedInUserId);
            return true;
        } else {
            System.out.println("Login failed: " + responseDTO.getMessage());
            return false;
        }
    }

    /**
     * Fetches the list of all projects from the server and displays them.
     * This method sends a GET request to the server's '/project/all' endpoint and prints the response.
     * @throws IOException if an I/O error occurs when sending or receiving
     * @throws InterruptedException if the operation is interrupted
     */
    public void getProjects() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/project/all"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JsonParser parser = new JsonParser();
            JsonArray jsonArray = parser.parse(response.body()).getAsJsonArray();

            System.out.println("Projects:");
            for (JsonElement element : jsonArray) {
                JsonObject project = element.getAsJsonObject();
                int projectId = project.get("id").getAsInt();
                String projectTitle = project.get("title").getAsString();
                String projectDescription = project.get("description").getAsString();
                int availabilityStatus = project.get("available").getAsInt();

                System.out.println("Project ID: " + projectId);
                System.out.println("Title: " + projectTitle);
                System.out.println("Description: " + projectDescription);
                System.out.println("Availability: " + (availabilityStatus == 1 ? "Available" : "Unavailable"));
                System.out.println("------------------------");
            }
        } else {
            System.out.println("Failed to retrieve projects: " + response.body());
        }
    }

    /**
     * Allows a student to express interest in a project.
     *
     * This method prompts the user to enter the ID of the project they are interested in. It then checks if the user
     * is logged in, if the project ID is valid, and if the project is available for registration. If all conditions are met,
     * it sends a request to the server to express interest in the project and displays the appropriate message based on
     * the response status code.
     *
     * @throws IOException if an I/O error occurs when sending the HTTP request
     * @throws InterruptedException if the current thread is interrupted while waiting for the HTTP response
     */
    public void expressInterestInProject() throws IOException, InterruptedException {
        System.out.println("Enter the ID of the project you are interested in:");
        String projectId = scanner.nextLine();

        if (loggedInUserId == null) {
            System.out.println("You must be logged in to express interest in a project.");
            return;
        }

        if (!isProjectIdValid(projectId)) {
            System.out.println("Invalid project ID. Please enter a valid project ID.");
            return;
        }

        if (!isProjectAvailable(projectId)) {
            System.out.println("The project is currently unavailable for registration or you have been assigned a project already.");
            return;
        }

        String requestBody = String.format("{\"projectId\": %s, \"studentId\": %d}", projectId, loggedInUserId);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/registration/create"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            System.out.println("You have successfully expressed interest in the project!");
        } else {
            System.out.println("Failed to express interest in the project: " + response.body());
        }
    }

    /**
     * Checks if a project is available for registration.
     *
     * @param projectId the ID of the project to check
     * @return {@code true} if the project is available, {@code false} otherwise
     * @throws IOException if an I/O error occurs when sending the HTTP request
     * @throws InterruptedException if the current thread is interrupted while waiting for the HTTP response
     */
    private boolean isProjectAvailable(String projectId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/project/" + projectId))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JsonParser parser = new JsonParser();
            JsonObject jsonObject = parser.parse(response.body()).getAsJsonObject();
            int availabilityStatus = jsonObject.get("available").getAsInt();
            return availabilityStatus == 1;
        }

        return false;
    }

    /**
     * Checks if a project ID is valid.
     *
     * @param projectId the ID of the project to check
     * @return {@code true} if the project ID is valid, {@code false} otherwise
     * @throws IOException if an I/O error occurs when sending the HTTP request
     * @throws InterruptedException if the current thread is interrupted while waiting for the HTTP response
     */
    private boolean isProjectIdValid(String projectId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/project/" + projectId))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        return response.statusCode() == 200;
    }

    /**
     * Displays the projects registered by the logged-in student.
     *
     * This method retrieves and displays the projects in which the logged-in student has expressed interest or
     * to which they have been assigned. It distinguishes between interested projects and assigned projects,
     * and provides appropriate messages if there are no registered projects.
     *
     * @throws IOException if an I/O error occurs when sending the HTTP request
     * @throws InterruptedException if the current thread is interrupted while waiting for the HTTP response
     */
    public void viewRegisteredProjects() throws IOException, InterruptedException {
        if (loggedInUserId == null || userType != 1) {
            System.out.println("You must be logged in as a student to view your registered projects.");
            return;
        }

        String url = "http://localhost:8080/registration/student/" + loggedInUserId;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JsonParser parser = new JsonParser();
            JsonArray jsonArray = parser.parse(response.body()).getAsJsonArray();

            List<String> interestedProjects = new ArrayList<>();
            List<String> assignedProjects = new ArrayList<>();

            for (JsonElement element : jsonArray) {
                JsonObject registration = element.getAsJsonObject();
                int registrationState = registration.get("registrationState").getAsInt();
                String projectTitle = registration.get("projectTitle").getAsString();

                if (registrationState == 1) {
                    interestedProjects.add(projectTitle);
                } else if (registrationState == 2) {
                    assignedProjects.add(projectTitle);
                }
            }

            if (!interestedProjects.isEmpty()) {
                System.out.println("Interested projects:");
                for (String projectTitle : interestedProjects) {
                    System.out.println("- " + projectTitle);
                }
            }

            if (!assignedProjects.isEmpty()) {
                System.out.println("You have been assigned to a project:");
                for (String projectTitle : assignedProjects) {
                    System.out.println("- " + projectTitle);
                }
            }

            if (interestedProjects.isEmpty() && assignedProjects.isEmpty()) {
                System.out.println("You have no registered projects.");
            }
        } else {
            System.out.println("Failed to retrieve your registered projects: " + response.body());
        }
    }

    /**
     * Makes a specified project unavailable by sending a PUT request to the server.
     * This method prompts the user to enter the ID of the project they want to make unavailable and sends a PUT request
     * to the server's '/project/make-unavailable/{projectId}' endpoint.
     * @throws IOException if an I/O error occurs when sending or receiving
     * @throws InterruptedException if the operation is interrupted
     */
    public void makeProjectUnavailable() throws IOException, InterruptedException {
        System.out.println("Enter the ID of the project you want to make unavailable:");
        String projectId = scanner.nextLine();

        // Check if the logged-in user is a staff member
        if (loggedInUserId == null || userType != 2) {
            System.out.println("You must be logged in as a staff member to make a project unavailable.");
            return;
        }

        // Check if the project belongs to the logged-in staff member
        if (!isProjectCreatedByStaff(projectId)) {
            System.out.println("You can only make your own projects unavailable.");
            return;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/project/make-unavailable/" + projectId))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            System.out.println("Project marked as unavailable successfully!");
        } else {
            System.out.println("Failed to mark project as unavailable: " + response.body());
        }
    }

    /**
     * Checks if the project with the given ID was created by the logged-in staff member.
     *
     * @param projectId the ID of the project to check
     * @return {@code true} if the project was created by the logged-in staff member, {@code false} otherwise
     * @throws IOException if an I/O error occurs when sending the HTTP request
     * @throws InterruptedException if the current thread is interrupted while waiting for the HTTP response
     */
    private boolean isProjectCreatedByStaff(String projectId) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/project/" + projectId))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JsonParser parser = new JsonParser();
            JsonObject jsonObject = parser.parse(response.body()).getAsJsonObject();
            int staffId = jsonObject.get("staffId").getAsInt();
            return staffId == loggedInUserId;
        }

        return false;
    }

    /**
     * Shows the list of students interested in projects proposed by the logged-in staff member.
     * This method sends a GET request to the server's '/registration/students-registration-terminal/{loggedInUsername}' endpoint
     * and prints the response containing interested students.
     *
     * @throws IOException if an I/O error occurs when sending or receiving the HTTP request
     * @throws InterruptedException if the operation is interrupted while waiting for the HTTP response
     */
    public void showInterestedStudents() throws IOException, InterruptedException {
        if (loggedInUserId == null || userType != 2) {
            System.out.println("You must be logged in as a staff member to view interested students.");
            return;
        }

        String url = "http://localhost:8080/registration/students-registration/" + loggedInUserId;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JsonParser parser = new JsonParser();
            JsonArray jsonArray = parser.parse(response.body()).getAsJsonArray();

            if (jsonArray.size() > 0) {
                System.out.println("Students interested in your projects:");
                for (JsonElement element : jsonArray) {
                    JsonObject registration = element.getAsJsonObject();
                    int registrationId = registration.get("registrationId").getAsInt();
                    String studentName = registration.get("studentName").getAsString();
                    String projectTitle = registration.get("projectTitle").getAsString();
                    int registrationState = registration.get("registrationState").getAsInt();

                    System.out.println("Registration ID: " + registrationId);
                    System.out.println("Student Name: " + studentName);
                    System.out.println("Project Title: " + projectTitle);
                    System.out.println("Registration State: " + (registrationState == 1 ? "Interested" : "Assigned"));
                    System.out.println("------------------------");
                }
            } else {
                System.out.println("No students are currently interested in your projects.");
            }
        } else {
            System.out.println("Failed to retrieve interested students: " + response.body());
        }
    }

    /**
     * Assigns a project to a student by updating the project's registration state.
     * This method prompts the user to enter the registration ID of the student and project pairing
     * and sends a PUT request to the server's '/registration/assign/{registrationId}' endpoint.
     * @throws IOException if an I/O error occurs when sending or receiving
     * @throws InterruptedException if the operation is interrupted
     */
    public void assignProjectToStudent() throws IOException, InterruptedException {
        System.out.println("Enter the registration ID of the student to assign the project:");
        String registrationId = scanner.nextLine();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/registration/assign/" + registrationId))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            System.out.println("Project successfully assigned to the student.");
        } else {
            System.out.println("Failed to assign project to the student: " + response.body());
        }
    }

    /**
     * Creates a new project by the logged-in staff member.
     *
     * <p>This method prompts the user to enter the project title and description.
     * It then constructs a new Project object with the provided details and sends a POST request
     * to the server's '/project/create' endpoint to create the project.
     *
     * @throws IOException if an I/O error occurs when sending or receiving the HTTP request
     * @throws InterruptedException if the operation is interrupted while waiting for the HTTP response
     */
    public void createProject() throws IOException, InterruptedException {
        if (loggedInUserId == null || userType != 2) {
            System.out.println("You must be logged in as a staff member to create a project.");
            return;
        }

        System.out.println("Enter the project title:");
        String title = scanner.nextLine();

        System.out.println("Enter the project description:");
        String description = scanner.nextLine();

        Project project = new Project();
        project.setTitle(title);
        project.setDescription(description);
        project.setStaffId(loggedInUserId);
        project.setAvailable(1); // Set the project as available by default

        Gson gson = new Gson();
        String requestBody = gson.toJson(project);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/project/create"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            System.out.println("Project created successfully!");
        } else {
            System.out.println("Failed to create project: " + response.body());
        }
    }

    /**
     * Logs out the current user.
     * Sets the logged-in username, user ID, and user type to null.
     * Prints a message indicating that the logout was successful.
     */
    public void logout() {
        loggedInUsername = null;
        loggedInUserId = null;
        userType = null;
        System.out.println("Logout successful.");
        TerminalClient client = new TerminalClient();
        client.run();
    }

    /**
     * Runs the TerminalClient application.
     * This method displays a welcome message, initiates the login process, and then presents a menu of options
     * to the user based on their user type. The method handles user input to perform various actions such as
     * listing projects, expressing interest in projects, and managing project availability.
     */
    public void run() {
        try {
            System.out.println("Welcome to the Project Allocation System!");
            if (!login()) {
                System.out.println("Login failed. Exiting...");
                TerminalClient client = new TerminalClient();
                client.run();
                return;
            }

            boolean running = true;
            while (running) {
                System.out.println("\nPlease choose an option:");

                if (userType == 1) {
                    System.out.println("1. Get all projects");
                    System.out.println("2. Express interest in a project");
                    System.out.println("3. View interested/assigned projects");
                } else if (userType == 2) {
                    System.out.println("1. Get all projects");
                    System.out.println("2. Make a project unavailable");
                    System.out.println("3. Show registered students");
                    System.out.println("4. Assign project to student");
                    System.out.println("5. Create a new project");
                }
                System.out.println("9. Logout");
                System.out.println("0. Exit");
                System.out.print("Enter choice: ");
                String choice = scanner.nextLine();

                if (userType == 1) {
                    switch (choice) {
                        case "1":
                            getProjects();
                            break;
                        case "2":
                            expressInterestInProject();
                            break;
                        case "3":
                            viewRegisteredProjects();
                            break;
                        case "9":
                            logout();
                            running = false;
                            break;
                        case "0":
                            running = false;
                            break;
                        default:
                            System.out.println("Invalid choice. Please try again.");
                    }
                } else if (userType == 2) {
                    switch (choice) {
                        case "1":
                            getProjects();
                            break;
                        case "2":
                            makeProjectUnavailable();
                            break;
                        case "3":
                            showInterestedStudents();
                            break;
                        case "4":
                            assignProjectToStudent();
                            break;
                        case "5":
                            createProject();
                            break;
                        case "9":
                            logout();
                            running = false;
                            break;
                        case "0":
                            running = false;
                            break;
                        default:
                            System.out.println("Invalid choice. Please try again.");
                    }
                }
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    /**
     * The entry point of the TerminalClient application.
     * @param args The command line arguments passed to the program.
     */
    public static void main(String[] args) {
        TerminalClient client = new TerminalClient();
        client.run();
    }



}
