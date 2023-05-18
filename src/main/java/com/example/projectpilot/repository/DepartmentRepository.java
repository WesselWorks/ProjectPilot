package com.example.projectpilot.repository;

import com.example.projectpilot.model.Department;
import org.springframework.beans.factory.annotation.Value;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentRepository {

    @Value("${spring.datasource.url}") //jdbc:mysql://localhost:3306/ProjectPilotDB
    private String DB_URL;
    @Value("${spring.datasource.username}") //ProjectPilotDB
    private String UID;
    @Value("${spring.datasource.password}") //Bugbusters23
    private String PWD;

    public Department getDepartment(ResultSet resultSet) throws SQLException
    {
        //get department_id from result set
        int department_id = resultSet.getInt(1);
        //get first_name from result set
        String departmentName = resultSet.getString(2);

        //create user object
        return new Department(department_id, departmentName);
    }

    public List<Department> getAllDepartments(){
        //create list of users
        List<Department> departmentList = new ArrayList<>();
        //execute statement, here there is no exceptions that need to be caught. It does need to be in try/catch.
        // Limit the scope of a try block to only the code that might throw an exception.
        final String SQL_QUERY = "SELECT * FROM ProjectPilotDB.department";
        try
        {
            //db connection
            Connection connection = DriverManager.getConnection(DB_URL, UID, PWD);
            //create statement
            Statement statement = connection.createStatement();
            //get result set
            ResultSet resultSet = statement.executeQuery(SQL_QUERY);
            //loop through result set
            while ( resultSet.next() )
            {
                //get user from result set
                Department department = getDepartment(resultSet);
                //add user to list
                departmentList.add(department);
                //print user
                System.out.println(department);
            }
        }
        catch (SQLException e)
        {
            System.out.println("Error querying database");
            e.printStackTrace();
        }
        return departmentList;

    }

    public boolean checkIfDepartmentExists(String checkName)
    {
        final String FIND_QUERY = "SELECT * FROM ProjectPilotDB.department WHERE email = ?";
        try
        {
            //db connection
            Connection connection = DriverManager.getConnection(DB_URL, UID, PWD);
            //prepared statement
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_QUERY);
            //set parameters
            preparedStatement.setString(1, checkName);
            //execute statement
            ResultSet resultSet = preparedStatement.executeQuery();
            // Check if there is a row in the resultSet with the specified email
            if ( resultSet.next() )
            {
                return true;
            }
        }
        catch (SQLException e)
        {
            System.out.println("Could not query database");
            e.printStackTrace();
        }
        //return false if user does not exist
        return false;}

    public boolean addDepartment(Department department){
        // Query to insert user
        final String INSERT_QUERY = "INSERT INTO ProjectPilotDB.department (first_name, last_name, email, password) VALUES (?, ?, ?, ?)";
        try
        {
            // DB connection
            Connection connection = DriverManager.getConnection(DB_URL, UID, PWD);
            // Prepared statement
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_QUERY);
            // Set first_name
            preparedStatement.setString(1, department.getDepartmentName());
            // Execute SQL statement and get number of rows affected by query (should be 1) and store in rowsAffected
            int rowsAffected = preparedStatement.executeUpdate();
            // Return true if rowsAffected is 1
            if ( rowsAffected == 1 )
            {
                return true;
            }
        }
        catch (SQLException e)
        {
            System.out.println("Could not query database");
            e.printStackTrace();
        }
        // Return false if user was not added
        return false;

    }

    public Department getDepartmentById(int departmentId){
        //query to find user
        final String FIND_QUERY = "SELECT * FROM ProjectPilotDB.department WHERE id = ?";
        try
        {
            //db connection
            Connection connection = DriverManager.getConnection(DB_URL, UID, PWD);
            //prepared statement
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_QUERY);
            //set parameters for prepared statement (user_id)
            preparedStatement.setInt(1, departmentId);
            //execute statement
            ResultSet resultSet = preparedStatement.executeQuery();
            //return user if user exists
            if ( resultSet.next() )
            {
                return getDepartment(resultSet);
            }
        }
        catch (SQLException e)
        {
            System.out.println("Could not query database");
            e.printStackTrace();
        }
        //return null if user does not exist
        return null;
    }

    public void updateDepartment(Department department){ //query to update user
        final String UPDATE_QUERY = "UPDATE ProjectPilotDB.department SET first_name = ?, last_name = ?, email = ?, password = ? WHERE id = ?";
        try
        {
            //db connection
            Connection connection = DriverManager.getConnection(DB_URL, UID, PWD);
            //prepared statement
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_QUERY);
            //set parameters for prepared statement
            preparedStatement.setString(1, department.getDepartmentName());
            //set user_id
            preparedStatement.setInt(5, department.getDepartment_id());
            //execute statement
            preparedStatement.executeUpdate();
        }
        catch (SQLException e)
        {
            System.out.println("Could not query database");
            e.printStackTrace();
        }}

    public boolean deleteDepartmentById(Department department){
        //query to delete user
        final String DELETE_QUERY = "DELETE FROM ProjectPilotDB.department WHERE id = ?";
        try
        {
            //db connection
            Connection connection = DriverManager.getConnection(DB_URL, UID, PWD);
            //prepared statement
            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_QUERY);
            //set parameters for prepared statement(user_id)
            preparedStatement.setInt(1, department.getDepartment_id());
            //execute statement
            int foundUser = preparedStatement.executeUpdate();
            //return true if user was found and deleted (foundUser should be 1).
            if ( foundUser == 1 )
            {
                return true;
            }
        }
        catch (SQLException e)
        {
            System.out.println("Could not query database");
            e.printStackTrace();
        }
        //return false if user was not found and deleted
        return false;
    }



}
