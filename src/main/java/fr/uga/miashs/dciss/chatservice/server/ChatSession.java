package fr.uga.miashs.dciss.chatservice.server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import fr.uga.miashs.dciss.chatservice.client.ClientMsg;
import fr.uga.miashs.dciss.chatservice.common.db.DatabaseManager;
import fr.uga.miashs.dciss.chatservice.client.ChatWindow;

public class ChatSession extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;

    public ChatSession() {
        setTitle("Chat Service");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel loginPanel = new JPanel(new GridLayout(4, 2, 5, 5)); // Ajouter un espace entre les lignes et les colonnes

        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");

        // Définir une taille préférée pour les champs de texte et les boutons
        usernameField.setPreferredSize(new Dimension(150, 25));
        passwordField.setPreferredSize(new Dimension(150, 25));
        loginButton.setPreferredSize(new Dimension(100, 25));
        registerButton.setPreferredSize(new Dimension(100, 25));

        // Définir un style pour les boutons
        loginButton.setBackground(Color.BLUE);
        loginButton.setForeground(Color.BLACK);
        registerButton.setBackground(Color.GRAY);
        registerButton.setForeground(Color.BLACK);

        // Définir un style pour les labels
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        // Ajouter un espace supplémentaire au panneau de connexion
        loginPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                char[] password = passwordField.getPassword();

                if (username.isEmpty() || password.length == 0) {
                    JOptionPane.showMessageDialog(ChatSession.this, "Veuillez entrer un nom d'utilisateur et un mot de passe.");
                    return;
                }

                try {
                    // Vérifier les identifiants de l'utilisateur
                    if(DatabaseManager.verifyCredentials(username, password)) {
                        JOptionPane.showMessageDialog(ChatSession.this, "Connecté avec succès!");
                        ClientMsg client = new ClientMsg("localhost", 1666); // Création du client
                        client.startSession(); // Initialisation de la session
                        ChatWindow chatWindow = new ChatWindow(username, client); // Créer la fenêtre de chat avec l’ID utilisateur
                        chatWindow.setVisible(true);
                    } else {
                        // Informer l'utilisateur que les identifiants sont incorrects
                        JOptionPane.showMessageDialog(ChatSession.this, "Nom d'utilisateur ou mot de passe incorrect.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(ChatSession.this, "Erreur d’obtention de l’ID utilisateur. Réessayez.");
                }
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RegisterDialog registerDialog = new RegisterDialog(ChatSession.this);
                registerDialog.setVisible(true); // Affiche la boîte de dialogue d'enregistrement
            }
        });

        loginPanel.add(usernameLabel);
        loginPanel.add(usernameField);
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);
        loginPanel.add(new JLabel()); // Espace vide
        loginPanel.add(loginButton);
        loginPanel.add(new JLabel()); // Espace vide
        loginPanel.add(registerButton);

        add(loginPanel, BorderLayout.CENTER);
        add(new JLabel("Bienvenue sur Chat Service", SwingConstants.CENTER), BorderLayout.NORTH); // Centrer le texte
    }

    public static void main(String[] args) {
        ChatSession chatSession = new ChatSession();
        chatSession.setVisible(true);
    }
}
