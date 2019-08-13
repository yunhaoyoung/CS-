package com.github.java.client.service;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UserLogin {
    private JPanel UserLogin;
    private JTextField textField1;
    private JPasswordField passwordField1;
    private JButton regButton;
    private JButton loginButton;
    private JPanel btnPanel;
    private JPanel UserPanel;

    public UserLogin() {
        regButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new UserReg();
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("UserLogin");
        frame.setContentPane(new UserLogin().UserLogin);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
    }
}
