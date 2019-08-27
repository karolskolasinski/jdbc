package demo;

import com.mysql.cj.jdbc.MysqlDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    /*QUERY*/
    private static final String CREATE_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS `STUDENTS` (\n" +
            "`id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,\n" +
            "`name` VARCHAR(255) NOT NULL,\n" +
            "`age` INT NOT NULL,\n" +
            "`average` DOUBLE NOT NULL,\n" +
            "`alive` TINYINT NOT NULL\n" +
            ");";
    private static final String INSERT_QUERY = "INSERT INTO `students` (`name`, `age`, `average`, `alive`)\n" +
            "VALUES( ? , ? , ? , ? );";
    private static final String DELETE_QUERY = "DELETE FROM `jdbc_students`.`students` WHERE `id` = ?";
    private static final String SELECT_ALL_QUERY = "SELECT * FROM `students`;";
    private static final String SELECT_BY_ID_QUERY = "SELECT * FROM `students` WHERE `id` = ?;";

    /*DBC_VAR*/
    private static final String DB_HOST = "localhost";
    private static final String DB_PORT = "3306";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "root";
    private static final String DB_NAME = "jdbc_students";

    /*CONNECTION*/
    private static Connection connection;

    public static void main(String[] args) {
        /*DBC_SET*/
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setPort(Integer.parseInt(DB_PORT));
        dataSource.setUser(DB_USERNAME);
        dataSource.setServerName(DB_HOST);
        dataSource.setPassword(DB_PASSWORD);
        dataSource.setDatabaseName(DB_NAME);
        openConnection(dataSource);

        /*LOCAL_VAR*/
        Scanner scanner = new Scanner(System.in);
        String command;

        do {
            System.out.println("co chcesz zrobić? \ndodać studenta (d) :: usuń (u) :: listuj (l) :: wypisz studenta (w) quit (q)");
            command = scanner.nextLine();

            switch (command) {
                case "d":
                    System.out.println("podaj imię");
                    String name = scanner.nextLine();

                    System.out.println("podaj wiek");
                    int age = Integer.parseInt(scanner.nextLine());

                    System.out.println("podaj średnią ocen");
                    Double average = Double.valueOf(scanner.nextLine());

                    System.out.println("czy jest żywy");
                    boolean isAlive = Boolean.parseBoolean(scanner.nextLine());

                    Student student = new Student(null, name, age, average, isAlive);

                    try {
                        insertStudent(connection, student);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case "u":
                    System.out.println("podaj id studenta do usunięcia");
                    int idToDelete = Integer.parseInt(scanner.nextLine());

                    try {
                        deleteStudent(connection, idToDelete);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case "l":
                    try {
                        listAllStudents(connection);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    break;
                case "w":
                    System.out.println("podaj id studenta do usunięcia");
                    Long searchedId = Long.valueOf(scanner.nextLine());
                    try {
                        getByIdStudent(connection, searchedId);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                case "q":
                    break;
                default:
                    System.err.println("podałeś złą komendę, spórbuj jeszcze raz");
            }
        } while (!command.equalsIgnoreCase("q"));

    }

    private static void getByIdStudent(Connection connection, Long searchedId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID_QUERY)) {
            statement.setLong(1, searchedId);
//            statement.setLong(1, "%" + searchedId + "%");
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) { // jeśli jest rekord
                Student student = new Student();
                student.setId(resultSet.getLong(1));
                student.setName(resultSet.getString(2));
                student.setAge(resultSet.getInt(3));
                student.setAverage(resultSet.getDouble(4));
                student.setAlive(resultSet.getBoolean(5));
                System.out.println(student);
            } else {
                System.out.println("Nie udało się odnaleźć studenta");
            }
        }
    }

    private static void deleteStudent(Connection connection, int idToDelete) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_QUERY)) {
            statement.setLong(1, idToDelete);
            boolean success = statement.execute();
            if (success) {
                System.out.println("SUKCES!");
            }
        }
    }

    private static void insertStudent(Connection connection, Student student) throws SQLException {
        if (student != null) {
            try (PreparedStatement statement = connection.prepareStatement(INSERT_QUERY)) {
                statement.setString(1, student.getName());
                statement.setInt(2, student.getAge());
                statement.setDouble(3, student.getAverage());
                statement.setBoolean(4, student.isAlive());
                boolean success = statement.execute();
                if (success) {
                    System.out.println("SUKCES!");
                }
            }
        }
    }

    private static void listAllStudents(Connection connection) throws SQLException {
        List<Student> studentList = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(SELECT_ALL_QUERY)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Student student = new Student();
                student.setId(resultSet.getLong(1));
                student.setName(resultSet.getString(2));
                student.setAge(resultSet.getInt(3));
                student.setAverage(resultSet.getDouble(4));
                student.setAlive(resultSet.getBoolean(5));
                studentList.add(student);
            }
        }
        for (Student student : studentList) {
            System.out.println(student);
        }
    }

    private static Connection openConnection(MysqlDataSource dataSource) {
        try {
            dataSource.setServerTimezone("Europe/Warsaw");
            dataSource.setUseSSL(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            connection = dataSource.getConnection();
            try (PreparedStatement statement = connection.prepareStatement(CREATE_TABLE_QUERY)) {
                statement.execute();
            }
            System.out.println("HURRA!");
        } catch (
                SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }


}
