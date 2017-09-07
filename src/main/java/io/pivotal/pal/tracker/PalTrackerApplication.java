package io.pivotal.pal.tracker;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@SpringBootApplication
public class PalTrackerApplication {


    public static void main(String[] args) {
        SpringApplication.run(PalTrackerApplication.class, args);
    }

   /* @Bean
    TimeEntryRepository timeEntryRepository() {
        return new InMemoryTimeEntryRepository();
    }*/
    @Bean
    JdbcTimeEntryRepository jdbcTimeEntryRepo(DataSource datasource){
       return new JdbcTimeEntryRepository(datasource);
    }
}
