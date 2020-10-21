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
		
		Role adminRole = new Role(1, "ADMIN");
		Role experimenterRole = new Role(2, "EXPERIMENTER");
		Role participantRole = new Role(3, "PARTICIPANT");
		adminRoles = new HashSet<>();
		adminRoles.add(adminRole);
		experimenterRoles = new HashSet<>();
		experimenterRoles.add(experimenterRole);
		participantRoles = new HashSet<>();
		participantRoles.add(participantRole);
		
		roleRepo.deleteAll();
		/*
		roleRepo.save(adminRole);
		roleRepo.save(experimenterRole);
		roleRepo.save(participantRole);
		*/
		
		admin = new Administrator("admin", "pwd", adminRoles);
		experimenter = new Experimenter("experimenter", "pwd", experimenterRoles);
		participant1 = new Participant("p1", "pwd", participantRoles);
		participant2 = new Participant("p2", "pwd", participantRoles);
				
		userRepo.deleteAll();
		userRepo.save(admin);
		userRepo.save(experimenter);
		userRepo.save(participant1);
		userRepo.save(participant2);		
	}
	
	@Test
	public void setupTest() {

		assertEquals(4, allUsers().size());
		assertEquals(1, allAdministrators().size());
		assertEquals(1, allExperimenters().size());
		assertEquals(2, allParticipants().size());
	}

	@Test
	public void saveTest() {
		
		Administrator newAdmin = new Administrator("admin1", "pwd", adminRoles);
		Experimenter newExperimenter = new Experimenter("experimenter1", "pwd", experimenterRoles);
		Participant newParticipant = new Participant("p3", "pwd", participantRoles);

		administratorRepo.save(newAdmin);
		experimenterRepo.save(newExperimenter);
		participantRepo.save(newParticipant);
		
		assertEquals(7, allUsers().size());
		assertEquals(2, allAdministrators().size());
		assertEquals(2, allExperimenters().size());
		assertEquals(3, allParticipants().size());
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





















