package com.github.java.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class CommUtils {
    private static Gson gson = new GsonBuilder().create();

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

    public static String object2Json(Object object){
        String str = gson.toJson(object);
        return str;
    }

    public static Object json2Object(String str, Class oclass){
        Object obj = gson.fromJson(str,oclass);
        return obj;
    }

}
