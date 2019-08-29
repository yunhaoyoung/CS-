package com.github.java.client.service;

import com.github.java.util.CommUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Properties;

public class Connect2Server {
    private static final int PORT;
    private static final String IP;
    static {
        Properties properties = CommUtils.loadProperties("socket.properties");
        IP = properties.getProperty("address");
        PORT = Integer.parseInt(properties.getProperty("port"));
    }
    private Socket client;
    private OutputStream out;
    private InputStream in;

    public Connect2Server(){
        try {
            client = new Socket(IP,PORT);
            in = client.getInputStream();
            out = client.getOutputStream();
        } catch (IOException e) {
            System.err.println("客户端与服务器连接失败");
            e.printStackTrace();
        }
    }
    public OutputStream getOut() {
        return out;
    }

    public InputStream getIn() {
        return in;
    }
}
