package carsharing;

import java.sql.SQLException;
import java.util.Scanner;

public class UserInputHandler {
    private final MenuPrinter menuPrinter;
    private final DatabaseHandler databaseHandler;
    private final String jdbcDriver;
    private final String dbUrl;
    private final String user;
    private final String pass;

    UserInputHandler(String jdbcDriver, String dbUrl, String user, String pass){
        menuPrinter = new MenuPrinter();
        databaseHandler = new DatabaseHandler();
        this.jdbcDriver = jdbcDriver;
        this.dbUrl = dbUrl;
        this.user = user;
        this.pass = pass;
    }

    void manageInput() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        databaseHandler.createDatabase(jdbcDriver, dbUrl, user, pass);
        while (true){
            menuPrinter.printMainMenu();
            String userInput = scanner.nextLine();
            System.out.println();

            if (userInput.equals("0")){
                databaseHandler.closeConnection();
                return;
            }
            managerMenu(scanner);
        }
    }

    private void managerMenu(Scanner scanner) throws SQLException {
        while (true) {
            menuPrinter.printManagerMenu();
            String userInput = scanner.nextLine();

            switch (userInput){
                case "0" -> {
                    System.out.println();
                    return;
                }
                case "1" -> {
                    databaseHandler.selectAll();
                    System.out.println();
                }
                case "2" -> {
                    System.out.println("\nEnter the company name:");
                    String companyName = scanner.nextLine();
                    databaseHandler.insert(companyName);
                    System.out.println();
                }
            }
        }
    }
}
