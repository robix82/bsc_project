package ch.usi.hse.db.entities;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.usi.hse.db.entities.Experiment.Status;

public class ExperimentTest {

	private int testId;
	private String testTitle;
	private Experimenter testExperimenter;
	private Set<TestGroup> testGroups;
	private LocalDateTime d_created, d_conducted, d_start, d_end;
	private Set<UsageEvent> usageEvents;
	
	@BeforeEach
	public void setUp() { 
		
		testId = 23;
		testTitle = "title";
		testGroups = new HashSet<>();
		testExperimenter = new Experimenter("e1", "pwd");
		testExperimenter.setId(724);
		Participant p = new Participant("p", "pwd");
		p.setId(74);
		
		TestGroup g1 = new TestGroup("g1");
		g1.setId(1);
		g1.addParticipant(p);
		TestGroup g2 = new TestGroup("g2");
		g2.setId(2);
		
		UsageEvent e1 = new UsageEvent(UsageEvent.Type.SESSION, p);
		e1.setId(1);
		UsageEvent e2 = new UsageEvent(UsageEvent.Type.QUERY, p);
		e2.setId(2);
		usageEvents = new HashSet<>();
		usageEvents.add(e1);
		usageEvents.add(e2);
		 
		testGroups.add(g1); 
		testGroups.add(g2);
		
		d_created = LocalDateTime.of(2020, 10, 1, 0, 0);
		d_conducted = LocalDateTime.of(2020, 10, 2, 0, 0);
		d_start = LocalDateTime.of(2020, 10, 2, 11, 30);
		d_end = LocalDateTime.of(2020, 10, 2, 11, 50);
	}  
	
	@Test
	public void testConstructor1() {
		
		Experiment e = new Experiment();
		
		assertEquals(0, e.getId());
		assertNotNull(e.getTestGroups());
		assertEquals(Status.NOT_READY, e.getStatus());
	}
	
	@Test
	public void testConstructor2() {
		
		Experiment e = new Experiment(testId, testTitle, testGroups);
		
		assertEquals(testId, e.getId());
		assertEquals(testTitle, e.getTitle());
		assertIterableEquals(testGroups, e.getTestGroups());
		assertEquals(Status.NOT_READY, e.getStatus());
		
		for (TestGroup g : testGroups) {
			
			assertEquals(e, g.getExperiment());
			assertEquals(e.getId(), g.getExperimentId());
			assertEquals(e.getTitle(), g.getExperimentTitle());
		}
	}
	
	@Test
	public void testConstructor3() {
		
		Experiment e = new Experiment(testTitle);
		
		assertEquals(testTitle, e.getTitle());
		assertEquals(0, e.getId());
		assertNotNull(e.getTestGroups());
		assertEquals(Status.NOT_READY, e.getStatus());
	}
	
	@Test
	public void testSetters() {
		
		Experiment e = new Experiment();
		
		assertNotEquals(testId, e.getId());
		assertNotEquals(testTitle, e.getTitle());
		assertNotEquals(d_created, e.getDateCreated());
		assertNotEquals(Status.READY, e.getStatus());
		assertNotEquals(d_conducted, e.getDateConducted());
		assertNotEquals(d_start, e.getStartTime());
		assertNotEquals(d_end, e.getEndTime());
		assertEquals(0, e.getTestGroups().size()); 
		assertNotEquals(testExperimenter, e.getExperimenter());
		assertNotEquals(usageEvents, e.getUsageEvents());
		
		e.setId(testId);
		e.setTitle(testTitle);
		e.setTestGroups(testGroups);
		e.setExperimenter(testExperimenter);
		e.setStatus(Status.READY);
		e.setDateCreated(d_created);
		e.setDateConducted(d_conducted);
		e.setStartTime(d_start);
		e.setEndTime(d_end);
		e.setUsageEvents(usageEvents);
		
		assertEquals(testId, e.getId());
		assertEquals(testTitle, e.getTitle());
		assertIterableEquals(testGroups, e.getTestGroups());
		assertEquals(Status.READY, e.getStatus());
		assertEquals(d_created, e.getDateCreated());
		assertEquals(d_conducted, e.getDateConducted());
		assertEquals(d_start, e.getStartTime());
		assertEquals(d_end, e.getEndTime());
		
		for (TestGroup g : e.getTestGroups()) {
			
			assertEquals(e, g.getExperiment());
			assertEquals(e.getId(), g.getExperimentId());
			assertEquals(e.getTitle(), g.getExperimentTitle());
		}
		
		assertEquals(testExperimenter, e.getExperimenter());
		assertEquals(testExperimenter.getId(), e.getExperimenterId());
		assertEquals(testExperimenter.getUserName(), e.getExperimenterName());
		assertIterableEquals(usageEvents, e.getUsageEvents());
	}
	
