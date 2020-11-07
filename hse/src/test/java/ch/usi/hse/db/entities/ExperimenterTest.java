package ch.usi.hse.db.entities;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ExperimenterTest {

	private int testId;
	private String testName;
	private String testPwd;
	private Set<Role> testRoles;
	private Set<Experiment> testExperiments;
	
	@BeforeEach
	public void init() {
		
		testId = 23;
		testName = "name";
		testPwd = "testPwd";
		testRoles = new HashSet<>();
		testRoles.add(new Role(1, "ROLE_1"));
		testRoles.add(new Role(2, "ROLE_2"));
		
		Experiment e1 = new Experiment("e1");
		Experiment e2 = new Experiment("e2");
		e1.setId(1);
		e2.setId(2);
		testExperiments = new HashSet<>();
		testExperiments.add(e1);
		testExperiments.add(e2);
	}
	
	@Test 
	public void testConstructor1() {
		
		Experimenter e = new Experimenter();
		
		assertEquals(0, e.getId());
		assertEquals("", e.getUserName());
		assertEquals("", e.getPassword());
		assertNotNull(e.getRoles());
		assertEquals(0, e.getRoles().size());
		assertEquals(0, e.getExperiments().size());
	}
	
	@Test
	public void testConstructor2() {
		
		Experimenter e = new Experimenter(testId, testName, testPwd, testRoles);
		
		assertEquals(testId, e.getId());
		assertEquals(testName, e.getUserName());
		assertEquals(testPwd, e.getPassword());
		assertIterableEquals(testRoles, e.getRoles());
		assertEquals(0, e.getExperiments().size());
	}
	
	@Test
	public void testConstructor3() {
		
		Experimenter e = new Experimenter(testName, testPwd);
		
		assertEquals(testName, e.getUserName());
		assertEquals(testPwd, e.getPassword());
		assertEquals(0, e.getExperiments().size());
	}
	
	@Test
	public void testSetExperiments() {
		
		Experimenter e = new Experimenter(testName, testPwd);
		e.setId(27);
		
		assertNotEquals(testExperiments, e.getExperiments());
		
		for (Experiment exp : testExperiments) {
			
			assertNotEquals(e, exp.getExperimenter());
			assertNotEquals(e.getId(), exp.getExperimenterId());
			assertNotEquals(e.getUserName(), exp.getExperimenterName());
		}
		
		e.setExperiments(testExperiments);
		
		assertEquals(testExperiments, e.getExperiments());
		
		for (Experiment exp : testExperiments) {
			
			assertEquals(e, exp.getExperimenter());
			assertEquals(e.getId(), exp.getExperimenterId());
			assertEquals(e.getUserName(), exp.getExperimenterName());
		}
	}
	
	@Test
	public void testAddExperiment() {
		
		Experimenter e = new Experimenter(testName, testPwd);
		e.setId(27);
		
		Experiment exp = new Experiment("e3");
		exp.setId(3);
		
		assertEquals(0, e.getExperiments().size());
		
		e.addExperiment(exp);
		
		assertEquals(1, e.getExperiments().size());
		
		Experiment exp_1 = List.copyOf(e.getExperiments()).get(0);
		assertEquals(exp, exp_1);
		
		assertEquals(e.getId(), exp_1.getExperimenterId());
		assertEquals(e.getUserName(), exp_1.getExperimenterName());
	}
	
	@Test
	public void testRemoveExperiment() {
		
		Experimenter e = new Experimenter(testName, testPwd);
		e.setId(27);
		
		e.setExperiments(testExperiments);
		
		Experiment exp = List.copyOf(testExperiments).get(0);
		
		long before = e.getExperiments().size();
		assertTrue(e.getExperiments().contains(exp));
		
		e.removeExperiment(exp);
		
		assertEquals(before -1, e.getExperiments().size());
		assertFalse(e.getExperiments().contains(exp));
	}
}












