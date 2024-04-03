package com.example.cs5031p3.demo;

import com.example.cs5031p3.demo.backend.enums.RegistrationStateEnum;
import com.example.cs5031p3.demo.backend.enums.TypeEnum;
import com.example.cs5031p3.demo.backend.model.Project;
import com.example.cs5031p3.demo.backend.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class with mock samples to reduce repeated code in tests
 */
public class MockObject {
    /**
     * Mock a list of users
     * @return mock user list
     */
    public static List<User> mockUserList()
    {
        List<User> userList=new ArrayList<>();
        for(int i =0;i<3;i++)
        {
            userList.add(new User(i,"mock","mock","mock",i));
        }
        return userList;
    }

    /**
     * Mock an arbitrary user
     * @return user
     */
    public static User mockGeneralUser()
    {
        int id = 33;
        String name = "mock";
        String username = "mock";
        String password = "mock";
        int typeId = 3;
        return new User(id,name,username,password,typeId);
    }

    /**
     * Mock a student
     * @return user with typeId 1
     */
    public static User mockStudent()
    {
        int id = 3;
        String name = "Student John Doe";
        String username = "20240009";
        String password = "password";
        int typeId = TypeEnum.STUDENT.getCode();
        return new User(id,name,username,password,typeId);
    }

    /**
     * Mock a staff
     * @return user with typeId 2
     */
    public static User mockStaff()
    {
        int id = 4;
        String name = "Staff John Doe";
        String username = "20240009";
        String password = "password";
        int typeId = TypeEnum.STAFF.getCode();
        return new User(id,name,username,password,typeId);
    }

    /**
     * Mock a registration list
     * @return mock registration list
     */
    public static List<Map<String, Object>> mockRegistration()
    {
        List<Map<String, Object>> registrationList=new ArrayList<>();
        for(int i =0;i<3;i++)
        {
            Map<String, Object> registration = new HashMap<>();
            registration.put("registrationId", i);
            registration.put("projectId", i);
            registration.put("studentId", i);
            registration.put("registrationState", RegistrationStateEnum.WAIT.getCode());
            registration.put("studentName", "mockStudentName");
            registration.put("projectTitle", "project_title"+i);
            registration.put("staffName","mockStaffName");
            registrationList.add(registration);
        }
        return registrationList;
    }
    /**
     * Mock a registration list with studentId
     * @return mock registration list of the specific student
     */
    public static List<Map<String, Object>> mockRegistration(int studentId)
    {
        List<Map<String, Object>> registrationList=new ArrayList<>();
        for(int i =0;i<3;i++)
        {
            Map<String, Object> registration = new HashMap<>();
            registration.put("registrationId", i);
            registration.put("projectId", i);
            registration.put("studentId", studentId);
            registration.put("registrationState", RegistrationStateEnum.WAIT.getCode());
            registration.put("studentName", "mockStudentName");
            registration.put("projectTitle", "project_title"+i);
            registration.put("staffName","mockStaffName");
            registrationList.add(registration);
        }
        return registrationList;
    }
    public static List<Project> projectList(int staffId)
    {
        List<Project> projectList=new ArrayList<>();
        for(int i=0;i<4;i++)
        {
            Project project=new Project(i,"mock","mock",staffId);
            projectList.add(project);
        }
        return projectList;
    }
}
