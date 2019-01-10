package intellij;

import intellij.database.DBConnection;

import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * this class is used before a user signs in.
 * its primary use is for the log in function and to display the user info in profile page.
 * because i don't know if the user is a student/teacher/admin, I have this function in its own class
 */

public class User {

    private static String username;
    private static String firstName;
    private static String lastName;
    private static byte grade; //byte, in order to match the data type in database
    private static String accessLevel; //to determine which GUI to use. If the logged in user is an admin, then this field is set to admin, which tells the GUI that the user should be taken to the admin GUI.


    /**
     * The logIn function is called in log in page
     * I don't know if the user is a student, a teacher, or an admin yet, so I can't instantiate an object from them because i don't know what username to use yet
     *
     * The logIn function checks three tables for the combination of the username and password. The admins, teachers, and students tables
     * it checks the students table last, because it's the biggest able, so if he user is an admin or a teacher, then it will save work for the user
     *
     * After the user logs in, the logIn function assigns the data of the user to the fields in this class.
     * most importantly, it gives a value to the accessLevel, which tells the GUI which page to go.
     *
     * @param username - the username that the user inputs
     * @param password - the password that the user inputs
     * @return boolean for whether the combination of inputted username and password is true or false
     */
    public static boolean logIn(String username, String password){
        try{
            Statement statement = DBConnection.getConnection().createStatement();

            //compares the inputted username and password with the password in the admins table
            String query = "SELECT COUNT(Password) AS Password FROM admins WHERE (Username LIKE '" + username +  "') AND (Password LIKE '" + password + "')";
            ResultSet results = statement.executeQuery(query);
            int combinationCount = 0;
            while(results.next()){
                combinationCount = results.getInt("Password");
            }

            //if they match, then the admin logs in, and the fields of this class are initialized based on the user's data. Notice, the grade field is not initialized because only the students has a grade field
            if(combinationCount == 1) {
                query = "SELECT First_name, Last_name FROM admins WHERE Username LIKE '" + username + "'";
                results = statement.executeQuery(query);
                while(results.next()){
                    setUsername(username);
                    setFirstName(results.getString("First_name"));
                    setLastName(results.getString("Last_name"));
                    setAccessLevel("admin");
                }
                return true;
            }



            //compares the inputted username and password with the password in the teachers table
            query = "SELECT COUNT(Password) AS Password FROM teachers WHERE (Username LIKE '" + username +  "') AND (Password = '" + password + "')";
            results = statement.executeQuery(query);
            combinationCount = 0;
            while(results.next()){
                combinationCount = results.getInt("Password");
            }

            //if they match, then the admin logs in, and the fields of this class are initialized based on the user's data. Notice, the grade field is not initialized because only the students has a grade field
            if(combinationCount == 1) {
                query = "SELECT First_name, Last_name FROM teachers WHERE Username LIKE '" + username + "'";
                results = statement.executeQuery(query);
                while(results.next()){
                    setUsername(username);
                    setFirstName(results.getString("First_name"));
                    setLastName(results.getString("Last_name"));
                    setAccessLevel("teacher");
                }
                return true;
            }


            //compares the inputted password with the password in the table
            query = "SELECT COUNT(Password) AS Password FROM students WHERE (Username LIKE '" + username +  "') AND (Password LIKE '" + password + "')";
            results = statement.executeQuery(query);
            combinationCount = 0;
            while(results.next()){
                combinationCount = results.getInt("Password");
            }

            //if they match, then the admin logs in, and the fields of this class are initialized based on the user's data. Notice, the grade field is initialized because user in this situation would be a student
            if(combinationCount == 1) {
                query = "SELECT First_name, Last_name, Grade FROM students WHERE Username LIKE '" + username + "'";
                results = statement.executeQuery(query);
                while(results.next()){
                    setUsername(username);
                    setFirstName(results.getString("First_name"));
                    setLastName(results.getString("Last_name"));
                    setGrade(results.getByte("Grade"));
                    setAccessLevel("student");
                }
                return true;
            }

        }catch(SQLException e) {
            e.printStackTrace();
            System.err.println("Error: can't connect to database or incorrect SQL Statement");
        }
        return false; //if no such username existed in all three tables
    }

    /**
     * this function is used when a user signs out
     * it sets the values that log in initialized to null
     */
    public static void signOut(){
        setUsername(null);
        setFirstName(null);
        setLastName(null);
    }

    /**
     * @return the username of the Student/Teacher/Admin
     */
    public static String getUsername() {
        return username;
    }

    /**
     * @param username - String to assign to the username field
     */
    public static void setUsername(String username) {
        User.username = username;
    }

    /**
     * @return the first name of the Student/Teacher/Admin
     */
    public static String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName - String to assign to the firstName field
     */
    public static void setFirstName(String firstName) {
        User.firstName = firstName;
    }

    /**
     * @return the last name of the Student/Teacher/Admin
     */
    public static String getLastName() {
        return lastName;
    }

    /**
     * @param lastName - String to assign to the lastName field
     */
    public static void setLastName(String lastName) {
        User.lastName = lastName;
    }

    /**
     * @return the grade that the student is in
     */
    public static byte getGrade() {
        return grade;
    }

    /**
     * @param grade - byte to assign to the grade field
     */
    public static void setGrade(byte grade) {
        User.grade = grade;
    }

    /**
     * @return what type of access the logged in user has
     */
    public static String getAccessLevel() {
        return accessLevel;
    }

    /**
     * @param accessLevel - String to assign the level of access of the logged in user
     */
    public static void setAccessLevel(String accessLevel) {
        User.accessLevel = accessLevel;
    }
}