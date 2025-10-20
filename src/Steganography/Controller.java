package Steganography;

import Steganography.Exceptions.CannotDecodeException;
import Steganography.Exceptions.CannotEncodeException;
import Steganography.Exceptions.UnsupportedImageTypeException;
import Steganography.Logic.ImageInImageSteganography;
import Steganography.Logic.ImageSteganography;
import Steganography.Logic.HiddenData;
import Steganography.Logic.Utils;
import Steganography.Modals.AlertBox;
import Steganography.Types.DataFormat;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class Controller {

    // JavaFX Components
    @FXML private Button newCoverImage, newSteganographicImage, newSecretDocument, newSecretImage, quitApp;
    @FXML private ImageView secretImageView, coverImageView, steganographicImageView;
    @FXML private TextArea secretMessage;
    @FXML private Button encodeDocument, encodeImage, encodeMessage, decodeImage;
    @FXML private Tab secretMessageTab, secretDocumentTab, secretImageTab;
    @FXML private BorderPane root;
    @FXML private VBox coverImagePane, secretImagePane, steganographicImagePane;
    @FXML private ListView<String> secretDocumentContent;
    @FXML private TabPane secretContentTabs;

    // Files
    private File coverImage, secretImage, secretDocument, steganographicImage, tempFile;

    // Initialize method to set up button bindings
    @FXML
    private void initialize() {
        // Bind newCoverImage disable property: enabled only when Encode tab (index 0) is selected
        newCoverImage.disableProperty().bind(
                Bindings.notEqual(0, secretContentTabs.getSelectionModel().selectedIndexProperty())
        );

        // Bind newSteganographicImage disable property: enabled only when Decode tab (index 1) is selected
        newSteganographicImage.disableProperty().bind(
                Bindings.notEqual(1, secretContentTabs.getSelectionModel().selectedIndexProperty())
        );
    }

    public void setCoverImage() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Select Cover Image");
        fc.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter(
                        "Image Files",
                        "*.png", "*.bmp", "*.jpg", "*.jpeg"));
        coverImage = fc.showOpenDialog(null);
        if (coverImage != null) {
            coverImagePane.setMinSize(0, 0);
            coverImageView.setImage(new Image("file:" + coverImage.getPath()));
            coverImageView.fitWidthProperty().bind(coverImagePane.widthProperty());
            coverImageView.fitHeightProperty().bind(coverImagePane.heightProperty());
            coverImagePane.setMaxSize(400, 300);
            newSecretDocument.setDisable(false);
            newSecretImage.setDisable(false);
            secretMessageTab.setDisable(false);
            secretDocumentTab.setDisable(false);
            secretImageTab.setDisable(false);
        } else {
            AlertBox.error("Error", "Failed to select cover image. Please try again.");
        }
    }

    public void setSteganographicImage() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Select Steganographic Image");
        fc.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter(
                        "Image Files",
                        "*.png", "*.bmp", "*.jpg", "*.jpeg"));
        steganographicImage = fc.showOpenDialog(null);
        if (steganographicImage != null) {
            steganographicImagePane.setMinSize(0, 0);
            steganographicImageView.setImage(new Image("file:" + steganographicImage.getPath()));
            steganographicImageView.fitWidthProperty().bind(steganographicImagePane.widthProperty());
            steganographicImageView.fitHeightProperty().bind(steganographicImagePane.heightProperty());
            steganographicImagePane.setMaxSize(400, 300);
            decodeImage.setDisable(false);
        } else {
            AlertBox.error("Error", "Failed to select steganographic image. Please try again.");
        }
    }

    public void setSecretDocument() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Select Secret Document");
        secretDocument = fc.showOpenDialog(null);
        if (secretDocument != null) {
            encodeDocument.setDisable(false);
            try {
                getDocumentContent(secretDocumentContent, secretDocument);
            } catch (IOException e) {
                e.printStackTrace();
                AlertBox.error("Error", "Failed to load secret document: " + e.getMessage());
            }
        } else {
            AlertBox.error("Error", "Failed to select secret document. Please try again.");
        }
    }

    public void setSecretImage() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Select Secret Image");
        fc.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter(
                        "Image Files",
                        "*.png", "*.bmp", "*.jpg", "*.jpeg"));
        secretImage = fc.showOpenDialog(null);
        if (secretImage != null) {
            secretImagePane.setMinSize(0, 0);
            secretImageView.setImage(new Image("file:" + secretImage.getPath()));
            secretImageView.fitWidthProperty().bind(secretImagePane.widthProperty());
            secretImageView.fitHeightProperty().bind(secretImagePane.heightProperty());
            secretImagePane.setMaxSize(350, 200);
            encodeImage.setDisable(false);
        } else {
            AlertBox.error("Error", "Failed to select secret image. Please try again.");
        }
    }

    public void encodeMessageInImage() {
        String message = secretMessage.getText();
        byte[] secret = message.getBytes(StandardCharsets.UTF_8);
        String imageExtension = Utils.getFileExtension(coverImage).toLowerCase();
        imageExtension = (imageExtension.matches("jpg|jpeg")) ? "png" : imageExtension;
        FileChooser fc = new FileChooser();
        fc.setTitle("Save Steganographic Image");
        fc.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter(
                        imageExtension.toUpperCase(),
                        "*." + imageExtension));
        steganographicImage = fc.showSaveDialog(null);
        if (steganographicImage != null) {
            try {
                ImageSteganography img = new ImageSteganography(coverImage);
                img.encode(secret, steganographicImage);
                AlertBox.information("Success", "Message encoded successfully in " + steganographicImage.getName() + ".", steganographicImage);
            } catch (IOException | CannotEncodeException | UnsupportedImageTypeException e) {
                e.printStackTrace();
                AlertBox.error("Error", "Encoding failed: " + e.getMessage());
            }
        }
    }

    public void encodeDocumentInImage() {
        String imageExtension = Utils.getFileExtension(coverImage).toLowerCase();
        imageExtension = (imageExtension.matches("jpg|jpeg")) ? "png" : imageExtension;
        FileChooser fc = new FileChooser();
        fc.setTitle("Save Steganographic Image");
        fc.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter(
                        imageExtension.toUpperCase(),
                        "*." + imageExtension));
        steganographicImage = fc.showSaveDialog(null);
        if (steganographicImage != null) {
            try {
                ImageSteganography img = new ImageSteganography(coverImage);
                img.encode(secretDocument, steganographicImage);
                AlertBox.information("Success", "Document encoded successfully in " + steganographicImage.getName() + ".", steganographicImage);
            } catch (IOException | CannotEncodeException | UnsupportedImageTypeException e) {
                e.printStackTrace();
                AlertBox.error("Error", "Encoding failed: " + e.getMessage());
            }
        }
    }

    public void encodeImageInImage() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Save Steganographic Image");
        fc.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter(
                        "PNG Image",
                        "*.png"));
        steganographicImage = fc.showSaveDialog(null);
        if (steganographicImage != null) {
            try {
                ImageInImageSteganography img = new ImageInImageSteganography(coverImage);
                img.encode(secretImage, steganographicImage);
                AlertBox.information("Success", "Image " + secretImage.getName() + " encoded successfully in " + steganographicImage.getName() + ".", steganographicImage);
            } catch (IOException | CannotEncodeException | UnsupportedImageTypeException e) {
                e.printStackTrace();
                AlertBox.error("Error", "Encoding failed: " + e.getMessage());
            }
        }
    }

    public void decodeImage() {
        String imageExtension = Utils.getFileExtension(steganographicImage);
        HiddenData hiddenData;
        FileChooser fc = new FileChooser();
        File file;
        try {
            ImageSteganography img = new ImageSteganography(steganographicImage);
            hiddenData = new HiddenData(img.getHeader());
            fc.getExtensionFilters()
                    .add(new FileChooser.ExtensionFilter(
                            hiddenData.extension.toUpperCase(),
                            "*." + hiddenData.extension));

            if (hiddenData.format == DataFormat.MESSAGE) {
                tempFile = File.createTempFile("message", ".txt");
                img.decode(tempFile);
                byte[] secret = Files.readAllBytes(tempFile.toPath());
                String message = new String(secret, StandardCharsets.UTF_8);
                if (message.length() > 0)
                    AlertBox.information("Success", "Decoded secret message:", message);
                tempFile.deleteOnExit();
            } else if (hiddenData.format == DataFormat.DOCUMENT) {
                file = fc.showSaveDialog(null);
                img.decode(file);
                if (file != null && file.length() > 0)
                    AlertBox.information("Success", "Document decoded in " + file.getName(), file);
            } else if (hiddenData.format == DataFormat.IMAGE) {
                ImageInImageSteganography imgInImg = new ImageInImageSteganography(steganographicImage);
                file = fc.showSaveDialog(null);
                imgInImg.decode(file);
                AlertBox.information("Success", "Image decoded in " + file.getName(), file);
            }
        } catch (IOException | CannotDecodeException | UnsupportedImageTypeException e) {
            e.printStackTrace();
            AlertBox.error("Error", "Decoding failed: " + e.getMessage());
        }
    }

    private static void getDocumentContent(ListView<String> documentView, File document) throws IOException {
        InputStreamReader streamReader = new InputStreamReader(new FileInputStream(document));
        BufferedReader reader = new BufferedReader(streamReader);
        String line;
        documentView.getItems().clear();
        while ((line = reader.readLine()) != null)
            documentView.getItems().add(line);
    }

    public void quitApp() {
        System.exit(0);
    }
}