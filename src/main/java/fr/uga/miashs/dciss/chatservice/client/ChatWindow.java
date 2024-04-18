/*
 * Copyright (c) 2024.  Jerome David. Univ. Grenoble Alpes.
 * This file is part of DcissChatService.
 *
 * DcissChatService is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * DcissChatService is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Foobar. If not, see <https://www.gnu.org/licenses/>.
 */
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
        sendButton = new JButton("Envoyer");

        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = messageField.getText();
                client.sendPacket(0, message.getBytes()); // Envía el mensaje al servidor
                // Ici, vous pouvez ajouter le code pour envoyer le message au serveur
                // client.sendMessage(userId, message);
                appendMessage("You", message);
                messageField.setText("");
            }
        });

        // Ajouter un ActionListener à messageField
        messageField.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Simuler un clic sur sendButton lorsque la touche Enter est pressée
            sendButton.doClick();
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
        // Exemple de comment créer et afficher la fenêtre de chat
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
