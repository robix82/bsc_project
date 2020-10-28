package ch.usi.hse.db.entities;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ParticipantTest {

	private int testId;
	private String testName, testPwd;
	private Set<Role> testRoles;
	private TestGroup testGroup;
	private Experiment testExperiment;
	
	@BeforeEach
	public void init() {
		
		testId = 23;
		testName = "name";
		testPwd = "testPwd";
		testRoles = new HashSet<>(); 
		testRoles.add(new Role(1, "ROLE_1"));
		testRoles.add(new Role(2, "ROLE_2"));
		
		testGroup = new TestGroup("g1");
		testGroup.setId(25);
		testExperiment = new Experiment("e1"); 
		testExperiment.setId(27);
		testExperiment.addTestGroup(testGroup);
	}
	
	
	
	@Test
	public void testConstructor1() {
		
		Participant p = new Participant();
		
		assertEquals(0, p.getId());
		assertNotNull(p.getRoles());
		assertEquals(0, p.getRoles().size());
	}
	
	@Test
	public void testCconstructor2() {
		
		Participant p = new Participant(testId, testName, testPwd, testRoles, testGroup);
		
		assertEquals(testId, p.getId());
		assertEquals(testName, p.getUserName());
		assertEquals(testPwd, p.getPassword());
		assertIterableEquals(testRoles, p.getRoles());
		assertEquals(testGroup, p.getTestGroup());
		assertEquals(testExperiment.getId(), p.getExperimentId());
		assertEquals(testExperiment.getTitle(), p.getExperimentTitle());
		assertEquals(testGroup.getId(), p.getTestGroupId());
		assertEquals(testGroup.getName(), p.getTestGroupName());
	}
	
	@Test
	public void testConstructor3() {
		
		Participant p = new Participant(testName, testPwd);
		
		assertEquals(testName, p.getUserName());
		assertEquals(testPwd, p.getPassword());
	}
	
	@Test
	public void testSetters() {
		
		Participant p = new Participant();
		
		int eId = 21;
		int gId = 22;
		String eTitle = "eTitle";
		String gName = "gName";
		
		assertNotEquals(eId, p.getExperimentId());
		assertNotEquals(gId, p.getTestGroupId());
		assertNotEquals(eTitle, p.getExperimentTitle());
		assertNotEquals(gName, p.getTestGroupName());
		
		p.setExperimentId(eId);
		p.setTestGroupId(gId);
		p.setExperimentTitle(eTitle);
		p.setTestGroupName(gName);
		
		assertEquals(eId, p.getExperimentId());
		assertEquals(gId, p.getTestGroupId());
		assertEquals(eTitle, p.getExperimentTitle());
		assertEquals(gName, p.getTestGroupName());
	}
}




















