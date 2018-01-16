package io.pivotal.pal.tracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;

@RestController
public class TimeEntryController {

    TimeEntryRepository timeEntryRepository;

    public TimeEntryController(@Autowired TimeEntryRepository timeEntryRepository) {
        this.timeEntryRepository = timeEntryRepository;
    }

    @PostMapping(value="/time-entries")
    public @ResponseBody ResponseEntity create(@RequestBody TimeEntry timeEntry) {

        TimeEntry newTimeEntry = timeEntryRepository.create(timeEntry);

        return new ResponseEntity<TimeEntry>(newTimeEntry, HttpStatus.CREATED);
    }

    @GetMapping(value = "/time-entries/{id}")
    public @ResponseBody ResponseEntity<TimeEntry> read(@PathVariable("id") Long id) {

        TimeEntry timeEntry = timeEntryRepository.find(id);

        if (timeEntry != null) {

            return new ResponseEntity<TimeEntry>(timeEntry, HttpStatus.OK);
        } else {
            return new ResponseEntity<TimeEntry>((TimeEntry)null, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/time-entries")
    public @ResponseBody ResponseEntity<List<TimeEntry>> list() {
        List<TimeEntry> results = timeEntryRepository.list();

        return new ResponseEntity<List<TimeEntry>>(results, HttpStatus.OK);
    }


    @PutMapping(value="/time-entries/{id}")
    public @ResponseBody ResponseEntity update(@PathVariable("id") Long id, @RequestBody TimeEntry timeEntry) {

        TimeEntry updatedTimeEntry = timeEntryRepository.update(id, timeEntry);

        if (updatedTimeEntry != null) {

            return new ResponseEntity<TimeEntry>(updatedTimeEntry, HttpStatus.OK);

        } else {
            return new ResponseEntity<TimeEntry>(timeEntry, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping(value="/time-entries/{id}")
    public @ResponseBody ResponseEntity<TimeEntry> delete(@PathVariable("id") Long id) {

        TimeEntry deletedTimeEntry = timeEntryRepository.find(id);

        timeEntryRepository.delete(id);

        return new ResponseEntity<TimeEntry>(deletedTimeEntry, HttpStatus.NO_CONTENT);

    }
}
