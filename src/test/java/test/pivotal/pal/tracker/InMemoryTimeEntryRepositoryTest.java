package test.pivotal.pal.tracker;

import io.pivotal.pal.tracker.TimeEntryRepository;
import io.pivotal.pal.tracker.InMemoryTimeEntryRepository;
import io.pivotal.pal.tracker.TimeEntry;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class InMemoryTimeEntryRepositoryTest {
    @Test
    public void create() throws Exception {
        TimeEntryRepository repo = new InMemoryTimeEntryRepository();
        TimeEntry createdTimeEntry = repo.create(new TimeEntry(123, 456, LocalDate.parse("2017-01-08"), 8));

        TimeEntry expected = new TimeEntry(1L, 123, 456, LocalDate.parse("2017-01-08"), 8);
        assertThat(createdTimeEntry).isEqualTo(expected);

        TimeEntry readEntry = repo.find(createdTimeEntry.getId());
        assertThat(readEntry).isEqualTo(expected);
    }

    @Test
    public void find() throws Exception {
        TimeEntryRepository repo = new InMemoryTimeEntryRepository();
        repo.create(new TimeEntry(123, 456, LocalDate.parse("2017-01-08"), 8));

        TimeEntry expected = new TimeEntry(1L, 123, 456, LocalDate.parse("2017-01-08"), 8);
        TimeEntry readEntry = repo.find(1L);
        assertThat(readEntry).isEqualTo(expected);
    }

    @Test
    public void list() throws Exception {
        TimeEntryRepository repo = new InMemoryTimeEntryRepository();
        repo.create(new TimeEntry(123, 456, LocalDate.parse("2017-01-08"), 8));
        repo.create(new TimeEntry(789, 654, LocalDate.parse("2017-01-07"), 4));

        List<TimeEntry> expected = asList(
                new TimeEntry(1L, 123, 456, LocalDate.parse("2017-01-08"), 8),
                new TimeEntry(2L, 789, 654, LocalDate.parse("2017-01-07"), 4)
        );
        assertThat(repo.list()).isEqualTo(expected);
    }

    @Test
    public void update() throws Exception {
        TimeEntryRepository repo = new InMemoryTimeEntryRepository();
        TimeEntry created = repo.create(new TimeEntry(123, 456, LocalDate.parse("2017-01-08"), 8));

        TimeEntry updatedEntry = repo.update(
                created.getId(),
                new TimeEntry(321, 654, LocalDate.parse("2017-01-09"), 5));

        TimeEntry expected = new TimeEntry(created.getId(), 321, 654, LocalDate.parse("2017-01-09"), 5);
        assertThat(updatedEntry).isEqualTo(expected);
        assertThat(repo.find(created.getId())).isEqualTo(expected);
    }

    @Test
    public void delete() throws Exception {
        TimeEntryRepository repo = new InMemoryTimeEntryRepository();
        TimeEntry created = repo.create(new TimeEntry(123, 456, LocalDate.parse("2017-01-08"), 8));

        repo.delete(created.getId());
        assertThat(repo.list()).isEmpty();
    }
}
