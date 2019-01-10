package intellij;

import intellij.database.DBConnection;

import java.time.LocalDate;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZoneId;

public class Book {
    private int redemptionCode;
    private String title;
    private String author;
    private int pages;
    private long ISBN;
    private String heldBy;
    private LocalDate dateOut;
    private LocalDate dueBy;
    private int renewalCount;
    private int reportId; //reportId is used to reference the row in reports table when renewing and returning
    private LocalDate dateIn;

    /**
     * This constructor is used in the library page to display books
     * because the library page has a table that only displays redemption code, Title, author, pages, and ISBN, there are no other parameters
     *
     * @param redemptionCode - int to assign the book's redemption code
     * @param title          - String to assign the book's title
     * @param author         - String to assign the book's author
     * @param pages          - int to assign how many pages the book has
     * @param ISBN           - Long to assign the book's ISBN
     */
    public Book(int redemptionCode, String title, String author, int pages, long ISBN) {
        this.setRedemptionCode(redemptionCode);
        this.setTitle(title);
        this.setAuthor(author);
        this.setPages(pages);
        this.setISBN(ISBN);
    }

    /**
     * This constructor is used in the student home page to display books
     * because the student home page has a table that only displays redemption code, Title, author, pages, date Out, due by date, renewal count, and report Id there are no other parameters
     * report Id is used to reference the row in reports table when renewing and returning
     * this is used to renew and return a book
     *
     * @param redemptionCode - int to assign the book's redemption code
     * @param title          - String to assign the book's title
     * @param author         - String to assign the book's author
     * @param pages          - int to assign how many pages the book has
     * @param dateOut        - The date that the book was checked out
     * @param dueBy          - The date the book is due
     * @param renewalCount   - int to assign the book's renewalCount
     * @param reportId       - int to assign the id currently being used in reports table for this book
     */
    public Book(int redemptionCode, String title, String author, int pages, LocalDate dateOut, LocalDate dueBy, int renewalCount, int reportId) {
        this.setRedemptionCode(redemptionCode);
        this.setTitle(title);
        this.setAuthor(author);
        this.setPages(pages);
        this.setDateOut(dateOut);
        this.setDueBy(dueBy);
        this.setRenewalCount(renewalCount);
        this.setReportId(reportId);
    }

    /**
     * This constructor is used to generate reports in teacher and admin accounts
     *
     * @param title - String to assign the book's title
     * @param heldBy - the user that the book is checked out to
     * @param dateOut - The date that the book was checked out
     * @param dateIn - The date that the book was returned
     * @param dueBy - The date the book is due
     * @param redemptionCode - int to assign the book's redemption code
     */
    public Book(String title, String heldBy, LocalDate dateOut, LocalDate dateIn, LocalDate dueBy, int redemptionCode) {
        this.setTitle(title);
        this.setHeldBy(heldBy);
        this.setDateOut(dateOut);
        this.setDateIn(dateIn);
        this.setDueBy(dueBy);
        this.setRedemptionCode(redemptionCode);
    }

