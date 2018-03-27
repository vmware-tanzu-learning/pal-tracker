package io.pivotal.pal.tracker;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * Created by accenturelabs on 3/26/18.
 */
@SpringBootApplication
public class PalTrackerApplication {
    public static void main(String[] args) {
        SpringApplication.run(PalTrackerApplication.class, args);
    }

    // TODO start here
    @Bean
    public TimeEntryRepository timeEntryRepository(){

        return null;
    }
}
