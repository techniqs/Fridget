package at.ac.tuwien.sepm.fridget.server.application;

import at.ac.tuwien.sepm.fridget.server.configuration.ProductionDatabaseConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@ComponentScan(basePackages = {
    "at.ac.tuwien.sepm.fridget.server.persistence",
    "at.ac.tuwien.sepm.fridget.server.services",
    "at.ac.tuwien.sepm.fridget.server.controllers",
    "at.ac.tuwien.sepm.fridget.server.configuration",
    "at.ac.tuwien.sepm.fridget.server.util",
})
@Import(ProductionDatabaseConfiguration.class)
public class FridgetServer {

    public static void main(String[] args) {
        SpringApplication.run(FridgetServer.class, args);
    }

}
