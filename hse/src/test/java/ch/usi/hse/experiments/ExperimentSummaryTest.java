package ch.usi.hse.experiments;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import ch.usi.hse.db.entities.DocCollection;
import ch.usi.hse.db.entities.Experiment;
import ch.usi.hse.db.entities.Participant;
import ch.usi.hse.db.entities.TestGroup;


public class ExperimentSummaryTest {
	
	@Mock
	private EventDataExtractor dataExtractor;

	private static ObjectMapper mapper;
	private static ObjectWriter writer;
	
	private String title;
	private LocalDateTime dateConducted;
	private Duration duration;
	private List<String> groupNames;
	private Map<String, Integer> participantsPerGroup;
	private int participants, totalQueries, totalClicks;
	private DataStats queriesPerUser, clicksPerUser, clicksPerQuery,
					  timePerQuery, timePerClick;
	
	private Experiment experiment;
	
	@BeforeAll
	public static void init() {
		
		mapper = new ObjectMapper();
		mapper.findAndRegisterModules();
		writer = mapper.writer().withDefaultPrettyPrinter();
	}
	
	@BeforeEach
	public void setUp() {
		
		initMocks(this);
		
		// set up test experiment
		
		TestGroup g1 = new TestGroup("g1");
		g1.setId(1);
		
		Participant p1 = new Participant("p1", "pwd");
		p1.setId(1);
		Participant p2 = new Participant("p2", "pwd");
		p2.setId(2);
		g1.addParticipant(p1);
		g1.addParticipant(p2);
		
		DocCollection c1 = new DocCollection("c1", "list1");
		c1.setId(1);
		DocCollection c2 = new DocCollection("c2", "list2");
		c2.setId(2);
		g1.addDocCollection(c1);
		g1.addDocCollection(c2);
		
		experiment = new Experiment("e1");
		experiment.setId(1);
		
		experiment.addTestGroup(g1);
		
		experiment.setDateConducted(LocalDateTime.of(2020, 12, 4, 15, 30));
		experiment.setDuration(Duration.ofMinutes(20));
		
		// set up data items
		
		title = experiment.getTitle();
		dateConducted = experiment.getDateConducted();
		duration = experiment.getDuration();
		groupNames = List.of(g1.getName());
		
		participantsPerGroup = new HashMap<>();
		participantsPerGroup.put(g1.getName(), 2);
		
		participants = 2;
		totalQueries = 20;
		totalClicks = 60;
		queriesPerUser = new DataStats(List.of(10.0, 10.0));
		clicksPerUser = new DataStats(List.of(20.0, 40.0));
		clicksPerQuery = new DataStats(List.of(3.0, 4.0, 2.0));
		timePerQuery =new DataStats(List.of(23.0, 21.0));
		timePerClick = new DataStats(List.of(32.0, 21.3));
		
		// set up mock EventDataExtractor
		
		when(dataExtractor.totalQueries(experiment)).thenReturn(totalQueries);
		when(dataExtractor.totalClicks(experiment)).thenReturn(totalClicks);
		when(dataExtractor.queriesPerUser(experiment)).thenReturn(queriesPerUser);
		when(dataExtractor.clicksPerUser(experiment)).thenReturn(clicksPerUser);
		when(dataExtractor.clicksPerQuery(experiment)).thenReturn(clicksPerQuery);
		when(dataExtractor.timePerQuery(experiment)).thenReturn(timePerQuery);
		when(dataExtractor.timePerClick(experiment)).thenReturn(timePerClick);
	}
	

	@Test
	public void testConstructor1() {
		
		ExperimentSummary s = new ExperimentSummary(experiment, dataExtractor);

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
	public void testConstructor2() {
		
		ExperimentSummary s = new ExperimentSummary(title, dateConducted, duration, 
													groupNames, participantsPerGroup,
													participants, totalQueries, totalClicks,
													queriesPerUser, clicksPerUser, clicksPerQuery,
													timePerQuery, timePerClick, null);
		
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
		
		ExperimentSummary s = new ExperimentSummary(title, dateConducted, duration, 
													groupNames, participantsPerGroup,
													participants, totalQueries, totalClicks,
													queriesPerUser, clicksPerUser, clicksPerQuery,
													timePerQuery, timePerClick, null);
		
		String jsonString = writer.writeValueAsString(s);
		
		ExperimentSummary reconstructed = mapper.readValue(jsonString, ExperimentSummary.class);
		
		assertEquals(title, reconstructed.getTitle());
		assertEquals(dateConducted, reconstructed.getDateConducted());
		assertEquals(duration, reconstructed.getDuration());
	}
}







