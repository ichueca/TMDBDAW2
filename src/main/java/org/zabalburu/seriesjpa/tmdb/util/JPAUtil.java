package org.zabalburu.seriesjpa.tmdb.util;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.HashMap;
import java.util.Map;

public class JPAUtil {

    private static EntityManagerFactory emf;

    public static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {
            String env = System.getProperty("app.env", "dev");

            String persistenceUnitName = switch (env) {
                case "test" -> "seriesjpa_test";
                case "prod" -> "seriesjpa_prod";
                default -> "seriesjpa_dev";
            };

            if ("prod".equals(env)) {
                Map<String, Object> overrides = new HashMap<>();

                String dbUrl = System.getProperty("db.url");
                String dbUser = System.getProperty("db.user");
                String dbPassword = System.getProperty("db.password");

                if (dbUrl != null && !dbUrl.isBlank()) {
                    overrides.put("jakarta.persistence.jdbc.url", dbUrl);
                }

                if (dbUser != null && !dbUser.isBlank()) {
                    overrides.put("jakarta.persistence.jdbc.user", dbUser);
                }

                if (dbPassword != null) {
                    overrides.put("jakarta.persistence.jdbc.password", dbPassword);
                }

                emf = Persistence.createEntityManagerFactory(persistenceUnitName, overrides);
            } else {
                emf = Persistence.createEntityManagerFactory(persistenceUnitName);
            }
        }

        return emf;
    }

    public static EntityManagerFactory getEntityManagerFactory(String persistenceUnitName) {
        return Persistence.createEntityManagerFactory(persistenceUnitName);
    }

    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
