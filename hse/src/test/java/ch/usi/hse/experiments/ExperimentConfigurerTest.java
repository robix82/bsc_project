package ch.usi.hse.experiments;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import ch.usi.hse.db.entities.Experiment;
import ch.usi.hse.db.entities.Participant;
import ch.usi.hse.db.entities.TestGroup;
import ch.usi.hse.db.repositories.ExperimentRepository;
import ch.usi.hse.db.repositories.ParticipantRepository;
import ch.usi.hse.db.repositories.RoleRepository;
import ch.usi.hse.db.repositories.TestGroupRepository;
import ch.usi.hse.exceptions.ConfigParseException;
import ch.usi.hse.exceptions.FileReadException;
import ch.usi.hse.exceptions.NoSuchFileException;
import ch.usi.hse.storage.ExperimentConfigStorage;

public class ExperimentConfigurerTest {

	@Mock
	private ExperimentConfigStorage configStorage;
	
	@Mock
	private ParticipantRepository participantRepo;
	
	@Mock
	private RoleRepository roleRepo;
	
	@Mock
	private TestGroupRepository testGroupRepo;
	
	@Mock 
	private ExperimentRepository experimentRepo;
	
	@Mock
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	private ExperimentConfigurer configurer;
	
	private String existingFileName, nonExistingFileName;	
	private List<String> configLines;
	private List<TestGroup> testGroups;
	private Set<Participant> participants1, participants2;
	
	@BeforeEach
	public void setUp() throws NoSuchFileException, FileReadException {
		
		initMocks(this);
		
		configurer = new ExperimentConfigurer(configStorage, 
											  participantRepo, 
											  roleRepo,
											  testGroupRepo, 
											  experimentRepo, 
											  bCryptPasswordEncoder);
		
		existingFileName = "existing.txt";
		nonExistingFileName = "nonExisting.txt";
		
		configLines = new ArrayList<>();
		configLines.add("group: testGroup1");
		configLines.add("user1 pwd1");
		configLines.add("user2 pwd2");
		configLines.add("user3 pwd3");
		configLines.add("group: testGroup2");
		configLines.add("user4 pwd4");
		configLines.add("user5 pwd5");
		
		when(configStorage.getConfigLines(existingFileName)).thenReturn(configLines);
		when(configStorage.getConfigLines(nonExistingFileName)).thenThrow(new NoSuchFileException(nonExistingFileName));
		
		TestGroup testGroup1 = new TestGroup("testGroup1");
		TestGroup testGroup2 = new TestGroup("testGroup2");
		testGroup1.setId(1);
		testGroup2.setId(2);
		
		Participant user1 = new Participant("user1", "pwd1");
		Participant user2 = new Participant("user2", "pwd2");
		Participant user3 = new Participant("user3", "pwd3");
		Participant user4 = new Participant("user4", "pwd4");
		Participant user5 = new Participant("user5", "pwd5");
		user1.setId(1);
		user2.setId(2);
		user3.setId(3);
		user4.setId(4);
		user5.setId(5);
		participants1 = Set.of(user1, user2, user3);
		participants2 = Set.of(user4, user5);
		
		testGroups = List.of(testGroup1, testGroup2);
		
		when(testGroupRepo.save(new TestGroup("testGroup1"))).thenReturn(testGroup1);
		when(testGroupRepo.save(new TestGroup("testGroup2"))).thenReturn(testGroup2);
		when(testGroupRepo.save(testGroup1)).thenReturn(testGroup1);
		when(testGroupRepo.save(testGroup2)).thenReturn(testGroup2);
	
		for (Participant p : participants1) {
			when(participantRepo.save(p)).thenReturn(p);
		}
	
		for (Participant p : participants2) {	
			when(participantRepo.save(p)).thenReturn(p);
		}
	}
	
	@Test
	public void testConfigureTestGroups1() throws Exception {
		
		Experiment experiment = new Experiment("e1");
		
		assertEquals(0, experiment.getTestGroups().size());
		
		Experiment configured = configurer.configureTestGroups(experiment, existingFileName);
		
		Set<TestGroup> groups = configured.getTestGroups();
		assertEquals(2, groups.size());
		
		assertIterableEquals(testGroups, groups);
	}
	
	@Test // non-existing config file
	public void testConfigureTestGroups2() throws Exception {
		
		boolean exc;
		
		Experiment experiment = new Experiment("e1");
		
		try {
			configurer.configureTestGroups(experiment, nonExistingFileName);
			exc = false;
		}
		catch (NoSuchFileException e) {
			
			assertTrue(e.getMessage().contains(nonExistingFileName));
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	@Test // config with duplicate group name
	public void testConfigureTestGroups3() throws Exception {
		
		String badLine = "group: testGroup1";
		configLines.add(badLine);
		Experiment experiment = new Experiment("e1");
		
		boolean exc;
		
		try {
			
			configurer.configureTestGroups(experiment, existingFileName);
			exc = false;
		}
		catch (ConfigParseException e) {
			
			assertTrue(e.getMessage().contains(badLine));
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	@Test // config with duplicate username
	public void testConfigureTestGroups4() throws Exception {
		
		String badLine = "user2 pwd";
		configLines.add(badLine);
		Experiment experiment = new Experiment("e1");
		
		boolean exc;
		
		try {
			
			configurer.configureTestGroups(experiment, existingFileName);
			exc = false;
		}
		catch (ConfigParseException e) {
			
			assertTrue(e.getMessage().contains(badLine));
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	@Test // config with missing group name
	public void testConfigureTestGroups5() throws Exception {
		
		String badLine = "group:";
		configLines.add(badLine);
		Experiment experiment = new Experiment("e1");
		
		boolean exc;
		
		try {
			
			configurer.configureTestGroups(experiment, existingFileName);
			exc = false;
		}
		catch (ConfigParseException e) {
			
			assertTrue(e.getMessage().contains(badLine));
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	@Test // config with missing user line word
	public void testConfigureTestGroups6() throws Exception {
		
		String badLine = "user9";
		configLines.add(badLine);
		Experiment experiment = new Experiment("e1");
		
		boolean exc;
		
		try {
			
			configurer.configureTestGroups(experiment, existingFileName);
			exc = false;
		}
		catch (ConfigParseException e) {
			
			assertTrue(e.getMessage().contains(badLine));
			exc = true;
		}
		
		assertTrue(exc);
	}
}











