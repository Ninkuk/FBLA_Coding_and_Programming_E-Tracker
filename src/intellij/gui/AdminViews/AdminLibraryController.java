package intellij.gui.AdminViews;

import intellij.Admin;
import intellij.Book;
import intellij.database.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class AdminLibraryController implements Initializable {

    @FXML
    TableView<Book> tableView;
    @FXML
    TableColumn<Book, Integer> redemption;
    @FXML
    TableColumn<Book, String> title;
    @FXML
    TableColumn<Book, String> author;
    @FXML
    TableColumn<Book, Integer> pagenum;
    @FXML
    TableColumn<Book, Byte> isbn;

    public Button addButton;
    public Button displayAddFieldsButton;
    public Button removeButton;
    public Button updateButton;
    public Button displayUpdateFieldsButton;
    public TextField redemptionCodeInput;
    public TextField titleInput;
    public TextField authorInput;
    public TextField pagesnumInput;
    public TextField isbnInput;
    public TextField oldRedemptionCodeInput;
    public TextField newRedemptionCodeInput;
    public TextField titleUpdateInput;
    public TextField authorUpdateInput;
    public TextField pagesnumUpdateInput;
    public TextField isbnUpdateInput;
    public Label messageLabel;
    public HBox addInfoFields;
    public VBox updateInfoFields;

    ObservableList<Book> book = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Statement statement = DBConnection.getConnection().createStatement();
            ResultSet results = statement.executeQuery("SELECT * FROM books"); //get available book

            while (results.next()) {
                book.add(new Book(results.getInt("Redemption_code"), results.getString("Title"), results.getString("Author"), results.getInt("Pages"), results.getLong("ISBN")));
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
            //checkoutButton.setDisable(true);
            messageLabel.setVisible(true);
            messageLabel.setText("No Books to Available to Check Out");
        }
    }

    public void displayAddFields() {
        addInfoFields.setVisible(true);
        updateInfoFields.setVisible(false);
    }

    public void displayUpdateFields() {
        addInfoFields.setVisible(false);
        updateInfoFields.setVisible(true);
    }

    public void addBook() {
        String titleString = titleInput.getText();
        String authorString = authorInput.getText();

        if ((redemptionCodeInput.getText().isEmpty()) && titleString.isEmpty() && authorString.isEmpty() && (pagesnumInput.getText().isEmpty()) && (isbnInput.getText().isEmpty())) {
            messageLabel.setVisible(true);
            messageLabel.setText("Enter all the values before adding books");
        } else {
            if (Admin.addBook(Integer.parseInt(redemptionCodeInput.getText()), titleString, authorString, Integer.parseInt(pagesnumInput.getText()), Long.parseLong(isbnInput.getText()))) {
                messageLabel.setVisible(true);
                messageLabel.setText("Book added successfully");

                book.clear();

                try {
                    Statement statement = DBConnection.getConnection().createStatement();
                    ResultSet results = statement.executeQuery("SELECT * FROM books"); //get available book

                    while (results.next()) {
                        book.add(new Book(results.getInt("Redemption_code"), results.getString("Title"), results.getString("Author"), results.getInt("Pages"), results.getLong("ISBN")));
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
            }
            else {
                messageLabel.setVisible(true);
                messageLabel.setText("Redemption Code is not unique");
            }
        }
        ObservableList<Book> bookSelected;
        bookSelected = tableView.getItems();

        if (bookSelected.isEmpty()) {
            //checkoutButton.setDisable(true);
            messageLabel.setVisible(true);
            messageLabel.setText("No Books to Available to Check Out");
        }
    }

    public void removeBook() {
        ObservableList<Book> bookSelected;
        bookSelected = tableView.getSelectionModel().getSelectedItems();

        int redemptionCode = bookSelected.get(0).getRedemptionCode();

        Admin.removeBook(redemptionCode);

        book.clear();

        messageLabel.setVisible(true);
        messageLabel.setText("Book has been removed");

        try {
            Statement statement = DBConnection.getConnection().createStatement();
            ResultSet results = statement.executeQuery("SELECT * FROM books"); //get available book

            while (results.next()) {
                book.add(new Book(results.getInt("Redemption_code"), results.getString("Title"), results.getString("Author"), results.getInt("Pages"), results.getInt("ISBN")));
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
    }

    public void updateBook() {
        String titleString = titleUpdateInput.getText();
        String authorString = authorUpdateInput.getText();

        if ((oldRedemptionCodeInput.getText().isEmpty()) && (newRedemptionCodeInput.getText().isEmpty()) && titleString.isEmpty() && authorString.isEmpty() && (pagesnumUpdateInput.getText().isEmpty()) && (isbnUpdateInput.getText().isEmpty())) {
            messageLabel.setVisible(true);
            messageLabel.setText("Enter all the values before updating books");
        } else {
            if (Admin.updateBook((Integer.parseInt(oldRedemptionCodeInput.getText())), Integer.parseInt(newRedemptionCodeInput.getText()), titleString, authorString, Integer.parseInt(pagesnumUpdateInput.getText()), Long.parseLong(isbnUpdateInput.getText()))) {
                messageLabel.setVisible(true);
                messageLabel.setText("Book added successfully");

                book.clear();

                try {
                    Statement statement = DBConnection.getConnection().createStatement();
                    ResultSet results = statement.executeQuery("SELECT * FROM books"); //get available book

                    while (results.next()) {
                        book.add(new Book(results.getInt("Redemption_code"), results.getString("Title"), results.getString("Author"), results.getInt("Pages"), results.getInt("ISBN")));
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
            }
        }
        ObservableList<Book> bookSelected;
        bookSelected = tableView.getItems();

        if (bookSelected.isEmpty()) {
            //checkoutButton.setDisable(true);
            messageLabel.setVisible(true);
            messageLabel.setText("No Books to Available to Check Out");
        }
    }
}
