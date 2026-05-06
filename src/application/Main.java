package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Main extends Application {
    public static double WIDTH;
    public static double HEIGHT;

    @Override
    public void start(Stage primaryStage) throws Exception {
        WIDTH  = Screen.getPrimary().getBounds().getWidth()  * 0.8;
        HEIGHT = Screen.getPrimary().getBounds().getHeight() * 0.8;

        AnchorPane root = FXMLLoader.load(getClass().getResource("login.fxml"));
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        primaryStage.setTitle("✈ Airline Management System");
        primaryStage.setScene(scene);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.show();
        primaryStage.toFront();
    }

    public static void main(String[] args) { launch(args); }
}
