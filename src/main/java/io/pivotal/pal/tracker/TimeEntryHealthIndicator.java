package io.pivotal.pal.tracker;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class TimeEntryHealthIndicator implements HealthIndicator {

    private static final int MAX_TIME_ENTRIES = 5;
    private final TimeEntryRepository timeEntryRepo;

    public TimeEntryHealthIndicator(TimeEntryRepository timeEntryRepo) {
        this.timeEntryRepo = timeEntryRepo;
    }

    @Override
    public Health health() {
        Health.Builder builder = new Health.Builder();

        if(timeEntryRepo.list().size() < MAX_TIME_ENTRIES) {
            builder.up();
        } else {
            builder.down();
        }

        return builder.build();
    }
}
