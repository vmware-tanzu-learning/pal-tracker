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
@ConditionalOnProperty("clean")
public class FlywayCleanTaskConfig {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Flyway flyway;

    public FlywayCleanTaskConfig(Flyway flyway) {
        this.flyway = flyway;
    }

    @Bean
    public CommandLineRunner runner() {
        return args -> {
            logger.info("Cleaning DB");
            flyway.clean();
            logger.info("Done Cleaning DB, Baselining DB...");
            flyway.setBaselineVersionAsString("0");
            flyway.baseline();
            logger.info("Completed flyway clean and baseline task");
        };
    }
}
