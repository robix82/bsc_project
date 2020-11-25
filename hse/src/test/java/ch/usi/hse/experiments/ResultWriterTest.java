package ch.usi.hse.experiments;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.*;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import ch.usi.hse.db.entities.DocClickEvent;
import ch.usi.hse.db.entities.DocCollection;
import ch.usi.hse.db.entities.Experiment;
import ch.usi.hse.db.entities.Participant;
import ch.usi.hse.db.entities.QueryEvent;
import ch.usi.hse.db.entities.SessionEvent;
import ch.usi.hse.db.entities.TestGroup;
import ch.usi.hse.db.entities.UsageEvent;
import ch.usi.hse.exceptions.NoSuchExperimentException;
import ch.usi.hse.retrieval.SearchResult;
import ch.usi.hse.retrieval.SearchResultList;

public class ResultWriterTest {
	
	@Mock
	private CsvWriter csvWriter;
	
	private ResultWriter resultWriter;
	private ObjectMapper mapper;
	private ObjectWriter writer;

	private Experiment testExperiment;
	private String csvString;
	
	@BeforeEach
	public void setUp() throws NoSuchExperimentException {
		
		initMocks(this);
		
		resultWriter = new ResultWriter(csvWriter);
		
		mapper = new ObjectMapper();
		mapper.findAndRegisterModules();
		writer = mapper.writer().withDefaultPrettyPrinter();
		
		// set up test experiment
		
		testExperiment = new Experiment("testExperiment");
		testExperiment.setId(23);
				
		Participant testParticipant = new Participant("testParticipant", "pwd");
		testParticipant.setId(11);	
				
		TestGroup testGroup = new TestGroup("testGroup");
		testGroup.setId(21);
				
		DocCollection c1 = new DocCollection("c1", "list1");
		DocCollection c2 = new DocCollection("c2", "list2");
		c1.setId(31);
		c2.setId(32);
				
		testGroup.addParticipant(testParticipant);
		testGroup.addDocCollection(c1);
		testGroup.addDocCollection(c2);
				
		testExperiment.addTestGroup(testGroup);
				
		// add usage events
				
		String query1 = "test query 1";
		String query2 = "test query 2";
		String url1 = "url1";
		String url2 = "url2";
		String url3 = "url3";
				
		SearchResult res1 = new SearchResult(1, url1, "");
		SearchResult res2 = new SearchResult(2, url2, "");
		SearchResult res3 = new SearchResult(3, url3, "");
		res1.setDocCollection(c1);
		res2.setDocCollection(c1);
		res3.setDocCollection(c2);
				
		SearchResultList resList1 = new SearchResultList(query1, Arrays.asList(res1, res2));
		SearchResultList resList2 = new SearchResultList(query2, Arrays.asList(res2, res3));
				
		SessionEvent se1 = new SessionEvent(testParticipant, SessionEvent.Event.LOGIN);
		QueryEvent qe1 = new QueryEvent(testParticipant, resList1);
		DocClickEvent de1 = new DocClickEvent(testParticipant, res1);
		QueryEvent qe2 = new QueryEvent(testParticipant, resList2);
		DocClickEvent de2 = new DocClickEvent(testParticipant, res2);
		DocClickEvent de3 = new DocClickEvent(testParticipant, res3);
		SessionEvent se2 = new SessionEvent(testParticipant, SessionEvent.Event.LOGOUT);
		se1.setId(1);
		se1.setTimestamp(LocalDateTime.of(2020, 11, 20, 0, 0));
		qe1.setId(2);
		qe1.setTimestamp(LocalDateTime.of(2020, 11, 20, 0, 1));
		de1.setId(3);
		de1.setTimestamp(LocalDateTime.of(2020, 11, 20, 0, 2));
		qe2.setId(4);
		qe2.setTimestamp(LocalDateTime.of(2020, 11, 20, 0, 3));
		de2.setId(5);
		de2.setTimestamp(LocalDateTime.of(2020, 11, 20, 0, 4));
		de3.setId(6);
		de3.setTimestamp(LocalDateTime.of(2020, 11, 20, 0, 5));
		se2.setId(7);
		se2.setTimestamp(LocalDateTime.of(2020, 11, 20, 0, 6));
				
		List<UsageEvent> events = Arrays.asList(se1, qe1, de1, qe2, de2, de3, se2);
				
		for (UsageEvent e : events) {
					
			testExperiment.addUsageEvent(e);
		}
		
		csvString = "csvString";
		
		when(csvWriter.writeExperimentData(testExperiment)).thenReturn(csvString);
	}
	
	@Test
	public void testRawDataCsv() throws Exception {
		
		InputStream is = resultWriter.rawDataCsv(testExperiment);
		
		byte[] data = new byte[is.available()];
		is.read(data);
		String res = new String(data);
		
		assertEquals(csvString, res);
	}
	
	@Test
	public void testRawDataJson() throws Exception {
		
		String expected = writer.writeValueAsString(testExperiment.getUsageEvents());
		
		InputStream is = resultWriter.rawDataJson(testExperiment);
		
		byte[] data = new byte[is.available()];
		is.read(data);
		String res = new String(data);
		
		assertEquals(expected, res);
	}
}










