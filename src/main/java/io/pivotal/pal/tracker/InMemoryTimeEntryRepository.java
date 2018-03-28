package io.pivotal.pal.tracker;

import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by accenturelabs on 3/27/18.
 */
public class InMemoryTimeEntryRepository implements TimeEntryRepository {

    Map<Long, TimeEntry> timeEntryMap;
    public  InMemoryTimeEntryRepository()
    {
        timeEntryMap = new HashMap<>();

    }

    public TimeEntry create (TimeEntry time) {
        long id = timeEntryMap.size() +1;
        time.setId(id);
        timeEntryMap.put(time.getId(),time);
        return time;
    }

    @Override
    public TimeEntry find(Long id) {

        return timeEntryMap.get(id);
    }

    @Override
    public List<TimeEntry> list() {

        return new ArrayList<>(timeEntryMap.values());
    }


    @Override
    public TimeEntry update(Long id, TimeEntry timeEntry) {
        TimeEntry inMem = timeEntryMap.get(id);
        //inMem.setId(timeEntry.getId());
        inMem.setDate(timeEntry.getDate());
        inMem.setHours(timeEntry.getHours());
        inMem.setProjectId(timeEntry.getProjectId());
        inMem.setUserId(timeEntry.getUserId());
        return inMem;
    }

    @Override
    public void delete(Long id) {

        timeEntryMap.remove(id);
    }
}
