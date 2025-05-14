package fr.univtln.infomath.dronsim;

import java.time.OffsetDateTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * Utility class providing helper methods for database connection, date
 * manipulation,
 * and creneau validation.
 */
public class Utils {
    private static final Logger log = LoggerFactory.getLogger(Utils.class);
    private static EntityManagerFactory emf;

    /**
     * Initializes the {@link EntityManagerFactory} with the provided user
     * credentials.
     *
     * @param user     The database username.
     * @param password The database password.
     */
    static public void initconnection(String user, String password) {
        log.info("Starting EntityManagerFactory initialization");
        EntityManagerFactory tryEmf = null;

        try {
            log.info("Initializing EntityManagerFactory");
            Map<String, String> properties = new HashMap<>();
            properties.put("jakarta.persistence.jdbc.user", user);
            properties.put("jakarta.persistence.jdbc.password", password);
            tryEmf = Persistence.createEntityManagerFactory(DatabaseConfig.PERSISTENCE_UNIT, properties);
            log.info("EntityManagerFactory initialized successfully");
        } catch (Exception e) {
            log.error("Failed to create EntityManagerFactory", e);
            throw new RuntimeException("Failed to create EntityManagerFactory", e);
            // System.exit(0);
        }
        emf = tryEmf;
    }

    /**
     * Retrieves the {@link EntityManagerFactory}.
     *
     * @return The {@link EntityManagerFactory} instance.
     */
    public static EntityManagerFactory getEntityManagerFactory() {
        return emf;
    }

    private static final class DatabaseConfig {
        private static final String PERSISTENCE_UNIT = "png";
    }

}
