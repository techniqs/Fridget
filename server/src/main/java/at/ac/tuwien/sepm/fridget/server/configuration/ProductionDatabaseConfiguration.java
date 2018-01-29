package at.ac.tuwien.sepm.fridget.server.configuration;

import at.ac.tuwien.sepm.fridget.server.persistence.H2Handler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Configuration for production environment - Connects with standard H2 database
 */
@Configuration
public class ProductionDatabaseConfiguration {

    @Autowired
    H2Handler h2Handler;

    @Bean
    public Connection connection() throws SQLException {
        return h2Handler.getConnection();
    }
}
