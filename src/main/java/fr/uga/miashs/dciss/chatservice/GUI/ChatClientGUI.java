package fr.uga.miashs.dciss.chatservice.GUI;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import fr.uga.miashs.dciss.chatservice.client.ClientMsg;

public class ChatClientGUI extends Application {

    private VBox messageArea;
    private Socket socket;
    private ObjectOutputStream outputStream;
    private int userId;
    private ClientMsg client;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        ListView<String> messageArea = new ListView<>();
        messageArea.setPrefHeight(400);
        messageArea.setDisable(false);
        messageArea.setPrefHeight(400);

        TextField inputField = new TextField();

        Button sendButton = new Button("Envoyer");
        Button sendFileButton = new Button("Send File");
        Button sendImageButton = new Button("Send Image");
        ImageView imageView = new ImageView();

        sendImageButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                try {
                    client.sendPacket(userId, true, file.getPath());
                    Image image = new Image(file.toURI().toString());
                    imageView.setImage(image);
                    // Add the image to the chatbox
                    messageArea.getItems().add(imageView.toString());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        
        sendButton.setOnAction(event -> {
                String message = inputField.getText();
                inputField.clear();
            
                // Send the message to the server
                try {
                    client.sendPacket(userId, false, message);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            
                // Add the message to the chatbox
                Label messageLabel = new Label("You: " + message);
                messageLabel.setPrefWidth(350);
                messageArea.getItems().add(messageLabel.getText());
            });
        
        inputField.setOnAction(event -> sendButton.fire());

        HBox hBox = new HBox(inputField, sendButton, sendFileButton, sendImageButton);
        VBox vbox = new VBox(messageArea, imageView, inputField, hBox);
        Scene scene = new Scene(vbox);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
