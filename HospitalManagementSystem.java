package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class HospitalManagementSystem {
    private static final String url = "jdbc:mysql://localhost:3306/hospital";
    private static final String username = "root";
    private static final String password = "mysql123/-"; // Ensure correct DB password

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC driver not found.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Patient patient = new Patient(connection, scanner);
            doctors doctor = new doctors(connection, scanner);

            while (true) {
                System.out.println("\n===== Hospital Management Menu =====");
                System.out.println("1. Add Patient");
                System.out.println("2. View Patients");
                System.out.println("3. View Doctors");
                System.out.println("4. Book Appointment");
                System.out.println("5. Exit");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        patient.addPatient();
                        break;
                    case 2:
                        patient.viewPatient();
                        break;
                    case 3:
                        doctor.viewDoctors();
                        break;
                    case 4:
                        bookAppointment(patient, doctor, connection, scanner);
                        break;
                    case 5:
                        System.out.println("Exiting...");
                        connection.close();
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
    }

    public static void bookAppointment(Patient patient, doctors doctors, Connection connection, Scanner scanner) {
        System.out.print("Enter Patient ID: ");
        int patientId = scanner.nextInt();

        System.out.print("Enter Doctor ID: ");
        int doctorId = scanner.nextInt();

        System.out.print("Enter Appointment Date (YYYY-MM-DD): ");
        String appointmentDate = scanner.next();

        if (patient.getPatientById(patientId) && doctors.getDoctorsById(doctorId)) {
            if (checkDoctorAvailability(connection, doctorId, appointmentDate)) {
                String appointmentQuery = "INSERT INTO appointment(patient_id, doctor_id, appointment_date) VALUES (?, ?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(appointmentQuery)) {
                    statement.setInt(1, patientId);
                    statement.setInt(2, doctorId);
                    statement.setString(3, appointmentDate);

                    int rowsInserted = statement.executeUpdate();
                    if (rowsInserted > 0) {
                        System.out.println("Appointment booked successfully!");
                    } else {
                        System.out.println("Failed to book appointment.");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Doctor not available on this date.");
            }
        } else {
            System.out.println("Patient or Doctor does not exist.");
        }
    }

    public static boolean checkDoctorAvailability(Connection connection, int doctorId, String appointmentDate) {
        try {
            String query = "SELECT COUNT(*) FROM appointment WHERE doctor_id = ? AND appointment_date = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, doctorId);
            statement.setString(2, appointmentDate);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count == 0; // true if no appointments exist for that doctor on that date
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
