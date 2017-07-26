package test.pivotal.pal.trackerapi;

import com.jayway.jsonpath.DocumentContext;
import com.mysql.cj.jdbc.MysqlDataSource;
import io.pivotal.pal.tracker.PalTrackerApplication;
import io.pivotal.pal.tracker.TimeEntry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;

import static com.jayway.jsonpath.JsonPath.parse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PalTrackerApplication.class, webEnvironment = RANDOM_PORT)
public class TimeEntryApiTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private TimeEntry timeEntry = new TimeEntry(123, 456, "today", 8);

    @Before
    public void setUp() throws Exception {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUrl(System.getenv("SPRING_DATASOURCE_URL"));

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.execute("TRUNCATE time_entries");
    }

    @Test
    public void testCreate() throws Exception {
        ResponseEntity<String> createResponse = restTemplate.postForEntity("/time-entries", timeEntry, String.class);


        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        DocumentContext createJson = parse(createResponse.getBody());
        assertThat(createJson.read("$.id", Long.class)).isGreaterThan(0);
        assertThat(createJson.read("$.projectId", Long.class)).isEqualTo(123L);
        assertThat(createJson.read("$.userId", Long.class)).isEqualTo(456L);
        assertThat(createJson.read("$.date", String.class)).isEqualTo("today");
        assertThat(createJson.read("$.hours", Long.class)).isEqualTo(8);
    }

    @Test
    public void testList() throws Exception {
        Long id = createTimeEntry();


        ResponseEntity<String> listResponse = restTemplate.getForEntity("/time-entries", String.class);


        assertThat(listResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext listJson = parse(listResponse.getBody());

        Collection timeEntries = listJson.read("$[*]", Collection.class);
        assertThat(timeEntries.size()).isEqualTo(1);

        Long readId = listJson.read("$[0].id", Long.class);
        assertThat(readId).isEqualTo(id);
    }

    @Test
    public void testRead() throws Exception {
        Long id = createTimeEntry();


        ResponseEntity<String> readResponse = this.restTemplate.getForEntity("/time-entries/" + id, String.class);


        assertThat(readResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        DocumentContext readJson = parse(readResponse.getBody());
        assertThat(readJson.read("$.id", Long.class)).isEqualTo(id);
        assertThat(readJson.read("$.projectId", Long.class)).isEqualTo(123L);
        assertThat(readJson.read("$.userId", Long.class)).isEqualTo(456L);
        assertThat(readJson.read("$.date", String.class)).isEqualTo("today");
        assertThat(readJson.read("$.hours", Long.class)).isEqualTo(8);
    }

    @Test
    public void testUpdate() throws Exception {
        Long id = createTimeEntry();
        TimeEntry updatedTimeEntry = new TimeEntry(2, 3, "tomorrow", 9);


        ResponseEntity<String> updateResponse = restTemplate.exchange("/time-entries/" + id, HttpMethod.PUT, new HttpEntity<>(updatedTimeEntry, null), String.class);


        assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext updateJson = parse(updateResponse.getBody());
        assertThat(updateJson.read("$.id", Long.class)).isEqualTo(id);
        assertThat(updateJson.read("$.projectId", Long.class)).isEqualTo(2L);
        assertThat(updateJson.read("$.userId", Long.class)).isEqualTo(3L);
        assertThat(updateJson.read("$.date", String.class)).isEqualTo("tomorrow");
        assertThat(updateJson.read("$.hours", Long.class)).isEqualTo(9);
    }

    @Test
    public void testDelete() throws Exception {
        Long id = createTimeEntry();


        ResponseEntity<String> deleteResponse = restTemplate.exchange("/time-entries/" + id, HttpMethod.DELETE, null, String.class);


        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> deletedReadResponse = this.restTemplate.getForEntity("/time-entries/" + id, String.class);
        assertThat(deletedReadResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    private Long createTimeEntry() {
        return restTemplate.postForObject("/time-entries", timeEntry, TimeEntry.class).getId();
    }
}
