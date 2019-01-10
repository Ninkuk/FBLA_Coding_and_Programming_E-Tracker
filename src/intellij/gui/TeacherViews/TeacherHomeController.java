package intellij.gui.TeacherViews;

import intellij.Book;
import intellij.Student;
import intellij.database.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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

public class TeacherHomeController implements Initializable {
    @FXML TableView<Student> tableView;
    @FXML TableColumn<Student, String> firstName;
    @FXML TableColumn<Student, String> lastName;
    @FXML TableColumn<Student, String> username;
    @FXML TableColumn<Student, Byte> grade;

    ObservableList<Student> student = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Statement statement = DBConnection.getConnection().createStatement();
            ResultSet results = statement.executeQuery("SELECT Username, First_name, Last_name, Grade FROM students"); //get available book

            while (results.next()){
                student.add(new Student(results.getString("First_name"), results.getString("Last_name"), results.getString("Username"), results.getByte("Grade")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        firstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        username.setCellValueFactory(new PropertyValueFactory<>("username"));
        grade.setCellValueFactory(new PropertyValueFactory<>("grade"));

        tableView.setItems(student);
    }
}
