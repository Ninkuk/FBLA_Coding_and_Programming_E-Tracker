package intellij.gui.TeacherViews;

import intellij.Teacher;
import intellij.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class TeacherAccountController implements Initializable {

    @FXML
    Label nameLabel;
    public Button changePasswordButton;
    public Button changePasswordValidateButton;
    public VBox changePasswordBox;
    public PasswordField password1;
    public PasswordField password2;
    public Label errorMessage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String firstName = User.getFirstName();
        String lastName = User.getLastName();

        nameLabel.setText(firstName + " " + lastName);
    }

    public void showChangePasswordBox() {
        changePasswordBox.setVisible(true);
    }

    public void changePasswordValidate() {
        String password1Text = password1.getText();
        String password2Text = password2.getText();

        if ((password1Text.equals(password2Text))) {
            String message = Teacher.changePassword(password1Text);
            errorMessage.setVisible(true);
            errorMessage.setText(message);
        } else {
            errorMessage.setVisible(true);
            errorMessage.setText("Passwords Do Not Match");
        }
    }
}