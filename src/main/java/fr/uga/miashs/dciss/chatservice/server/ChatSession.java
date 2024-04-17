package fr.uga.miashs.dciss.chatservice.server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;



import fr.uga.miashs.dciss.chatservice.client.ClientMsg;
import fr.uga.miashs.dciss.chatservice.common.db.DatabaseManager;

public class ChatSession extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JTextArea messageArea;
    private JTextField inputField;
    private ClientMsg client;

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

                //  Vérifier si les champs de texte sont vides
                if (username.isEmpty() || password.length == 0) {
                    JOptionPane.showMessageDialog(ChatSession.this, "Veuillez entrer un nom d'utilisateur et un mot de passe.");
                    return;
                }

                try {
                    // Construire la requête SQL pour vérifier les identifiants de l'utilisateur
                    String query = "SELECT id FROM Utilisateurs WHERE nom_utilisateur = ? AND mot_de_passe_hash = ?";
                    ResultSet resultSet = DatabaseManager.executeQuery(query, username, new String(password));

                    if (resultSet.next()) {
                        // Connexion réussie
                        int userId = resultSet.getInt("id");
                        JOptionPane.showMessageDialog(ChatSession.this, "Connexion réussie!");

                        // Ici, vous pouvez ouvrir une nouvelle fenêtre ou effectuer d'autres actions après la connexion
                        // Par exemple, vous pourriez créer une instance de votre classe ChatWindow et l'afficher
                        // ChatWindow chatWindow = new ChatWindow(userId);
                        // chatWindow.setVisible(true);
                    } else {
                        // Identifiants incorrects
                        JOptionPane.showMessageDialog(ChatSession.this, "Identifiants incorrects. Veuillez réessayer.");
                    }
                } catch (SQLException ex) {
                    // Gérer toute exception de la base de données
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(ChatSession.this, "Erreur lors de la connexion. Veuillez réessayer.");
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
