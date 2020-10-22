package ch.usi.hse.db.repositories;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import ch.usi.hse.db.entities.Administrator;
import ch.usi.hse.db.entities.Experimenter;
import ch.usi.hse.db.entities.Participant;
import ch.usi.hse.db.entities.Role;
import ch.usi.hse.db.entities.User;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class UserRepositoriesTest {

	@Autowired
	private RoleRepository roleRepo;
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private AdministratorRepository administratorRepo;
	
	@Autowired
	private ExperimenterRepository experimenterRepo;
	
	@Autowired
	private ParticipantRepository participantRepo;

	private Set<Role> adminRoles, experimenterRoles, participantRoles;
	private User admin, experimenter, participant1, participant2;
	
	@BeforeEach
	public void setUp() {
		
		/*
		Role adminRole = new Role("ADMIN");
		Role experimenterRole = new Role("EXPERIMENTER");
		Role participantRole = new Role("PARTICIPANT");
		
		roleRepo.deleteAll();
		
		roleRepo.save(adminRole);
		roleRepo.save(experimenterRole);
		roleRepo.save(participantRole);
		*/
		
		adminRoles = new HashSet<>(); 
		adminRoles.add(roleRepo.findByRole("ADMIN"));
		experimenterRoles = new HashSet<>();
		experimenterRoles.add(roleRepo.findByRole("EXPERIMENTER"));
		participantRoles = new HashSet<>();
		participantRoles.add(roleRepo.findByRole("PARTICICPANT"));
		
		userRepo.deleteAll();
		admin = userRepo.save(new Administrator("admin", "pwd", adminRoles));
		experimenter = userRepo.save(new Experimenter("experimenter", "pwd", experimenterRoles));
		participant1 = userRepo.save(new Participant("p1", "pwd", participantRoles));
		participant2 = userRepo.save(new Participant("p2", "pwd", participantRoles));				
	}
	
	@Test
	public void setupTest() {
		
		List<Role> allRoles = (List<Role>) roleRepo.findAll();
		assertEquals(3, allRoles.size());

		assertEquals(4, allUsers().size());
		assertEquals(1, allAdministrators().size());
		assertEquals(1, allExperimenters().size());
		assertEquals(2, allParticipants().size());
		
		assertTrue(allAdministrators().contains(admin));
		assertTrue(allExperimenters().contains(experimenter));
		assertTrue(allParticipants().contains(participant1));
		assertTrue(allParticipants().contains(participant1));
	}
	
	@Test
	public void testFindById() {
		
		assertEquals(admin, userRepo.findById(admin.getId()));
		assertEquals(admin, administratorRepo.findById(admin.getId()));
		assertEquals(experimenter, userRepo.findById(experimenter.getId()));
		assertEquals(experimenter, experimenterRepo.findById(experimenter.getId()));
		assertEquals(participant1, userRepo.findById(participant1.getId()));
		assertEquals(participant1, participantRepo.findById(participant1.getId()));
	}
	
	@Test
	public void testFindByUsername() {
		
		assertEquals(admin, userRepo.findByUserName(admin.getUserName()));
		assertEquals(admin, administratorRepo.findByUserName(admin.getUserName()));
		assertEquals(experimenter, userRepo.findByUserName(experimenter.getUserName()));
		assertEquals(experimenter, experimenterRepo.findByUserName(experimenter.getUserName()));
		assertEquals(participant1, userRepo.findByUserName(participant1.getUserName()));
		assertEquals(participant1, participantRepo.findByUserName(participant1.getUserName()));
	}

	@Test
	public void testSave() {
		
		Administrator newAdmin = administratorRepo.save(
									new Administrator("admin1", "pwd", adminRoles));
		
		Experimenter newExperimenter = experimenterRepo.save(
									new Experimenter("experimenter1", "pwd", experimenterRoles));
		
		Participant newParticipant = participantRepo.save(
									new Participant("p3", "pwd", participantRoles));
		
		assertEquals(7, allUsers().size());
		assertEquals(2, allAdministrators().size());
		assertEquals(2, allExperimenters().size());
		assertEquals(3, allParticipants().size());
		
		assertTrue(allAdministrators().contains(newAdmin));
		assertTrue(allExperimenters().contains(newExperimenter));
		assertTrue(allParticipants().contains(newParticipant));
	}
	
	@Test
	public void testUpdate() {
		
		int id = admin.getId();
		String newName = "newName";
		
		assertNotEquals(newName, userRepo.findById(id).getUserName());
		
		admin.setUserName(newName);
		userRepo.save(admin);
		
		assertEquals(newName, userRepo.findById(id).getUserName());
	}
	
	@Test
	public void testDelete() {
		
		assertEquals(2, allParticipants().size());
		assertTrue(allUsers().contains(participant2));
		
		userRepo.delete(participant2);
		
		assertEquals(1, allParticipants().size());
		assertFalse(allUsers().contains(participant2));
	}
	
	
	private List<User> allUsers() {
		
		return (List<User>) userRepo.findAll();
	}
	
	private List<Administrator> allAdministrators() {
		
		return (List<Administrator>) administratorRepo.findAll();
	}
	
	private List<Experimenter> allExperimenters() {
		
		return (List<Experimenter>) experimenterRepo.findAll();
	}
	
	private List<Participant> allParticipants() {
		
		return (List<Participant>) participantRepo.findAll();
	}
}





















