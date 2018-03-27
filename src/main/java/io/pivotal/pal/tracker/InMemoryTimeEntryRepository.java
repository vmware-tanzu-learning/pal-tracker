package io.pivotal.pal.tracker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by accenturelabs on 3/27/18.
 */
public class InMemoryTimeEntryRepository {
    public TimeEntry create (TimeEntry time) {

        return time;
    }
    public TimeEntry find (long time) {

        return null;
    }
    public TimeEntry update(long id, TimeEntry timeEntry) {
        return timeEntry;
    }
    public void delete(long id) {

    }
    public List<TimeEntry> list() {
        List<TimeEntry> list = new ArrayList<TimeEntry>();
        return list;
    }
}
