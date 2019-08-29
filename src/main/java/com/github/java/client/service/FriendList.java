package com.github.java.client.service;

import com.github.java.util.CommUtils;
import com.github.java.vo.MessageVO;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class FriendList {
    private JPanel fridensPanel;
    private JScrollPane friendsList;
    private JScrollPane groupList;
    private JButton createGroupBtn;
    private JFrame frame;

    private String userName;
    private Set<String> users;
    private Connect2Server connect2Server;

    // 缓存所有私聊界面
    private Map<String,PrivateChatGUI> privateChatGUIList = new ConcurrentHashMap<>();

    private class DaemonTask implements Runnable{
        private Scanner in = new Scanner(connect2Server.getIn());

        public DaemonTask() {

        }

        @Override
        public void run() {
            while (true){
                if(in.hasNextLine()){
                    String strFromServer = in.nextLine();
                    if(strFromServer.startsWith("{")){
                        MessageVO messageVO = (MessageVO) CommUtils.json2Object(strFromServer,
                                MessageVO.class);
                        if (messageVO.getType().equals("2")) {
                            // 服务器发来的私聊信息
                            String friendName = messageVO.getContent().split("-")[0];
                            String msg = messageVO.getContent().split("-")[1];
                            // 判断此私聊是否是第一次创建
                            if (privateChatGUIList.containsKey(friendName)) {
                                PrivateChatGUI privateChatGUI = privateChatGUIList.get(friendName);
                                privateChatGUI.getFrame().setVisible(true);
                                privateChatGUI.readFromServer(friendName+"说:"+msg);
                            }else {
                                PrivateChatGUI privateChatGUI = new PrivateChatGUI(friendName,
                                        userName,connect2Server);
                                privateChatGUIList.put(friendName,privateChatGUI);
                                privateChatGUI.readFromServer(friendName+"说:"+msg);
                            }
                        }
                    } else {
                       if(strFromServer.startsWith("newLogin")){
                           String newfriendName = strFromServer.split(":")[1];
                           users.add(newfriendName);
                           JOptionPane.showMessageDialog(frame,
                                   newfriendName+"上线了!",
                                   "上线提醒",JOptionPane.INFORMATION_MESSAGE);
                           loadUsers();
                       }
                    }
                }
            }

        }
    }

    private class PrivateLabelAction implements MouseListener{

        private String labelName;

        public PrivateLabelAction(String labelName) {
            this.labelName = labelName;
        }
        @Override
        public void mouseClicked(MouseEvent e) {
            if (privateChatGUIList.containsKey(labelName)) {
                PrivateChatGUI privateChatGUI = privateChatGUIList.get(labelName);
                privateChatGUI.getFrame().setVisible(true);
            }else {
                // 第一次点击，创建私聊界面
                PrivateChatGUI privateChatGUI = new PrivateChatGUI(
                        labelName,userName,connect2Server
                );
                privateChatGUIList.put(labelName,privateChatGUI);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    public FriendList(String userName, Set<String> users,
                       Connect2Server connect2Server) {
        this.userName = userName;
        this.users = users;
        this.connect2Server = connect2Server;
        frame = new JFrame(userName);
        frame.setContentPane(fridensPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400,300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        loadUsers();
        Thread thread = new Thread(new DaemonTask());
        thread.setDaemon(true);
        thread.start();
    }
    // 加载所有在线的用户信息
    public void loadUsers() {
        JLabel[] userLabels = new JLabel [users.size()];
        JPanel friends = new JPanel();
        friends.setLayout(new BoxLayout(friends,BoxLayout.Y_AXIS));
        // set遍历
        Iterator<String> iterator = users.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            String userName = iterator.next();
            userLabels[i] = new JLabel(userName);
            // 添加标签点击事件
            userLabels[i].addMouseListener(new PrivateLabelAction(userName));
            friends.add(userLabels[i]);
            i++;
        }
        friendsList.setViewportView(friends);
        // 设置滚动条垂直滚动
        friendsList.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        friends.revalidate();
        friendsList.revalidate();
    }
}
