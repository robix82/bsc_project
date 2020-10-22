package ch.usi.hse.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import ch.usi.hse.db.entities.Administrator;
import ch.usi.hse.db.entities.Experimenter;
import ch.usi.hse.db.entities.Participant;
import ch.usi.hse.db.entities.Role;
import ch.usi.hse.db.entities.User;
import ch.usi.hse.db.repositories.AdministratorRepository;
import ch.usi.hse.db.repositories.ExperimenterRepository;
import ch.usi.hse.db.repositories.ParticipantRepository;
import ch.usi.hse.db.repositories.RoleRepository;
import ch.usi.hse.db.repositories.UserRepository;
import ch.usi.hse.exceptions.NoSuchUserException;

public class UserServiceTest {

	@Mock
	private UserRepository userRepository;
	
	@Mock
	private AdministratorRepository administratorRepository;
	
	@Mock
	private ExperimenterRepository experimenterRepository;
	
	@Mock
	private ParticipantRepository participantRepository;
	
	@Mock
	private RoleRepository roleRepository;
	
	@Mock
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	private UserService testService;
	private List<User> users;
	private List<Administrator> administrators;
	private List<Experimenter> experimenters;
	private List<Participant> participants;
	
	@BeforeEach
	public void setUp() {
		
		initMocks(this);
		
		// create test data
		
		Set<Role> adminRoles = new HashSet<>(); 
		adminRoles.add(new Role(1, "ADMIN"));
		Set<Role> experimenterRoles = new HashSet<>();
		experimenterRoles.add(new Role(2, "EXPERIMENTER"));
		Set<Role> participantRoles = new HashSet<>();
		participantRoles.add(new Role(1, "PARTICICPANT"));
		
		users = new ArrayList<>();
		administrators = new ArrayList<>();
		experimenters = new ArrayList<>();
		participants = new ArrayList<>();
		
		administrators.add(new Administrator(1, "admin1", "pdw", adminRoles));
		administrators.add(new Administrator(2, "admin2", "pdw", adminRoles));
		experimenters.add(new Experimenter(3, "exp1", "pwd", experimenterRoles));
		experimenters.add(new Experimenter(4, "exp2", "pwd", experimenterRoles));
		participants.add(new Participant(5, "part1", "pwd", participantRoles));
		participants.add(new Participant(6, "part2", "pwd", participantRoles));
		
		users.addAll(administrators);
		users.addAll(experimenters);
		users.addAll(participants);
		
		testService = new UserService( userRepository,
									   administratorRepository,
									   experimenterRepository,
									   participantRepository,
									   roleRepository,
									   bCryptPasswordEncoder );
								
		// set up mock returns
		
		// count()
		when(userRepository.count()).thenReturn((long) users.size());
		when(administratorRepository.count()).thenReturn((long) administrators.size());
		when(experimenterRepository.count()).thenReturn((long) experimenters.size());
		when(participantRepository.count()).thenReturn((long) participants.size());
		
		// findAll()
		when(userRepository.findAll()).thenReturn(users);
		when(administratorRepository.findAll()).thenReturn(administrators);
		when(experimenterRepository.findAll()).thenReturn(experimenters);
		when(participantRepository.findAll()).thenReturn(participants);
		
		// findById() and findByUserName()
		
		when(userRepository.existsById(anyInt())).thenReturn(false);
		when(userRepository.existsByUserName(anyString())).thenReturn(false);
		
		for (User u : users) {
			
			when(userRepository.existsById(u.getId())).thenReturn(true);
			when(userRepository.existsByUserName(u.getUserName())).thenReturn(true);
			when(userRepository.findById(u.getId())).thenReturn(u);
			when(userRepository.findByUserName(u.getUserName())).thenReturn(u);
		}
		
		for (Administrator a : administrators) {
			when(administratorRepository.findById(a.getId())).thenReturn(a);
			when(administratorRepository.findByUserName(a.getUserName())).thenReturn(a);
		}
		
		for (Experimenter e : experimenters) {
			when(experimenterRepository.findById(e.getId())).thenReturn(e);
			when(experimenterRepository.findByUserName(e.getUserName())).thenReturn(e);
		}
		
		for (Participant p : participants) {
			when(participantRepository.findById(p.getId())).thenReturn(p);
			when(participantRepository.findByUserName(p.getUserName())).thenReturn(p);
		}
	}
	
	@Test
	public void testSetup() {
		
		assertNotNull(userRepository);
		assertNotNull(administratorRepository);
		assertNotNull(experimenterRepository);
		assertNotNull(participantRepository);
		assertNotNull(roleRepository);
		assertNotNull(bCryptPasswordEncoder);
		assertNotNull(testService);
		
		assertEquals(2, administrators.size());
		assertEquals(2, experimenters.size());
		assertEquals(2, participants.size());
		assertEquals(6, users.size());
		
		assertTrue(userRepository.existsById(users.get(0).getId()));
		assertFalse(userRepository.existsById(users.get(0).getId() + 999));
		assertTrue(userRepository.existsByUserName(users.get(0).getUserName()));
		assertFalse(userRepository.existsByUserName("xxx"));
	}
	
	@Test
	public void testUserCount() {
		
		assertEquals(users.size(), testService.userCount());
	}
	
	@Test
	public void testAdministratorCount() {
		
		assertEquals(administrators.size(), testService.administratorCount());
	}
	
	@Test
	public void testExperimenterCount() {
		
		assertEquals(experimenters.size(), testService.experimenterCount());
	}
	
	@Test
	public void testParticipantCount() {
		
		assertEquals(participants.size(), testService.participantCount());
	}
	
	@Test
	public void testAllUsers() {
		
		List<User> res = testService.allUsers();
		assertIterableEquals(users, res);
	}
	
	@Test
	public void testAllAdministrators() {
		
		List<Administrator> res = testService.allAdministrators();
		assertIterableEquals(administrators, res);
	}
	
	@Test
	public void testAllExperimenters() {
		
		List<Experimenter> res = testService.allExperimenters();
		assertIterableEquals(experimenters, res);
	}
	
	@Test
	public void testAllParticipants() {
		
		List<Participant> res = testService.allParticipants();
		assertIterableEquals(participants, res);
	}
	
	@Test
	public void testFindUser1() {
		
		boolean noexc = false;
		boolean exc = false;
		
				
		try {
			
			User res = testService.findUser(users.get(0).getId());
			assertEquals(users.get(0), res);
			noexc = true;
		}
		catch (NoSuchUserException e) {

			noexc = false;
		}
		
		try {
			
			testService.findUser(users.get(0).getId() + 999);
		}
		catch (NoSuchUserException e) {
			
			exc = true;
		}
		
		assertTrue(noexc);
		assertTrue(exc);
	}
	
	@Test
	public void testFindUser2() {
		
		boolean noexc = false;
		boolean exc = false;
		
		try {
			
			User res = testService.findUser(users.get(0).getUserName());
			assertEquals(users.get(0), res);
			noexc = true;
		}
		catch (NoSuchUserException e) {
			
			noexc = false;
		}
		
		try {
			
			testService.findUser("xxxx");
		}
		catch (NoSuchUserException e) {
			
			exc = true;
		}
		
		assertTrue(noexc);
		assertTrue(exc);
	}
}




























