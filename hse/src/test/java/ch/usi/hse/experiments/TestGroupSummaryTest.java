package ch.usi.hse.experiments;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

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
import ch.usi.hse.db.entities.Participant;
import ch.usi.hse.db.entities.TestGroup;

public class TestGroupSummaryTest {

	@Mock
	private EventDataExtractor dataExtractor;
	
	private static ObjectMapper mapper;
	private static ObjectWriter writer;
	
	private String groupName;
	private List<String> collectionNames;
	private int participants, totalQueries, totalClicks;
	private DataStats queriesPerUser, clicksPerUser, clicksPerQuery,
			timePerQuery, timePerClick;
	private Map<String, DataStats> clicksPerDocCollection, timePerDocCollection;
	
	private TestGroup testGroup;
	
	@BeforeAll 
	public static void init() {
		
		mapper = new ObjectMapper();
		mapper.findAndRegisterModules();
		writer = mapper.writer().withDefaultPrettyPrinter();
	}
	
	@BeforeEach
	public void setUp() {
		
		initMocks(this);
		
		// set up testGroup
		
		testGroup = new TestGroup("g1");
		testGroup.setId(1);
		
		Participant p1 = new Participant("p1", "pwd");
		p1.setId(1);
		Participant p2 = new Participant("p2", "pwd");
		p2.setId(2);
		testGroup.addParticipant(p1);
		testGroup.addParticipant(p2);
		
		DocCollection c1 = new DocCollection("c1", "list1");
		c1.setId(1);
		DocCollection c2 = new DocCollection("c2", "list2");
		c2.setId(2);
		testGroup.addDocCollection(c1);
		testGroup.addDocCollection(c2);
		
		// set up data items
		
		groupName = testGroup.getName();
		collectionNames = List.of(c1.getName(), c2.getName());
		participants = testGroup.getParticipants().size();
		totalQueries = 23;
		totalClicks = 120;
		queriesPerUser = new DataStats(List.of(11.0, 12.0));
		clicksPerUser = new DataStats(List.of(50.0, 70.0));
		clicksPerQuery = new DataStats(List.of(4.0, 5.0, 6.0));
		timePerQuery = new DataStats(List.of(20.0, 16.0, 30.0));
		timePerClick = new DataStats(List.of(21.0, 22.0, 23.0));
		
		clicksPerDocCollection = new HashMap<>();
		clicksPerDocCollection.put(c1.getName(), new DataStats(List.of(11.0, 12.0, 13.0)));
		clicksPerDocCollection.put(c2.getName(), new DataStats(List.of(14.0, 15.0, 16.0)));
		
		timePerDocCollection = new HashMap<>();
		timePerDocCollection.put(c1.getName(), new DataStats(List.of(17.0, 18.0, 19.0)));
		timePerDocCollection.put(c2.getName(), new DataStats(List.of(20.0, 21.0, 22.0)));
		
		// set up mock EventDataExtractor
		
		when(dataExtractor.totalQueries(testGroup)).thenReturn(totalQueries);
		when(dataExtractor.totalClicks(testGroup)).thenReturn(totalClicks);
		when(dataExtractor.queriesPerUser(testGroup)).thenReturn(queriesPerUser);
		when(dataExtractor.clicksPerUser(testGroup)).thenReturn(clicksPerUser);
		when(dataExtractor.clicksPerQuery(testGroup)).thenReturn(clicksPerQuery);
		when(dataExtractor.timePerQuery(testGroup)).thenReturn(timePerQuery);
		when(dataExtractor.timePerClick(testGroup)).thenReturn(timePerClick);
		when(dataExtractor.clicksPerDocCollection(testGroup)).thenReturn(clicksPerDocCollection);
		when(dataExtractor.timePerDocCollection(testGroup)).thenReturn(timePerDocCollection);
	}
	
	@Test
	public void testConstructor1() {
		
		TestGroupSummary summary = new TestGroupSummary(testGroup, dataExtractor);
		
		assertEquals(groupName, summary.getGroupName());
		assertIterableEquals(collectionNames, summary.getCollectionNames());
		assertEquals(participants, summary.getParticipants());
		assertEquals(totalQueries, summary.getTotalQueries());
		assertEquals(totalClicks, summary.getTotalClicks());
		assertEquals(queriesPerUser, summary.getQueriesPerUser());
		assertEquals(clicksPerUser, summary.getClicksPerUser());
		assertEquals(clicksPerQuery, summary.getClicksPerQuery());
		assertEquals(timePerQuery, summary.getTimePerQuery());
		assertEquals(timePerClick, summary.getTimePerClick());
		assertIterableEquals(clicksPerDocCollection.entrySet(), summary.getClicksPerDocCollection().entrySet());
		assertIterableEquals(timePerDocCollection.entrySet(), summary.getTimePerDocCollection().entrySet());
	}
	
	@Test
	public void testConstructor2() {
		
		TestGroupSummary summary = new TestGroupSummary(groupName, collectionNames, participants,
														totalQueries, totalClicks, queriesPerUser,
														clicksPerUser, clicksPerQuery, timePerQuery, 
														timePerClick, clicksPerDocCollection, 
														timePerDocCollection);
		
		assertEquals(groupName, summary.getGroupName());
		assertIterableEquals(collectionNames, summary.getCollectionNames());
		assertEquals(participants, summary.getParticipants());
		assertEquals(totalQueries, summary.getTotalQueries());
		assertEquals(totalClicks, summary.getTotalClicks());
		assertEquals(queriesPerUser, summary.getQueriesPerUser());
		assertEquals(clicksPerUser, summary.getClicksPerUser());
		assertEquals(clicksPerQuery, summary.getClicksPerQuery());
		assertEquals(timePerQuery, summary.getTimePerQuery());
		assertEquals(timePerClick, summary.getTimePerClick());
		assertIterableEquals(clicksPerDocCollection.entrySet(), summary.getClicksPerDocCollection().entrySet());
		assertIterableEquals(timePerDocCollection.entrySet(), summary.getTimePerDocCollection().entrySet());
	}
	
	@Test
	public void testJsonIo() throws Exception {
		
		TestGroupSummary summary = new TestGroupSummary(groupName, collectionNames, participants,
														totalQueries, totalClicks, queriesPerUser,
														clicksPerUser, clicksPerQuery, timePerQuery, 
														timePerClick, clicksPerDocCollection, 
														timePerDocCollection);
		
		String jsonString = writer.writeValueAsString(summary);
		
		TestGroupSummary reconstructed = mapper.readValue(jsonString,  TestGroupSummary.class);
		
		assertEquals(groupName, reconstructed.getGroupName());
		assertIterableEquals(collectionNames, reconstructed.getCollectionNames());
		assertEquals(participants, reconstructed.getParticipants());
		assertEquals(totalQueries, reconstructed.getTotalQueries());
		assertEquals(totalClicks, reconstructed.getTotalClicks());
	}
}

























