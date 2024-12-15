package carsharing;

import carsharing.domain.Car;
import carsharing.domain.Company;
import carsharing.domain.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
            stmt.executeUpdate(drop1);
            String drop = "drop table customer";
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

    boolean insertCompany(String companyName) {
        String insert = "INSERT INTO COMPANY (id, name) VALUES (default, ?)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(insert)) {
            preparedStatement.setString(1, companyName);
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    boolean insertNewCar(String carName, int companyId) {
        String insert = "INSERT INTO CAR (ID, name, COMPANY_ID) VALUES (default, ?, ?)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(insert)) {
            preparedStatement.setString(1, carName);
            preparedStatement.setInt(2, companyId);
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    boolean insertNewCustomer(String customerName) {
        String insert = "INSERT INTO CUSTOMER (ID, name, RENTED_CAR_ID) VALUES (default, ?, null)";
        try (PreparedStatement preparedStatement = conn.prepareStatement(insert)) {
            preparedStatement.setString(1, customerName);
            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    void rentCar(int carId, int customerID) throws SQLException {
        Statement statement = conn.createStatement();
        String update = "UPDATE CUSTOMER SET RENTED_CAR_ID=" + carId +
                " WHERE ID = " + customerID;
        try {
            statement.executeUpdate(update);
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

    List<Company> selectAllCompanies() throws SQLException {
        Statement statement = conn.createStatement();
        String select = "SELECT * FROM COMPANY ORDER BY ID";
        try (ResultSet companies = statement.executeQuery(select)) {
            if (!companies.isBeforeFirst()) {
                return List.of();
            } else {
                List<Company> companyList = new ArrayList<>();
                while (companies.next()) {
                    Company company = getCompany(companies);
                    companyList.add(company);
                }
                return companyList;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    List<Customer> selectAllCustomers() throws SQLException {
        Statement statement = conn.createStatement();
        String select = "SELECT * FROM CUSTOMER ORDER BY ID";
        try (ResultSet customers = statement.executeQuery(select)) {
            if (!customers.isBeforeFirst()) {
                return List.of();
            } else {
                List<Customer> customerList = new ArrayList<>();
                while (customers.next()) {
                    Customer customer = getCustomer(customers);
                    customerList.add(customer);
                }
                return customerList;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    List<Car> selectAllAvailableCarsByCompany(int companyId) throws SQLException {
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
                List<Car> companyCars = new ArrayList<>();
                while (cars.next()) {
                    int id = cars.getInt("id");
                    String name = cars.getString("name");
                    companyCars.add(new Car(id, name, companyId));
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
                return List.of();
            } else {
                List<Car> companyCars = new ArrayList<>();
                while (cars.next()) {
                    int id = cars.getInt("id");
                    String name = cars.getString("name");
                    companyCars.add(new Car(id, name, companyId));
                }
                return companyCars;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return List.of();
    }

    Car getCar(int carId) throws SQLException {
        Statement statement = conn.createStatement();
        String select = "SELECT * FROM CAR " +
                "WHERE CAR.ID = " + carId;
        try (ResultSet car = statement.executeQuery(select)) {
            if (car.next()) {
                return getCar(car);
            } else {
                throw new IllegalStateException("Unknown customer %s".formatted(carId));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    Customer getCustomer(int customerId) throws SQLException {
        Statement statement = conn.createStatement();
        String select = "SELECT * FROM CUSTOMER " +
                "WHERE CUSTOMER.ID = " + customerId;
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

    Company getCompany(int companyId) throws SQLException {
        Statement statement = conn.createStatement();
        String select = "SELECT * FROM COMPANY " +
                "WHERE COMPANY.ID = " + companyId;
        try (ResultSet company = statement.executeQuery(select)) {
            if (company.next()) {
                return getCompany(company);
            } else {
                throw new IllegalStateException("Unknown company %s".formatted(companyId));
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

    Company getCompany(ResultSet company) throws SQLException {
        int id = company.getInt("id");
        String name = company.getString("name");
        return new Company(id, name);
    }

    private Car getCar (ResultSet car) throws SQLException {
        int id = car.getInt("id");
        String name = car.getString("name");
        int companyId = car.getInt("company_id");
        return new Car(id, name, companyId);
    }

    void closeConnection() throws SQLException {
        conn.close();
    }
}
