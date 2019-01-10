package intellij.gui.AdminViews;

import intellij.Admin;
import intellij.Student;
import intellij.database.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class AdminHomeController implements Initializable {

    @FXML
    TableView<Student> tableView;
    @FXML
    TableColumn<Student, String> firstName;
    @FXML
    TableColumn<Student, String> lastName;
    @FXML
    TableColumn<Student, String> username;
    @FXML
    TableColumn<Student, Byte> grade;

    public Button addButton;
    public Button displayAddFieldsButton;
    public Button removeButton;
    public Button updateButton;
    public Button displayUpdateFieldsButton;
    public TextField firstNameInput;
    public TextField lastNameInput;
    public TextField passwordInput;
    public TextField gradeInput;
    public TextField firstNameUpdateInput;
    public TextField lastNameUpdateInput;
    public TextField passwordUpdateInput;
    public TextField usernameUpdateInput;
    public TextField gradeUpdateInput;
    public Label messageLabel;
    public HBox addInfoFields;
    public HBox updateInfoFields;

    ObservableList<Student> student = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Statement statement = DBConnection.getConnection().createStatement();
            ResultSet results = statement.executeQuery("SELECT * FROM students"); //get available book

            while (results.next()) {
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

        tableView.getSelectionModel().selectFirst();

        ObservableList<Student> studentsSelected;
        studentsSelected = tableView.getItems();

        if (studentsSelected.isEmpty()) {
            removeButton.setDisable(true);
            messageLabel.setVisible(true);
            messageLabel.setText("No Students to show");
        }
    }

    public void displayAddFields(){
        addInfoFields.setVisible(true);
        updateInfoFields.setVisible(false);
    }

    public void displayUpdateFields(){
        addInfoFields.setVisible(false);
        updateInfoFields.setVisible(true);
    }

    public void addStudent() {
        String firstNameString = firstNameInput.getText();
        String lastNameString = lastNameInput.getText();
        String passwordString = passwordInput.getText();

        if (firstNameString.isEmpty() && lastNameString.isEmpty() && passwordString.isEmpty() && (gradeInput.getText().isEmpty())) {
            messageLabel.setVisible(true);
            messageLabel.setText("Enter all the values before adding students");
        } else {
            String newUsernameGenerate = Admin.addStudent(firstNameString, lastNameString, passwordString, Byte.parseByte(gradeInput.getText()));
            messageLabel.setVisible(true);
            messageLabel.setText("Student added and the new username is " + newUsernameGenerate);

            student.clear();

            try {
                Statement statement = DBConnection.getConnection().createStatement();
                ResultSet results = statement.executeQuery("SELECT Username, First_name, Last_name, Grade FROM students"); //get available book

                while (results.next()) {
                    student.add(new Student(results.getString("First_name"), results.getString("Last_name"), results.getString("Username"), results.getByte("Grade")));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            firstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
            lastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
            username.setCellValueFactory(new PropertyValueFactory<>("username"));
            grade.setCellValueFactory(new PropertyValueFactory<>("grade"));
        }
        tableView.setItems(student);
    }

    public void removeStudent() {
        ObservableList<Student> studentsSelected;
        studentsSelected = tableView.getSelectionModel().getSelectedItems();

        String user = studentsSelected.get(0).getUsername();

        Admin.removeStudent(user);

        student.clear();

        messageLabel.setVisible(true);
        messageLabel.setText("Student has been removed");

        try {
            Statement statement = DBConnection.getConnection().createStatement();
            ResultSet results = statement.executeQuery("SELECT Username, First_name, Last_name, Grade FROM students"); //get available book

            while (results.next()) {
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

        tableView.getSelectionModel().selectFirst();
    }

    public void updateStudent(){
        String usernameString = usernameUpdateInput.getText();
        String firstNameString = firstNameUpdateInput.getText();
        String lastNameString = lastNameUpdateInput.getText();
        String passwordString =  passwordUpdateInput.getText();

        student.clear();

        if (usernameString.isEmpty() && firstNameString.isEmpty() && lastNameString.isEmpty() && passwordString.isEmpty() && (gradeUpdateInput.getText().isEmpty())) {
            messageLabel.setVisible(true);
            messageLabel.setText("Enter all the values before updating students");
        } else {
            if (Admin.updateStudent(usernameString, firstNameString, lastNameString, passwordString, (Byte.parseByte(gradeUpdateInput.getText())))) {
                messageLabel.setVisible(true);
                messageLabel.setText("Student information has been updated");

                student.clear();

                try {
                    Statement statement = DBConnection.getConnection().createStatement();
                    ResultSet results = statement.executeQuery("SELECT Username, First_name, Last_name, Grade FROM students");

                    while (results.next()) {
                        student.add(new Student(results.getString("First_name"), results.getString("Last_name"), results.getString("Username"), results.getByte("Grade")));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                firstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
                lastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
                username.setCellValueFactory(new PropertyValueFactory<>("username"));
                grade.setCellValueFactory(new PropertyValueFactory<>("grade"));
            }
            else {
                messageLabel.setVisible(true);
                messageLabel.setText("Student could not be updated");
            }
        }
        tableView.setItems(student);
    }
}
