package demo;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        /*LOCAL_VAR*/
        Scanner scanner = new Scanner(System.in);
        String command;
        StudentDAO studentDAO;
        try {
            studentDAO = new StudentDAO();
        } catch (SQLException e) {
            System.err.println("Student dao cannot be created. Mysql error");
            System.err.println("Error: " + e.getMessage());
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        do {
            System.out.println("co chcesz zrobić? \ndodać studenta (d) :: usuń (u) :: listuj (l) :: wypisz studenta po id (id) :: wypisz po nazwisku (name) :: wypisz po wieku (wiek) :: quit (q)");
            command = scanner.nextLine();
            try {
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

                        studentDAO.insertStudent(student);
                        break;
                    case "u":
                        System.out.println("podaj id studenta do usunięcia");
                        int idToDelete = Integer.parseInt(scanner.nextLine());
                        studentDAO.deleteStudent(idToDelete);
                        break;
                    case "l":
                        studentDAO.listAllStudents().forEach(System.out::println);
                        break;
                    case "id":
                        System.out.println("podaj id studenta do wypisania");
                        Long searchedId = Long.valueOf(scanner.nextLine());
                        System.out.println(studentDAO.getByIdStudent(searchedId));
                        break;
                    case "name":
                        System.out.println("podaj nazwisko studenta do wypisania");
                        String searchedName = scanner.nextLine();
                        System.out.println(studentDAO.getByNameStudent(searchedName));
                        break;
                    case "wiek":
                        System.out.println("wiek od");
                        Long from = Long.valueOf(scanner.nextLine());
                        System.out.println("wiek do");
                        Long to = Long.valueOf(scanner.nextLine());
                        System.out.println(studentDAO.getByAgeStudent(from, to));
                        break;
                    case "q":
                        break;
                    default:
                        System.err.println("podałeś złą komendę, spórbuj jeszcze raz");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } while (!command.equalsIgnoreCase("q"));
    }


}
