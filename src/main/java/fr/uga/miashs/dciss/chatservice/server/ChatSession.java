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

        JPanel loginPanel = new JPanel(new GridLayout(4, 2, 5, 5)); // Agregar un espacio entre filas y columnas

        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");

        // Establecer un tamaño preferido para los campos de texto y los botones
        usernameField.setPreferredSize(new Dimension(150, 25));
        passwordField.setPreferredSize(new Dimension(150, 25));
        loginButton.setPreferredSize(new Dimension(100, 25));
        registerButton.setPreferredSize(new Dimension(100, 25));

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

                // Verificar si los campos de texto están vacíos
                if (username.isEmpty() || password.length == 0) {
                    JOptionPane.showMessageDialog(ChatSession.this, "Por favor, ingrese un nombre de usuario y contraseña.");
                    return;
                }

                try {
                    // Construir la consulta SQL para verificar las credenciales del usuario
                    String query = "SELECT id FROM Utilisateurs WHERE nom_utilisateur = ? AND mot_de_passe_hash = ?";
                    ResultSet resultSet = DatabaseManager.executeQuery(query, username, new String(password));

                    if (resultSet.next()) {
                        // Inicio de sesión exitoso
                        int userId = resultSet.getInt("id");
                        JOptionPane.showMessageDialog(ChatSession.this, "Inicio de sesión exitoso!");

                        // Aquí puedes abrir una nueva ventana o realizar otras acciones después del inicio de sesión
                        // Por ejemplo, podrías crear una instancia de tu clase ChatWindow y mostrarla
                        // ChatWindow chatWindow = new ChatWindow(userId);
                        // chatWindow.setVisible(true);
                    } else {
                        // Credenciales incorrectas
                        JOptionPane.showMessageDialog(ChatSession.this, "Credenciales incorrectas. Por favor, inténtelo de nuevo.");
                    }
                } catch (SQLException ex) {
                    // Manejar cualquier excepción de la base de datos
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(ChatSession.this, "Error al iniciar sesión. Inténtalo de nuevo.");
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


    public static void main(String[] args) {
        ChatSession chatSession = new ChatSession();
        chatSession.setVisible(true);
    }
}
