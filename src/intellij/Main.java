/**
 * FBLA Coding and Programming Submission
 * Ninad Kulkarni
 * Pinnacle High School
 */
package intellij;

//import statements
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


//This main method starts the application by launching the JavaFX
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("gui/StartScreen.fxml")); //launches the start screen
        primaryStage.getIcons().add(new Image("intellij/gui/res/img/e-tracker_logo_340x340.png")); //sets the icon of the application
        primaryStage.setTitle("E-Tracker"); //sets the title of the application
        primaryStage.setScene(new Scene(root, 1000, 650));//creates a new scene with 1000x650 dimensions
        primaryStage.show(); //lastly this is used to display the stage/window
    }


    public static void main(String[] args) {
        launch(args);
    }
}
