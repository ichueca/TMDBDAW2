/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.zabalburu.seriesjpa.tmdb.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class TmdbConfig {

    private static final String CONFIG_PATH = "config/tmdb.properties";
    private static final Properties properties = new Properties();

    static {
        try (InputStream is = new FileInputStream(CONFIG_PATH)) {
            properties.load(is);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "No se ha podido cargar el fichero de configuración externo: " + CONFIG_PATH, e
            );
        }
    }

    public static String getApiKey() {
        return properties.getProperty("tmdb.api.key");
    }

    public static String getBaseUrl() {
        return properties.getProperty("tmdb.base.url");
    }

    public static String getImageBaseUrl() {
        return properties.getProperty("tmdb.image.base.url");
    }
}

