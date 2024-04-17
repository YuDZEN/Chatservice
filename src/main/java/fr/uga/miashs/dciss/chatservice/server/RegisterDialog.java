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

package fr.uga.miashs.dciss.chatservice.server;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import fr.uga.miashs.dciss.chatservice.common.db.DatabaseManager;


public class RegisterDialog extends JDialog {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField emailField;
    private JButton registerButton;
    private JButton cancelButton;

    public RegisterDialog(Frame parent) {
        super(parent, "Register", true); // Diálogo modal

        JPanel registerPanel = new JPanel(new GridLayout(4, 2, 5, 5)); // Grid layout para organizar los componentes

        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        JLabel emailLabel = new JLabel("Email:");
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        emailField = new JTextField();
        registerButton = new JButton("Register");
        cancelButton = new JButton("Cancel");

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                char[] password = passwordField.getPassword();
                String email = emailField.getText();

                // Validación de datos
                if (username.isEmpty() || password.length == 0 || email.isEmpty()) {
                    JOptionPane.showMessageDialog(RegisterDialog.this, "Por favor, complete todos los campos.");
                    return; // Detener el proceso de registro si los campos están vacíos
                }

                // Otras validaciones (puedes agregar más según tus requisitos)

                try {
                    // Construir la consulta SQL de inserción
                    String insertQuery = "INSERT INTO Utilisateurs (nom_utilisateur, mot_de_passe_hash, email) VALUES (?, ?, ?)";

                    // Ejecutar la consulta SQL
                    int rowsAffected = DatabaseManager.executeUpdate(insertQuery, username, new String(password), email);

                    if (rowsAffected > 0) {
                        // Registro exitoso
                        JOptionPane.showMessageDialog(RegisterDialog.this, "Registro exitoso!");
                        dispose(); // Cierra el diálogo
                    } else {
                        // Registro fallido
                        JOptionPane.showMessageDialog(RegisterDialog.this, "Error al registrar usuario. Inténtalo de nuevo.");
                    }
                } catch (SQLException ex) {
                    // Manejar cualquier excepción de la base de datos
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(RegisterDialog.this, "Error al registrar usuario. Inténtalo de nuevo.");
                }
            }
        });


        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Cierra el diálogo
            }
        });

        registerPanel.add(usernameLabel);
        registerPanel.add(usernameField);
        registerPanel.add(passwordLabel);
        registerPanel.add(passwordField);
        registerPanel.add(emailLabel);
        registerPanel.add(emailField);
        registerPanel.add(registerButton);
        registerPanel.add(cancelButton);

        add(registerPanel);
        pack(); // Ajusta el tamaño del diálogo automáticamente según su contenido
        setLocationRelativeTo(parent); // Centra el diálogo en la pantalla
    }
}

