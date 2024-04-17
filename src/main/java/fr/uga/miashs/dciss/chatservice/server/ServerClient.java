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

import fr.uga.miashs.dciss.chatservice.common.Message;
import fr.uga.miashs.dciss.chatservice.common.MessageType;

import javax.swing.*;
import java.awt.*;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerClient extends JFrame {
    public static void main(String[] args){
        ServerClient serverClient = new ServerClient();
        serverClient.createJFrame();
    }

    UserDao userDao = new UserDao();

    public void createJFrame() {
        JLabel jLabel = new JLabel("Le serveur est en place, il écoute sur le port 8080 et attend que le client se connecte.", JLabel.CENTER);
        this.add(jLabel, BorderLayout.CENTER);

        // 由于 ServerClient 是 JFrame 的子类，我们可以使用 this 来设置 JFrame 的属性
        this.setTitle("Server");
        this.setBounds(505, 300, 800, 400);
        this.setVisible(true);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //创建服务端界面时启动服务端
        try {
            this.server();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 服务器端连接方法
    public void server() throws Exception {
        ServerSocket serverSocket = new ServerSocket(8080);//服务监听在8080端口
        while(true){
            Socket socket = serverSocket.accept();//等待客户端连接

            // 服务器端接收消息
            InputStream inputStream = socket.getInputStream();
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            User user = (User) objectInputStream.readObject();


            Message message = new Message();
            if(userDao.login(user.getUsername(), user.getPassword())!=null){
                message.setMessageType(MessageType.LOGIN_SUCCESS);
            } else {
                message.setMessageType(MessageType.LOGIN_FAIL);
            }

            //向客户端输出验证结果
            OutputStream outputStream = socket.getOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(message);
        }
    }
}