	@Test
	public void testDurationSetting() {
		
		LocalDateTime t0 = LocalDateTime.of(2020, 11, 15, 13, 0);
		LocalDateTime t1 = LocalDateTime.of(2020, 11, 15, 13, 20);
		
		Experiment ex = new Experiment("ex");
		
		ex.setStartTime(t0);
		
		assertEquals(Duration.ofSeconds(0), ex.getDuration());
		
		ex.setEndTime(t1);
		
		assertEquals(Duration.between(t0, t1), ex.getDuration());
		assertEquals(20 * 60, ex.getDuration().getSeconds());
	}
	
	@Test
	public void testAddTestGroup() {
		
		Experiment e = new Experiment(testId, testTitle, testGroups);
		TestGroup g = new TestGroup("newGroup");
		g.setId(99);
		
		assertEquals(2, e.getTestGroups().size());
		assertFalse(e.getTestGroups().contains(g));
		
		e.addTestGroup(g);
		
		assertEquals(3, e.getTestGroups().size());
		assertTrue(e.getTestGroups().contains(g));
	}
	
	@Test
	public void testClearTestGroups() {
		
		Experiment e = new Experiment(testId, testTitle, testGroups);
		
		assertNotEquals(0, e.getTestGroups().size());
		
		e.clearTestGroups();
		
		assertEquals(0, e.getTestGroups().size());
	}
	
	@Test
	public void testRemoveTestGroup1() {
		
		Experiment e = new Experiment(testId, testTitle, testGroups);
		TestGroup g = (TestGroup) testGroups.toArray()[0];
		
		assertEquals(2, e.getTestGroups().size());
		assertTrue(e.getTestGroups().contains(g));
		
		e.removeTestGroup(g);
		
		assertEquals(1, e.getTestGroups().size());
		assertFalse(e.getTestGroups().contains(g));
	}
	
	@Test
	public void testAddUsageEvent() {
		
		Experiment e = new Experiment(testId, testTitle, testGroups);
		e.setUsageEvents(usageEvents);
		UsageEvent newEvent = new UsageEvent();
		newEvent.setId(99);
		
		int count = e.getUsageEvents().size();
		assertFalse(e.getUsageEvents().contains(newEvent));
		
		e.addUsageEvent(newEvent);
		
		assertEquals(count +1, e.getUsageEvents().size());
		assertTrue(e.getUsageEvents().contains(newEvent));
	}
	
	@Test
	public void testRemoveUsageEvent() {
		
		Experiment e = new Experiment(testId, testTitle, testGroups);
		e.setUsageEvents(usageEvents);
		
		UsageEvent evt = (UsageEvent) usageEvents.toArray()[0];
		
		int count = e.getUsageEvents().size();
		assertTrue(e.getUsageEvents().contains(evt));
		
		e.removeUsageEvent(evt);
		
		assertEquals(count -1, e.getUsageEvents().size());
		assertFalse(e.getUsageEvents().contains(evt));
	}
	
	@Test
	public void testClearUsageEvents() {
		
		Experiment e = new Experiment(testId, testTitle, testGroups);
		e.setUsageEvents(usageEvents);
		
		assertNotEquals(0, e.getUsageEvents().size());
		
		e.clearUsageEvents();
		
		assertEquals(0, e.getUsageEvents().size());
	}
	
	@Test
	public void testEqualsAndHashCode() {
		
		Experiment e1 = new Experiment(1, testTitle, testGroups);
		Experiment e2 = new Experiment(1, testTitle, testGroups);
		Experiment e3 = new Experiment(2, testTitle, testGroups);
		
		assertTrue(e1.equals(e1));
		assertTrue(e1.equals(e2));
		assertFalse(e1.equals(e3));
		
		assertEquals(e1.hashCode(), e2.hashCode());
		assertNotEquals(e1.hashCode(), e3.hashCode());
	}
}






 








