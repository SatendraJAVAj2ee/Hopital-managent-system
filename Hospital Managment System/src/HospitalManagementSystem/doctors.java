package HospitalManagementSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class doctors {
    private Connection connection;

    public doctors(Connection connection, Scanner scanner) {
        this.connection = connection;
    }



    public void viewDoctors() {
        String query = "SELECT * FROM doctors";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            System.out.println("Patients:");
            System.out.println("+------------+-----------------------------+-----------------------------------+");
            System.out.println("| doctor ID  |              Name          |         Specialization            |");
            System.out.println("+------------+-----------------------------+-----------------------------------+");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String specialization = resultSet.getString("specialization");

                System.out.printf("| %-12s | %-29s | %-35s |\n", id, name, specialization);
                System.out.println("+------------+-----------------------------+-----------------------------------+");

            }

            } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean getDoctorsById(int id) {
        String query = "SELECT * FROM doctors WHERE id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next(); // returns true if patient found
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}


