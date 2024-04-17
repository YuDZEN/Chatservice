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

package fr.uga.miashs.dciss.chatservice.client;

import fr.uga.miashs.dciss.chatservice.common.Message;
import fr.uga.miashs.dciss.chatservice.common.MessageType;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class UserService {
    public boolean login(User user){
        try{
            Socket socket = new Socket("localhost", 8080);//和本机的8080端口建立连接

            OutputStream outputStream = socket.getOutputStream(); //获取输出流，向外发送数据
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream); //将对象转换为输出流
            objectOutputStream.writeObject(user); //将对象写入输出流
            //通过socket连接的输入流，读入服务器的返回信息
            InputStream inputStream = socket.getInputStream();
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            Message message = (Message) objectInputStream.readObject();

            if (message.getMessageType() == MessageType.LOGIN_SUCCESS){
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
