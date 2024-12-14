package carsharing;

import carsharing.domain.Car;
import carsharing.domain.Customer;

import java.sql.*;
import java.util.*;

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
            /*String drop = "drop table customer";
            stmt.executeUpdate(drop);*/

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

    void rentCar(int carId, int customerID) throws SQLException {
        Statement statement = conn.createStatement();
        String update = "UPDATE CUSTOMER SET RENTED_CAR_ID=" + carId +
                " WHERE ID = " + customerID;

        try {
            int updateRental = statement.executeUpdate(update);
            if (updateRental != 0) {
                System.out.println("You rented '" + findCarName(carId)+"'");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void returnCar(int customerId) throws SQLException {
        Statement statement = conn.createStatement();
        String update = "UPDATE CUSTOMER SET RENTED_CAR_ID=NULL" +
                " WHERE ID = " + customerId;

        try {
            statement.executeUpdate(update);
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

    List<Customer> selectAllCustomers() throws SQLException {
        Statement statement = conn.createStatement();
        String select = "SELECT * FROM CUSTOMER ORDER BY ID";
        try (ResultSet customers = statement.executeQuery(select)) {
            if (!customers.isBeforeFirst()) {
                System.out.println("The customer list is empty!");
                return List.of();
            } else {
                System.out.println("Choose a customer:");
                List<Customer> customerList = new ArrayList<>();
                while (customers.next()) {
                    Customer customer = getCustomer(customers);
                    customerList.add(customer);
                    System.out.println(customer.customerId() + ". " + customer.name());
                }
                return customerList;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    List<Car> selectAllAveilableCarsByCompany(int companyId) throws SQLException {
        Statement statement = conn.createStatement();
        String select = """
                SELECT *
                FROM Car c
                LEFT JOIN Customer cu ON c.id = cu.rented_car_id
                WHERE cu.rented_car_id IS NULL
                  AND c.company_id = %s
                """.formatted(companyId);
        try (ResultSet cars = statement.executeQuery(select)) {
            if (!cars.isBeforeFirst()) {
                return List.of();
            } else {
                System.out.println("\nCar list:");
                int index = 1;
                List<Car> companyCars = new ArrayList<>();
                while (cars.next()) {
                    int id = cars.getInt("id");
                    String name = cars.getString("name");
                    companyCars.add(new Car(id, name));
                    System.out.println(index + ". " + name);
                    index++;
                }
                return companyCars;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return List.of();

    }

    List<Car> selectAllCarsByCompany(int companyId) throws SQLException {
        Statement statement = conn.createStatement();
        String select = "SELECT CAR.ID, CAR.NAME FROM CAR " +
                "WHERE CAR.COMPANY_ID = " + companyId + " ORDER BY CAR.ID";
        try (ResultSet cars = statement.executeQuery(select)) {
            if (!cars.isBeforeFirst()) {
                System.out.println("\nThe car list is empty!");
                return List.of();
            } else {
                System.out.println("\nCar list:");
                int index = 1;
                List<Car> companyCars = new ArrayList<>();
                while (cars.next()) {
                    int id = cars.getInt("id");
                    String name = cars.getString("name");
                    companyCars.add(new Car(id, name));
                    System.out.println(index + ". " + name);
                    index++;
                }
                return companyCars;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return List.of();
    }

    void selectAllCarsByCustomer(int customerId) throws SQLException {
        Statement statement = conn.createStatement();
        String select = "SELECT CUSTOMER.RENTED_CAR_ID FROM CUSTOMER " +
                "WHERE CUSTOMER.ID = " + customerId;
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
                "WHERE CAR.ID = " + carId;
        try (ResultSet car = statement.executeQuery(select)) {
            if (car.next()) {
                return car.getString("name");
            } else {
                System.out.println("Couldnt find the car with id: " + carId);
                return null;
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
            if (company.next()) {
                return company.getString("name");
            } else {
                throw new IllegalStateException("Unknown company %s".formatted(companyId));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    Customer getCustomer(int customerId) throws SQLException {
        Statement statement = conn.createStatement();
        String select = "SELECT * FROM CUSTOMER " +
                "WHERE CUSTOMER.ID = " + String.valueOf(customerId);
        try (ResultSet customer = statement.executeQuery(select)) {
            if (customer.next()) {
                return getCustomer(customer);
            } else {
                throw new IllegalStateException("Unknown customer %s".formatted(customerId));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Customer getCustomer(ResultSet customer) throws SQLException {
        int id = customer.getInt("id");
        String name = customer.getString("name");
        int rentedCarId = customer.getInt("rented_car_id");
        return new Customer(id, name, rentedCarId);
    }

    String findCompanyNameByCarID(int carId) throws SQLException {
        Statement statement = conn.createStatement();
        int companyId;
        String selectCompanyId = "SELECT CAR.COMPANY_ID FROM CAR " +
                "WHERE CAR.ID = " + carId;

        try (ResultSet companyIDs = statement.executeQuery(selectCompanyId)) {
            if (companyIDs.next()) {
                companyId = companyIDs.getInt("COMPANY_ID");
            } else {
                System.out.println("Couldnt find the company with id: " + carId);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String selectCompanyName = "SELECT COMPANY.NAME FROM COMPANY " +
                "WHERE COMPANY.ID = " + companyId;
        try (ResultSet companyNames = statement.executeQuery(selectCompanyName)) {
            if (companyNames.next()) {
                return companyNames.getString("name");
            } else {
                System.out.println("Couldnt find the company with id: " + carId);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void closeConnection() throws SQLException {
        conn.close();
    }
}
