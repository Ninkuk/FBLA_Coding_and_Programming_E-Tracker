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
    private static String accessLevel;


    /**
     * The logIn function is called in log in page
     * I don't know if the user is a student, a teacher, or an admin yet, so I can't instantiate an object from them because i don't know what username to use yet
     *
     * The logIn function checks three tables for the combination of the username and password. The admins, teachers, and students tables
     * it checks the students table last, because it's the biggest able, so if he user is an admin or a teacher, then it will save work for the user
     *
     * After the user logs in, the logIn function instantiates a student, teacher, or admin object depending on who logged in order to know to which gui to go.
     * for example, if an admin logs in, then they will go the gui meant for an admin, but if a student logs in, then a student object is instantiated so that user is taken tothe student gui
     * On the GUI side of this application, I check which object has a value, and that's how I know which one was instantiated
     *
     * @param username - the username that the user inputs
     * @param password - the password that the user inputs
     * @return boolean for whether the combination of inputted username and password is true or false
     */
    public static boolean logIn(String username, String password){
        try{
            Statement statement = DBConnection.getConnection().createStatement();

            /*
             * checks if the username exists in the admins table
             * if the username doesn't exist, then it will skip the process of hashing and salting the inputted password to be checked with the password in the table
             * that way it saves the user some loading time
             */
            String query = "SELECT COUNT(Username) AS Username FROM admins WHERE Username LIKE '" + username + "'";
            ResultSet results = statement.executeQuery(query);
            int usernameCount = 0;
            while(results.next()){
                usernameCount = results.getInt("Username");
            }
            if(usernameCount == 1) {
                //checks if there is a matching password, but the password inputted has to be salted and hashed in the same way as the password in the table
                //queries the unique salt of the username
                query = "SELECT Salt FROM admins WHERE Username LIKE '" + username + "'";
                results = statement.executeQuery(query);
                String salt = "";
                while(results.next()){
                    salt = results.getString("Salt");
                }

                //from the queried salt, the following code creates a salted hash of the inputted password
                query = "SELECT SHA2(CONCAT('" + salt + "', '" + password + "'), 512) AS Password";
                results = statement.executeQuery(query);
                String saltedHashedPassword = "";
                while(results.next()){
                    saltedHashedPassword = results.getString("Password");
                }

                //compares the inputted password with the password in the table
                query = "SELECT COUNT(Password) AS Password FROM admins WHERE (Username LIKE '" + username +  "') AND (Password LIKE '" + saltedHashedPassword + "')";
                results = statement.executeQuery(query);
                int combinationCount = 0;
                while(results.next()){
                    combinationCount = results.getInt("Password");
                }

                //if they match, then the admin logs in, and an Admin account is initialized
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
            }

            /*
             * checks if the username exists in the teachers table
             * if the username doesn't exist, then it will skip the process of hashing and salting the inputted password to be checked with the password in the table
             * that way it saves the user some loading time
             *
             */
            query = "SELECT COUNT(Username) AS Username FROM teachers WHERE Username LIKE '" + username + "'";
            results = statement.executeQuery(query);
            usernameCount = 0;
            while(results.next()){
                usernameCount = results.getInt("Username");
            }
            if(usernameCount == 1) {
                //checks if there is a matching password, but the password inputted has to be salted and hashed in the same way as the password in the table
                //queries the unique salt of the username
                query = "SELECT Salt FROM teachers WHERE Username LIKE '" + username + "'";
                results = statement.executeQuery(query);
                String salt = "";
                while(results.next()){
                    salt = results.getString("Salt");
                }

                //from the queried salt, the following code creates a salted hash of the inputted password
                query = "SELECT SHA2(CONCAT('" + salt + "', '" + password + "'), 512) AS Password";
                results = statement.executeQuery(query);
                String saltedHashedPassword = "";
                while(results.next()){
                    saltedHashedPassword = results.getString("Password");
                }

                //compares the inputted password with the password in the table
                query = "SELECT COUNT(Password) AS Password FROM teachers WHERE (Username LIKE '" + username +  "') AND (Password = '" + saltedHashedPassword + "')";
                results = statement.executeQuery(query);
                int combinationCount = 0;
                while(results.next()){
                    combinationCount = results.getInt("Password");
                }

                //if they match, then the teacher logs in, and an Teacher account is initialized
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
            }

            /*
             * checks if the username exists in the students table
             * if the username doesn't exist, then it will skip the process of hashing and salting the inputted password to be checked with the password in the table
             * that way it saves the user some loading time
             * */
            query = "SELECT COUNT(Username) AS Username FROM students WHERE Username LIKE '" + username + "'";
            results = statement.executeQuery(query);
            usernameCount = 0;
            while(results.next()){
                usernameCount = results.getInt("Username");
            }
            if(usernameCount == 1) {
                //checks if there is a matching password, but the password inputted has to be salted and hashed in the same way as the password in the table
                //queries the unique salt of the username
                query = "SELECT Salt FROM students WHERE Username LIKE '" + username + "'";
                results = statement.executeQuery(query);
                String salt = "";
                while(results.next()){
                    salt = results.getString("Salt");
                }

                //from the queried salt, the following code creates a salted hash of the inputted password
                query = "SELECT SHA2(CONCAT('" + salt + "', '" + password + "'), 512) AS Password";
                results = statement.executeQuery(query);
                String saltedHashedPassword = "";
                while(results.next()){
                    saltedHashedPassword = results.getString("Password");
                }

                //compares the inputted password with the password in the table
                query = "SELECT COUNT(Password) AS Password FROM students WHERE (Username LIKE '" + username +  "') AND (Password LIKE '" + saltedHashedPassword + "')";
                results = statement.executeQuery(query);
                int combinationCount = 0;
                while(results.next()){
                    combinationCount = results.getInt("Password");
                }

                //if they match, then the student logs in, and an student account is initialized
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