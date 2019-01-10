package intellij.gui.TeacherViews;

import intellij.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;


public class TeacherController implements Initializable {

    @FXML
    BorderPane teacherDashBorderPane;
    public Button signOutButton;
    public Button homeButton;
    public Button reportButton;
    public Button accountButton;
    public Button helpButton;

    public int viewState = 1;

    public void switchToHome() throws Exception {
        homeButton.setStyle("-fx-background-color: #37474F");
        reportButton.setStyle("-fx-background-color: #455A64");
        accountButton.setStyle("-fx-background-color: #455A64");
        BorderPane homeView = FXMLLoader.load(getClass().getResource("TeacherHomeView.fxml"));
        teacherDashBorderPane.setCenter(homeView);
        viewState = 1;
    }

    public void switchToReport() throws Exception {
        homeButton.setStyle("-fx-background-color: #455A64");
        reportButton.setStyle("-fx-background-color: #37474F");
        accountButton.setStyle("-fx-background-color: #455A64");
        BorderPane reportView = FXMLLoader.load(getClass().getResource("TeacherReportView.fxml"));
        teacherDashBorderPane.setCenter(reportView);
        viewState = 2;
    }

    public void switchToAccount() throws Exception {
        homeButton.setStyle("-fx-background-color: #455A64");
        reportButton.setStyle("-fx-background-color: #455A64");
        accountButton.setStyle("-fx-background-color: #37474F");
        BorderPane accountView = FXMLLoader.load(getClass().getResource("TeacherAccountView.fxml"));
        teacherDashBorderPane.setCenter(accountView);
        viewState = 3;
    }

    public void openHelp() throws Exception {
        switch (viewState) {
            case 1:
                FXMLLoader fxmlLoader1 = new FXMLLoader(getClass().getResource("../HelpViews/HelpTeacherDashboardView.fxml"));
                Parent root1 = (Parent) fxmlLoader1.load();
                Stage primaryStage1 = new Stage();
                primaryStage1.setTitle("Dashboard Help");
                primaryStage1.setScene(new Scene(root1));
                primaryStage1.show();
                break;
            case 2:
                FXMLLoader fxmlLoader2 = new FXMLLoader(getClass().getResource("../HelpViews/HelpTeacherReportView.fxml"));
                Parent root2 = (Parent) fxmlLoader2.load();
                Stage primaryStage2 = new Stage();
                primaryStage2.setTitle("Reports Help");
                primaryStage2.setScene(new Scene(root2));
                primaryStage2.show();
                break;
            case 3:
                FXMLLoader fxmlLoader3 = new FXMLLoader(getClass().getResource("../HelpViews/HelpTeacherAccountView.fxml"));
                Parent root3 = (Parent) fxmlLoader3.load();
                Stage primaryStage3 = new Stage();
                primaryStage3.setTitle("Account Help");
                primaryStage3.setScene(new Scene(root3));
                primaryStage3.show();
                break;
            default:
                System.out.println("Please press help button to launch help");
        }
    }

    public void signOutToLogin(ActionEvent event) throws Exception {
        User.signOut();
        Parent root = FXMLLoader.load(getClass().getResource("../LoginScreen.fxml"));
        Scene scene = new Scene(root, 1000, 650);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        homeButton.fire();
    }
}