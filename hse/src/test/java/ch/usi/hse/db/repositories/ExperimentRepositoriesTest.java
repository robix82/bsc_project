package ch.usi.hse.db.repositories;


import static org.junit.jupiter.api.Assertions.*;

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
	
	@Test
	public void testSave() {
		
		Participant p = new Participant("p", "pwd");
		TestGroup g = new TestGroup("g");
		Experiment e = new Experiment("e");

		g.addParticipant(p);
		e.addTestGroup(g);
		
		assertEquals(0, experimentRepo.count());
		assertEquals(0, groupRepo.count());
		assertEquals(0, participantRepo.count());
		
		Experiment e_saved = experimentRepo.save(e);
		
		assertEquals(1, experimentRepo.count());
		assertEquals(1, groupRepo.count());
		assertEquals(1, participantRepo.count());
		
		Experiment e_retrieved = experimentRepo.findById(e_saved.getId());
		TestGroup g_retrieved = (TestGroup) e_retrieved.getTestGroups().toArray()[0];
		Participant p_retrieved = (Participant) g_retrieved.getParticipants().toArray()[0];
		
		assertEquals(g_retrieved.getName(), g.getName());
		assertEquals(p_retrieved.getUserName(), p.getUserName());
	}
	
	@Test
	public void testExperimentDeleteExperiment() {
		
		Participant p = new Participant("p", "pwd");
		TestGroup g = new TestGroup("g");
		Experiment e = new Experiment("e");

		g.addParticipant(p);
		e.addTestGroup(g);
		
		assertEquals(0, experimentRepo.count());
		assertEquals(0, groupRepo.count());
		assertEquals(0, participantRepo.count());
		
		Experiment e_saved = experimentRepo.save(e);
		
		assertEquals(1, experimentRepo.count());
		assertEquals(1, groupRepo.count());
		assertEquals(1, participantRepo.count());
		
		experimentRepo.delete(e_saved);
		
		assertEquals(0, experimentRepo.count());
		assertEquals(0, groupRepo.count());
		assertEquals(0, participantRepo.count());
	}
	
	@Test
	public void testRemoveTetGroup() {
		
		Participant p1 = new Participant("p1", "pwd");
		Participant p2 = new Participant("p2", "pwd");
		TestGroup g1 = new TestGroup("g1");
		g1.setId(1);
		TestGroup g2 = new TestGroup("g2");
		g2.setId(2);
		Experiment e = new Experiment("e");

		g1.addParticipant(p1);
		g2.addParticipant(p2);
		e.addTestGroup(g1);
		e.addTestGroup(g2);
		
		assertEquals(0, experimentRepo.count());
		assertEquals(0, groupRepo.count());
		assertEquals(0, participantRepo.count());
		
		Experiment e_saved = experimentRepo.save(e);
		
		assertEquals(1, experimentRepo.count());
		assertEquals(2, groupRepo.count());
		assertEquals(2, participantRepo.count());
		
		e_saved.removeTestGroup(g1.getName());
		experimentRepo.save(e_saved);
		
		assertEquals(1, experimentRepo.count());
		assertEquals(1, groupRepo.count());
		assertEquals(1, participantRepo.count());
	}
	
	@Test
	public void testClearTestGroups() {
		
		Participant p1 = new Participant("p1", "pwd");
		Participant p2 = new Participant("p2", "pwd");
		TestGroup g1 = new TestGroup("g1");
		g1.setId(1);
		TestGroup g2 = new TestGroup("g2");
		g2.setId(2);
		Experiment e = new Experiment("e");

		g1.addParticipant(p1);
		g2.addParticipant(p2);
		e.addTestGroup(g1);
		e.addTestGroup(g2);
		
		assertEquals(0, experimentRepo.count());
		assertEquals(0, groupRepo.count());
		assertEquals(0, participantRepo.count());
		
		Experiment e_saved = experimentRepo.save(e);
		
		assertEquals(1, experimentRepo.count());
		assertEquals(2, groupRepo.count());
		assertEquals(2, participantRepo.count());
		
		e_saved.clearTestGroups();
		experimentRepo.save(e_saved);
		
		assertEquals(1, experimentRepo.count());
		assertEquals(0, groupRepo.count());
		assertEquals(0, participantRepo.count());
	}

}





 






