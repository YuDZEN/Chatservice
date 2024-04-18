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

        JPanel loginPanel = new JPanel(new GridLayout(4, 2, 5, 5)); // Agregar un espacio entre filas y columnas

        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");

        // Establecer un tamaño preferido para los campos de texto y los botones
        usernameField.setPreferredSize(new Dimension(150, 30));
        passwordField.setPreferredSize(new Dimension(150, 30));
        loginButton.setPreferredSize(new Dimension(100, 30));
        registerButton.setPreferredSize(new Dimension(100, 30));

        // Establecer un estilo para los botones
        loginButton.setBackground(Color.BLUE);
        loginButton.setForeground(Color.BLACK);
        registerButton.setBackground(Color.GRAY);
        registerButton.setForeground(Color.BLACK);

        // Establecer un estilo para los labels
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        // Agregar espacio adicional al panel de login
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
                    int userId = DatabaseManager.getUserIdByUsername(username); // Obtener el ID de usuario
                    JOptionPane.showMessageDialog(ChatSession.this, "Connecté avec succès!");
                    ClientMsg client = new ClientMsg("localhost", 1666); // Crear el cliente
                    client.startSession(); // Iniciar la sesión
                    ChatWindow chatWindow = new ChatWindow(username, client); // Crear la ventana del chat con el ID de usuario y el cliente
                    chatWindow.setVisible(true);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(ChatSession.this, "Nom d'utilisateur ou mot de passe incorrect.");
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
                registerDialog.setVisible(true); // Muestra el diálogo de registro
            }
        });

        loginPanel.add(usernameLabel);
        loginPanel.add(usernameField);
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);
        loginPanel.add(new JLabel()); // Espacio vacío
        loginPanel.add(loginButton);
        loginPanel.add(new JLabel()); // Espacio vacío
        loginPanel.add(registerButton);

        add(loginPanel, BorderLayout.CENTER);
        add(new JLabel("Bienvenue sur Chat Service", SwingConstants.CENTER), BorderLayout.NORTH); // Centrar el texto
    }

    private String hashPassword(char[] password) {
        // Implementa tu lógica de hash aquí (por ejemplo, usando bcrypt o PBKDF2)
        return ""; // Devuelve el hash de la contraseña
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ChatSession chatSession = new ChatSession();
                chatSession.setVisible(true);
            }
        });
    }
}
