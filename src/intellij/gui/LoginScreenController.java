package intellij.gui;

import intellij.User;
import intellij.gui.AdminViews.AdminController;
import intellij.gui.StudentViews.StudentController;
import intellij.gui.TeacherViews.TeacherController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginScreenController {

    public TextField usernameInput;
    public TextField passwordInput;
    public Button loginButton;
    public Label inputError;

    public void buttonToDashboard(ActionEvent event) throws Exception {

        String username = usernameInput.getText();
        String password = passwordInput.getText();

        if ((User.logIn(username, password))) { //passes in the username and password values to the login function in the user class and if it returns true, the application will progress to a dashboard
            switch (User.getAccessLevel()) {
                case "admin": { //admin has access to add, view, edit and remove books and students
                    AdminController.username = username;
                    Parent root = FXMLLoader.load(getClass().getResource("AdminViews/AdminDashboardView.fxml"));
                    Scene scene = new Scene(root, 1000, 650);
                    Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    window.setScene(scene);
                    window.show();
                    break;
                }
                case "teacher": { //teacher can see their students and view reports
                    TeacherController.username = username;
                    Parent root = FXMLLoader.load(getClass().getResource("TeacherViews/TeacherDashboardView.fxml"));
                    Scene scene = new Scene(root, 1000, 650);
                    Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    window.setScene(scene);
                    window.show();
                    break;
                }
                case "student": { //students can check out, return and renew books
                    StudentController.username = username;
                    Parent root = FXMLLoader.load(getClass().getResource("StudentViews/StudentDashboardView.fxml"));
                    Scene scene = new Scene(root, 1000, 650);
                    Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    window.setScene(scene);
                    window.show();
                    break;
                }
            }
        } else {
            inputError.setVisible(true);
        }
    }
}
