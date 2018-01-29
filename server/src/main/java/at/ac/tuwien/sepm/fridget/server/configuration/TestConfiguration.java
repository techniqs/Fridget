package at.ac.tuwien.sepm.fridget.server.configuration;

import at.ac.tuwien.sepm.fridget.server.services.EmailService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import java.sql.Connection;
import java.sql.SQLException;

@Configuration
@Profile("unit-test")
@ComponentScan("at.ac.tuwien.sepm.fridget.server")
public class TestConfiguration extends ProductionDatabaseConfiguration {

    @Bean
    public Connection connection() {
        try {
            return new EmbeddedDatabaseBuilder()
                .setName("Test DB")
                .setType(EmbeddedDatabaseType.H2)
                .addScript("sql/database_init.sql")
                .build()
                .getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Bean
    public EmailService emailService() {
        EmailService emailService = Mockito.mock(EmailService.class);
        Mockito.doNothing().when(emailService).send(null);
        return emailService;
    }
}
