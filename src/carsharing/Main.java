package carsharing;


import java.sql.SQLException;

public class Main {

    // JDBC driver name and database URL
    private static final String JDBC_DRIVER = "org.h2.Driver";
    private static String dbUrl = "jdbc:h2:./src/carsharing/db";

    //  Database credentials
    private static final String USER = "";
    private static final String PASS = "";

    public static void main(String[] args) throws SQLException {

        if (args.length > 0) {
            if (args[0].equals("-databaseFileName"))
                dbUrl = dbUrl + "/" + args[1];
        } else{
            dbUrl = dbUrl + "/" + "CompanyDB";
        }

        UserInputHandler inputHandler= new UserInputHandler(JDBC_DRIVER, dbUrl, USER, PASS);
        inputHandler.manageInput();
    }
}