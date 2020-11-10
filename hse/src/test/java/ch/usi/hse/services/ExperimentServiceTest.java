package ch.usi.hse.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import ch.usi.hse.db.entities.Experiment;
import ch.usi.hse.db.entities.Experimenter;
import ch.usi.hse.db.repositories.DocCollectionRepository;
import ch.usi.hse.db.repositories.ExperimentRepository;
import ch.usi.hse.db.repositories.ExperimenterRepository;
import ch.usi.hse.db.repositories.ParticipantRepository;
import ch.usi.hse.db.repositories.TestGroupRepository;
import ch.usi.hse.exceptions.ExperimentExistsException;
import ch.usi.hse.exceptions.NoSuchExperimentException;
import ch.usi.hse.exceptions.NoSuchUserException;
import ch.usi.hse.experiments.ExperimentConfigurer;
import ch.usi.hse.storage.ExperimentConfigStorage;

public class ExperimentServiceTest {

	@Mock
	private ExperimentRepository experimentRepo;
	
	@Mock
	private TestGroupRepository testGroupRepo;
	
	@Mock
	private ParticipantRepository participantRepo;
	
	@Mock
	private DocCollectionRepository collectionRepo;
	
	@Mock
	private ExperimenterRepository experimenterRepo;
	
	@Mock
	private ExperimentConfigurer experimentConfigurer;
	
	@Mock
	private ExperimentConfigStorage experimentConfigStorage;
	
	private ExperimentService service;
	
	private List<Experiment> savedExperiments;
	private List<Experimenter> savedExperimenters;
	private Experiment newExperiment;
	
	@BeforeEach
	public void setUp() {
		
		initMocks(this);
		
		service = new ExperimentService(experimentRepo,
										testGroupRepo,
										participantRepo,
										collectionRepo,
										experimenterRepo,
										experimentConfigurer,
										experimentConfigStorage);
		
		// EXPERIMENTERS
		
		Experimenter experimenter1 = new Experimenter("experimenter1", "pwd");
		Experimenter experimenter2 = new Experimenter("experimenter2", "pwd");
		experimenter1.setId(1);
		experimenter2.setId(2);
		savedExperimenters = List.of(experimenter1, experimenter2); 
		
		when(experimenterRepo.existsById(anyInt())).thenReturn(false);
		when(experimenterRepo.existsByUserName(anyString())).thenReturn(false);
		when(experimenterRepo.findAll()).thenReturn(savedExperimenters);
		
		for (Experimenter e : savedExperimenters) {
			
			when(experimenterRepo.existsById(e.getId())).thenReturn(true);
			when(experimenterRepo.existsByUserName(e.getUserName())).thenReturn(true);
			when(experimenterRepo.findById(e.getId())).thenReturn(e);
			when(experimenterRepo.findByUserName(e.getUserName())).thenReturn(e);
			when(experimenterRepo.save(e)).thenReturn(e);
		}
		
		// EXPERIMENTS
		
		Experiment e1 = new Experiment("e1");
		Experiment e2 = new Experiment("e2");
		Experiment e3 = new Experiment("e3");
		Experiment e4 = new Experiment("e4");
		e1.setId(1);
		e2.setId(2);
		e3.setId(3);
		e4.setId(4);
		experimenter1.addExperiment(e1);
		experimenter1.addExperiment(e2);
		experimenter2.addExperiment(e3);
		experimenter2.addExperiment(e4);
		savedExperiments = List.of(e1, e2, e3, e4);
		newExperiment = new Experiment("e5");
		newExperiment.setId(5);
		
		when(experimentRepo.existsById(anyInt())).thenReturn(false);
		when(experimentRepo.existsByTitle(anyString())).thenReturn(false);
		when(experimentRepo.findAll()).thenReturn(savedExperiments);
		
		for (Experiment e : savedExperiments) {
			
			when(experimentRepo.existsById(e.getId())).thenReturn(true);
			when(experimentRepo.existsByTitle(e.getTitle())).thenReturn(true);
			when(experimentRepo.findById(e.getId())).thenReturn(e);
			when(experimentRepo.save(e)).thenReturn(e);
		}
		
		when(experimentRepo.findByExperimenter(experimenter1)).thenReturn(List.of(e1, e2));
		when(experimentRepo.findByExperimenter(experimenter2)).thenReturn(List.of(e3, e4));
		when(experimentRepo.save(newExperiment)).thenReturn(newExperiment);
	}
	
	@Test
	public void testAllExperiments() {
		
		List<Experiment> res = service.allExperiments();
		
		assertIterableEquals(savedExperiments, res);
	}
	
	@Test
	public void testFindExperiment1() throws NoSuchExperimentException {
		
		Experiment e = savedExperiments.get(0);
		
		Experiment found = service.findExperiment(e.getId());
		assertEquals(e, found);
	}
	
