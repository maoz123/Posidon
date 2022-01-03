package com.demo.posidon.server.common;

import java.io.IOException;
import java.util.Properties;

public class PropertiesFactory {
    public static Properties getProperties() {
        return properties;
    }

    private static Properties properties;

    static {
        properties = new Properties();
        try
        {
            properties.load(PropertiesFactory.class.getClassLoader().getResourceAsStream( "posidon.properties"));
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}