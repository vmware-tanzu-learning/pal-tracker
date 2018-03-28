package io.pivotal.pal.tracker;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by accenturelabs on 3/27/18.
 */
@RestController
@RequestMapping("/time-entries")
public class TimeEntryController {

    private TimeEntryRepository timeEntriesRepo;

    public TimeEntryController(TimeEntryRepository timeEntryRepository) {
        this.timeEntriesRepo = timeEntryRepository;

    }
    @PostMapping
    public ResponseEntity<TimeEntry> create(@RequestBody TimeEntry timeEntry) {
        TimeEntry createdTimeEntry = timeEntriesRepo.create(timeEntry);
                return new ResponseEntity<TimeEntry>(createdTimeEntry, HttpStatus.CREATED);
    }


    @GetMapping("{id}")
    public ResponseEntity<TimeEntry> read(@PathVariable long id) {
        TimeEntry timeEntry =timeEntriesRepo.find(id);
        if(timeEntry != null)
        return new ResponseEntity<TimeEntry>(timeEntry, HttpStatus.OK);

        return new ResponseEntity<TimeEntry>(timeEntry, HttpStatus.NOT_FOUND);
    }
    @GetMapping
    public ResponseEntity<List<TimeEntry>> list() {
        return new ResponseEntity<List<TimeEntry>>(timeEntriesRepo.list(), HttpStatus.OK);
    }
    @PutMapping("{id}")
    public ResponseEntity<TimeEntry> update(@PathVariable long id, @RequestBody TimeEntry timeEntry) {
        TimeEntry timeEntry1  = timeEntriesRepo.update(id,timeEntry);
        if(timeEntry1 != null)
        return new ResponseEntity<TimeEntry>(timeEntry1,HttpStatus.OK);

        return new ResponseEntity<TimeEntry>(HttpStatus.NOT_FOUND);
    }
    @DeleteMapping("{id}")
    public ResponseEntity<TimeEntry> delete(@PathVariable long id) {
        timeEntriesRepo.delete(id);
        return new ResponseEntity<TimeEntry>(HttpStatus.NO_CONTENT);
    }
}
