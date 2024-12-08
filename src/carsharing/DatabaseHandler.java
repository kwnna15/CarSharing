package carsharing;

import java.sql.*;

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

//            String drop = "drop table car";
//            stmt.executeUpdate(drop);

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

    boolean selectAllCompanies() throws SQLException {
        Statement statement = conn.createStatement();
        String select = "SELECT * FROM COMPANY ORDER BY ID";
        try (ResultSet companies = statement.executeQuery(select)) {
            if (!companies.isBeforeFirst()) {
                System.out.println("The company list is empty!");
                return false;
            } else {
                //System.out.println("\nCompany list:");
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

    void selectAllCarsByCompany(int companyId) throws SQLException {
        Statement statement = conn.createStatement();
        String select = "SELECT CAR.ID, CAR.NAME FROM CAR " +
                        "WHERE CAR.COMPANY_ID = "+ String.valueOf(companyId) +" ORDER BY CAR.ID";
        try (ResultSet cars = statement.executeQuery(select)) {
            if (!cars.isBeforeFirst()) {
                System.out.println("\nThe car list is empty!");
            } else {
                System.out.println("\nCar list:");
                int index = 1;
                while (cars.next()) {
                    String name = cars.getString("name");
                    System.out.println(index + ". " + name);
                    index++;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void closeConnection() throws SQLException {
        conn.close();
    }
}
