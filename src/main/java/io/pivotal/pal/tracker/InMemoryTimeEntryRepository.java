package io.pivotal.pal.tracker;

import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InMemoryTimeEntryRepository implements TimeEntryRepository {


    private Map<Long, TimeEntry> repoMap = new HashMap<>();


    @Override
    public TimeEntry create(TimeEntry timeEntry) {

        long nextId = getNextId();
        timeEntry.setId(nextId);
        repoMap.put(nextId, timeEntry);
        return timeEntry;
    }

    @Override
    public TimeEntry find(long timeEntryId) {
        return repoMap.get(timeEntryId);
    }

    @Override
    public List<TimeEntry> list() {
        List<TimeEntry> results = new ArrayList<>();
        for (TimeEntry entry: repoMap.values()) {
            results.add(entry);
        }
        return results;
    }

    @Override
    public TimeEntry update(long timeEntryId, TimeEntry timeEntry) {
        TimeEntry entry = repoMap.get(timeEntryId);

        if (entry != null) {
            // update the entry
            entry.setHours(timeEntry.getHours());
            entry.setProjectId(timeEntry.getProjectId());
            entry.setDate(timeEntry.getDate());
            entry.setUserId(timeEntry.getUserId());
        }


        return entry;
    }

    @Override
    public void delete(long timeEntryId) {
        repoMap.remove(timeEntryId);
    }

    public long getNextId() {
        long id = 0;

        for (long key : repoMap.keySet()) {
            if (key > id) {
                id = key;
            }
        }

        id++;

        return id;
    }
}
