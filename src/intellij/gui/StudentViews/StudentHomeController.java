package intellij.gui.StudentViews;

import intellij.Book;
import intellij.User;
import intellij.database.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;

public class StudentHomeController implements Initializable {
    @FXML TableView<Book> tableView;
    @FXML TableColumn<Book, Integer> redemption;
    @FXML TableColumn<Book, String> title;
    @FXML TableColumn<Book, String> author;
    @FXML TableColumn<Book, Integer> pagenum;
    @FXML TableColumn<Book, Date> dateout;
    @FXML TableColumn<Book, Date> dueby;
    @FXML TableColumn<Book, Integer> renewalcount;
    @FXML TableColumn<Book, Integer> id;

    public Button renewButton;
    public Button returnButton;
    @FXML public Label messageLabel;

    ObservableList<Book> book = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Statement statement = DBConnection.getConnection().createStatement();
            ResultSet results = statement.executeQuery("SELECT * FROM books WHERE Held_by LIKE '" + User.getUsername() + "'"); //get available book

            while (results.next()){
                book.add(new Book(results.getInt("Redemption_code"), results.getString("Title"), results.getString("Author"), results.getInt("Pages"), results.getObject("Date_out", LocalDate.class), results.getObject("Due_by", LocalDate.class), results.getInt("Renewal_count"), results.getInt("Report_id")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        redemption.setCellValueFactory(new PropertyValueFactory<>("redemptionCode"));
        title.setCellValueFactory(new PropertyValueFactory<>("title"));
        author.setCellValueFactory(new PropertyValueFactory<>("author"));
        pagenum.setCellValueFactory(new PropertyValueFactory<>("pages"));
        dateout.setCellValueFactory(new PropertyValueFactory<>("dateOut"));
        dueby.setCellValueFactory(new PropertyValueFactory<>("dueBy"));
        renewalcount.setCellValueFactory(new PropertyValueFactory<>("renewalCount"));
        id.setCellValueFactory(new PropertyValueFactory<>("reportId"));

        tableView.setItems(book);

        tableView.getSelectionModel().selectFirst();

        ObservableList<Book> bookSelected;
        bookSelected = tableView.getItems();

        if (bookSelected.isEmpty()){
            renewButton.setDisable(true);
            returnButton.setDisable(true);
            messageLabel.setVisible(true);
            messageLabel.setText("No Books Checked Out");
        }
    }


    public void renewBook() {
        ObservableList<Book> bookSelected;
        bookSelected = tableView.getSelectionModel().getSelectedItems();

        int code = bookSelected.get(0).getRedemptionCode();
        int idBook = bookSelected.get(0).getReportId();

        if (Book.renew(code, idBook)){
            messageLabel.setVisible(true);
            messageLabel.setText("Your Book Has Been Renewed");

            book.clear();

            try {
                Statement statement = DBConnection.getConnection().createStatement();
                ResultSet results = statement.executeQuery("SELECT * FROM books WHERE Held_by LIKE '" + User.getUsername() + "'"); //get available book

                while (results.next()){
                    book.add(new Book(results.getInt("Redemption_code"), results.getString("Title"), results.getString("Author"), results.getInt("Pages"), results.getObject("Date_out", LocalDate.class), results.getObject("Due_by", LocalDate.class), results.getInt("Renewal_count"), results.getInt("Report_id")));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            redemption.setCellValueFactory(new PropertyValueFactory<>("redemptionCode"));
            title.setCellValueFactory(new PropertyValueFactory<>("title"));
            author.setCellValueFactory(new PropertyValueFactory<>("author"));
            pagenum.setCellValueFactory(new PropertyValueFactory<>("pages"));
            dateout.setCellValueFactory(new PropertyValueFactory<>("dateOut"));
            dueby.setCellValueFactory(new PropertyValueFactory<>("dueBy"));
            renewalcount.setCellValueFactory(new PropertyValueFactory<>("renewalCount"));
            id.setCellValueFactory(new PropertyValueFactory<>("reportId"));

            tableView.getSelectionModel().selectFirst();
        }
        else {
            messageLabel.setVisible(true);
            messageLabel.setText("You can't renew a book more than 3 times");
        }
        tableView.setItems(book);
    }

    public void returnBookBack() {
        ObservableList<Book> bookSelected = null;
        bookSelected = tableView.getSelectionModel().getSelectedItems();

        int code = bookSelected.get(0).getRedemptionCode();
        int idBook = bookSelected.get(0).getReportId();

        if (Book.returnBook(code, idBook)){
            messageLabel.setVisible(true);
            messageLabel.setText("Your Book Has Been Returned");

            book.clear();

            try {
                Statement statement = DBConnection.getConnection().createStatement();
                ResultSet results = statement.executeQuery("SELECT * FROM books WHERE Held_by LIKE '" + User.getUsername() + "'"); //get available book

                while (results.next()){
                    book.add(new Book(results.getInt("Redemption_code"), results.getString("Title"), results.getString("Author"), results.getInt("Pages"), results.getObject("Date_out", LocalDate.class), results.getObject("Due_by", LocalDate.class), results.getInt("Renewal_count"), results.getInt("Report_id")));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            redemption.setCellValueFactory(new PropertyValueFactory<>("redemptionCode"));
            title.setCellValueFactory(new PropertyValueFactory<>("title"));
            author.setCellValueFactory(new PropertyValueFactory<>("author"));
            pagenum.setCellValueFactory(new PropertyValueFactory<>("pages"));
            dateout.setCellValueFactory(new PropertyValueFactory<>("dateOut"));
            dueby.setCellValueFactory(new PropertyValueFactory<>("dueBy"));
            renewalcount.setCellValueFactory(new PropertyValueFactory<>("renewalCount"));
            id.setCellValueFactory(new PropertyValueFactory<>("reportId"));

            tableView.setItems(book);
        }
        else {
            messageLabel.setVisible(true);
            messageLabel.setText("Error Returning Book");
        }
        tableView.getSelectionModel().selectFirst();
    }
}
