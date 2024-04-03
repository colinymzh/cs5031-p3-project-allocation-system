# Project Allocation System

This is a project allocation system that allows students to express interest in projects offered by staff members and enables staff to assign students to those projects.

## How to Run

### Server

To install the dependencies, build the project, and run the unit tests:

```bash
cd P3-proj
mvn clean install
```

To run the back-end of the application in the terminal:

```bash
cd P3-proj
mvn spring-boot:run
```

To run the target '.jar' file:

```bash
cd P3-proj
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

To run the unit tests separately:

```bash
cd P3-proj
mvn test install
```

To view the code coverage files produced by Jacoco, open the file at the following directory in a web browser:

```bash
cd P3-proj
target/site/jacoco/index.html
```

To view the Javadocs as a webpage:

```bash
cd P3-proj
mvn javadoc:javadoc
target/site/apidocs/index.html
```

### Client

After the backend has been run, the terminal client and the web client can be launched.

To run the terminal client:

You can directly run the main() in TerminalClient class in Idea, or:

```bash
cd P3-proj

Linux/MacOS:

java -cp .:target/classes:lib/gson-2.8.9.jar com.example.cs5031p3.demo.terminal.TerminalClient
```

To run the web client (install node.js first):

```bash
cd P3-proj/src/main/java/com/example/cs5031p3/demo/frontend
npm install
npm start
```

After successfully running the frontend application, you can connect to `http://localhost:3000/` to enter the web client. Additionally, we have deployed our web client application on [Netlify](https://www.netlify.com/). After the backend application has been run, you can simply visit `https://project-allocation-system.netlify.app` to access our web client, which connects to the local backend application on `http://localhost:8080/`.

### Sample User Accounts for Client Login

These are sample accounts intended for client login. Users can log in by entering their account password and selecting their role. Additionally, student 20240001 has pre-registered two projects offered by staff 20240002, while student 20240003 has pre-registered one project offered by staff 20240002.

| Name              | Username | Password | Role    |
| ----------------- | -------- | -------- | ------- |
| Student John Doe  | 20240001 | password | Student |
| Staff Jim Beam    | 20240002 | password | Staff   |
| Student Jill Hill | 20240003 | password | Student |
| Staff Jack Black  | 20240004 | password | Staff   |

## Dependencies

The project uses the following dependencies:

- Spring Boot Starter
- H2 Database
- Spring Boot Starter Test
- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Javax Persistence API
- Node.js 
