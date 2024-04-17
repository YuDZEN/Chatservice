package fr.uga.miashs.dciss.chatservice.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.UnknownHostException;

public class ChatWindow extends JFrame {
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private ClientMsg client;

    public ChatWindow(int userId, ClientMsg client) {
        this.client = client;
        setTitle("Chat - User " + userId);
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel chatPanel = new JPanel(new BorderLayout());

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        JPanel messagePanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        sendButton = new JButton("Send");

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = messageField.getText();
                client.sendPacket(0, message.getBytes()); // Envía el mensaje al servidor
                appendMessage("You", message);
                messageField.setText("");
            }
        });

        messagePanel.add(messageField, BorderLayout.CENTER);
        messagePanel.add(sendButton, BorderLayout.EAST);

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

                    ChatWindow chatWindow = new ChatWindow(client.getIdentifier(), client);
                    chatWindow.setVisible(true);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
