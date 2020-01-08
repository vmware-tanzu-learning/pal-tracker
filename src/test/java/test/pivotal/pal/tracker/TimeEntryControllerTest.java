package test.pivotal.pal.tracker;

import io.pivotal.pal.tracker.TimeEntry;
import io.pivotal.pal.tracker.TimeEntryController;
import io.pivotal.pal.tracker.TimeEntryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class TimeEntryControllerTest {
    private TimeEntryRepository timeEntryRepository;
    private TimeEntryController controller;

    @BeforeEach
    public void setUp() {
        timeEntryRepository = mock(TimeEntryRepository.class);
        controller = new TimeEntryController(timeEntryRepository);
    }

    @Test
    public void testCreate() {
        long projectId = 123L;
        long userId = 456L;
        TimeEntry timeEntryToCreate = new TimeEntry(0, projectId, userId, LocalDate.parse("2017-01-08"), 8);

        long timeEntryId = 1L;
        TimeEntry expectedResult = new TimeEntry(timeEntryId, projectId, userId, LocalDate.parse("2017-01-08"), 8);
        doReturn(expectedResult)
            .when(timeEntryRepository)
            .create(any(TimeEntry.class));

        ResponseEntity<TimeEntry> response = controller.create(timeEntryToCreate);

        verify(timeEntryRepository).create(timeEntryToCreate);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(expectedResult);
    }

    @Test
    public void testRead() {
        long timeEntryId = 1L;
        long projectId = 123L;
        long userId = 456L;
        TimeEntry expected = new TimeEntry(timeEntryId, projectId, userId, LocalDate.parse("2017-01-08"), 8);
        doReturn(expected)
            .when(timeEntryRepository)
            .find(timeEntryId);

        ResponseEntity<TimeEntry> response = controller.read(timeEntryId);

        verify(timeEntryRepository).find(timeEntryId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    public void testRead_NotFound() {
        long nonExistentTimeEntryId = 1L;
        doReturn(null)
            .when(timeEntryRepository)
            .find(nonExistentTimeEntryId);

        ResponseEntity<TimeEntry> response = controller.read(nonExistentTimeEntryId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testList() {
        List<TimeEntry> expected = asList(
            new TimeEntry(1L, 123L, 456L, LocalDate.parse("2017-01-08"), 8),
            new TimeEntry(2L, 789L, 321L, LocalDate.parse("2017-01-07"), 4)
        );
        doReturn(expected).when(timeEntryRepository).list();

        ResponseEntity<List<TimeEntry>> response = controller.list();

        verify(timeEntryRepository).list();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    public void testUpdate() {
        long timeEntryId = 1L;
        long projectId = 987L;
        long userId = 654L;
        TimeEntry timeEntryToUpdate = new TimeEntry(0, projectId, userId, LocalDate.parse("2017-01-07"), 4);

        TimeEntry expected = new TimeEntry(timeEntryId, projectId, userId, LocalDate.parse("2017-01-07"), 4);
        doReturn(expected)
            .when(timeEntryRepository)
            .update(eq(timeEntryId), any(TimeEntry.class));

        ResponseEntity<TimeEntry> response = controller.update(timeEntryId, timeEntryToUpdate);

        verify(timeEntryRepository).update(timeEntryId, timeEntryToUpdate);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expected);
    }

    @Test
    public void testUpdate_NotFound() {
        long nonExistentTimeEntryId = 1L;
        doReturn(null)
            .when(timeEntryRepository)
            .update(eq(nonExistentTimeEntryId), any(TimeEntry.class));

        ResponseEntity<TimeEntry> response = controller.update(nonExistentTimeEntryId, new TimeEntry(0,0,0,LocalDate.EPOCH,0));
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    public void testDelete() {
        long timeEntryId = 1L;
        ResponseEntity<Void> response = controller.delete(timeEntryId);
        verify(timeEntryRepository).delete(timeEntryId);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
