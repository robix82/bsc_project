package ch.usi.hse.db.repositories;


import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import ch.usi.hse.db.entities.Experiment;
import ch.usi.hse.db.entities.Participant;
import ch.usi.hse.db.entities.TestGroup;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class ExperimentRepositoriesTest {

	@Autowired
	private ExperimentRepository experimentRepo;
	
	@Autowired
	private TestGroupRepository groupRepo;
	
	@Autowired
	private ParticipantRepository participantRepo;
	
	@Autowired
	private RoleRepository roleRepo;
	
	@BeforeEach
	public void setUp() {
		
		experimentRepo.deleteAll();
		groupRepo.deleteAll();
		participantRepo.deleteAll();
	}
	
	@Test
	public void setupTest() {
		
		assertNotNull(experimentRepo);
		assertNotNull(groupRepo);
		assertNotNull(participantRepo);
		assertNotNull(roleRepo);
	} 
	
	// simple entity persistence
	
	@Test
	public void testSaveParticipant() {
		
		Participant p = new Participant("p", "pwd");
		
		Participant saved = participantRepo.save(p);
		int id = saved.getId();
		
		assertTrue(participantRepo.existsById(id));
	}
	
	@Test
	public void testSaveTestGroup() {
		
		TestGroup g = new TestGroup("g");
		
		TestGroup saved = groupRepo.save(g);
		int id = saved.getId();
		
		assertTrue(groupRepo.existsById(id));
	}
	
	@Test
	public void testSaveExperiment() {
		
		Experiment e = new Experiment("e");
		
		Experiment saved = experimentRepo.save(e);
		int id = saved.getId();
		
		assertTrue(experimentRepo.existsById(id));
	}
	
	// participant-group persistence

	@Test
	public void testParticipantGroup() {
		
		Participant p = new Participant("p", "pwd");
		TestGroup g = new TestGroup("g");
		
		Participant p_saved = participantRepo.save(p);
		TestGroup g_saved = groupRepo.save(g);
		
		g_saved.addParticipant(p_saved);
		
		groupRepo.save(g_saved);
		
		TestGroup retrieved = groupRepo.findById(g_saved.getId());
		
		assertTrue(retrieved.getParticipants().contains(p_saved));
		
		assertTrue(participantRepo.existsById(p_saved.getId()));
		
		groupRepo.delete(retrieved);
		
		assertFalse(participantRepo.existsById(p_saved.getId()));
	}
	
	// participant-group-experiment persistence
	
	@Test
	public void testParticipantGroupExperiment() {
		
		Participant p = new Participant("p", "pwd");
		TestGroup g = new TestGroup("g");
		Experiment e = new Experiment("e");
		
		Participant p_saved = participantRepo.save(p);
		TestGroup g_saved = groupRepo.save(g);
		Experiment e_saved = experimentRepo.save(e);
		
		g_saved.addParticipant(p_saved);
		participantRepo.save(p_saved);
		
		e_saved.addTestGroup(g_saved);
		groupRepo.save(g_saved); 
		
		Experiment e_retrieved = experimentRepo.findById(e_saved.getId());
		TestGroup g_retrieved = (TestGroup) e_retrieved.getTestGroups().toArray()[0];
		Participant p_retrieved = (Participant) g_retrieved.getParticipants().toArray()[0];
		
		assertEquals(e_saved, e_retrieved);
		assertEquals(g_saved, g_retrieved);
		assertEquals(p_saved, p_retrieved);
		
		assertTrue(groupRepo.existsById(g_saved.getId()));
		assertTrue(participantRepo.existsById(p_saved.getId()));
		
		experimentRepo.delete(e_saved);
		
		assertFalse(groupRepo.existsById(g_saved.getId()));
		assertFalse(participantRepo.existsById(p_saved.getId()));
	}
}












