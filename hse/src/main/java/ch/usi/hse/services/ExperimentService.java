package ch.usi.hse.services;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ch.usi.hse.db.entities.DocCollection;
import ch.usi.hse.db.entities.Experiment;
import ch.usi.hse.db.entities.Experimenter;
import ch.usi.hse.db.entities.Participant;
import ch.usi.hse.db.entities.TestGroup;
import ch.usi.hse.db.repositories.DocCollectionRepository;
import ch.usi.hse.db.repositories.ExperimentRepository;
import ch.usi.hse.db.repositories.ExperimenterRepository;
import ch.usi.hse.db.repositories.ParticipantRepository;
import ch.usi.hse.db.repositories.TestGroupRepository;
import ch.usi.hse.exceptions.ConfigParseException;
import ch.usi.hse.exceptions.ExperimentExistsException;
import ch.usi.hse.exceptions.FileDeleteException;
import ch.usi.hse.exceptions.FileReadException;
import ch.usi.hse.exceptions.FileWriteException;
import ch.usi.hse.exceptions.NoSuchDocCollectionException;
import ch.usi.hse.exceptions.NoSuchExperimentException;
import ch.usi.hse.exceptions.NoSuchFileException;
import ch.usi.hse.exceptions.NoSuchTestGroupException;
import ch.usi.hse.exceptions.NoSuchUserException;
import ch.usi.hse.exceptions.UserExistsException;
import ch.usi.hse.experiments.ExperimentConfigurer;
import ch.usi.hse.storage.ExperimentConfigStorage;


@Service
public class ExperimentService {

	private ExperimentRepository experimentRepo;
	private TestGroupRepository testGroupRepo;
	private ParticipantRepository participantRepo;
	private DocCollectionRepository collectionRepo;
	private ExperimenterRepository experimenterRepo;
	ExperimentConfigurer experimentConfigurer;
	ExperimentConfigStorage experimentConfigStorage;
	
	@Autowired
	public ExperimentService(ExperimentRepository experimentRepo,
							 TestGroupRepository testGroupRepo,
							 ParticipantRepository participantRepo,
							 DocCollectionRepository collectionRepo,
							 ExperimenterRepository experimenterRepo,
							 ExperimentConfigurer experimentConfigurer,
							 @Qualifier("ExperimentConfigStorage")
							 ExperimentConfigStorage experimentConfigStorage) {
		
		this.experimentRepo = experimentRepo;
		this.testGroupRepo = testGroupRepo;
		this.participantRepo = participantRepo;
		this.collectionRepo = collectionRepo;
		this.experimenterRepo = experimenterRepo;
		this.experimentConfigurer = experimentConfigurer;
		this.experimentConfigStorage = experimentConfigStorage;
	}
	
	// GENERAL DB OPERATIONS
	
	public List<Experiment> allExperiments() {
		
		return experimentRepo.findAll();
	}
	
	public Experiment findExperiment(int id) throws NoSuchExperimentException {
		
		if (! experimentRepo.existsById(id)) {
			throw new NoSuchExperimentException(id);
		}
		
		return experimentRepo.findById(id);
	}
	
	public List<Experiment> findByExperimenter(Experimenter experimenter) throws NoSuchUserException {
		
		if (! experimenterRepo.existsById(experimenter.getId())) {
			throw new NoSuchUserException("Experimenter", experimenter.getId());
		}
		
		return experimentRepo.findByExperimenter(experimenter);
	}
	
	public Experiment addExperiment(Experiment experiment)
			throws ExperimentExistsException, NoSuchUserException {
		
		int id = experiment.getId();
		String title = experiment.getTitle();
		
		if (experimentRepo.existsById(id)) {
			throw new ExperimentExistsException(id);
		}
		
		if (experimentRepo.existsByTitle(title)) {
			throw new ExperimentExistsException(title);
		}
		
		int experimenterId = experiment.getExperimenterId();
		
		if (experimenterId != 0) {
			
			if (! experimenterRepo.existsById(experimenterId)) {
				throw new NoSuchUserException("Experimenter", experimenterId);
			}
			
			experiment.setExperimenter(experimenterRepo.findById(experimenterId));
		}
		
		experiment.setDateCreated(LocalDateTime.now());
		
		Experiment saved = experimentRepo.save(experiment);
		
		return saved;
	}
	
