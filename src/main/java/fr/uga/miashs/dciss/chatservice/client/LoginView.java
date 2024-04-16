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

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginView extends JFrame {

    public static void main(String[] args) {
        LoginView loginView = new LoginView();
        loginView.createFrame();
    }

    //North Panel
    JPanel northJPanel = null;
    JLabel photoJLabel = null;

    //Center Panel
    JLabel usernameJLabel = null;
    JTextField usernameJTextField = null;
    JLabel passwordJLabel = null;
    JPasswordField passwordJPasswordField = null;
    JPanel centerJPanel = null;

    //South Panel
    JButton loginJButton = null;
    JButton registerJButton = null;
    JPanel southJPanel = null;

    public void createFrame(){
        //north image
        ImageIcon imageIcon = new ImageIcon(LoginView.class.getResource("/logo.png"));
        imageIcon.setImage(imageIcon.getImage().getScaledInstance(100, 65, Image.SCALE_DEFAULT));
        photoJLabel = new JLabel(imageIcon, JLabel.CENTER);
        northJPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        northJPanel.add(photoJLabel);
        this.add(northJPanel, BorderLayout.NORTH);

        // center
        centerJPanel = new JPanel();
        centerJPanel.setLayout(new BoxLayout(centerJPanel, BoxLayout.Y_AXIS));

        // 用户名 panel
        JPanel usernamePanel = new JPanel();
        usernamePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        usernameJLabel = new JLabel("Username");
        usernameJTextField = new JTextField(20);
        usernamePanel.add(usernameJLabel);
        usernamePanel.add(usernameJTextField);

        // 密码 panel
        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        passwordJLabel = new JLabel("Password");
        passwordJPasswordField = new JPasswordField(20);
        passwordPanel.add(passwordJLabel);
        passwordPanel.add(passwordJPasswordField);

        // 将 usernamePanel 和 passwordPanel 添加到 centerJPanel
        centerJPanel.add(Box.createVerticalGlue());
        centerJPanel.add(usernamePanel);
        centerJPanel.add(passwordPanel);
        centerJPanel.add(Box.createVerticalGlue());

        // 将 centerJPanel 添加到 frame
        this.add(centerJPanel, BorderLayout.CENTER);

        // south
        loginJButton = new JButton("Login");
        loginJButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                User user = new User();
                user.setUsername(usernameJTextField.getText());
                user.setPassword(new String(passwordJPasswordField.getPassword()));

                UserService userService = new UserService();
                if(userService.login(user)){
                    LoginView.this.dispose();
                } else{
                    JOptionPane.showMessageDialog(LoginView.this, "Username or password is incorrect", "Error", JOptionPane.ERROR_MESSAGE);
                }

            }
        });

        registerJButton = new JButton("Register");
        southJPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        southJPanel.add(loginJButton);
        southJPanel.add(registerJButton);
        this.add(southJPanel, BorderLayout.SOUTH);


        this.setTitle("Login");
        this.setBounds(500,300, 400, 300);
        this.setVisible(true);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
