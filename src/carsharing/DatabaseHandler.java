package carsharing;

import java.sql.*;
import java.util.Scanner;

public class DatabaseHandler {

    private Connection conn = null;

    void createDatabase(String jdbcDriver, String dbUrl, String user, String pass) {
        Statement stmt = null;
        try {
            // STEP 1: Register JDBC driver
            Class.forName(jdbcDriver);

            //STEP 2: Open a connection
            System.out.println(dbUrl);
            System.out.println("Connecting to database...");
            connectToDatabase(dbUrl, user, pass);

            //STEP 3: Execute a query

            System.out.println("Creating table in given database...");
            stmt = conn.createStatement();
            conn.setAutoCommit(true);

            /*String drop1 = "drop table car";
            stmt.executeUpdate(drop1);*/
            String drop = "drop table customer";
            stmt.executeUpdate(drop);

            String companySql = "CREATE TABLE IF NOT EXISTS COMPANY" +
                                "(ID INTEGER not NULL AUTO_INCREMENT, " +
                                " NAME VARCHAR(255) not NULL UNIQUE, " +
                                " PRIMARY KEY ( ID ))";
            stmt.executeUpdate(companySql);

            String carsSql = "CREATE TABLE IF NOT EXISTS CAR" +
                             "(ID INTEGER not NULL AUTO_INCREMENT, " +
                             " NAME VARCHAR(255) not NULL, " +
                             " COMPANY_ID INTEGER not NULL, " +
                             " PRIMARY KEY ( ID ), " +
                             " FOREIGN KEY (COMPANY_ID) REFERENCES COMPANY(ID))";

            stmt.executeUpdate(carsSql);

            String customerSql = "CREATE TABLE IF NOT EXISTS CUSTOMER" +
                                 "(ID INTEGER not NULL AUTO_INCREMENT, " +
                                 " NAME VARCHAR(255) not NULL UNIQUE, " +
                                 " RENTED_CAR_ID INTEGER, " +
                                 " PRIMARY KEY ( ID ), " +
                                 " FOREIGN KEY (RENTED_CAR_ID) REFERENCES CAR(ID))";

            stmt.executeUpdate(customerSql);

            System.out.println("Created tables in given database...");

            // STEP 4: Clean-up environment
            stmt.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.out.println("Unable to close statement");
            }
        }
    }

    private void connectToDatabase(String dbUrl, String user, String pass) throws SQLException {
        if (conn == null) {
            conn = DriverManager.getConnection(dbUrl, user, pass);
        }
    }

    void insertCompanyName(String companyName) {
        String insert = "INSERT INTO COMPANY (id, name) VALUES (default, ?)";

        try (PreparedStatement preparedStatement = conn.prepareStatement(insert)) {
            preparedStatement.setString(1, companyName);
            preparedStatement.executeUpdate();
            System.out.println("The company was created!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void insertNewCar(String carName, int companyId) {
        String insert = "INSERT INTO CAR (ID, name, COMPANY_ID) VALUES (default, ?, ?)";

        try (PreparedStatement preparedStatement = conn.prepareStatement(insert)) {
            preparedStatement.setString(1, carName);
            preparedStatement.setInt(2, companyId);
            preparedStatement.executeUpdate();
            System.out.println("The car was added!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void insertNewCustomer(String customerName) {
        String insert = "INSERT INTO CUSTOMER (ID, name, RENTED_CAR_ID) VALUES (default, ?, null)";

        try (PreparedStatement preparedStatement = conn.prepareStatement(insert)) {
            preparedStatement.setString(1, customerName);
            preparedStatement.executeUpdate();
            System.out.println("The customer was added!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void rentCar(Scanner scanner, int carId, int customerID) throws SQLException {
        Statement statement = conn.createStatement();
        String update = "UPDATE CUSTOMER SET RENTED_CAR_ID=" + carId;
        try {
            boolean isUpdated = statement.execute(update);
            if (isUpdated) {
                System.out.println("You rented " + findCarName(carId));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    boolean selectAllCompanies() throws SQLException {
        Statement statement = conn.createStatement();
        String select = "SELECT * FROM COMPANY ORDER BY ID";
        try (ResultSet companies = statement.executeQuery(select)) {
            if (!companies.isBeforeFirst()) {
                System.out.println("The company list is empty!");
                return false;
            } else {
                while (companies.next()) {
                    int id = companies.getInt("id");
                    String name = companies.getString("name");
                    System.out.println(id + ". " + name);
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    boolean selectAllCustomers() throws SQLException {
        Statement statement = conn.createStatement();
        String select = "SELECT * FROM CUSTOMER ORDER BY ID";
        try (ResultSet customers = statement.executeQuery(select)) {
            if (!customers.isBeforeFirst()) {
                System.out.println("The customer list is empty!");
                return false;
            } else {
                System.out.println("Choose a customer:");
                while (customers.next()) {
                    int id = customers.getInt("id");
                    String name = customers.getString("name");
                    System.out.println(id + ". " + name);
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    boolean selectAllCarsByCompany(int companyId) throws SQLException {
        Statement statement = conn.createStatement();
        String select = "SELECT CAR.ID, CAR.NAME FROM CAR " +
                        "WHERE CAR.COMPANY_ID = " + String.valueOf(companyId) + " ORDER BY CAR.ID";
        try (ResultSet cars = statement.executeQuery(select)) {
            if (!cars.isBeforeFirst()) {
                System.out.println("\nThe car list is empty!");
                return false;
            } else {
                System.out.println("\nCar list:");
                int index = 1;
                while (cars.next()) {
                    String name = cars.getString("name");
                    System.out.println(index + ". " + name);
                    index++;
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    void selectAllCarsByCustomer(int customerId) throws SQLException {
        Statement statement = conn.createStatement();
        String select = "SELECT CUSTOMER.RENTED_CAR_ID FROM CUSTOMER " +
                        "WHERE CUSTOMER.ID = " + String.valueOf(customerId);
        try (ResultSet car = statement.executeQuery(select)) {
            if (!car.isBeforeFirst()) {
                System.out.println("You didn't rent a car!");
            } else {
                while (car.next()) {
                    int carId = car.getInt("RENTED_CAR_ID");
                    if (carId == 0) {
                        System.out.println("You didn't rent a car!");
                        return;
                    }
                    String carName = findCarName(carId);
                    if (carName == null) {
                        throw new IllegalStateException("Unable to find car for id %s".formatted(carId));
                    }
                    System.out.println("Your rented car:");
                    System.out.println(findCarName(carId));
                    System.out.println("Company:");
                    System.out.println(findCompanyNameByCarID(carId));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    String findCarName(int carId) throws SQLException {
        Statement statement = conn.createStatement();
        String select = "SELECT CAR.NAME FROM CAR " +
                        "WHERE CAR.ID = " + String.valueOf(carId);
        try (ResultSet car = statement.executeQuery(select)) {
            if (!car.isBeforeFirst()) {
                System.out.println("Couldnt find the car with id: " + carId);
                return null;
            } else {
                return car.getString("name");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    String findCompanyName(int companyId) throws SQLException {
        Statement statement = conn.createStatement();
        String select = "SELECT COMPANY.NAME FROM COMPANY " +
                        "WHERE COMPANY.ID = " + String.valueOf(companyId);
        try (ResultSet company = statement.executeQuery(select)) {
            if (!company.isBeforeFirst()) {
                System.out.println("Couldnt find the car with id: " + companyId);
                return null;
            } else {
                return company.getString("name");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    String findCompanyNameByCarID(int carId) throws SQLException {
        Statement statement = conn.createStatement();
        int companyId;
        String selectCompanyId = "SELECT CAR.COMPANY_ID FROM CAR " +
                                 "WHERE CAR.ID = " + String.valueOf(carId);

        try (ResultSet companyIDs = statement.executeQuery(selectCompanyId)) {
            if (!companyIDs.isBeforeFirst()) {
                System.out.println("Couldnt find the company with id: " + carId);
                return null;
            } else {
                companyId = companyIDs.getInt("COMPANY_ID");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String selectCompanyName = "SELECT COMPANY.NAME FROM COMPANY " +
                                   "WHERE COMPANY.ID = " + String.valueOf(companyId);
        try (ResultSet companyNames = statement.executeQuery(selectCompanyName)) {
            if (!companyNames.isBeforeFirst()) {
                System.out.println("Couldnt find the company with id: " + carId);
                return null;
            } else {
                return companyNames.getString("name");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    boolean customerHasRentedACar(int customerId) throws SQLException {
        Statement statement = conn.createStatement();
        String select = "SELECT CUSTOMER.RENTED_CAR_ID FROM CUSTOMER " +
                        "WHERE CUSTOMER.ID = " + String.valueOf(customerId);
        try (ResultSet carInfo = statement.executeQuery(select)) {
            int numberOfCars;
            return !carInfo.isBeforeFirst();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void closeConnection() throws SQLException {
        conn.close();
    }
}