	public Experiment updateExperiment(Experiment experiment) 
			throws NoSuchExperimentException, 
				   ExperimentExistsException, 
				   NoSuchUserException, 
				   NoSuchTestGroupException, 
				   UserExistsException, 
				   NoSuchDocCollectionException {
		
		int id = experiment.getId();
		
		if (! experimentRepo.existsById(id)) {
			throw new NoSuchExperimentException(id);
		}
		
		Experiment found = experimentRepo.findById(id);
		
		String title = experiment.getTitle();
		
		if (! title.equals(found.getTitle()) && experimentRepo.existsByTitle(title)) {
			throw new ExperimentExistsException(title);
		}
		
		String experimenterName = experiment.getExperimenterName();
		int experimenterId = experiment.getExperimenterId();
		
		if (experimenterId != found.getExperimenterId()) {
			
			if (! experimenterRepo.existsById(experimenterId)) {
				throw new NoSuchUserException("Experimenter", experimenterId);
			}
			
			if (! experimenterRepo.existsByUserName(experimenterName)) {
				throw new NoSuchUserException("Experimenter", experimenterName);
			}
			
			Experimenter experimenter = experimenterRepo.findById(experimenterId);
			
			found.setExperimenter(experimenter);
		}
		
		Set<TestGroup> updatedGroups = new HashSet<>();
		
		for (TestGroup g : experiment.getTestGroups()) {
			
			updatedGroups.add(updateTestGroup(g, experiment));
		}
		
		found.setTestGroups(updatedGroups);
		
		found.setTitle(title);
		found.setStatus(experiment.getStatus());
		found.setDateConducted(experiment.getDateConducted());
		found.setStartTime(experiment.getStartTime());
		found.setEndTime(experiment.getEndTime());
		
		checkReadyStatus(found);
		
		Experiment updated = experimentRepo.save(found);
		
		return updated;
	}
	
	public void deleteExperiment(Experiment experiment) 
			throws NoSuchExperimentException {
		
		if (! experimentRepo.existsById(experiment.getId())) {
			throw new NoSuchExperimentException(experiment.getId());
		}
		
		experimentRepo.delete(experiment);
	}
	
	// EXPERIMENT CONFIGURATION
	
	public Experiment configureTestGroups(Experiment experiment, String configFileName) 
			throws NoSuchFileException, 
				   FileReadException, 
				   ConfigParseException, 
				   NoSuchExperimentException {
		
		if (! experimentRepo.existsById(experiment.getId())) {
			throw new NoSuchExperimentException(experiment.getId());
		}
		
		Experiment configured = experimentConfigurer.configureTestGroups(experiment, configFileName);
		
		checkReadyStatus(configured);
		
		Experiment saved = experimentRepo.save(configured); 
		
		return saved;
	}
	
	public List<DocCollection> getDocCollections() {
		
		return collectionRepo.findAll();
	}
	
	public List<String> savedConfigFiles() throws FileReadException {
		
		return experimentConfigStorage.listConfigFiles();
	}
	
	public void addConfigFile(MultipartFile file) throws FileWriteException {
		
		experimentConfigStorage.storeConfigFile(file);
	}
	
	public void removeConfigFile(String fileName) 
			throws NoSuchFileException, FileDeleteException {
		
		experimentConfigStorage.deleteConfigFile(fileName);
	}
	
	public InputStream getConfigFile(String fileName) 
			throws NoSuchFileException, FileReadException {
		
		return experimentConfigStorage.getConfigFileAsStream(fileName);
	}
	

