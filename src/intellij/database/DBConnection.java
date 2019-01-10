package intellij.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static Connection connection;
    private static boolean connected = false;
    private static Connection backupConnection;
    private static boolean backupConnected = false;

    /*
      this block of code is called on startup to connect to both the database
      it also connects to the back up database to dynamically update any change to the original database
     */
    static {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library", "root", "FBLA2019");
            connected = true;
            backupConnection = DriverManager.getConnection("jdbc:mysql://localhost:3306/library_backup", "root", "FBLA2019");
            backupConnected = true;
        }catch(ClassNotFoundException e){
            e.printStackTrace();
            System.err.println("This exception happened due to the class in the parameter of Class.forName() is incorrect. This could be due to a typo");
        }catch(SQLException e) {
            e.printStackTrace();
            System.err.println("Error: can't connect to database or incorrect SQL Statement");
        }
    }

    /**
     * used in functions to create a Statement object
     * @return the Connection object to use the method createStatement
     */
    public static Connection getConnection() {
        return connection;
    }

    /**
     * used at startup to verify a connection has been established with the data base
     * @return true if connected and false if not connected
     */
    public static boolean isConnnected() {
        return connected;
    }

    /**
     * used in functions to create a Statement object when backing up changes to the back up database
     * @return the Connection object to use the method createStatement
     */
    public static Connection getBackupConnection() {
        return backupConnection;
    }

    /**
     * used at startup to verify a connection has been established with the back up database
     * @return true if connected and false if not connected
     */
    public static boolean isBackupConnected() {
        return backupConnected;
    }
}