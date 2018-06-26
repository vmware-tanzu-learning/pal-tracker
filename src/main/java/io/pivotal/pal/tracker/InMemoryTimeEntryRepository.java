package io.pivotal.pal.tracker;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InMemoryTimeEntryRepository  implements TimeEntryRepository {
    private Map<Long, TimeEntry> timeEntries;


    public InMemoryTimeEntryRepository() {
        this.timeEntries = new HashMap<>();
    }

    public TimeEntry create(TimeEntry timeEntry) {
        if (timeEntry == null) {
            return null;
        }

        timeEntry.setId(timeEntries.size() + 1);
        timeEntries.put(timeEntry.getId(), timeEntry);
        return timeEntry;
    }

    public TimeEntry find(long id) {
        return timeEntries.get(id);
    }

    public List<TimeEntry> list() {
        return new ArrayList<>(timeEntries.values());
    }

    public TimeEntry update(long id, TimeEntry timeEntry) {
        if (timeEntries.get(id) == null) {
            return null;
        }
        timeEntry.setId(id);
        timeEntries.put(id, timeEntry);
        return timeEntry;
    }

    public TimeEntry delete(long id) {
        if (timeEntries.get(id) == null) {
            return null;
        }
        return timeEntries.remove(id);
    }
}
