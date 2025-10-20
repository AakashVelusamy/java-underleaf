package Steganography.Modals;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;

public class AlertBox {

    public static void error(String title, String message) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(300);
        Label label = new Label();
        label.setText(message);
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> window.close());
        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, closeButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(10, 10, 10, 10));
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }

    public static void information(String title, String message, File file) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(300);
        Label label = new Label();
        label.setText(message);
        Button openButton = new Button("Open");
        openButton.setOnAction(e -> {
            try {
                new ProcessBuilder("cmd", "/c", file.getAbsolutePath()).start();
            } catch (Exception ex) {
                error("Error", "Could not open file: " + ex.getMessage());
            }
        });
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> window.close());
        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, openButton, closeButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(10, 10, 10, 10));
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }

    public static void information(String title, String message, String text) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle(title);
        window.setMinWidth(300);
        Label label = new Label();
        label.setText(message);
        Label textLabel = new Label();
        textLabel.setText(text);
        textLabel.setWrapText(true);
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> window.close());
        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, textLabel, closeButton);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(10, 10, 10, 10));
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }
}