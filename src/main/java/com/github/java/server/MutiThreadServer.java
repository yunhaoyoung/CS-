package com.github.java.server;

import com.github.java.util.CommUtils;
import com.github.java.vo.MessageVO;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MutiThreadServer {
    private static final int PORT;
    private static final String IP;
    static {
        Properties properties = CommUtils.loadProperties("socket.properties");
        IP = properties.getProperty("address");
        PORT = Integer.parseInt(properties.getProperty("port"));
    }

    static class ExecuteClient implements Runnable{

        private Socket client;
        private Scanner in;
        private PrintStream out;

        private static Map<String, Socket> clients = new ConcurrentHashMap<>();
        ExecuteClient(Socket client){
            this.client = client;
            try {
                this.in = new Scanner(client.getInputStream());
                this.out = new PrintStream(client.getOutputStream(),
                        true,"UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void run() {
            while (true) {
                if (in.hasNextLine()) {
                    String jsonStrFromClient = in.nextLine();
                    MessageVO msgFromClient =
                            (MessageVO) CommUtils.json2Object(jsonStrFromClient,
                                    MessageVO.class);
                    // 新用户注册到服务端
                    if (msgFromClient.getType().equals("1")) {
                        String userName = msgFromClient.getContent();
                        // 将当前在线的所有用户名发回客户端
                        MessageVO msg2Client = new MessageVO();
                        msg2Client.setType("1");
                        msg2Client.setContent(CommUtils.object2Json(
                                clients.keySet()
                        ));
                        out.println(CommUtils.object2Json(msg2Client));
                        // 将新上线的用户信息发回给当前已在线的所有用户
                        sendUserLogin("newLogin:" + userName);
                        // 将当前新用户注册到服务端缓存
                        clients.put(userName, client);
                        System.out.println(userName + "上线了!");
                        System.out.println("当前聊天室共有" +
                                clients.size() + "人");
                    }else if (msgFromClient.getType().equals("2")) {
                        // 用户私聊
                        // type:2
                        //  Content:myName-msg
                        //  to:friendName
                        String friendName = msgFromClient.getTo();
                        Socket clientSocket = clients.get(friendName);
                        try {
                            PrintStream out = new PrintStream(clientSocket.getOutputStream(),
                                    true,"UTF-8");
                            MessageVO msg2Client = new MessageVO();
                            msg2Client.setType("2");
                            msg2Client.setContent(msgFromClient.getContent());
                            System.out.println("收到私聊信息,内容为"+msgFromClient.getContent());
                            out.println(CommUtils.object2Json(msg2Client));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        private void sendUserLogin(String msg) {
            for (Map.Entry<String,Socket> entry: clients.entrySet()) {
                Socket socket = entry.getValue();
                try {
                    PrintStream out = new PrintStream(socket.getOutputStream(),
                            true,"UTF-8");
                    out.println(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        ExecutorService executors = Executors.newFixedThreadPool(50);
        for (int i=0; i<50; i++){
            System.out.println("等待客户端连接");
            Socket client = serverSocket.accept();
            executors.submit(new ExecuteClient(client));
            System.out.println("客户端连接成功，端口号为:"+client.getPort());
        }
    }
}
