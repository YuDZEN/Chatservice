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

package fr.uga.miashs.dciss.chatservice.common.db;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import fr.uga.miashs.dciss.chatservice.client.Message;
import org.mindrot.jbcrypt.BCrypt; // Pour hacher les mots de passe


import fr.uga.miashs.dciss.chatservice.client.ChatWindow;

public class DatabaseManager {
    private static final Logger LOG = Logger.getLogger(DatabaseManager.class.getName());
    private static final String DB_URL = "jdbc:mysql://localhost/chat_service";
    private static final String USER = "root";
    private static final String PASSWORD = null;

    // Método para establecer una conexión con la base de datos
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASSWORD);
    }

    // Método para ejecutar una consulta SQL y obtener un conjunto de resultados
    public static ResultSet executeQuery(String query, Object... params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(query)) {
            // Establecer parámetros de consulta, si los hay
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }
            // Ejecutar la consulta y devolver el conjunto de resultados
            return statement.executeQuery();
        }
    }

    // Método para ejecutar una actualización SQL (INSERT, UPDATE, DELETE)
    public static int executeUpdate(String query, Object... params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(query)) {
            // Establecer parámetros de consulta, si los hay
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }
            // Ejecutar la actualización y devolver el número de filas afectadas
            return statement.executeUpdate();
        }
    }

    public static boolean usernameExists(String username) throws SQLException {
        // Requête SQL pour vérifier l'existence du nom d'utilisateur
        String query = "SELECT COUNT(*) FROM Utilisateurs WHERE nom_utilisateur = ?";

        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count >= 1; // S'il y a une ligne avec le nom d'utilisateur fourni, retourne vrai
                }
            }
        }

        return false; // S'il n'y a pas de lignes trouvées, le nom d'utilisateur n'existe pas
    }



    public static List<Message> getMessagesForUser(int userId) throws SQLException {
        List<Message> messages = new ArrayList<>();
        String query = "SELECT sender_id, message FROM Messages WHERE recipient_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int senderId = resultSet.getInt("sender_id");
                    String message = resultSet.getString("message");
                    messages.add(new Message(senderId, message));
                }
            }
        }
        return messages;
    }

    public static void saveMessage(int senderId, int recipientId, String message) throws SQLException {
        String query = "INSERT INTO Messages (sender_id, recipient_id, message) VALUES (?, ?, ?)";
        executeUpdate(query, senderId, recipientId, message);
    }

    public static boolean userExists(int userId) throws SQLException {
        String query = "SELECT COUNT(*) FROM Utilisateurs WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public static boolean verifyCredentials(String username, char[] password) throws SQLException {
        String query = "SELECT mot_de_passe_hash FROM Utilisateurs WHERE nom_utilisateur = ?";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String hashedPassword = resultSet.getString("mot_de_passe_hash");
                    return BCrypt.checkpw(new String(password), hashedPassword);
                }
            }
        }
        return false; // S'il n'y a pas de lignes trouvées, les identifiants sont invalides
    }


    public static int getUserIdByUsername(String username) throws SQLException {
        String query = "SELECT id FROM Utilisateurs WHERE nom_utilisateur = ?";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                } else {
                    // Agrega un registro (log) para indicar que el usuario no fue encontrado en la base de datos
                    LOG.warning("User with username " + username + " not found in the database");
                    throw new SQLException("User with username " + username + " not found");
                }
            }
        } catch (SQLException e) {
            // Maneja la excepción y registra (log) el error
            LOG.severe("Error while fetching user ID for username " + username + ": " + e.getMessage());
            throw e;
        }
    }


    public static String getUserNameById(int userId) throws SQLException {
        String query = "SELECT nom_utilisateur FROM Utilisateurs WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("nom_utilisateur");
                }
            }
        }
        throw new SQLException("User with ID " + userId + " not found");
    }

    // Fonction pour hacher le mot de passe
    public static String hashPassword(char[] password) {
        // Convertir le tableau de caractères en String
        String passwordStr = new String(password);
        // Hacher le mot de passe
        String hashedPassword = BCrypt.hashpw(passwordStr, BCrypt.gensalt());

        return hashedPassword; // Retourne le hachage du mot de passe
    }


    public static ArrayList<String> getAllUsernames() throws SQLException {
        ArrayList<String> usernames = new ArrayList<>();
        String query = "SELECT nom_utilisateur FROM Utilisateurs";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                usernames.add(resultSet.getString("nom_utilisateur"));
            }
        }
        return usernames;
    }


}