package ch.usi.hse.db.repositories;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
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
import ch.usi.hse.db.entities.HseUser;

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
	private HseUser admin, experimenter, participant1, participant2;
	
	@BeforeEach
	public void setUp() {
		
		adminRoles = new HashSet<>(); 
		adminRoles.add(roleRepo.save(new Role(1, "ADMIN")));
		experimenterRoles = new HashSet<>();
		experimenterRoles.add(roleRepo.save(new Role(2, "EXPERIMENTER")));
		participantRoles = new HashSet<>();
		participantRoles.add(roleRepo.save(new Role(3, "PARTICICPANT")));
		
		admin = new Administrator("admin", "pwd");
		experimenter = new Experimenter("experimenter", "pwd");
		participant1 = new Participant("p1", "pwd");
		participant2 = new Participant("p2", "pwd");
		admin.setRoles(adminRoles);
		experimenter.setRoles(experimenterRoles);
		participant1.setRoles(participantRoles);
		participant2.setRoles(participantRoles);
		
		userRepo.deleteAll();
		admin = userRepo.save(admin);
		experimenter = userRepo.save(experimenter);
		participant1 = userRepo.save(participant1);
		participant2 = userRepo.save(participant2);	 			
	}
	
	@Test
	public void setupTest() {

		assertEquals(3, roleRepo.count());

		assertEquals(4, userRepo.count());
		assertEquals(1, administratorRepo.count());
		assertEquals(1, experimenterRepo.count());
		assertEquals(2, participantRepo.count());
		
		assertTrue(administratorRepo.findAll().contains(admin));
		assertTrue(experimenterRepo.findAll().contains(experimenter));
		assertTrue(participantRepo.findAll().contains(participant1));
		assertTrue(participantRepo.findAll().contains(participant1));
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
	public void testExistsById() {
		
		assertTrue(userRepo.existsById(admin.getId()));
		assertFalse(userRepo.existsById(admin.getId() + 999));
		assertTrue(administratorRepo.existsById(admin.getId()));
		assertFalse(experimenterRepo.existsById(admin.getId()));
	}
	
	@Test
	public void testExistsByUserName() {
		
		assertTrue(userRepo.existsByUserName(admin.getUserName()));
		assertFalse(userRepo.existsByUserName("noSuchName"));
		assertTrue(administratorRepo.existsByUserName(admin.getUserName()));
		assertFalse(experimenterRepo.existsByUserName(admin.getUserName()));
	}

	@Test
	public void testSave() {
		
		Administrator newAdmin = new Administrator("admin1", "pwd");
		newAdmin.setRoles(adminRoles);
		Experimenter newExperimenter = new Experimenter("experimenter1", "pwd");
		newExperimenter.setRoles(experimenterRoles);
		Participant newParticipant = new Participant("p3", "pwd");
		newParticipant.setRoles(participantRoles);
		
		administratorRepo.save(newAdmin);
		experimenterRepo.save(newExperimenter);
		participantRepo.save(newParticipant);
		
		assertNotNull(newAdmin);
		assertNotNull(newExperimenter);
		assertNotNull(newParticipant);
		
		assertEquals(7, userRepo.count());
		assertEquals(2, administratorRepo.count());
		assertEquals(2, experimenterRepo.count());
		assertEquals(3, participantRepo.count());
		
		assertTrue(administratorRepo.findAll().contains(newAdmin));
		assertTrue(experimenterRepo.findAll().contains(newExperimenter));
		assertTrue(participantRepo.findAll().contains(newParticipant));
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
		
		assertEquals(2, participantRepo.count());
		assertTrue(userRepo.findAll().contains(participant2));
		
		userRepo.delete(participant2);
		
		assertEquals(1, participantRepo.count());
		assertFalse(userRepo.findAll().contains(participant2));
	}
}





















