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
import java.util.ResourceBundle;

public class StudentLibraryController implements Initializable {

    @FXML
    TableView<Book> tableView;
    @FXML
    TableColumn<Book, String> title;
    @FXML
    TableColumn<Book, String> author;
    @FXML
    TableColumn<Book, Integer> pagenum;
    @FXML
    TableColumn<Book, Integer> isbn;
    @FXML
    TableColumn<Book, Integer> redemption;

    public Button checkoutButton;
    @FXML
    public Label messageLabel;

    ObservableList<Book> book = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Statement statement = DBConnection.getConnection().createStatement();
            ResultSet results = statement.executeQuery("SELECT * FROM books WHERE Held_by IS NULL"); //get available book

            while (results.next()) {
                book.add(new Book(results.getInt("Redemption_code"), results.getString("Title"), results.getString("Author"), results.getInt("Pages"), results.getString("ISBN")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        title.setCellValueFactory(new PropertyValueFactory<>("title"));
        author.setCellValueFactory(new PropertyValueFactory<>("author"));
        pagenum.setCellValueFactory(new PropertyValueFactory<>("pages"));
        isbn.setCellValueFactory(new PropertyValueFactory<>("ISBN"));
        redemption.setCellValueFactory(new PropertyValueFactory<>("redemptionCode"));

        tableView.setItems(book);

        tableView.getSelectionModel().selectFirst();


        ObservableList<Book> bookSelected;
        bookSelected = tableView.getItems();

        if (bookSelected.isEmpty()) {
            checkoutButton.setDisable(true);
            messageLabel.setVisible(true);
            messageLabel.setText("No Books to Available to Check Out");
        }
    }

    public void checkOutBook() {
        ObservableList<Book> bookSelected;
        bookSelected = tableView.getSelectionModel().getSelectedItems();

        int code = bookSelected.get(0).getRedemptionCode();
        String user = User.getUsername();


        if (Book.checkOut(code, user)) {
            messageLabel.setVisible(true);
            messageLabel.setText("Your Book Has Been Checked Out");

            book.clear();

            try {
                Statement statement = DBConnection.getConnection().createStatement();
                ResultSet results = statement.executeQuery("SELECT * FROM books WHERE Held_by IS NULL"); //get available book

                while (results.next()) {
                    book.add(new Book(results.getInt("Redemption_code"), results.getString("Title"), results.getString("Author"), results.getInt("Pages"), results.getString("ISBN")));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            title.setCellValueFactory(new PropertyValueFactory<>("title"));
            author.setCellValueFactory(new PropertyValueFactory<>("author"));
            pagenum.setCellValueFactory(new PropertyValueFactory<>("pages"));
            isbn.setCellValueFactory(new PropertyValueFactory<>("ISBN"));
            redemption.setCellValueFactory(new PropertyValueFactory<>("redemptionCode"));

            tableView.setItems(book);
        } else {
            messageLabel.setVisible(true);
            messageLabel.setText("Your can't check out more than 5 books");
        }

        tableView.getSelectionModel().selectFirst();
    }
}