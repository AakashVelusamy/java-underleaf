package Steganography;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.image.Image;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load custom fonts
        Font.loadFont(getClass().getResource("/Steganography/Resources/fonts/JetBrainsMono-Regular.ttf").toExternalForm(), 14);
        Font.loadFont(getClass().getResource("/Steganography/Resources/fonts/JetBrainsMono-Medium.ttf").toExternalForm(), 14);
        Font.loadFont(getClass().getResource("/Steganography/Resources/fonts/JetBrainsMono-Bold.ttf").toExternalForm(), 14);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Steganography/Resources/layout.fxml"));
        Image icon = new Image(Controller.class.getResource("Resources/logo.png").toExternalForm(), false);
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(getClass().getResource("/Steganography/Resources/neon_theme.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.setTitle("underLeaf");
        primaryStage.getIcons().add(icon);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
