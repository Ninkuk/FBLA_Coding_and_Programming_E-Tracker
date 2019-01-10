package intellij;

import intellij.database.DBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Teacher{

    private String username;
    private String firstName;
    private String lastName;

    /**
     * a function that Teachers can use in their profile page to change their password
     * The function is static so it cam be called on the GUI side without creating a admin object
     * if a Teacher object is created, then I would have to query the data necessary to pass to the parameters, which is unnecessary since I only need this function
     * @param password - The new password set for the user
     * @return String that will be printed to front-end to notify the user the result of their action
     */
    public static String changePassword(String password){
        try {
            Statement statement = DBConnection.getConnection().createStatement();
            Statement backupStatement = DBConnection.getBackupConnection().createStatement(); //connects to backup database

            //updates the old password with the new password
            String query = "UPDATE teachers SET Password = '" + password + "' WHERE (Username LIKE '" + User.getUsername() + "')";
            statement.executeUpdate(query);
            backupStatement.executeUpdate(query);//updates the old password with the new password in the back up database to dynamically back up data
        }catch(SQLException e) {
            e.printStackTrace();
            System.err.println("Error: can't connect to database or incorrect SQL Statement");
        }
        return "Your password has been changed";
    }

    /**
     * @return the username of the Student/Teacher/Admin
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username - String to assign to the username field
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the first name of the Student/Teacher/Admin
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName - String to assign to the firstName field
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return the last name of the Student/Teacher/Admin
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName - String to assign to the lastName field
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}