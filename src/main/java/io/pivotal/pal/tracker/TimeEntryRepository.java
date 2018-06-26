package io.pivotal.pal.tracker;

import java.util.List;

public interface TimeEntryRepository {
    TimeEntry create(TimeEntry timeEntry);

    TimeEntry find(long l);

    List<TimeEntry> list();

    TimeEntry update(long id, TimeEntry timeEntry);

    TimeEntry delete(long id);
}