	@Test
	public void testFindExperiment2() {
		
		boolean exc;
		int badId = 999999;
		
		try {
			service.findExperiment(badId);
			exc = false;
		}
		catch (NoSuchExperimentException e) {
			
			assertTrue(e.getMessage().contains(Integer.toString(badId)));
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	@Test
	public void testFindByExperimenter1() throws NoSuchUserException {
		
		Experimenter e = savedExperimenters.get(0);
		
		List<Experiment> res = service.findByExperimenter(e);
		
		assertIterableEquals(e.getExperiments(), res);
	}
	
	@Test
	public void testFindByExperimenter2() {
		
		boolean exc;
		Experimenter e = new Experimenter("e", "pwd");
		int badId = 99999;
		e.setId(badId);
		
		try {
			service.findByExperimenter(e);
			exc = false;
		}
		catch (NoSuchUserException ex) {
			
			assertTrue(ex.getMessage().contains("Experimenter"));
			assertTrue(ex.getMessage().contains(Integer.toString(badId)));
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	@Test
	public void testAddExperiment1() throws ExperimentExistsException, NoSuchUserException {
		
		Experiment saved = service.addExperiment(newExperiment);
		
		assertEquals(saved, newExperiment);
	}
	
	@Test
	public void tesstAddExperiment2() throws NoSuchUserException {
		
		boolean exc;
		int existingId = savedExperiments.get(0).getId();
		newExperiment.setId(existingId);
		
		try {
			
			service.addExperiment(newExperiment);
			exc = false;
		}
		catch (ExperimentExistsException e) {
			
			assertTrue(e.getMessage().contains(Integer.toString(existingId)));
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	@Test
	public void testAddExperiment3() throws NoSuchUserException {
		
		boolean exc;
		String existingTitle= savedExperiments.get(0).getTitle();
		newExperiment.setTitle(existingTitle);
		
		try {
			
			service.addExperiment(newExperiment);
			exc = false;
		}
		catch (ExperimentExistsException e) {
			
			assertTrue(e.getMessage().contains(existingTitle));
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	@Test
	public void testUpdateExperiment1() throws Exception {
		
		Experiment experiment = savedExperiments.get(0);
		
		String newTitle = "newTitle";
		Experimenter newExperimenter = savedExperimenters.get(1);
		experiment.setTitle(newTitle);
		experiment.setExperimenter(newExperimenter);
		
		Experiment updated = service.updateExperiment(experiment);
		
		assertEquals(experiment, updated);
	}
	
	@Test // non-existing experiment id
	public void testUpdateExperiment2() throws Exception {
		
		int badId = 999999;
		Experiment experiment = savedExperiments.get(0);
		experiment.setId(badId);
				
		boolean exc;
		
		try {
			service.updateExperiment(experiment);
			exc = false;
		}
		catch (NoSuchExperimentException e) {
			
			assertTrue(e.getMessage().contains(Integer.toString(badId)));
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	@Test // new title already exists
	public void testUpdateExperiment3() throws Exception {
		
		int id = savedExperiments.get(0).getId();
		String usedTitle = savedExperiments.get(1).getTitle();
		Experiment experiment = new Experiment(usedTitle);
		experiment.setId(id);
		
		boolean exc;
		
		try {
			service.updateExperiment(experiment);
			exc = false;
		}
		catch (ExperimentExistsException e) {
			
			assertTrue(e.getMessage().contains(usedTitle));
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	@Test // non-existing experimenter id
	public void testUpdateExperiment4() throws Exception {
		
		int badId = 999999;
		int existingExperimentId = savedExperiments.get(0).getId();
		String existingTitle = savedExperiments.get(0).getTitle();
		Experiment experiment = new Experiment(existingTitle);
		experiment.setId(existingExperimentId);
		experiment.setExperimenterId(badId);
		
		boolean exc;
		
		try {
			service.updateExperiment(experiment);
			exc = false;
		}
		catch (NoSuchUserException e) {
			
			assertTrue(e.getMessage().contains("Experimenter"));
			assertTrue(e.getMessage().contains(Integer.toString(badId)));
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	@Test // non-existing experimenter name
	public void testUpdateExperiment5() throws Exception {
		
		int experimentId = savedExperiments.get(0).getId();
		int experimenterId = savedExperimenters.get(1).getId();
		String experimentTitle = savedExperiments.get(0).getTitle();
		String badName = "badName";
		Experiment experiment = new Experiment(experimentTitle);
		experiment.setId(experimentId);
		experiment.setExperimenterId(experimenterId);
		experiment.setExperimenterName(badName);
		
		boolean exc;
		
		try {
			service.updateExperiment(experiment);
			exc = false;
		}
		catch (NoSuchUserException e) {
			
			assertTrue(e.getMessage().contains("Experimenter"));
			assertTrue(e.getMessage().contains(badName));
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	@Test
	public void testDeleteExperiment1() {
		
		boolean exc;
		
		try {
			service.deleteExperiment(savedExperiments.get(0));
			exc = false;
		}
		catch (Exception e) {
			exc = true;
		}
		
		assertFalse(exc);
	}
	
	@Test
	public void testDeleteExperiment2() {
		
		int badId = 99999;
		Experiment experiment = savedExperiments.get(0);
		experiment.setId(badId);
		
		boolean exc;
		
		try {
			service.deleteExperiment(experiment);
			exc = false;
		}
		catch (NoSuchExperimentException e) {
			
			assertTrue(e.getMessage().contains(Integer.toString(badId)));
			exc = true;
		}
		
		assertTrue(exc);
	}
}






















