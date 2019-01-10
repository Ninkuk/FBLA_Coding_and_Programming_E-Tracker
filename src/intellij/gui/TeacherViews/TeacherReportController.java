package intellij.gui.TeacherViews;

import intellij.Book;
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
import java.util.ResourceBundle;

public class TeacherReportController implements Initializable {

    @FXML
    TableView<Book> tableView;
    @FXML
    TableColumn<Book, String> title;
    @FXML
    TableColumn<Book, String> heldBy;
    @FXML
    TableColumn<Book, LocalDate> dateOut;
    @FXML
    TableColumn<Book, LocalDate> dateIn;
    @FXML
    TableColumn<Book, LocalDate> dueBy;
    @FXML
    TableColumn<Book, Integer> redemption;

    public Button generateButton;
    public Label messageLabel;

    ObservableList<Book> report = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void generateReport() {
        try {
            Statement statement = DBConnection.getConnection().createStatement();
            ResultSet results = statement.executeQuery("SELECT Id, Title, Held_by, Date_out, Date_in, Due_by, Redemption_code FROM weekly_reports");

            while (results.next()) {
                report.add(new Book(results.getString("Title"), results.getString("Held_by"), results.getObject("Date_out", LocalDate.class), results.getObject("Date_in", LocalDate.class), results.getObject("Due_by", LocalDate.class), results.getInt("Redemption_code")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        title.setCellValueFactory(new PropertyValueFactory<>("title"));
        heldBy.setCellValueFactory(new PropertyValueFactory<>("heldBy"));
        dateOut.setCellValueFactory(new PropertyValueFactory<>("dateOut"));
        dateIn.setCellValueFactory(new PropertyValueFactory<>("dateIn"));
        dueBy.setCellValueFactory(new PropertyValueFactory<>("dueBy"));
        redemption.setCellValueFactory(new PropertyValueFactory<>("redemptionCode"));

        tableView.setItems(report);
    }
}