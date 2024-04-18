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
import java.sql.*;
import javax.swing.JOptionPane;
import java.util.ArrayList;
import fr.uga.miashs.dciss.chatservice.common.db.DatabaseManager;
import fr.uga.miashs.dciss.chatservice.server.ChatSession;


public class ChatWindow extends JFrame {
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;
    private JComboBox<String> userComboBox; // Agregar JComboBox para seleccionar usuario
    private ClientMsg client;
    private int userId;

    public ChatWindow(String nom_utilisateur, ClientMsg client) {
        this.userId = userId;
        this.client = client;
        setTitle("Chat - User : " + nom_utilisateur);
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());

        // Botón de Cerrar Sesión
        JButton logoutButton = new JButton("Se déconnecter");
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Cierra la conexión del cliente si es necesario
                client.closeSession(); // Suponiendo que tienes un método closeSession en tu clase ClientMsg para cerrar la sesión del cliente

                // Cierra la ventana actual
                dispose();

                ChatSession chatSession = new ChatSession();
                chatSession.setVisible(true);
            }
        });
        topPanel.add(logoutButton, BorderLayout.EAST);


        add(topPanel, BorderLayout.NORTH); // Agregar el panel superior con el JComboBox

        // ComboBox para seleccionar usuario
        userComboBox = new JComboBox<>();
        topPanel.add(userComboBox, BorderLayout.CENTER);

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
                String selectedUser = (String) userComboBox.getSelectedItem(); // Obtener el usuario seleccionado
                if (selectedUser != null) {
                    // Aquí necesitarás obtener el ID del usuario seleccionado
                    // luego enviar el mensaje al servidor y guardar en la base de datos
                    // Supongamos que obtienes el ID del usuario seleccionado en selectedUserId
                    int selectedUserId = getUserIdByName(selectedUser); // Corregir el nombre del método
                    client.sendPacket(selectedUserId, message.getBytes());
                    appendMessage("You", message);
                    saveMessage(userId, selectedUserId, message);
                    messageField.setText("");
                } else {
                    JOptionPane.showMessageDialog(ChatWindow.this, "Please select a user to send message.");
                }
            }
        });

        messagePanel.add(messageField, BorderLayout.CENTER);
        messagePanel.add(sendButton, BorderLayout.EAST);

        chatPanel.add(scrollPane, BorderLayout.CENTER);
        chatPanel.add(messagePanel, BorderLayout.SOUTH);

        add(chatPanel, BorderLayout.CENTER);

        // Recuperar y mostrar mensajes desde la base de datos al iniciar la ventana
        retrieveAndDisplayMessages();

        // Cargar usuarios disponibles en el JComboBox
        loadUsers();
    }

    private void retrieveAndDisplayMessages() {
        try {
            ResultSet rs = DatabaseManager.getMessagesForUser(userId);
            while (rs.next()) {
                int senderId = rs.getInt("sender_id");
                String senderName = DatabaseManager.getUserNameById(senderId);
                String message = rs.getString("message");
                appendMessage(senderName, message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveMessage(int senderId, int recipientId, String message) {
        try {
            DatabaseManager.saveMessage(senderId, recipientId, message);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadUsers() {
        try {
            ArrayList<String> usernames = DatabaseManager.getAllUsernames();
            for (String username : usernames) {
                userComboBox.addItem(username);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getUserIdByName(String username) {
        try {
            return DatabaseManager.getUserIdByUsername(username);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
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

                    ChatWindow chatWindow = new ChatWindow("lainean", client); // Aquí necesitas pasar el ID del usuario
                    chatWindow.setVisible(true);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
