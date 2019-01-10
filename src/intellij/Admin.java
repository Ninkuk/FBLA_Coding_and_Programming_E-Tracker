package intellij;

import intellij.database.DBConnection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class Admin{

    private String username;
    private String firstName;
    private String lastName;

    /**
     * a function that Admins can use in their profile page to change their password
     * the function queries the salt of the username and uses it to hash the new password
     * The salt is used to prevent rainbow tabling and the hash is to avoid saving the passwords in plain text
     * The function is static so it cam be called on the GUI side without creating a admin object
     * if a admin object is created, then I would have to query the data necessary to pass to the parameters, which is unnecessary since I only need this function
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
            String query = "SELECT Salt FROM admins WHERE (Username LIKE '" + User.getUsername() + "')";
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
            query = "UPDATE admins SET Password = '" + saltedHashedPassword + "' WHERE (Username LIKE '" + User.getUsername() + "')";
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
     * The addStudents function adds a student to the students table and to the students backup table.
     * it generated a username from the first and last name of the student
     * a salt is generated to be stored in the row of the student being created
     * the salt is used to create a salted hashed password for security
     * the salt is to prevent rainbow tabling, and the hashing is to avoid storing passwords in plain text
     *
     * @param firstName - the first name of the student being created
     * @param lastName - the last name of the student being created
     * @param password - the password of the student being created
     * @param grade - the grade of the student being created
     * @return the generated username of the new student
     */
    public static String addStudent(String firstName, String lastName, String password, byte grade){

        String username = null;//declared outside of the try catch block so it can be returned without error

        try{
            Statement statement = DBConnection.getConnection().createStatement();
            Statement backupStatement = DBConnection.getBackupConnection().createStatement();//Creates Statement from backup database connection to dynamically update

            /*
             Creates username from first and last name of student
             Username follows the convention of first letter of first name + the last name
             if that username exists, then number 1 is added to the username and is incremented for each student that has the same first and last name currently in the table
             */
            username = firstName.charAt(0) + lastName; //creating a new username based off of the first and last name of the student
            String query = "SELECT COUNT(Username) AS Username FROM students WHERE Username LIKE '" + username + "'";
            ResultSet results = statement.executeQuery(query);//checks how many usernames like this exist
            int usernameCount = 0;
            while(results.next()) {
                usernameCount = results.getInt("Username");
            }
            if(usernameCount > 0) {
                username += usernameCount;//if a username already exists like the one generated from first and last name, then a number is added to the end of the number to make it distinct
            }

            //generates a random salt to use when hashing the password, in order to prevent rainbow tabling
            query = "SELECT MD5(RAND()) AS Salt";
            results = statement.executeQuery(query);
            String salt = "";
            while(results.next()) {
                salt = results.getString("Salt");
            }

            //hashes the password entered by concatenating the password with the unique salt. This is done to prevent rainbow tabling and to avoid writing the password in plain text
            query = "SELECT SHA2(CONCAT('" + salt + "', '" + password + "'), 512) AS Password";
            results = statement.executeQuery(query);
            String saltedHashedPassword = "";
            while(results.next()) {
                saltedHashedPassword = results.getString("Password");
            }

            query = "INSERT INTO students (Username, Salt, Password, First_name, Last_name, Grade) VALUES ('" + username + "', '" + salt + "', '" + saltedHashedPassword + "', '" + firstName + "', '" + lastName + "', '" + grade + "')";
            statement.executeUpdate(query);//creates a new row in the student table with the first and last name inputted, the username generated from that, the salt generated, and the salted hashed password from the password inputted
            backupStatement.executeUpdate(query);//creates a new row in the back up student table with the first and last name inputted, the username generated from that, the salt generated, and the salted hashed password from the password inputted
        }catch(SQLException e) {
            e.printStackTrace();
            System.err.println("Error: can't connect to database or incorrect SQL Statement");
        }
        return username; //a username is returned to notify the admin of the username created from first and last name
    }

    /**
     * Deletes student from students table and students backup table
     * @param username - the username of the student getting removed
     */
    public static void removeStudent(String username){
        try{
            Statement statement = DBConnection.getConnection().createStatement();
            Statement backupStatement = DBConnection.getBackupConnection().createStatement();//Creates Statement from backup database connection to dynamically update

            String query = "DELETE FROM students WHERE Username LIKE '" + username + "'";
            statement.executeUpdate(query);//deletes student from students table
            backupStatement.executeUpdate(query);//deletes student from students backup table
        }catch(SQLException e) {
            e.printStackTrace();
            System.err.println("Error: can't connect to database or incorrect SQL Statement");
        }
    }

    /**
     * Updates the information of a student
     * Updates in both the students table and the back up students table
     * uses salt and hashing when changing the password
     *
     * @param username - used to identify what teacher is being updated. Is NOT for setting a new username. the username is based off of first and last names
     * @param firstName - new value for first name.
     * @param lastName - new value for last name.
     * @param password - new value for password.
     * @param grade - new value for grade
     * @return returns a false if the username specified does not exist. Returns true if the update was a sucess
     */
    public static boolean updateStudent(String username,String firstName, String lastName, String password, byte grade){
        try{
            Statement statement = DBConnection.getConnection().createStatement();
            Statement backupStatement = DBConnection.getBackupConnection().createStatement();//Creates Statement from backup database connection to dynamically update

            //queries from the students table to check if the username is unique
            String query = "SELECT COUNT(Username) AS Username FROM students WHERE Username LIKE '" + username + "'";
            ResultSet results = statement.executeQuery(query);//queries how many usernames exist with the given username
            int usernameCount = 0;
            while(results.next()) {
                usernameCount = results.getInt("Username");
            }

            //if the username exists, then the function will update the student, otherwise a false will be returned
            if(usernameCount == 1) {
                //queries the unique salt of the username to use in hashing the password
                query = "SELECT Salt FROM students WHERE (Username LIKE '" + username + "')";
                results = statement.executeQuery(query);
                String salt = "";
                while(results.next()) {
                    salt = results.getString("Salt");
                }

                //hashes the password entered by concatenating the password with the unique salt. This is done to prevent rainbow tabling and to avoid writing the password in plain text
                query = "SELECT SHA2(CONCAT('" + salt + "', '" + password + "'), 512) AS Password";
                results = statement.executeQuery(query);
                String saltedHashedPassword = "";
                while(results.next()) {
                    saltedHashedPassword = results.getString("Password");
                }
            /*
             Creates a new username from first and last name of student
             Username follows the convention of first letter of first name + the last name
             if that username exists, then number 1 is added to the username and is incremented for each student that has the same first and last name currently in the table
             */
                String newUsername = firstName.charAt(0) + lastName; //creating a new username based off of the first and last name of the student
                query = "SELECT COUNT(Username) AS Username FROM students WHERE Username LIKE '" + newUsername + "'";
                results = statement.executeQuery(query);//checks how many usernames like this exist
                usernameCount = 0;
                while(results.next()) {
                    usernameCount = results.getInt("Username");
                }

                if(usernameCount > 0) {
                    newUsername += usernameCount;//if a username already exists like the one generated from first and last name, then a number is added to the end of the number to make it distinct
                    query = "UPDATE students SET Username = '" + newUsername + "', First_name = '" + firstName + "', Last_name = '" + lastName + "', Password = '" + saltedHashedPassword + "', Grade = '" + grade + "' WHERE Username LIKE '" + username + "'";
                    statement.executeUpdate(query);//updates table with newUsername, new first name, and new last name
                    backupStatement.executeUpdate(query);//updates to backup table with newUsername, new first name, new last name, new password, and new grade
                    return true;
                }else {
                    //if no username is similar to what is created, then the new username is updated in the students table without adding any numbers
                    query = "UPDATE students SET Username = '" + newUsername + "', First_name = '" + firstName + "', Last_name = '" + lastName + "', Password = '" + saltedHashedPassword + "', Grade = '" + grade + "' WHERE Username LIKE '" + username + "'";
                    statement.executeUpdate(query);//updates to backup table with newUsername, new first name, new last name, new password, and new grade
                    backupStatement.executeUpdate(query);//updates to backup table with newUsername, new first name, new last name, new password, and new grade
                    return true;
                }
            }
        }catch(SQLException e) {
            e.printStackTrace();
            System.err.println("Error: can't connect to database or incorrect SQL Statement");
        }
        return false;
    }

    /**
     * adds a book to the books table and back up table
     * checks if the redemption given is unique.
     * if not, then the function returns false
     *
     * @param redemptionCode - the redemptionCode int value to be added to the new row. Has to be unique
     * @param title - the title of the book added
     * @param author - the author of the book added
     * @param pages - the pages of the book added
     * @param ISBN - the ISBN of the book added
     * @return a false if the redemption code is not unique or if an exception occurred
     */
    public static boolean addBook(int redemptionCode, String title, String author, int pages, long ISBN){
        try{
            Statement statement = DBConnection.getConnection().createStatement();
            Statement backupStatement = DBConnection.getBackupConnection().createStatement();//Creates Statement from backup database connection to dynamically update

            //queries how many rows exist with the redemption code passed in the parameter
            String query = "SELECT COUNT(Redemption_code) AS Redemption_code FROM books WHERE Redemption_code = '" + redemptionCode + "'";
            ResultSet results = statement.executeQuery(query);
            int redemptionCodeCount = 0;
            while(results.next()) {
                redemptionCodeCount = results.getInt("Redemption_code");
            }

            //if the redemption code is unique, then the book is created
            if(redemptionCodeCount == 0) {
                query = "INSERT INTO books (Redemption_code, Title, Author, Pages, ISBN) VALUES ('" + redemptionCode + "', '" + title + "', '" + author + "', '" + pages + "', '" + ISBN + "')";
                statement.executeUpdate(query);//creates a new row in the books table with the passed parameters
                backupStatement.executeUpdate(query);//creates a new row in the books backup table with the passed parameters
                return true;
            }

        }catch(SQLException e) {
            e.printStackTrace();
            System.err.println("Error: can't connect to database or incorrect SQL Statement");
            return false;//returns false if an exception happened
        }
        return false;//returns false if the redemption code entered is not unique
    }

    /**
     * Deletes book from books table and books backup table
     * @param redemptionCode - the redemption code of the book getting removed
     */
    public static void removeBook(int redemptionCode){
        try{
            Statement statement = DBConnection.getConnection().createStatement();
            Statement backupStatement = DBConnection.getBackupConnection().createStatement();//Creates Statement from backup database connection to dynamically update

            String query = "DELETE FROM books WHERE Redemption_code = '" + redemptionCode + "'";
            statement.executeUpdate(query);//deletes book from books table
            backupStatement.executeUpdate(query);//deletes book from books backup table
        }catch(SQLException e) {
            e.printStackTrace();
            System.err.println("Error: can't connect to database or incorrect SQL Statement");
        }
    }

    /**
     * updates the information of the book in the books table and the books backup table
     * the redemption code can be changed, but it has to be unique
     * if the redemption code has not been changed, then the function skips checking if it's unique
     *
     * @param redemptionCode -
     * @param newRedemptionCode -
     * @param title - the title of the book
     * @param author - the author of the book
     * @param pages - the pages of the book
     * @param ISBN - the ISBN of the book
     * @return a false if the new redemption code is not unique or if an exception occurred
     */
    public static boolean updateBook(int redemptionCode, int newRedemptionCode, String title, String author, int pages, long ISBN){
        try{
            Statement statement = DBConnection.getConnection().createStatement();
            Statement backupStatement = DBConnection.getBackupConnection().createStatement();//Creates Statement from backup database connection to dynamically update

            //if the redemption code has not been changed, then there is no need to check if it is unique
            if(redemptionCode == newRedemptionCode) {
                String query = "UPDATE books SET Title = '" + title + "', Author = '" + author + "', Pages = '" + pages + "', ISBN = '" + ISBN + "' WHERE Redemption_code = '" + redemptionCode + "'";
                statement.executeUpdate(query);//updates the row in the books table with the passed parameters
                backupStatement.executeUpdate(query);//updates the row in the books backup table with the passed parameters
                return true;
            }else {
                //if the redemption code has been changed, then I need to check if it's unique
                String query = "SELECT COUNT(Redemption_code) AS Redemption_code FROM books WHERE Redemption_code = '" + newRedemptionCode + "'";
                ResultSet results = statement.executeQuery(query);//queries how many rows exist with the new redemption code passed in the parameter
                int redemptionCodeCount = 0;
                while(results.next()) {
                    redemptionCodeCount = results.getInt("Redemption_code");
                }

                //if the new redemption code is unique, then it updates the row in the books table and books backup table
                if(redemptionCodeCount == 0){
                    query = "UPDATE books SET Redemption_code = '" + newRedemptionCode + "', Title = '" + title + "', Author = '" + author + "', Pages = '" + pages + "', ISBN = '" + ISBN + "' WHERE Redemption_code = '" + redemptionCode + "'";
                    statement.executeUpdate(query);//updates the row in the books table with the passed parameters
                    backupStatement.executeUpdate(query);//updates the row in the books backup table with the passed parameters
                    return true;
                }
            }
        }catch(SQLException e) {
            e.printStackTrace();
            System.err.println("Error: can't connect to database or incorrect SQL Statement");
            return false;//returns false if an exception happened
        }
        return false;//returns false if the redemption code entered is not unique
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