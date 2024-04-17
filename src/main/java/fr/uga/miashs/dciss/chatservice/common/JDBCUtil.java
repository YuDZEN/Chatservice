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

package fr.uga.miashs.dciss.chatservice.common;

import java.sql.*;

public class JDBCUtil {
    private static JDBCUtil jdbcUtil = null;

    //确保工具类只会被new一次
    private JDBCUtil() {

    }

    //获取工具类的方法
    public static JDBCUtil getInstance() {
        if (jdbcUtil == null) {
            jdbcUtil = new JDBCUtil();
        }
        return jdbcUtil;
    }

    static{
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/chatService", "root", "root");
    }

    public void closeConnection(ResultSet resultSet, Statement statement, Connection connection) {
        try {
            resultSet.close();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try{
            statement.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
            finally {
                try{
                connection.close();
            }catch (Exception e) {
                e.printStackTrace();
                }
        }
    }

}}
