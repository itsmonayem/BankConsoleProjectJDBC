package com.jdbc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.Scanner;

public class DbConsoleProject {
    private static final String url = "jdbc:mysql://localhost:3306/bank";
    private static final String username = "root";
    private static final String password = "ABcdEF1@";

    static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) {


        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            while ( true ) {
                System.out.println("Press 1 to add new user: ");
                System.out.println("Press 2 debit money: ");
                System.out.println("Press 3 to show a user status: ");
                System.out.println("Press 4 to transfer amount to another account: ");
                System.out.println("Press 5 to delete account: ");
                System.out.println("Press 6 to exit: ");
                System.out.print("Enter your number: ");

                int choice = Integer.parseInt(in.readLine());

                switch (choice) {
                    case 1:
                        addUser(connection);
                        break;
                    case 2:
                        deposit_amount(connection);
                        break;
                    case 3:
                        userStatus(connection);
                        break;
                    case 4:
                        transferamount(connection);
                        break;
                    case 5:
                        deleteAccount(connection);
                        break;
                    case 6:
                        return;
                    default:
                        System.out.println("Please choose correct number");
                        break;
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void deleteAccount(Connection connection) throws IOException, SQLException {
        System.out.println("Enter User account");
        int account_no = Integer.parseInt(in.readLine());

        String getDataQuery = "SELECT * from user WHERE account_no = " + account_no;
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(getDataQuery);

        if (resultSet.next()) {
            System.out.println("Thank you Mr./Ms. " + resultSet.getString("name")
                    + "\nfor using our banking system and Please take your "
                    + "total balance : " + resultSet.getInt("balance") + "\n\n");

            String query = "DELETE FROM user WHERE account_no = " + account_no;
            int rowsEffect = statement.executeUpdate(query);
            System.out.println("Total " + rowsEffect + " rows affected\n\n");

        } else {
            System.out.println("No User available for this account_no\n\n");
        }
    }





    private static void transferamount(Connection connection) throws IOException, SQLException {
        System.out.println("Enter sender account no:");
        int sender_account_no = Integer.parseInt(in.readLine());
        System.out.println("Enter receiver account no: ");
        int receiver_account_no = Integer.parseInt(in.readLine());
        System.out.println("Enter Amount: ");
        int amount = Integer.parseInt(in.readLine());

        connection.setAutoCommit(false);

        String debit_query = "UPDATE user SET balance = balance - " + amount + " WHERE account_no = " + sender_account_no;
        String creadit_query = "UPDATE user SET balance = balance + " + amount + " WHERE account_no = " + receiver_account_no;
        PreparedStatement debitPreparedStatement = connection.prepareStatement(debit_query);
        PreparedStatement creaditPreparedStatement = connection.prepareStatement(creadit_query);
        creaditPreparedStatement.executeUpdate();
        debitPreparedStatement.executeUpdate();


        if ( isSufficientBalance(connection, sender_account_no, amount)) {
            connection.commit();
            System.out.println("Transaction is successful");
        } else {
            connection.rollback();
            System.out.println("Insufficient Balance");
        }

    }

    private static boolean isSufficientBalance(Connection connection, int account_no, int amount) throws SQLException {
        String getDataQuery = "SELECT * from user WHERE account_no = " + account_no;
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(getDataQuery);

        if (resultSet.next() && resultSet.getInt("amount") >= amount) {
           return true;
        } else {
            return false;
        }
    }


    private static void userStatus(Connection connection) throws IOException, SQLException {
        System.out.println("Enter User account");
        int account_no = Integer.parseInt(in.readLine());

        String getDataQuery = "SELECT * from user WHERE account_no = " + account_no;
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(getDataQuery);

        if (resultSet.next()) {
            System.out.println("User name " + resultSet.getString("name")
                    + "\nTotal balance : " + resultSet.getInt("balance") + "\n\n");
        } else {
            System.out.println("No User available for this account_no\n\n");
        }
    }

    private static void deposit_amount(Connection connection) throws IOException, SQLException {
        System.out.println("Enter User account");
        int account_no = Integer.parseInt(in.readLine());
        System.out.println("Enter initial deposit");
        int amount = Integer.parseInt(in.readLine());


//        Why PreparedStatement not worked here!!!!
        String getDataQuery = "SELECT * from user WHERE account_no = " + account_no;
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(getDataQuery);


        if (resultSet.next()) {
            String query = "UPDATE user set balance = (?) where account_no = (?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, amount + resultSet.getInt("balance"));
            preparedStatement.setInt(2, account_no);

            int rowsEffect = preparedStatement.executeUpdate();
            System.out.println("Total " + rowsEffect + " rows affected.\n\n");
        } else {
            System.out.println("No User available for this account.\n\n");
        }
    }

    private static void addUser(Connection connection) throws IOException, SQLException {
        System.out.println("Enter User name");
        String name = in.readLine();
        System.out.println("Enter initial deposit");
        int amount = Integer.parseInt(in.readLine());
        

        String query = "INSERT INTO user(name, balance) VALUES (?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, name);
        preparedStatement.setInt(2, amount);

        int rowsEffect = preparedStatement.executeUpdate();
        System.out.println("Total " + rowsEffect + " rows affected\n\n");
    }
}
