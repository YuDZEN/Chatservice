package fr.uga.miashs.dciss.chatservice.GUI;


import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ChatClientGUI extends Application {
    private TextArea messageArea;
    private Socket socket;
    private ObjectOutputStream outputStream;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        messageArea = new TextArea();
        messageArea.setEditable(false);
        messageArea.setWrapText(true);
        

        TextField inputField = new TextField();
        Button sendButton = new Button("Envoyer");

        try {
            // Connect to the server
            socket = new Socket("localhost", 1234); // replace with your server's IP address and port
            outputStream = new ObjectOutputStream(socket.getOutputStream());

            // Start a new thread to receive messages
            new Thread(() -> {
                try {
                    ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                    while (true) {
                        String message = (String) inputStream.readObject();
                        messageArea.appendText(message + "\n");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        sendButton.setOnAction(event -> {
            String message = inputField.getText();
            inputField.clear();

            // Send the message to the server
            try {
                outputStream.writeObject(message);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Add the message to the message area
            messageArea.appendText("Vous: " + message + "\n");
        });

        inputField.setOnAction(event -> {
            sendButton.fire();
        });

        VBox vbox = new VBox(messageArea, inputField, sendButton);
        Scene scene = new Scene(vbox);

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}