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

    UserInputHandler(String jdbcDriver, String dbUrl, String user, String pass) {
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
        while (true) {
            menuPrinter.printMainMenu();
            String userInput = scanner.nextLine();
            System.out.println();

            if (userInput.equals("0")) {
                databaseHandler.closeConnection();
                return;
            }

            if (userInput.equals("1")) {
                managerMenu(scanner);
            }
            if (userInput.equals("2")) {
                selectCustomerMenu(scanner);
            }
            if (userInput.equals("3")) {
                createCustomer(scanner);
            }
        }
    }

    private void managerMenu(Scanner scanner) throws SQLException {
        while (true) {
            menuPrinter.printCompanyMenu();
            String userInput = scanner.nextLine();

            switch (userInput) {
                case "0" -> {
                    System.out.println();
                    return;
                }
                case "1" -> {
                    System.out.println();
                    boolean companiesExist = databaseHandler.selectAllCompanies();
                    if (companiesExist) {
                        System.out.println("0. Back");
                        System.out.println("\nChoose a company:");
                        int companyId = Integer.valueOf(scanner.nextLine());
                        if (companyId != 0) {
                            carMenu(scanner, companyId);
                        }
                    } else {
                        System.out.println();
                    }
                }
                case "2" -> {
                    System.out.println("\nEnter the company name:");
                    String companyName = scanner.nextLine();
                    databaseHandler.insertCompanyName(companyName);
                    System.out.println();
                }
            }
        }
    }

    private void carMenu(Scanner scanner, int companyId) throws SQLException {
        while (true) {
            System.out.println();
            menuPrinter.printCarMenu();
            String userInput = scanner.nextLine();

            switch (userInput) {
                case "0" -> {
                    System.out.println();
                    return;
                }
                case "1" -> {
                    databaseHandler.selectAllCarsByCompany(companyId);
                }
                case "2" -> {
                    System.out.println("\nEnter the car name:");
                    String carName = scanner.nextLine();
                    databaseHandler.insertNewCar(carName, companyId);
                }
            }
        }
    }

    private void selectCustomerMenu(Scanner scanner) throws SQLException {
        while (true) {
            // System.out.println();
            boolean customersExist = databaseHandler.selectAllCustomers();
            if (customersExist) {
                System.out.println("0. Back");
                int customerId = Integer.valueOf(scanner.nextLine());
                System.out.println();
                if (customerId != 0) {
                    customerMenu(scanner, customerId);
                } else {
                    return;
                }
            } else {
                System.out.println();
                return;
            }
        }
    }

    private void customerMenu(Scanner scanner, int customerId) throws SQLException {
        while (true) {
            //System.out.println();
            menuPrinter.printCustomerMenu();
            String userInput = scanner.nextLine();
            System.out.println();

            switch (userInput) {
                case "0" -> {
                    return;
                }
                case "1" -> {
                    if (databaseHandler.customerHasRentedACar(customerId)){
                        System.out.println("You've already rented a car!");
                        break;
                    }
                    boolean companiesExist = databaseHandler.selectAllCompanies();
                    if (companiesExist) {
                        System.out.println("0. Back");
                        System.out.println("\nChoose a company:");
                        int companyId = Integer.valueOf(scanner.nextLine());
                        if (companyId != 0) {
                            rentACar(scanner, companyId, customerId);
                        }
                    } else {
                        System.out.println();
                    }
                }
                case "2" -> {
                    /*System.out.println("\nEnter the car name:");
                    String carName = scanner.nextLine();
                    databaseHandler.insertNewCar(carName, companyId);*/
                }
                case "3" -> {
                    databaseHandler.selectAllCarsByCustomer(customerId);
                }
            }
            System.out.println();
        }
    }

    private void rentACar(Scanner scanner, int companyId, int customerID) throws SQLException {
        boolean carsExist = databaseHandler.selectAllCarsByCompany(companyId);
        if (carsExist) {
            System.out.println("0. Back");
            System.out.println("\nChoose a car:");
            int carId = Integer.valueOf(scanner.nextLine());
            if (carId != 0) {
                databaseHandler.rentCar(scanner, carId, customerID);
            }
        }
        else {
            System.out.println("No available cars in the "+databaseHandler.findCompanyName(companyId)+" company");
        }
    }

    private void createCustomer(Scanner scanner) {
        System.out.println("Enter the customer name:");
        String customerName = scanner.nextLine();
        databaseHandler.insertNewCustomer(customerName);
        System.out.println();
    }
}
