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
    private static final String DB_URL = "jdbc:mysql://localhost/Chat_Service";
    private static final String USER = "AdminChat";
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
    public static boolean verifyCredentials(String username, char[] password) throws SQLException {
        // Consulta SQL para verificar las credenciales del usuario
        String query = "SELECT COUNT(*) FROM Utilisateurs WHERE nom_utilisateur = ? AND mot_de_passe_hash = ?";

        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, new String(password)); // Establecer la contraseña como un String

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count == 1; // Si hay una fila con las credenciales proporcionadas, devuelve true
                }
            }
        }

        return false; // Si no se encontraron filas, las credenciales son inválidas
    }

    public static int getUserIdByUsername(String username) throws SQLException {
        String query = "SELECT id FROM Utilisateurs WHERE nom_utilisateur = ?";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id"); // Devuelve el ID de usuario si se encuentra el nombre de usuario
                }
            }
        }
        throw new SQLException("Le nom d'utilisateur n'existe pas"); // Lanza una excepción si no se encuentra el nombre de usuario
    }




    // Otros métodos para manejar transacciones, cierre de recursos, etc., según sea necesario
}