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


public class DatabaseManager {
    private static final String DB_URL = "jdbc:mysql://localhost/chat_service";
    private static final String USER = "AdminChat";
    private static final String PASSWORD = null;

    // Méthode pour établir une connexion avec la base de données
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASSWORD);
    }

    // Méthode pour exécuter une requête SQL et obtenir un ensemble de résultats
    public static ResultSet executeQuery(String query, Object... params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(query)) {
            // Définir les paramètres de la requête, s'il y en a
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }
            // Exécuter la requête et retourner l'ensemble de résultats
            return statement.executeQuery();
        }
    }

    // Méthode pour exécuter une mise à jour SQL (INSERT, UPDATE, DELETE)
    public static int executeUpdate(String query, Object... params) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(query)) {
            // Définir les paramètres de la requête, s'il y en a
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }
            // Exécuter la mise à jour et retourner le nombre de lignes affectées
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
    public static boolean verifyCredentials(String username, char[] password) throws SQLException {
        // Requête SQL pour vérifier les identifiants de l'utilisateur
        String query = "SELECT COUNT(*) FROM Utilisateurs WHERE nom_utilisateur = ? AND mot_de_passe_hash = ?";

        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, String.valueOf(password)); // Convertir char[] en String

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count >= 1; // S'il y a une ligne avec les identifiants fournis, retourne vrai
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
                    return resultSet.getInt("id"); // Renvoie l'ID de l'utilisateur si le nom d'utilisateur est trouvé
                }
            }
        }
        throw new SQLException("Le nom d'utilisateur n'existe pas"); // Lance une exception si le nom d'utilisateur n'est pas trouvé




    // Autres méthodes pour gérer les transactions, fermer les ressources, etc., selon les besoins
    }
    public static String getUsernameById(int userId) throws SQLException {
        // Requête SQL pour obtenir le nom d'utilisateur
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
    
        return null; // Retourne null si aucun utilisateur avec cet ID n'a été trouvé
    }
}
