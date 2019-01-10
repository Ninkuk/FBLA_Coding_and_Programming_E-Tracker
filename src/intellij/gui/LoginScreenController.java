package intellij.gui;

import intellij.User;
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

        if((User.logIn(username, password))){
            if(User.getAccessLevel().equals("admin")) {
                Parent root = FXMLLoader.load(getClass().getResource("AdminViews/AdminDashboardView.fxml"));
                Scene scene = new Scene(root, 1000, 650);
                Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                window.setScene(scene);
                window.show();
            }
            else if(User.getAccessLevel().equals("teacher")) {
                Parent root = FXMLLoader.load(getClass().getResource("TeacherViews/TeacherDashboardView.fxml"));
                Scene scene = new Scene(root, 1000, 650);
                Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                window.setScene(scene);
                window.show();
            }
            else if(User.getAccessLevel().equals("student")) {
                Parent root = FXMLLoader.load(getClass().getResource("StudentViews/StudentDashboardView.fxml"));
                Scene scene = new Scene(root, 1000, 650);
                Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
                window.setScene(scene);
                window.show();
            }
        }
        else {
            inputError.setVisible(true);
        }
    }
}
