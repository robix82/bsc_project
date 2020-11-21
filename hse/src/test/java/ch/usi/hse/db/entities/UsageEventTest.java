package ch.usi.hse.db.entities;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UsageEventTest {

	private UsageEvent.Type testType = UsageEvent.Type.QUERY;
	private String testContent = "some query";
	private Participant testUser;
	private TestGroup testGroup;
	private Experiment testExperiment;
	
	@BeforeEach
	public void setUp() {
		
		testUser = new Participant("p1", "pwd");
		testUser.setId(43);
		testGroup = new TestGroup("g1");
		testGroup.setId(12);
		testExperiment = new Experiment("e1");
		testExperiment.setId(35);
		
		testGroup.addParticipant(testUser);
		testExperiment.addTestGroup(testGroup);
	}
	
	@Test
	public void testSetup() {
		
		assertEquals(testGroup, testUser.getTestGroup());
		assertEquals(testExperiment, testGroup.getExperiment());
	}
	
	@Test
	public void testConstructor1() {
		
		UsageEvent evt = new UsageEvent();
		
		assertEquals(0, evt.getId());
		assertTrue(timeApproxEquals(LocalDateTime.now(), evt.getTimeStamp()));
	}
	
	@Test
	public void testConstructor2() {
		
		UsageEvent evt = new UsageEvent(testType, testUser, testContent);
		
		assertEquals(0, evt.getId());
		assertTrue(timeApproxEquals(LocalDateTime.now(), evt.getTimeStamp()));
		assertEquals(testUser.getId(), evt.getUserId());
		assertEquals(testGroup.getId(), evt.getGroupId());
		assertEquals(testGroup.getName(), evt.getGroupName());
		assertEquals(testType, evt.getEventType());
		assertEquals(testContent, evt.getContent());
	}
	
	@Test
	public void testSetters() {
		
		int testId = 23;
		int userId = 12;
		int groupId = 72;
		String groupName = "gr32";
		LocalDateTime testTime = LocalDateTime.of(2020, 11, 20, 11, 11, 21);
		
		UsageEvent evt = new UsageEvent();
		
		assertNotEquals(testId, evt.getId());
		assertNotEquals(testTime, evt.getTimeStamp());
		assertNotEquals(userId, evt.getUserId());
		assertNotEquals(groupId, evt.getGroupId());
		assertNotEquals(groupName, evt.getGroupName());
		assertNotEquals(testType, evt.getEventType());
		assertNotEquals(testContent, evt.getContent());
		
		evt.setId(testId);
		evt.setTimestamp(testTime);
		evt.setUserId(userId);
		evt.ssetGroupId(groupId);
		evt.setGroupName(groupName);
		evt.setEventType(testType);
		evt.setContent(testContent);
		
		assertEquals(testId, evt.getId());
		assertEquals(testTime, evt.getTimeStamp());
		assertEquals(userId, evt.getUserId());
		assertEquals(groupId, evt.getGroupId());
		assertEquals(groupName, evt.getGroupName());
		assertEquals(testType, evt.getEventType());
		assertEquals(testContent, evt.getContent());
	}
	
	@Test
	public void testEqualsAndHashCode() {
		
		UsageEvent e1 = new UsageEvent();
		UsageEvent e2 = new UsageEvent();
		UsageEvent e3 = new UsageEvent();
		
		e1.setId(21);
		e2.setId(21);
		e3.setId(42);
		
		assertTrue(e1.equals(e1));
		assertTrue(e1.equals(e2));
		assertFalse(e1.equals(e3));
		
		assertEquals(e1.hashCode(), e2.hashCode());
		assertNotEquals(e1.hashCode(), e3.hashCode());
	}
	
	
	///////////////////////////////
	
	private boolean timeApproxEquals(LocalDateTime t1, LocalDateTime t2) {
		
		if (t1 == null && t2 == null) {
			return true;
		}
		
		if (t1 == null || t2 == null) {
			return false;
		}
		
		return t1.getYear() == t2.getYear() &&
			   t1.getMonth() == t2.getMonth() &&
			   t1.getDayOfMonth() == t2.getDayOfMonth() &&
			   t1.getHour() == t2.getHour() &&
			   t1.getMinute() == t2.getMinute() &&
			   t1.getSecond() == t2.getSecond();
	}
}










