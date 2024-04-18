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
        super(parent, "Register", true); // Dialogue modal

        JPanel registerPanel = new JPanel(new GridLayout(4, 2, 5, 5)); // Grid layout pour organiser les composants

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

                // Validation des données
                if (username.isEmpty() || password.length == 0 || email.isEmpty()) {
                    JOptionPane.showMessageDialog(RegisterDialog.this, "Veuillez remplir tous les champs.");
                    return; // Arrête le processus d'inscription si les champs sont vides
                }

                // Hacher le mot de passe
                String hashedPassword = DatabaseManager.hashPassword(password);

                // Autres validations (vous pouvez en ajouter plus selon vos besoins)
                try {
                    // Vérifier si le nom d'utilisateur existe déjà
                    if (DatabaseManager.usernameExists(username)) {
                        // Informer l'utilisateur que le nom d'utilisateur est déjà pris
                        JOptionPane.showMessageDialog(RegisterDialog.this, "Le nom d'utilisateur est déjà pris. Veuillez choisir un autre nom d'utilisateur.");
                    } else {
                        // Construire la requête SQL d'insertion
                        String insertQuery = "INSERT INTO Utilisateurs (nom_utilisateur, mot_de_passe_hash, email) VALUES (?, ?, ?)";

                        // Exécuter la requête SQL
                        int rowsAffected = DatabaseManager.executeUpdate(insertQuery, username, hashedPassword, email);

                        if (rowsAffected > 0) {
                            // Enregistrement réussi
                            JOptionPane.showMessageDialog(RegisterDialog.this, "Enregistrement réussi !");
                            dispose(); // Ferme la boîte de dialogue
                        } else {
                            // Échec de l'enregistrement
                            JOptionPane.showMessageDialog(RegisterDialog.this, "Erreur lors de l'enregistrement de l'utilisateur. Essayez à nouveau.");
                        }
                    }
                } catch (SQLException ex) {
                    // Gérer toute exception de la base de données
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(RegisterDialog.this, "Erreur lors de l'enregistrement de l'utilisateur. Essayez à nouveau.");
                }

            }
        });


        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Ferme la boîte de dialogue
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
        pack(); // Ajuste la taille de la boîte de dialogue automatiquement en fonction de son contenu
        setLocationRelativeTo(parent); // Centre la boîte de dialogue sur l'écran
    }
}