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
import ch.usi.hse.exceptions.UserExistsException;

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
	private Set<Role> adminRoles, experimenterRoles, participantRoles;
	private String password, encryptedPassword;
	
	@BeforeEach
	public void setUp() {
		
		initMocks(this);
		
		// create test data
		
		adminRoles = new HashSet<>(); 
		adminRoles.add(new Role(1, "ADMIN"));
		experimenterRoles = new HashSet<>();
		experimenterRoles.add(new Role(2, "EXPERIMENTER"));
		participantRoles = new HashSet<>();
		participantRoles.add(new Role(1, "PARTICICPANT"));
		
		password = "pwd";
		encryptedPassword = "pwd_e";
		
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
		
		// roles
		when(roleRepository.findByRole("ADMIN")).thenReturn(new Role(1, "ADMIN"));
		when(roleRepository.findByRole("EXPERIMENTER")).thenReturn(new Role(2, "EXPERIMENTER"));
		when(roleRepository.findByRole("PARTICICPANT")).thenReturn(new Role(3, "PARTICICPANT"));
		
		// bCrypt
		when(bCryptPasswordEncoder.encode(password)).thenReturn(encryptedPassword);
		
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
		when(administratorRepository.existsById(anyInt())).thenReturn(false);
		when(administratorRepository.existsByUserName(anyString())).thenReturn(false);
		when(experimenterRepository.existsById(anyInt())).thenReturn(false);
		when(experimenterRepository.existsByUserName(anyString())).thenReturn(false);
		when(participantRepository.existsById(anyInt())).thenReturn(false);
		when(participantRepository.existsByUserName(anyString())).thenReturn(false);
		
		for (User u : users) {
			
			when(userRepository.existsById(u.getId())).thenReturn(true);
			when(userRepository.existsByUserName(u.getUserName())).thenReturn(true);
			when(userRepository.findById(u.getId())).thenReturn(u);
			when(userRepository.findByUserName(u.getUserName())).thenReturn(u);
		}
		
		for (Administrator a : administrators) {
			
			when(administratorRepository.existsById(a.getId())).thenReturn(true);
			when(administratorRepository.existsByUserName(a.getUserName())).thenReturn(true);
			when(administratorRepository.findById(a.getId())).thenReturn(a);
			when(administratorRepository.findByUserName(a.getUserName())).thenReturn(a);
		}
		
		for (Experimenter e : experimenters) {
			
			when(experimenterRepository.existsById(e.getId())).thenReturn(true);
			when(experimenterRepository.existsByUserName(e.getUserName())).thenReturn(true);
			when(experimenterRepository.findById(e.getId())).thenReturn(e);
			when(experimenterRepository.findByUserName(e.getUserName())).thenReturn(e);
			
		}
		
		for (Participant p : participants) {
			
			when(participantRepository.existsById(p.getId())).thenReturn(true);
			when(participantRepository.existsByUserName(p.getUserName())).thenReturn(true);
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
		
		boolean noexc, exc;
		User u = users.get(0);
				
		try {
			
			User res = testService.findUser(u.getId());
			assertEquals(u, res);
			noexc = true;
		}
		catch (Exception e) {
			noexc = false;
		}
		
		try { 
			
			testService.findUser(u.getId() + 999);
			exc = false;
		}
		catch (NoSuchUserException e) {	
			exc = true;
		}
		
		assertTrue(noexc);
		assertTrue(exc);
	}
	
	@Test
	public void testFindUser2() {
		
		boolean noexc, exc;
		User u = users.get(0);
		
		try {
			
			User res = testService.findUser(u.getUserName());
			assertEquals(u, res);
			noexc = true;
		}
		catch (Exception e) {
			noexc = false;
		}
		
		try {
			
			testService.findUser("xxxx");
			exc = false;
		}
		catch (NoSuchUserException e) {		
			exc = true;
		}
		
		assertTrue(noexc);
		assertTrue(exc);
	}
	
	@Test
	public void testFindAdministrator1() {
		
		boolean noexc, exc;
		Administrator a = administrators.get(0);
		
		try {
			
			Administrator res = testService.findAdministrator(a.getId());
			assertEquals(a, res);
			noexc = true;
		}
		catch (Exception e) {
			noexc = false;
		}
		
		try {
			
			testService.findAdministrator(experimenters.get(0).getId());
			exc = false;
		}
		catch (NoSuchUserException e) {	
			exc = true;
		}
		
		assertTrue(noexc);
		assertTrue(exc);
	}
	
	@Test
	public void testFindAdministrator2() {
		
		boolean noexc, exc;
		Administrator a = administrators.get(0);
		
		try {
			
			Administrator res = testService.findAdministrator(a.getUserName());
			assertEquals(a, res);
			noexc = true;
		}
		catch (Exception e) {
			noexc = false;
		}
		
		try {
			
			testService.findAdministrator(experimenters.get(0).getUserName());
			exc = false;
		}
		catch (NoSuchUserException e) {
			exc = true;
		}
		
		assertTrue(noexc);
		assertTrue(exc);
	}
	
	@Test
	public void testFindExperimenter1() {
		
		boolean noexc, exc;
		Experimenter e = experimenters.get(0);
		
		try {
			
			Experimenter res = testService.findExperimenter(e.getId());
			assertEquals(e, res);
			noexc = true;
		}
		catch (Exception ex) {
			noexc = false;
		}
		
		try {
			
			testService.findExperimenter(administrators.get(0).getId());
			exc = false;
		}
		catch (NoSuchUserException ex) {
			exc = true;
		}
		
		assertTrue(noexc);
		assertTrue(exc);
	}
	
	@Test
	public void testFindExperimenter2() {
		
		boolean noexc, exc;
		Experimenter e = experimenters.get(0);
		
		try {
			
			Experimenter res = testService.findExperimenter(e.getUserName());
			assertEquals(e, res);
			noexc = true;
		}
		catch (Exception ex) {
			noexc = false;
		}
		
		try {
			
			testService.findExperimenter(administrators.get(0).getUserName());
			exc = false;
		}
		catch (NoSuchUserException ex) {
			exc = true;
		}
		
		assertTrue(noexc);
		assertTrue(exc);
	}
	
	@Test
	public void testFindParticipant1() {
		
		boolean noexc, exc;
		Participant p = participants.get(0);
		
		try {
			
			Participant res = testService.findParticipant(p.getId());
			assertEquals(p, res);
			noexc = true;
		}
		catch (Exception e) {
			noexc = false;
		}
		
		try {
			
			testService.findParticipant(administrators.get(0).getId());
			exc = false;
		}
		catch (NoSuchUserException e) {
			exc = true;
		}
		
		assertTrue(noexc);
		assertTrue(exc);
	}
	
	@Test
	public void testFindParticipant2() {
		
		boolean noexc, exc;
		Participant p = participants.get(0);
		
		try {
			
			Participant res = testService.findParticipant(p.getUserName());
			assertEquals(p, res);
			noexc = true;
		}
		catch (Exception e) {
			noexc = false;
		}
		
		try {
			
			testService.findParticipant(administrators.get(0).getUserName());
			exc = false;
		}
		catch (NoSuchUserException e) {
			exc = true;
		}
		
		assertTrue(noexc);
		assertTrue(exc);
	}
	
	@Test
	public void testAddAdministrator() {
		
		boolean noexc, exc;
		String newName = "newAdmin";
		String existingName = participants.get(0).getUserName();
		Administrator expected = new Administrator(newName, encryptedPassword);
		expected.setRoles(adminRoles);
		when(administratorRepository.save(any(Administrator.class))).thenReturn(expected);
		
		try {
			
			Administrator newAdmin = testService.addAdministrator(new Administrator(newName, encryptedPassword));
			assertEquals(newName, newAdmin.getUserName());
			assertEquals(encryptedPassword, newAdmin.getPassword());
			assertIterableEquals(adminRoles, newAdmin.getRoles());
			noexc = true;
		}
		catch (Exception e) {
			noexc = false;
			System.out.println("EXCEPTION: " + e);
		}
		
		try {
			
			testService.addAdministrator(new Administrator(existingName, encryptedPassword));
			exc = false;
		}
		catch (UserExistsException e) {
			exc = true;
		}
		
		assertTrue(noexc);
		assertTrue(exc);
	}
	
	@Test
	public void testAddExperimenter() {
		
		boolean noexc, exc;
		String newName = "newExperimenter";
		String existingName = participants.get(0).getUserName();
		Experimenter expected = new Experimenter(newName, encryptedPassword);
		expected.setRoles(experimenterRoles);
		when(experimenterRepository.save(any(Experimenter.class))).thenReturn(expected);
		
		try {
			
			Experimenter newExperimenter = testService.addExperimenter(new Experimenter(newName, encryptedPassword));
			assertEquals(newName, newExperimenter.getUserName());
			assertEquals(encryptedPassword, newExperimenter.getPassword());
			assertIterableEquals(experimenterRoles, newExperimenter.getRoles());
			noexc = true;
		}
		catch (Exception e) {
			noexc = false;
		}
		
		try {
			
			testService.addExperimenter(new Experimenter(existingName, encryptedPassword));
			exc = false;
		}
		catch (UserExistsException e) {
			exc = true;
		}
		
		assertTrue(noexc);
		assertTrue(exc);
	}
	
	@Test
	public void testAddParticipant() {
		
		boolean noexc, exc;
		String newName = "newParticipant";
		String existingName = administrators.get(0).getUserName();
		Participant expected = new Participant(newName, encryptedPassword);
		expected.setRoles(participantRoles);
		when(participantRepository.save(any(Participant.class))).thenReturn(expected);
		
		try {
			
			Participant newParticipant = testService.addParticipant(new Participant(newName, encryptedPassword));
			assertEquals(newName, newParticipant.getUserName());
			assertEquals(encryptedPassword, newParticipant.getPassword());
			assertIterableEquals(participantRoles, newParticipant.getRoles());
			noexc = true;
		}
		catch (Exception e) {
			noexc = false;
		}
		
		try {
			
			testService.addParticipant(new Participant(existingName, encryptedPassword));
			exc = false;
		}
		catch (UserExistsException e) {
			exc = true;
		}
		
		assertTrue(noexc);
		assertTrue(exc);
	}
	
	@Test
	public void testUpdateAdministrator() {
		
		boolean noexc = false;
		boolean exc1 = false;
		boolean exc2 = false; 
		String newName = "newAdmin";
		String existingName = participants.get(0).getUserName();
		String newPwd = "newPwd";
		String newEncryptedPwd = "newPwd_e";
		Administrator expected = new Administrator(newName, newEncryptedPwd);
		expected.setRoles(adminRoles);
		when(administratorRepository.save(any(Administrator.class))).thenReturn(expected);
		when(bCryptPasswordEncoder.encode(newPwd)).thenReturn(newEncryptedPwd);
		
		try {
			
			Administrator a = new Administrator(administrators.get(0).getId(), 
												newName, 
												newPwd, 
												adminRoles);
			
			Administrator res = testService.updateAdministrator(a);
			assertEquals(newName, res.getUserName());
			assertEquals(newEncryptedPwd, res.getPassword());
			assertIterableEquals(adminRoles, res.getRoles());
			noexc = true;
		}
		catch (Exception e) {
			noexc = false;
		}
		
		try {
			
			Administrator a = new Administrator(participants.get(0).getId(), 
												newName, 
												newPwd, 
												adminRoles);
			
			testService.updateAdministrator(a);
			exc1 = false; 
		}
		catch (NoSuchUserException e) {
			exc1 = true; 
		}
		catch (Exception ex) {
			noexc = false;
		}
		
		try {
		
			Administrator a = new Administrator(administrators.get(0).getId(), 
												existingName, 
												newPwd, 
												adminRoles);

			a.setUserName(existingName);
			testService.updateAdministrator(a);
			exc2 = false;
		}
		catch (UserExistsException e) {
			exc2 = true;
		}
		catch (Exception ex) {
			noexc = false;
		}
		
		assertTrue(noexc);
		assertTrue(exc1);
		assertTrue(exc2);
	}

	@Test
	public void testUpdateExperimenter() {
		
		boolean noexc = false;
		boolean exc1 = false;
		boolean exc2 = false; 
		String newName = "newExperimenter";
		String existingName = participants.get(0).getUserName();
		String newPwd = "newPwd";
		String newEncryptedPwd = "newPwd_e";
		Experimenter expected = new Experimenter(newName, newEncryptedPwd);
		expected.setRoles(experimenterRoles);
		when(experimenterRepository.save(any(Experimenter.class))).thenReturn(expected);
		when(bCryptPasswordEncoder.encode(newPwd)).thenReturn(newEncryptedPwd);
		
		try {
			
			Experimenter newExp = new Experimenter(experimenters.get(0).getId(),
												   newName,
												   newPwd,
												   experimenterRoles);
			
			Experimenter res = testService.updateExperimenter(newExp);
			assertEquals(newName, res.getUserName());
			assertEquals(newEncryptedPwd, res.getPassword());
			assertIterableEquals(experimenterRoles, res.getRoles());
			noexc = true;
		}
		catch (Exception e) {
			noexc = false;
		}
		
		try {
			
			Experimenter newExp = new Experimenter(administrators.get(0).getId(),
												   newName,
												   newPwd,
												   experimenterRoles);
			
			testService.updateExperimenter(newExp);
			exc1 = false;
		}
		catch (NoSuchUserException e) {
			exc1 = true;
		}
		catch (Exception ex) {
			noexc = false;
		}
		
		try {
			
			Experimenter newExp = new Experimenter(experimenters.get(0).getId(),
												   existingName,
												   newPwd,
												   experimenterRoles);

			testService.updateExperimenter(newExp);
			exc2 = false;
		}
		catch (UserExistsException e) {
			exc2 = true;
		}
		catch (Exception ex) {
			noexc = false;
		}
		
		assertTrue(noexc);
		assertTrue(exc1);
		assertTrue(exc2);
	}
	
	@Test
	public void testUpdateParticipant() {
		
		boolean noexc = false;
		boolean exc1 = false;
		boolean exc2 = false; 
		String newName = "newParticipant";
		String existingName = administrators.get(0).getUserName();
		String newPwd = "newPwd";
		String newEncryptedPwd = "newPwd_e";
		Participant expected = new Participant(newName, newEncryptedPwd);
		expected.setRoles(participantRoles);
		when(participantRepository.save(any(Participant.class))).thenReturn(expected);
		when(bCryptPasswordEncoder.encode(newPwd)).thenReturn(newEncryptedPwd);
		
		try {
			
			Participant newPart = new Participant(participants.get(0).getId(),
												  newName,
												  newPwd,
												  participantRoles);
			
			Participant res = testService.updateParticipant(newPart);
			assertEquals(newName, res.getUserName());
			assertEquals(newEncryptedPwd, res.getPassword());
			assertIterableEquals(participantRoles, res.getRoles());
			noexc = true;
		}
		catch (Exception e) {
			noexc = false;
		}
		
		try {
			
			Participant newPart = new Participant(administrators.get(0).getId(),
												  newName,
												  newPwd,
												  participantRoles);
			
			testService.updateParticipant(newPart);
			exc1 = false;
		}
		catch (NoSuchUserException e) {
			exc1 = true;
		}
		catch (Exception e) {
			noexc = false;
		}
		
		try {
			
			Participant newPart = new Participant(participants.get(0).getId(),
												  existingName,
												  newPwd,
												  participantRoles);
							
			testService.updateParticipant(newPart);
			exc2 = false;
		}
		catch (UserExistsException e) {
			exc2 = true;
		}
		catch (Exception ex) {
			noexc = false;
		}
		
		assertTrue(noexc);
		assertTrue(exc1);
		assertTrue(exc2);
	}
	
	@Test
	public void testRemoveUser1() {
		
		boolean noexc, exc;
		
		try {
			testService.removeUser(administrators.get(0).getId());
			noexc = true;
		}
		catch (Exception e) {
			noexc = false;
		}
		
		try {
			testService.removeUser(administrators.get(0).getId() + 999);
			exc = false;
		}
		catch (NoSuchUserException e) {
			exc = true;
		}
		
		assertTrue(noexc);
		assertTrue(exc);
	}
	
	@Test
	public void testRemoveUser2() {
		
		boolean noexc, exc;
		
		try {
			testService.removeUser(administrators.get(0).getUserName());
			noexc = true;
		}
		catch (Exception e) {
			noexc = false;
		}
		
		try {
			testService.removeUser("xxx");
			exc = false;
		}
		catch (NoSuchUserException e) {
			exc = true;
		}
		
		assertTrue(noexc);
		assertTrue(exc);
	}
}




























