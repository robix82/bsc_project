package ch.usi.hse.db.repositories;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import ch.usi.hse.db.entities.Participant;
import ch.usi.hse.db.entities.Role;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class ParticipantRepositoryTest {

	@Autowired
	private ParticipantRepository participantRepo;
	
	@Autowired
	private RoleRepository roleRepo;
	
	private List<Participant> e1, e2;
	private List<Participant> g1, g2, g3, g4;
	
	@BeforeEach
	public void setUp() {
		
		Set<Role> roles = new HashSet<>();
		roles.add(roleRepo.findByRole("PARTICIPANT"));
		
		g1 = new ArrayList<>();
		g2 = new ArrayList<>();
		g3 = new ArrayList<>();
		g4 = new ArrayList<>();
		
		g1.add(new Participant(1, "p1", "pwd", roles, 1, 1, "g1"));
		g1.add(new Participant(2, "p2", "pwd", roles, 1, 1, "g1"));
		g2.add(new Participant(3, "p3", "pwd", roles, 1, 2, "g2"));
		g2.add(new Participant(4, "p4", "pwd", roles, 1, 2, "g2"));
		g3.add(new Participant(5, "p5", "pwd", roles, 2, 3, "g3"));
		g3.add(new Participant(6, "p6", "pwd", roles, 2, 3, "g3"));
		g4.add(new Participant(7, "p7", "pwd", roles, 2, 4, "g4"));
		g4.add(new Participant(8, "p8", "pwd", roles, 2, 4, "g4"));
		
		e1 = new ArrayList<>();
		e2 = new ArrayList<>();
		
		e1.addAll(g1);
		e1.addAll(g2);
		e2.addAll(g3);
		e2.addAll(g4);
		
		for (Participant p : e1) {			
			participantRepo.save(p);
		}
		
		for (Participant p : e2) {
			participantRepo.save(p);
		}
	} 
	
	@Test
	public void testFindByExperimentId() {
		
		List<Participant> e1_p = participantRepo.findByExperimentId(1);
		List<Participant> e2_p = participantRepo.findByExperimentId(2);
		
		assertIterableEquals(e1, e1_p);
		assertIterableEquals(e2, e2_p);
	}
	
	@Test
	public void testFindByGroupId() {
		
		List<Participant> g1_p = participantRepo.findByGroupId(1);
		List<Participant> g2_p = participantRepo.findByGroupId(2);
		List<Participant> g3_p = participantRepo.findByGroupId(3);
		List<Participant> g4_p = participantRepo.findByGroupId(4);
		
		assertIterableEquals(g1, g1_p);
		assertIterableEquals(g2, g2_p);
		assertIterableEquals(g3, g3_p);
		assertIterableEquals(g4, g4_p);
	}
	
	@Test
	public void testFindByGroupName() {
		
		List<Participant> g1_p = participantRepo.findByGroupName("g1");
		List<Participant> g2_p = participantRepo.findByGroupName("g2");
		List<Participant> g3_p = participantRepo.findByGroupName("g3");
		List<Participant> g4_p = participantRepo.findByGroupName("g4");
		
		assertIterableEquals(g1, g1_p);
		assertIterableEquals(g2, g2_p);
		assertIterableEquals(g3, g3_p);
		assertIterableEquals(g4, g4_p);
	}
}
