	// EXPERIMENT EXECUTION
	/*
	public Experiment startExperiment(Experiment experiment) 
			throws NoSuchExperimentException, 
				   ExperimentStatusException {
		
		int id = experiment.getId();
		
		if (! experimentRepo.existsById(id)) {
			throw new NoSuchExperimentException(id);
		}
		
		Experiment ex = experimentRepo.findById(id);
		
		Experiment.Status requiredStatus = Experiment.Status.READY;
		
		if (! ex.getStatus().equals(requiredStatus)) {
			throw new ExperimentStatusException(requiredStatus, ex.getStatus());
		}
		
		for (TestGroup g : ex.getTestGroups()) {
			for (Participant p : g.getParticipants()) {
				
				p.setActive(true);
			}
		}
		
		ex.setStatus(Experiment.Status.RUNNING);
		ex.setStartTime(LocalDateTime.now());	
		
		Experiment updated = experimentRepo.save(ex);
		
		return updated;
	}
	
	public Experiment stopExperiment(Experiment experiment) 
			throws NoSuchExperimentException, 
				   ExperimentStatusException {
		
		int id = experiment.getId();
		
		if (! experimentRepo.existsById(id)) {
			throw new NoSuchExperimentException(id);
		}
		
		Experiment ex = experimentRepo.findById(id);
		
		Experiment.Status requiredStatus = Experiment.Status.RUNNING;
		
		if (! ex.getStatus().equals(requiredStatus)) {
			throw new ExperimentStatusException(requiredStatus, ex.getStatus());
		}
		
		for (TestGroup g : ex.getTestGroups()) {
			for (Participant p : g.getParticipants()) {
				
				p.setActive(false);
			}
		}
		
		ex.setStatus(Experiment.Status.COMPLETE);
		ex.setStartTime(LocalDateTime.now());	
		
		Experiment updated = experimentRepo.save(ex);
		
		return updated;
	}
	*/
	
	private TestGroup updateTestGroup(TestGroup testGroup, Experiment experiment) 
			throws NoSuchTestGroupException,
				   NoSuchUserException, 
				   UserExistsException, 
				   NoSuchDocCollectionException {
		
		int groupId = testGroup.getId();
		
		if (! testGroupRepo.existsById(groupId)) {
			throw new NoSuchTestGroupException(groupId);
		}
		
		TestGroup found = testGroupRepo.findById(groupId);
		
		Set<Participant> newParticipants = testGroup.getParticipants();
		
		for (Participant p : newParticipants) {
			
			int id = p.getId();
			
			if (! participantRepo.existsById(id)) {
				throw new NoSuchUserException("Participant", id);
			}
			
			Participant foundParticipant = participantRepo.findById(id);
			
			String name = p.getUserName();
			
			if (! foundParticipant.getUserName().equals(name) && participantRepo.existsByUserName(name)) {
				throw new UserExistsException(name);
			}
			
			found.setName(name);
		}
		
		Set<DocCollection> newDocCollections = testGroup.getDocCollections();
		
		for (DocCollection c : newDocCollections) {
			
			if (! collectionRepo.existsById(c.getId())) {
				throw new NoSuchDocCollectionException(c.getId());
			}
		}
		
		found.setParticipants(newParticipants);
		found.setDocCollections(newDocCollections);
		found.setExperiment(experiment);
		
		TestGroup updated = testGroupRepo.save(found);
		
		return updated;
	}
	
	private void checkReadyStatus(Experiment e) {
		
		if (e.getTestGroups().isEmpty()) {
			
			e.setStatus(Experiment.Status.NOT_READY);
			return;
		}
		
		for (TestGroup g : e.getTestGroups()) {
			
			if (g.getParticipants().isEmpty() || g.getDocCollections().isEmpty()) {
				
				e.setStatus(Experiment.Status.NOT_READY);
				return;
			}
		}
	}
}





















