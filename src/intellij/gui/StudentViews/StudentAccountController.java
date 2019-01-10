package intellij.gui.StudentViews;

import intellij.Student;
import intellij.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class StudentAccountController implements Initializable {

    @FXML
    Label nameLabel;
    @FXML
    Label gradeLabel;
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
        byte grade = User.getGrade();

        nameLabel.setText(firstName + " " + lastName);
        gradeLabel.setText("Grade " + grade);
    }

    public void showChangePasswordBox() {
        changePasswordBox.setVisible(true);
    }

    public void changePasswordValidate() {
        String password1Text = password1.getText();
        String password2Text = password2.getText();

        if ((password1Text.equals(password2Text))) {
            String message = Student.changePassword(password1Text);
            errorMessage.setVisible(true);
            errorMessage.setText(message);
        } else {
            errorMessage.setVisible(true);
            errorMessage.setText("Passwords Do Not Match");
        }
    }
}