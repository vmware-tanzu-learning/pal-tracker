package io.pivotal.pal.tracker.migration;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableTask
@ConditionalOnProperty("migrate")
public class FlywayMigrationTaskConfig {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Flyway flyway;

    public FlywayMigrationTaskConfig(Flyway flyway) {
        this.flyway = flyway;
    }

    @Bean
    public CommandLineRunner runner() {
        return args -> {
            flyway.migrate();
            logger.info("Completed flyway migration task");
        };
    }
}
