package demo;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static demo.StudentQueries.*;

public class StudentDAO {
    private MysqlConnection mysqlConnection;

    public StudentDAO() throws SQLException, IOException {
        mysqlConnection = new MysqlConnection();
        createTableIfNotExist();
    }

    private void createTableIfNotExist() throws SQLException {
        try (Connection connection = mysqlConnection.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(CREATE_TABLE_QUERY)) {
                statement.execute();
            }
        }
    }

    public void insertStudent(Student student) throws SQLException {
        try (Connection connection = mysqlConnection.getConnection()) {
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

    public void deleteStudent(int idToDelete) throws SQLException {
        try (Connection connection = mysqlConnection.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(DELETE_QUERY)) {
                statement.setLong(1, idToDelete);
                boolean success = statement.execute();
                if (success) {
                    System.out.println("SUKCES!");
                }
            }
        }
    }


    public Optional<Student> getByNameStudent(String searchedName) throws SQLException {
        Student student = null;
        try (Connection connection = mysqlConnection.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SELECT_BY_NAME_QUERY)) {
                statement.setString(1, "%" + searchedName + "%");
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) { // jeśli jest rekord
                    student = loadStudentFromResultSet(resultSet);
                    return Optional.of(student);
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Student> getByIdStudent(Long searchedId) throws SQLException {
        Student student = null;
        try (Connection connection = mysqlConnection.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID_QUERY)) {
                statement.setLong(1, searchedId);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) { // jeśli jest rekord
                    student = loadStudentFromResultSet(resultSet);
                    return Optional.of(student);
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Student> getByAgeStudent(Long from, Long to) throws SQLException {
        Student student = null;
        try (Connection connection = mysqlConnection.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SELECT_BY_AGE_QUERY)) {
                statement.setLong(1, from);
                statement.setLong(2, to);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) { // jeśli jest rekord
                    student = loadStudentFromResultSet(resultSet);
                    return Optional.of(student);
                }
            }
        }
        return Optional.empty();
    }


    public List<Student> listAllStudents() throws SQLException {
        List<Student> studentList = new ArrayList<>();
        try (Connection connection = mysqlConnection.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SELECT_ALL_QUERY)) {
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    Student student = loadStudentFromResultSet(resultSet);

                    studentList.add(student);
                }
            }
        }
        return studentList;
    }

    private Student loadStudentFromResultSet(ResultSet resultSet) throws SQLException {
        Student student = new Student();
        student.setId(resultSet.getLong(1));
        student.setName(resultSet.getString(2));
        student.setAge(resultSet.getInt(3));
        student.setAverage(resultSet.getDouble(4));
        student.setAlive(resultSet.getBoolean(5));
        return student;
    }

}
