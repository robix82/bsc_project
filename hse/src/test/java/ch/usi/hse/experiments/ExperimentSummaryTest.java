package ch.usi.hse.experiments;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.MockitoAnnotations.initMocks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;


public class ExperimentSummaryTest {
	
	@Mock
	private EventDataExtractor dataExtractor;

	private static ObjectMapper mapper;
	private static ObjectWriter writer;
	
	@BeforeAll
	public static void init() {
		
		mapper = new ObjectMapper();
		mapper.findAndRegisterModules();
		writer = mapper.writer().withDefaultPrettyPrinter();
	}
	
	@BeforeEach
	public void setUp() {
		
		initMocks(this);
	}
	
	@Test
	public void testConstructor1() {
		
		ExperimentSummary summary = new ExperimentSummary(dataExtractor);
		assertNotNull(summary);
	}
	
	@Test
	public void testConstructor2() {
		
		String title = "title";
		LocalDateTime dateConducted = LocalDateTime.of(2020, 11, 10, 1, 0, 0);
		Duration duration = Duration.of(20, ChronoUnit.MINUTES);
		List<String> groupNames = List.of("g1", "g2");
		Map<String, Integer> participantsPerGroup = Map.of("g1", 10, "g2", 11);
		int participants = 21;
		int totalQueries = 34;
		int totalClicks = 42;
		DataStats queriesPerUser = new DataStats(List.of(2.1, 2.3));
		DataStats clicksPerUser = new DataStats(List.of(2.1, 2.7));
		DataStats clicksPerQuery = new DataStats(List.of(2.1, 2.4));
		DataStats timePerQuery = new DataStats(List.of(2.1, 2.9));
		DataStats timePerClick = new DataStats(List.of(2.1, 2.1));
		
		ExperimentSummary s = new ExperimentSummary(title, dateConducted, duration, 
													groupNames, participantsPerGroup,
													participants, totalQueries, totalClicks,
													queriesPerUser, clicksPerUser, clicksPerQuery,
													timePerQuery, timePerClick);
		
		assertEquals(title, s.getTitle());
		assertEquals(dateConducted, s.getDateConducted());
		assertEquals(duration, s.getDuration());
		assertEquals(groupNames, s.getGroupNames());
		assertEquals(participantsPerGroup, s.getParticipantsPerGroup());
		assertEquals(participants, s.getParticipants());
		assertEquals(totalQueries, s.getTotalQueries());
		assertEquals(totalClicks, s.getTotalClicks());
		assertEquals(queriesPerUser, s.getQueriesPerUser());
		assertEquals(clicksPerUser, s.getClicksPerUser());
		assertEquals(clicksPerQuery, s.getClicksPerQuery());
		assertEquals(timePerQuery, s.getTimePerQuery());
		assertEquals(timePerClick, s.getTimePerClick());
	}
	
	@Test
	public void testJsonIo() throws Exception {
		
		String title = "title";
		LocalDateTime dateConducted = LocalDateTime.of(2020, 11, 10, 1, 0, 0);
		Duration duration = Duration.of(20, ChronoUnit.MINUTES);
		List<String> groupNames = List.of("g1", "g2");
		Map<String, Integer> participantsPerGroup = Map.of("g1", 10, "g2", 11);
		int participants = 21;
		int totalQueries = 34;
		int totalClicks = 42;
		DataStats queriesPerUser = new DataStats(List.of(2.1, 2.3));
		DataStats clicksPerUser = new DataStats(List.of(2.1, 2.7));
		DataStats clicksPerQuery = new DataStats(List.of(2.1, 2.4));
		DataStats timePerQuery = new DataStats(List.of(2.1, 2.9));
		DataStats timePerClick = new DataStats(List.of(2.1, 2.1));
		
		ExperimentSummary s = new ExperimentSummary(title, dateConducted, duration, 
													groupNames, participantsPerGroup,
													participants, totalQueries, totalClicks,
													queriesPerUser, clicksPerUser, clicksPerQuery,
													timePerQuery, timePerClick);
		
		String jsonString = writer.writeValueAsString(s);
		
		ExperimentSummary reconstructed = mapper.readValue(jsonString, ExperimentSummary.class);
		
		assertEquals(title, reconstructed.getTitle());
		assertEquals(dateConducted, reconstructed.getDateConducted());
		assertEquals(duration, reconstructed.getDuration());
	}
}







