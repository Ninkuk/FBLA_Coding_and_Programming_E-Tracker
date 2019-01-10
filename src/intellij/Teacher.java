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
     * the function queries the salt of the username and uses it to hash the new password
     * The salt is used to prevent rainbow tabling and the hash is to avoid saving the passwords in plain text
     *
     * The function is static so it cam be called on the GUI side without creating a teacher object
     * if a teacher object is created, then I would have to query the data necessary to pass to the parameters, which is unnecessary since I only need this function
     * @param password - The new password set for the user
     * @return String that will be printed to front-end to notify the user the result of their action
     */
    public static String changePassword(String password){
        try {
            Statement statement = DBConnection.getConnection().createStatement();

            //checks that the password is at least 6 characters to make sure that the passwords are not easily susceptible to hacking
            if(password.length() <= 5 ) {
                return "Password must be longer than 5 characters";
            }

            //queries the unique salt of the username to use in hashing the password
            String query = "SELECT Salt FROM teachers WHERE (Username LIKE '" + User.getUsername() + "')";
            ResultSet results = statement.executeQuery(query);
            String salt = "";
            while(results.next()){
                salt = results.getString("Salt");
            }

            //hashes the password entered by concatenating the password with the unique salt. This is done to prevent rainbow tabling and to avoid writing the password in plain text
            query = "SELECT SHA2(CONCAT('" + salt + "', '" + password + "'), 512) AS Password";
            results = statement.executeQuery(query);
            String saltedHashedPassword = "";
            while(results.next()){
                saltedHashedPassword = results.getString("Password");
            }


            //updates the old password with the new password
            query = "UPDATE teachers SET Password = '" + saltedHashedPassword + "' WHERE (Username LIKE '" + User.getUsername() + "')";
            int rowsUpdated = statement.executeUpdate(query);
            if(rowsUpdated == 0) {
                return "Error, unable to change password. Check the Help page or contact us for more information"; //will only return this if the password has not been changed
            }

            //updates the old password with the new password in the back up database to dynamically back up data
            Statement backupStatement = DBConnection.getBackupConnection().createStatement(); //connects to backup database
            backupStatement.executeUpdate(query);

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