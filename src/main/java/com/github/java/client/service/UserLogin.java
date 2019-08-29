package com.github.java.client.service;

import com.github.java.client.dao.AccountDao;
import com.github.java.client.entity.User;
import com.github.java.util.CommUtils;
import com.github.java.vo.MessageVO;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import java.util.Set;

public class UserLogin {
    private JPanel UserLogin;
    private JTextField userNameText;
    private JPasswordField passwordText;
    private JButton regButton;
    private JButton loginButton;
    private JPanel btnPanel;
    private JPanel UserPanel;
    private AccountDao accountDao = new AccountDao();

    public UserLogin() {
        JFrame frame = new JFrame("UserLogin");
        frame.setContentPane(UserLogin);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
        regButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new UserReg();
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userName = userNameText.getText();
                String password = String.valueOf(
                        passwordText.getPassword());
                User user = accountDao.login(userName,password);
                if (user != null) {
                    // 成功，加载用户列表
                    JOptionPane.showMessageDialog(frame,
                            "登录成功!","提示信息",JOptionPane.INFORMATION_MESSAGE);
                    frame.setVisible(false);
                    // 与服务器建立连接，将当前用户的用户名发送到服务端
                    Connect2Server connect2Server = new Connect2Server();
                    MessageVO msg2Server = new MessageVO();
                    msg2Server.setType("1");
                    msg2Server.setContent(userName);
                    String json2Server = CommUtils.object2Json(msg2Server);
                    try {
                        PrintStream out = new PrintStream(connect2Server.getOut(),
                                true,"UTF-8");
                        out.println(json2Server);
                        // 读取服务端发回的所有在线用户信息
                        Scanner in = new Scanner(connect2Server.getIn());
                        if (in.hasNextLine()) {
                            String msgFromServerStr = in.nextLine();
                            MessageVO msgFromServer =
                                    (MessageVO) CommUtils.json2Object(msgFromServerStr,
                                            MessageVO.class);
                            Set<String> users =
                                    (Set<String>) CommUtils.json2Object(msgFromServer.getContent(),
                                            Set.class);
                            System.out.println("所有在线用户为:"+users);
                            // 加载用户列表界面
                            // 将当前用户名、所有在线好友、与服务器建立连接传递到好友列表界面
                            new FriendList(userName,users,connect2Server);
                        }
                    } catch (UnsupportedEncodingException ex) {
                        ex.printStackTrace();
                    }
                }else {
                    // 失败，停留在当前登录页面，提示用户信息错误
                    JOptionPane.showMessageDialog(frame,
                            "登录失败!","错误信息",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    public static void main(String[] args) {
        UserLogin userLogin = new UserLogin();
    }
}
