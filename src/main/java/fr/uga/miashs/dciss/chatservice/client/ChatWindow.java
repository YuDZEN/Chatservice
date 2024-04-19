package fr.uga.miashs.dciss.chatservice.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.UnknownHostException;
import java.io.File;

public class ChatWindow extends JFrame {
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private JButton addButton; // The new button for adding files
    private ClientMsg client;

    public ChatWindow(String nom_utilisateur, ClientMsg client) {
        this.client = client;
        setTitle("Chat - User : " + nom_utilisateur);
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel chatPanel = new JPanel(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        JPanel messagePanel = new JPanel(new BorderLayout());
        messageField = new JTextField();

        // Create the send button
        sendButton = new JButton("Envoyer");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = messageField.getText();
                client.sendPacket(0, message.getBytes()); // Envía el mensaje al servidor
                appendMessage("You", message);
                messageField.setText("");
            }
        });

        // Add ActionListener to messageField
        messageField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendButton.doClick();
            }
        });

        // Create the add button
        addButton = new JButton("Add");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int option = fileChooser.showOpenDialog(ChatWindow.this);
                if (option == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    // Here, you can handle the file
                    // For example, you could convert it to bytes and send it using a method in the client
                    // client.sendFile(userId, file);
                    appendMessage("You", "Sent a file: " + file.getName());
                }
            }
        });

        // Layout for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(addButton);
        buttonPanel.add(sendButton);

        // Adding components to messagePanel
        messagePanel.add(messageField, BorderLayout.CENTER);
        messagePanel.add(buttonPanel, BorderLayout.EAST);

        // Adding components to chatPanel
        chatPanel.add(scrollPane, BorderLayout.CENTER);
        chatPanel.add(messagePanel, BorderLayout.SOUTH);

        add(chatPanel, BorderLayout.CENTER);
    }

    public void appendMessage(String sender, String message) {
        chatArea.append(sender + ": " + message + "\n");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    ClientMsg client = new ClientMsg("localhost", 1666);
                    client.startSession();

                    ChatWindow chatWindow = new ChatWindow("lainean", client);
                    chatWindow.setVisible(true);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
