package com.github.java.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class CommUtils {
    public static Properties loadProperties(String fileName){
        Properties properties = new Properties();
        InputStream in = CommUtils.class.getClassLoader().getResourceAsStream(fileName);
        try {
            properties.load(in);
        } catch (IOException e){
            return  null;
        }
        return properties;
    }

}
