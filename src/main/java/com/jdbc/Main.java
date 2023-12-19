package com.jdbc;

import java.sql.*;


public class Main {

    private static final String url = "jdbc:mysql://localhost:3306/mydb";
    private static final String username = "root";
    private static final String password = "ABcdEF1@";


    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();

            String query = "select * from students";

            //String inset = "Insert into students(name, age, marks)  values ('Karim', 21, 99.5)";
//
//            String update = String.format("update students set marks = %f where id = %d", 55.6, 2);
//            int rowsAffected = statement.executeUpdate(update);
//            System.out.println(rowsAffected + " rows affected");


            //prepared statement
            String insert = "Insert into students(name, age, marks)  values (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insert);
            preparedStatement.setString(1, "Dipu");
            preparedStatement.setInt(2, 25);
            preparedStatement.setDouble(3, 36.3);
            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println(rowsAffected + " rows affected");

            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                double marks = resultSet.getDouble("marks");

                System.out.println("ID: " + id);
                System.out.println("Name: " + name);
                System.out.println("Age: " + age);
                System.out.println("Marks: " + marks + "\n\n");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("eikhane vul hosei");
        }
    }
}
