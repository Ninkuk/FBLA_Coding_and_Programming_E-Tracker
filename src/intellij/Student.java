package intellij;

import intellij.database.DBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;



public class Student{

    private String username;
    private String firstName;
    private String lastName;
    private byte grade;

    /**
     * used in teacher and admin home page to display the student information
     * @param username - String to assign username of the student
     * @param firstName - String to assign firstName of the student
     * @param lastName - String to assign lastName of the student
     * @param grade - byte to assign grade of the student
     */
    public Student(String firstName, String lastName, String username, byte grade){
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setUsername(username);
        this.setGrade(grade);
    }

    /**
     * a function that students can use in their profile page to change their password
     * The function is static so it cam be called on the GUI side without creating a admin object
     * if a student object is created, then I would have to query the data necessary to pass to the parameters, which is unnecessary since I only need this function
     * @param password - The new password set for the user
     * @return String that will be printed to front-end to notify the user the result of their action
     */
    public static String changePassword(String password){
        try {
            Statement statement = DBConnection.getConnection().createStatement();
            Statement backupStatement = DBConnection.getBackupConnection().createStatement(); //connects to backup database

            //updates the old password with the new password
            String query = "UPDATE students SET Password = '" + password + "' WHERE (Username LIKE '" + User.getUsername() + "')";
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

    /**
     * @return the grade that the student is in
     */
    public byte getGrade() {
        return grade;
    }

    /**
     * @param grade - byte to assign to the grade field
     */
    public void setGrade(byte grade) {
        this.grade = grade;
    }

}