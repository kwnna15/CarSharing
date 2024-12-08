package carsharing;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class Main {

    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "org.h2.Driver";
    static String DB_URL = "jdbc:h2:./src/carsharing/db";

    //  Database credentials
    static final String USER = "";
    static final String PASS = "";

    public static void main(String[] args) {
        // write your code here

        if (args.length > 0) {
            if (args[0].equals("-databaseFileName"))
                DB_URL = DB_URL + "/" + args[1];
        } else{
            DB_URL = DB_URL + "/" + "CompanyDB";
        }
        //System.out.println(DB_URL);

        Connection conn = null;
        Statement stmt = null;
        try {
            // STEP 1: Register JDBC driver
            Class.forName(JDBC_DRIVER);

            //STEP 2: Open a connection
            System.out.println(DB_URL);
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL,USER,PASS);

            //STEP 3: Execute a query
            System.out.println("Creating table in given database...");
            stmt = conn.createStatement();
            conn.setAutoCommit(true);
            String drop = "DROP TABLE   COMPANY ";
            String sql =  "CREATE TABLE   COMPANY " +
                          "(ID INTEGER not NULL AUTO_INCREMENT, " +
                          " NAME VARCHAR(255) not NULL UNIQUE, " +
                          " PRIMARY KEY ( ID ))";
            //stmt.executeUpdate(drop);
            stmt.executeUpdate(sql);
            System.out.println("Created table in given database...");

            HandleUserInput UI = new HandleUserInput();
            UI.manageInput();
            // STEP 4: Clean-up environment
            stmt.close();
            conn.close();
        } catch(SQLException se) {
            //Handle errors for JDBC
            se.printStackTrace();
        } catch(Exception e) {
            //Handle errors for Class.forName
            e.printStackTrace();
        } finally {
            //finally block used to close resources
            try{
                if(stmt!=null) stmt.close();
            } catch(SQLException se2) {
            } // nothing we can do
            try {
                if(conn!=null) conn.close();
            } catch(SQLException se){
                se.printStackTrace();
            } //end finally try
        } //end try
        System.out.println("Goodbye!");
    }
}