    /**
     * This function is used in the student home page to renew any book they have
     * if the book has already been renewed three times, then this function will return false
     * it increments the renewal count and adds 7 days to the due by date in the books table and the books back up table
     * it also updates the value in the reports table and the reports backup table to
     *
     * @param redemptionCode - used to know which row to update in the books table
     * @param reportId       - used to know which row to update in the reports table
     * @return a boolean on whether the book has been renewed or not.
     * Will not be renewed if the book has been renewed 3 times
     */
    public static boolean renew(int redemptionCode, int reportId) {
        try {
            Statement statement = DBConnection.getConnection().createStatement();
            Statement backupStatement = DBConnection.getBackupConnection().createStatement();//Creates Statement from backup database connection to dynamically update

            //queries how many times a book has been renewed and assigns it to renewalCount
            String query = "SELECT Renewal_count FROM books WHERE Redemption_code = '" + redemptionCode + "'";
            ResultSet results = statement.executeQuery(query);
            int renewalCount = 0;
            while (results.next()) {
                renewalCount = results.getInt("Renewal_count");
            }

            //the book will only be renewed if the user has not exceeded their renewal limit per book
            if (renewalCount < 3) {

                //queries the date the book is due and assigns it to newDueBy
                query = "SELECT Due_by FROM books WHERE Redemption_code = '" + redemptionCode + "'";
                results = statement.executeQuery(query);
                LocalDate dueBy = null;
                while (results.next()) {
                    dueBy = results.getObject("Due_by", LocalDate.class);
                }

                LocalDate newDueBy = dueBy.plusDays(7);//calculates the new value of due by by adding 7 days
                int newRenewalCount = renewalCount + 1; //calculates the new value of renewalCount by incrementing it

                //updates the books table and books back up table with the new values
                query = "UPDATE books SET Due_by = '" + newDueBy + "', Renewal_count = '" + newRenewalCount + "' WHERE Redemption_code = '" + redemptionCode + "'";
                statement.executeUpdate(query); //increments the Renewal_count column by one and adds 7 days to the Due_by column in the the books table
                backupStatement.executeUpdate(query); //increments the Renewal_count column by one and adds 7 days to the Due_by column in the the books back up table

                //updates the reports table and reports back up table with the new values, so that generating a report will have the correct values
                query = "UPDATE weekly_reports SET Due_by = '" + newDueBy + "' WHERE Id = '" + reportId + "'";
                statement.executeUpdate(query); //increments the Renewal_count column by one and adds 7 days to the Due_by column in the the reports table
                backupStatement.executeUpdate(query); //increments the Renewal_count column by one and adds 7 days to the Due_by column in the the reports back up table

                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error: can't connect to database or incorrect SQL Statement");
            return false;//returns false if an exception happened
        }
        return false;//returns false if the user has reached their renewal limit
    }

    /**
     * this function is used in the library page of the student account
     * the function will only run if the student has less than 5 checked out books
     *
     * @param redemptionCode
     * @param username
     * @return returns a false if the user has 5 books checked out
     */
    public static boolean checkOut(int redemptionCode, String username) {
        try {
            Statement statement = DBConnection.getConnection().createStatement();
            Statement backupStatement = DBConnection.getBackupConnection().createStatement();//Creates Statement from backup database connection to dynamically update

            String query = "SELECT COUNT(Held_by) AS Books_out FROM books WHERE Held_by = '" + username + "'";
            ResultSet results = statement.executeQuery(query);//queries how many the books the user has out
            int booksOut = 0;
            while (results.next()) {
                booksOut = results.getInt("Books_out");
            }

            //the book will only be checked out if the user has not exceeded their book limit
            if (booksOut < 5) {

                LocalDate newDateOut = LocalDate.now(ZoneId.of("America/Phoenix"));//generates the current date
                LocalDate newDueBy = newDateOut.plusDays(7);//adds 7 days to the current date and assigns that date to newDueBy

                //generates the id to be used in the weekly_reports
                query = "SELECT MAX(Id) AS Id FROM weekly_reports";
                results = statement.executeQuery(query);//queries the largest Id from the table
                int newReportId = 0;
                while (results.next()) {
                    newReportId = results.getInt("Id") + 1;//adds one to the largest Id in the column to ensure that the new inserted id is unique
                }

                query = "UPDATE books SET Held_by = '" + username + "', Date_out = '" + newDateOut + "', Due_by = '" + newDueBy + "', Renewal_count = '0', Report_id = '" + newReportId + "' WHERE Redemption_code = '" + redemptionCode + "'";
                statement.executeUpdate(query);//updates the held by, date out, due by, report id values with the new values and set the renewal count to 0 in the books table
                backupStatement.executeUpdate(query);//updates the held by, date out, due by, report id values with the new values and set the renewal count to 0 in the books backup table

                //gets the title of the book being checked out to in order to set the title to the newly generated row in the weekly_reports table
                query = "SELECT Title FROM books WHERE Redemption_code = " + redemptionCode + "";
                results = statement.executeQuery(query);//queries the title of the checked out book
                String title = "";
                while (results.next()) {
                    title = results.getString("Title");
                }

                query = "INSERT INTO weekly_reports (Id, Title, Held_by, Date_out, Due_by, Redemption_code) VALUES ('" + newReportId + "', '" + title + "', '" + username + "', '" + newDateOut + "', '" + newDueBy + "', '" + redemptionCode + "')";
                statement.executeUpdate(query);//creates a new row in the weekly_report table with the newReportId, title, username, newDateOut, newDueBy, and redemptionCode values. it skips Date_in, because the book hasn't been returned yet.
                backupStatement.executeUpdate(query);//creates a new row in the weekly_report backup table with the newReportId, title, username, newDateOut, newDueBy, and redemptionCode values. it skips Date_in, because the book hasn't been returned yet.

                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error: can't connect to database or incorrect SQL Statement");
            return false;//returns false if an exception happened
        }
        return false;
    }

    /**
     * This function is used when the user wants to return a checked out book to the library
     * it sets Held_by, Date_out, Due_by, Renewal_count, and Report_id to null in both the books table and the books backup table by using the first parameter to reference the primary key
     * it also updates the Date_in and Due_by column in the weekly_reports table and back up table by using the second parameter as a reference to the primary key
     *
     * @param redemptionCode - used to know which row to update in the books table
     * @param reportId       - used to know which row to update in the reports table
     */
    public static boolean returnBook(int redemptionCode, int reportId) {
        try {
            Statement statement = DBConnection.getConnection().createStatement();
            Statement backupStatement = DBConnection.getBackupConnection().createStatement();//Creates Statement from backup database connection to dynamically update

            String query = "UPDATE books SET Held_by = NULL, Date_out = NULL, Due_by = NULL, Renewal_count = NULL, Report_id = NULL WHERE Redemption_code = '" + redemptionCode + "'";
            statement.executeUpdate(query);//Sets the Held by, date out, due by, renewal count, and report id fields equal to null in the specified row in the books table
            backupStatement.executeUpdate(query);//Sets the held by, date out, due by, renewal count, and report id fields equal to null in the specified row in the books backup table

            LocalDate newDateIn = LocalDate.now(ZoneId.of("America/Phoenix"));//generates the current date to input in the weekly reports table
            query = "UPDATE weekly_reports SET Date_in = '" + newDateIn + "', Due_by = NULL WHERE Id = '" + reportId + "'";
            statement.executeUpdate(query);//sets the Date in to the current date and due by equal to null in the specified row in the report table
            backupStatement.executeUpdate(query);//sets the Date in to the current date and due by equal to null in the specified row in the report backup table
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error: can't connect to database or incorrect SQL Statement");
            return false;//returns false if an exception happened
        }
        return true;
    }

    /**
     * @return the unique redemptionCode of the book
     */
    public int getRedemptionCode() {
        return redemptionCode;
    }

    /**
     * @param redemptionCode - int to assign the book's redemption code
     */
    public void setRedemptionCode(int redemptionCode) {
        this.redemptionCode = redemptionCode;
    }

    /**
     * @return the title of the book
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title - String to assign the book's title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the author of the book
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @param author - String to assign the book's author
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * @return the number of pages in the book
     */
    public int getPages() {
        return pages;
    }

    /**
     * @param pages - int to assign how many pages the book has
     */
    public void setPages(int pages) {
        this.pages = pages;
    }

    /**
     * @return the ISBN of the book
     */
    public long getISBN() {
        return ISBN;
    }

    /**
     * @param ISBN - Long to assign the book's ISBN
     */
    public void setISBN(long ISBN) {
        this.ISBN = ISBN;
    }

    /**
     * @return the user holding the book
     */
    public String getHeldBy() {
        return heldBy;
    }

    /**
     * @param heldBy - String to assign who the book is checked out to
     */
    public void setHeldBy(String heldBy) {
        this.heldBy = heldBy;
    }

    /**
     * @return the Date the book was checked out of the book
     */
    public LocalDate getDateOut() {
        return dateOut;
    }

    /**
     * @param dateOut - The date that the book was checked out
     */
    public void setDateOut(LocalDate dateOut) {
        this.dateOut = dateOut;
    }

    /**
     * @return the Date that the book is due by of the book
     */
    public LocalDate getDueBy() {
        return dueBy;
    }

    /**
     * @param dueBy - The date the book is due
     */
    public void setDueBy(LocalDate dueBy) {
        this.dueBy = dueBy;
    }

    /**
     * @return the renewal count of the book
     */
    public int getRenewalCount() {
        return renewalCount;
    }

    /**
     * @param renewalCount - int to assign the book's renewalCount
     */
    public void setRenewalCount(int renewalCount) {
        this.renewalCount = renewalCount;
    }

    /**
     * @return the report Id of the book
     */
    public int getReportId() {
        return reportId;
    }

    /**
     * @param reportId - int to assign the id currently being used in reports table for this book
     */
    public void setReportId(int reportId) {
        this.reportId = reportId;
    }

    /**
     * @return the Date that the book is returned
     */
    public LocalDate getDateIn() { return dateIn; }

    /**
     * @param dateIn - The date the book is returned
     */
    public void setDateIn(LocalDate dateIn) { this.dateIn = dateIn; }
}