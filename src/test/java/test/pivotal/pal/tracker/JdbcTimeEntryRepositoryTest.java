package test.pivotal.pal.tracker;


import com.mysql.cj.jdbc.MysqlDataSource;
import io.pivotal.pal.tracker.JdbcTimeEntryRepository;
import io.pivotal.pal.tracker.TimeEntry;
import io.pivotal.pal.tracker.TimeEntryRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class JdbcTimeEntryRepositoryTest {
    private TimeEntryRepository subject;
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() throws Exception {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUrl(System.getenv("SPRING_DATASOURCE_URL"));

        subject = new JdbcTimeEntryRepository(dataSource);

        jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.execute("DELETE FROM time_entries");
    }

    @Test
    public void createInsertsATimeEntryRecord() throws Exception {
        TimeEntry newTimeEntry = new TimeEntry(123, 321, "today", 8);
        TimeEntry entry = subject.create(newTimeEntry);

        Map<String, Object> foundEntry = jdbcTemplate.queryForMap("Select * from time_entries where id = ?", entry.getId());

        assertThat(foundEntry.get("id")).isEqualTo(entry.getId());
        assertThat(foundEntry.get("project_id")).isEqualTo(123L);
        assertThat(foundEntry.get("user_id")).isEqualTo(321L);
        assertThat(foundEntry.get("date")).isEqualTo("today");
        assertThat(foundEntry.get("hours")).isEqualTo(8);
    }

    @Test
    public void createReturnsTheCreatedTimeEntry() throws Exception {
        TimeEntry newTimeEntry = new TimeEntry(123, 321, "today", 8);
        TimeEntry entry = subject.create(newTimeEntry);

        assertThat(entry.getId()).isNotNull();
        assertThat(entry.getProjectId()).isEqualTo(123);
        assertThat(entry.getUserId()).isEqualTo(321);
        assertThat(entry.getDate()).isEqualTo("today");
        assertThat(entry.getHours()).isEqualTo(8);
    }

    @Test
    public void findFindsATimeEntry() throws Exception {
        jdbcTemplate.execute(
            "INSERT INTO time_entries (id, project_id, user_id, date, hours) " +
                "VALUES (999, 123, 321, 'today', 8)"
        );

        TimeEntry timeEntry = subject.find(999L);

        assertThat(timeEntry.getId()).isEqualTo(999L);
        assertThat(timeEntry.getProjectId()).isEqualTo(123L);
        assertThat(timeEntry.getUserId()).isEqualTo(321L);
        assertThat(timeEntry.getDate()).isEqualTo("today");
        assertThat(timeEntry.getHours()).isEqualTo(8);
    }

    @Test
    public void findReturnsNullWhenNotFound() throws Exception {
        TimeEntry timeEntry = subject.find(999L);

        assertThat(timeEntry).isNull();
    }

    @Test
    public void listFindsAllTimeEntries() throws Exception {
        jdbcTemplate.execute(
            "INSERT INTO time_entries (id, project_id, user_id, date, hours) " +
                "VALUES (999, 123, 321, 'today', 8), (888, 456, 678, 'yesterday', 9)"
        );

        List<TimeEntry> timeEntries = subject.list();
        assertThat(timeEntries.size()).isEqualTo(2);

        TimeEntry timeEntry = timeEntries.get(0);
        assertThat(timeEntry.getId()).isEqualTo(888L);
        assertThat(timeEntry.getProjectId()).isEqualTo(456L);
        assertThat(timeEntry.getUserId()).isEqualTo(678L);
        assertThat(timeEntry.getDate()).isEqualTo("yesterday");
        assertThat(timeEntry.getHours()).isEqualTo(9);

        timeEntry = timeEntries.get(1);
        assertThat(timeEntry.getId()).isEqualTo(999L);
        assertThat(timeEntry.getProjectId()).isEqualTo(123L);
        assertThat(timeEntry.getUserId()).isEqualTo(321L);
        assertThat(timeEntry.getDate()).isEqualTo("today");
        assertThat(timeEntry.getHours()).isEqualTo(8);
    }

    @Test
    public void updateReturnsTheUpdatedRecord() throws Exception {
        jdbcTemplate.execute(
            "INSERT INTO time_entries (id, project_id, user_id, date, hours) " +
                "VALUES (1000, 123, 321, 'today', 8)");

        TimeEntry timeEntryUpdates = new TimeEntry(456, 987, "tomorrow", 10);

        TimeEntry updatedTimeEntry = subject.update(1000L, timeEntryUpdates);

        assertThat(updatedTimeEntry.getId()).isEqualTo(1000L);
        assertThat(updatedTimeEntry.getProjectId()).isEqualTo(456L);
        assertThat(updatedTimeEntry.getUserId()).isEqualTo(987L);
        assertThat(updatedTimeEntry.getDate()).isEqualTo("tomorrow");
        assertThat(updatedTimeEntry.getHours()).isEqualTo(10);
    }

    @Test
    public void updateUpdatesTheRecord() throws Exception {
        jdbcTemplate.execute(
            "INSERT INTO time_entries (id, project_id, user_id, date, hours) " +
                "VALUES (1000, 123, 321, 'today', 8)");

        TimeEntry updatedTimeEntry = new TimeEntry(456, 322, "tomorrow", 10);

        TimeEntry timeEntry = subject.update(1000L, updatedTimeEntry);

        Map<String, Object> foundEntry = jdbcTemplate.queryForMap("Select * from time_entries where id = ?", timeEntry.getId());

        assertThat(foundEntry.get("id")).isEqualTo(timeEntry.getId());
        assertThat(foundEntry.get("project_id")).isEqualTo(456L);
        assertThat(foundEntry.get("user_id")).isEqualTo(322L);
        assertThat(foundEntry.get("date")).isEqualTo("tomorrow");
        assertThat(foundEntry.get("hours")).isEqualTo(10);
    }

    @Test
    public void deleteRemovesTheRecord() throws Exception {
        jdbcTemplate.execute(
            "INSERT INTO time_entries (id, project_id, user_id, date, hours) " +
                "VALUES (999, 123, 321, 'today', 8)"
        );

        subject.delete(999L);

        Map<String, Object> foundEntry = jdbcTemplate.queryForMap("Select count(*) count from time_entries where id = ?", 999);
        assertThat(foundEntry.get("count")).isEqualTo(0L);
    }
}
