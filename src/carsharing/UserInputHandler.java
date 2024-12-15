package carsharing;

import carsharing.domain.Car;
import carsharing.domain.Company;
import carsharing.domain.Customer;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
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
                    List<Company> companies = databaseHandler.selectAllCompanies();
                    if (!companies.isEmpty()) {
                        System.out.println("Choose a company:");
                        for (Company company : companies) {
                            System.out.println(company.companyId() + ". " + company.name());
                        }
                        System.out.println("0. Back");
                        int index = Integer.valueOf(scanner.nextLine());
                        if (index > companies.size()) {
                            System.out.println("Index does not exist!");
                        }
                        if (index != 0) {
                            carMenu(scanner, companies.get(index - 1));
                        } else {
                            System.out.println();
                        }
                    } else {
                        System.out.println("The company list is empty!");
                        System.out.println();
                    }
                }
                case "2" -> {
                    System.out.println("\nEnter the company name:");
                    String companyName = scanner.nextLine();
                    if (databaseHandler.insertCompany(companyName)) {
                        System.out.println("The company was created!");
                    }
                    System.out.println();
                }
            }
        }
    }

    private void carMenu(Scanner scanner, Company company) throws SQLException {
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
                    List<Car> cars = databaseHandler.selectAllCarsByCompany(company.companyId());
                    if (!cars.isEmpty()) {
                        System.out.println("\nCar list:");
                        int index = 1;
                        for (Car car : cars) {
                            System.out.println(index + ". " + car.name());
                            index++;
                        }
                    } else {
                        System.out.println("\nThe car list is empty!");
                    }
                }
                case "2" -> {
                    System.out.println("\nEnter the car name:");
                    String carName = scanner.nextLine();
                    if (databaseHandler.insertNewCar(carName, company.companyId())) {
                        System.out.println("The car was added!");
                    }
                }
            }
        }
    }

    private void selectCustomerMenu(Scanner scanner) throws SQLException {
        while (true) {
            // System.out.println();
            List<Customer> customers = databaseHandler.selectAllCustomers();
            if (!customers.isEmpty()) {
                System.out.println("Choose a customer:");
                for (Customer customer : customers) {
                    System.out.println(customer.customerId() + ". " + customer.name());
                }
                System.out.println("0. Back");
                int index = Integer.valueOf(scanner.nextLine());
                System.out.println();
                if (index > customers.size()) {
                    System.out.println("Index does not exist!");
                }
                if (index != 0) {
                    customerMenu(scanner, customers.get(index - 1));
                } else {
                    return;
                }
            } else {
                System.out.println("The customer list is empty!");
                System.out.println();
                return;
            }
        }
    }

    private void customerMenu(Scanner scanner, Customer customer) throws SQLException {
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
                    if (customer.carId() != 0) {
                        System.out.println("You've already rented a car!");
                        break;
                    }
                    List<Company> companies = databaseHandler.selectAllCompanies();
                    if (!companies.isEmpty()) {
                        System.out.println("Choose a company:");
                        for (Company company : companies) {
                            System.out.println(company.companyId() + ". " + company.name());
                        }
                        System.out.println("0. Back");
                        int index = Integer.valueOf(scanner.nextLine());
                        System.out.println();
                        if (index > companies.size()) {
                            System.out.println("Index does not exist!");
                        }
                        if (index != 0) {
                            Optional<Customer> updatedCustomer = rentACar(scanner, companies.get(index - 1), customer);
                            if (updatedCustomer.isPresent()) {
                                customer = updatedCustomer.get();
                            }
                        } else {
                            System.out.println("The company list is empty!");
                            return;
                        }
                    }
                }
                case "2" -> {
                    if (customer.carId() != 0) {
                        databaseHandler.returnCar(customer.customerId());
                        System.out.println("You've returned a rented car!");
                        customer = databaseHandler.getCustomer(customer.customerId());
                    } else {
                        System.out.println("You didn't rent a car!");
                    }
                }
                case "3" -> {
                    if (customer.carId() != 0) {
                        System.out.println("Your rented car:");
                        System.out.println(databaseHandler.getCar(customer.carId()).name());
                        System.out.println("Company:");
                        //System.out.println(databaseHandler.findCompanyNameByCarID(carResult.carId()));
                        System.out.println(databaseHandler.getCompany(databaseHandler.getCar(customer.carId()).companyId()).name());
                    } else {
                        System.out.println("You didn't rent a car!");
                    }
                }
            }
            System.out.println();
        }
    }

    private Optional<Customer> rentACar(Scanner scanner, Company company, Customer customer) throws SQLException {
        List<Car> cars = databaseHandler.selectAllAvailableCarsByCompany(company.companyId());
        if (!cars.isEmpty()) {
            System.out.println("\nCar list:");
            int index = 1;
            for (Car car : cars) {
                System.out.println(index + ". " + car.name());
                index++;
            }
            System.out.println("0. Back");
            System.out.println("\nChoose a car:");
            index = Integer.valueOf(scanner.nextLine());
            if (index > cars.size()) {
                System.out.println("Index does not exist!");
            }
            if (index != 0) {
                databaseHandler.rentCar(cars.get(index - 1).carId(), customer.customerId());
                System.out.println("You rented '" + cars.get(index - 1).name() + "'");
                return Optional.of(databaseHandler.getCustomer(customer.customerId()));
            }
        } else {
            System.out.println("No available cars in the " + company.name() + " company");
        }
        return Optional.empty();
    }

    private void createCustomer(Scanner scanner) {
        System.out.println("Enter the customer name:");
        String customerName = scanner.nextLine();
        if (databaseHandler.insertNewCustomer(customerName)) {
            System.out.println("The customer was added!");
        }
        System.out.println();
    }
}
