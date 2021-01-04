package ch.usi.hse.services;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;

import ch.usi.hse.db.entities.DocCollection;
import ch.usi.hse.db.entities.Experiment;
import ch.usi.hse.db.entities.Experimenter;
import ch.usi.hse.db.entities.Participant;
import ch.usi.hse.db.entities.SessionEvent;
import ch.usi.hse.db.entities.TestGroup;
import ch.usi.hse.db.repositories.DocCollectionRepository;
import ch.usi.hse.db.repositories.ExperimentRepository;
import ch.usi.hse.db.repositories.ExperimenterRepository;
import ch.usi.hse.db.repositories.ParticipantRepository;
import ch.usi.hse.db.repositories.TestGroupRepository;
import ch.usi.hse.exceptions.ConfigParseException;
import ch.usi.hse.exceptions.ExperimentExistsException;
import ch.usi.hse.exceptions.ExperimentStatusException;
import ch.usi.hse.exceptions.FileDeleteException;
import ch.usi.hse.exceptions.FileReadException;
import ch.usi.hse.exceptions.FileWriteException;
import ch.usi.hse.exceptions.NoSuchDocCollectionException;
import ch.usi.hse.exceptions.NoSuchExperimentException;
import ch.usi.hse.exceptions.NoSuchFileException;
import ch.usi.hse.exceptions.NoSuchTestGroupException;
import ch.usi.hse.exceptions.NoSuchUserException;
import ch.usi.hse.exceptions.TestGroupExistsException;
import ch.usi.hse.exceptions.UserExistsException;
import ch.usi.hse.experiments.EventDataExtractor;
import ch.usi.hse.experiments.ExperimentConfigurer;
import ch.usi.hse.experiments.ExperimentSummary;
import ch.usi.hse.experiments.ResultWriter;
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
	private SimpMessagingTemplate simpMessagingTemplate;
	private ResultWriter resultWriter;
	private EventDataExtractor dataExtractor;
	
	@Autowired
	public ExperimentService(ExperimentRepository experimentRepo,
							 TestGroupRepository testGroupRepo,
							 ParticipantRepository participantRepo,
							 DocCollectionRepository collectionRepo,
							 ExperimenterRepository experimenterRepo,
							 ExperimentConfigurer experimentConfigurer,
							 @Qualifier("ExperimentConfigStorage")
							 ExperimentConfigStorage experimentConfigStorage,
							 SimpMessagingTemplate simpMessagingTemplate,
							 ResultWriter resultWriter,
							 EventDataExtractor dataExtractor) {
		
		this.experimentRepo = experimentRepo;
		this.testGroupRepo = testGroupRepo;
		this.participantRepo = participantRepo;
		this.collectionRepo = collectionRepo;
		this.experimenterRepo = experimenterRepo;
		this.experimentConfigurer = experimentConfigurer;
		this.experimentConfigStorage = experimentConfigStorage;
		this.simpMessagingTemplate = simpMessagingTemplate;
		this.resultWriter = resultWriter;
		this.dataExtractor = dataExtractor;
	}
	
	// GENERAL DB OPERATIONS
	
	/**
	 * returns all saved experiments
	 * 
	 * @return
	 */
	public List<Experiment> allExperiments() {
		
		return experimentRepo.findAll();
	}
	
	/**
	 * retrieve an experiment by id
	 * 
	 * @param id
	 * @return Experiment
	 * @throws NoSuchExperimentException
	 */
	public Experiment findExperiment(int id) throws NoSuchExperimentException {
		
		if (! experimentRepo.existsById(id)) {
			throw new NoSuchExperimentException(id);
		}
		
		return experimentRepo.findById(id);
	}
	
	/**
	 * retrieve a TestGroup by id
	 * 
	 * @param id
	 * @return TestGroup
	 * @throws NoSuchTestGroupException 
	 */
	public TestGroup findTestGroup(int id) throws NoSuchTestGroupException {
		
		if (! testGroupRepo.existsById(id)) {
			throw new NoSuchTestGroupException(id);
		}
		
		return testGroupRepo.findById(id);
	}
	
	/**
	 * returns all experiments belonging to the given experimenter
	 * 
	 * @param experimenter
	 * @return
	 * @throws NoSuchUserException
	 */
	public List<Experiment> findByExperimenter(Experimenter experimenter) throws NoSuchUserException {
		
		if (! experimenterRepo.existsById(experimenter.getId())) {
			throw new NoSuchUserException("Experimenter", experimenter.getId());
		}
		
		return experimentRepo.findByExperimenter(experimenter);
	}
	
	/**
	 * save a new Experiment
	 * 
	 * @param experiment
	 * @return
	 * @throws ExperimentExistsException
	 * @throws NoSuchUserException
	 */
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
		}
		
		experiment.setDateCreated(LocalDateTime.now());
		checkReadyStatus(experiment);
			
		Experimenter experimenter = experimenterRepo.findById(experimenterId);
		experiment.setExperimenter(experimenter);
		Experiment saved = experimentRepo.save(experiment);
			
		return saved;
	}
	
	/**
	 * Update an existing Experiment.
	 * Warning: TestGroups must to be updated separately 
	 * by calling updateTestGroup(Testgroup), addTestGroup(TestGroup)
	 * or removeTestGroup(TestGroup)
	 * 
	 * @param experiment
	 * @return
	 * @throws NoSuchExperimentException
	 * @throws ExperimentExistsException
	 * @throws NoSuchUserException
	 * @throws UserExistsException
	 */
	public Experiment updateExperiment(Experiment experiment) 
			throws NoSuchExperimentException, 
				   ExperimentExistsException, 
				   NoSuchUserException {
		
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
		
		found.setTitle(title);
		found.setStatus(experiment.getStatus());
		found.setDateCreated(experiment.getDateCreated());
		found.setDateConducted(experiment.getDateConducted());
		found.setStartTime(experiment.getStartTime());
		found.setEndTime(experiment.getEndTime());

		checkReadyStatus(found);
	
		Experiment updated = experimentRepo.save(found);
		
		return updated;
	}
	
	/**
	 * delete an existing Experiment.
	 * all associated TestGroups and Participants will be deleted
	 * 
	 * @param experiment
	 * @throws NoSuchExperimentException
	 */
	public void deleteExperiment(Experiment experiment) 
			throws NoSuchExperimentException {
		
		int experimentId = experiment.getId();
		
		if (! experimentRepo.existsById(experimentId)) {
			throw new NoSuchExperimentException(experiment.getId());
		}
		
		int experimenterId = experiment.getExperimenterId();
		Experiment found = experimentRepo.findById(experimentId);
		
		if (experimenterRepo.existsById(experimenterId)) {
					
			Experimenter experimenter = experimenterRepo.findById(experimenterId);
			experimenter.removeExperiment(found);	
			experimenterRepo.save(experimenter);
		}

		experimentRepo.delete(found);
	}
	
	// TEST GROUP CONFIGURATION
	
	/**
	 * add a new TestGroup to the Experiment
	 * specified in the TestGroups experimentId field
	 * 
	 * @param testGroup
	 * @return
	 * @throws NoSuchExperimentException 
	 * @throws TestGroupExistsException 
	 */
	public TestGroup addTestGroup(TestGroup testGroup) 
			throws NoSuchExperimentException, TestGroupExistsException {
		
		int experimentId = testGroup.getExperimentId();
		
		if (! experimentRepo.existsById(experimentId)) {
			throw new NoSuchExperimentException(experimentId);
		}
		
		if (testGroupRepo.existsByNameAndExperimentId(testGroup.getName(), experimentId)) {
			throw new TestGroupExistsException(testGroup.getName());
		}
		
		Experiment experiment = experimentRepo.findById(experimentId);
		experiment.addTestGroup(testGroup);
		checkReadyStatus(experiment);
		experimentRepo.save(experiment);
		
		return testGroup;
	}
	
	/**
	 * update an existing TestGroup
	 * 
	 * @param testGroup
	 * @return
	 * @throws NoSuchTestGroupException 
	 * @throws NoSuchExperimentException 
	 * @throws NoSuchDocCollectionException 
	 * @throws UserExistsException 
	 */
	public TestGroup updateTestGroup(TestGroup testGroup) 
			throws NoSuchTestGroupException, 
				   NoSuchExperimentException, 
				   NoSuchDocCollectionException, 
				   UserExistsException {
		
		int groupId = testGroup.getId();
		
		if (! testGroupRepo.existsById(groupId)) {
			throw new NoSuchTestGroupException(groupId);
		}
		
		int experimentId = testGroup.getExperimentId();
		
		if (! experimentRepo.existsById(experimentId)) {
			throw new NoSuchExperimentException(experimentId);
		}
		
		for (DocCollection c : testGroup.getDocCollections()) {		
			if (! collectionRepo.existsById(c.getId())) {
				throw new NoSuchDocCollectionException(c.getId());
			}
		}
		
		for (Participant p : testGroup.getParticipants()) {	
			
			if (! participantRepo.existsById(p.getId())) {
			
				if (participantRepo.existsByUserName(p.getUserName())) {
					throw new UserExistsException(p.getUserName());
				}			
			}
			else {
				
				Participant existing = participantRepo.findById(p.getId());
				
				if (! p.getUserName().equals(existing.getUserName()) &&
					participantRepo.existsByUserName(p.getUserName())) {
					
					throw new UserExistsException(p.getUserName());
				}
			}
		}
		
		TestGroup found = testGroupRepo.findById(testGroup.getId());
		found.setName(testGroup.getName());
		found.setDocCollections(testGroup.getDocCollections());
		found.setFirstQueryCollection(testGroup.getFirstQueryCollection());

		found.clearParticipants();		
		testGroupRepo.save(found);
		
		for (Participant p : testGroup.getParticipants()) {
			
			found.addParticipant(p);
		}
		
		TestGroup updated = testGroupRepo.save(found);
		
		Experiment experiment = experimentRepo.findById(experimentId);
		checkReadyStatus(experiment);
		experimentRepo.save(experiment);
		
		return updated;
	}
	
	
	/**
	 * delete an existing TestGroup
	 * warning: all related Participants will be deleted as well
	 * 
	 * @param testGroup
	 * @throws NoSuchExperimentException 
	 * @throws NoSuchTestGroupException 
	 */
	public void deleteTestGroup(TestGroup testGroup) throws NoSuchExperimentException, NoSuchTestGroupException {
		
		int groupId = testGroup.getId();
		int experimentId = testGroup.getExperimentId();
		
		if (! testGroupRepo.existsById(groupId)) {
			throw new NoSuchTestGroupException(groupId);
		}
		
		if (! experimentRepo.existsById(experimentId)) {
			throw new NoSuchExperimentException(experimentId);
		}
		
		Experiment experiment = experimentRepo.findById(experimentId);
		
		experiment.removeTestGroup(testGroup);
		checkReadyStatus(experiment);
		
		experimentRepo.save(experiment);
	}
	
	/**
	 * Set the Experiment's TestGroups based on the given configuration file
	 * 
	 * @param experiment
	 * @param configFileName
	 * @return
	 * @throws NoSuchFileException
	 * @throws FileReadException
	 * @throws ConfigParseException
	 * @throws NoSuchExperimentException
	 */
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
	
	
	
	/**
	 * returns all available DocCollections
	 * 
	 * @return
	 */
	public List<DocCollection> getIndexedDocCollections() {
		
		return collectionRepo.findByIndexed(true);
	}
	
	// CONFIG FILE MANAGEMENT
	
	/**
	 * returns the names of all ailable configuration files
	 * 
	 * @return
	 * @throws FileReadException
	 */
	public List<String> savedConfigFiles() throws FileReadException {
		
		return experimentConfigStorage.listConfigFiles();
	}
	
	/**
	 * add (upload) a new configuration file
	 * 
	 * @param file
	 * @throws FileWriteException
	 */
	public void addConfigFile(MultipartFile file) throws FileWriteException {
		
		experimentConfigStorage.storeConfigFile(file);
	}
	
	/**
	 * remove a configuration file
	 * 
	 * @param fileName
	 * @throws NoSuchFileException
	 * @throws FileDeleteException
	 */
	public void removeConfigFile(String fileName) 
			throws NoSuchFileException, FileDeleteException {
		
		experimentConfigStorage.deleteConfigFile(fileName);
	}
	
	/**
	 * get a configuration file as InputStram (for download)
	 * 
	 * @param fileName
	 * @return
	 * @throws NoSuchFileException
	 * @throws FileReadException
	 */
	public InputStream getConfigFile(String fileName) 
			throws NoSuchFileException, FileReadException {
		
		return experimentConfigStorage.getConfigFileAsStream(fileName);
	}
	

	// EXPERIMENT EXECUTION

	/**
	 * Starts an experiment execution.
	 * Effects: 
	 *    - the related participants are enabled to log in
	 *    - The Experiments DateConducted and StartTime fields are set to current time
	 * 
	 * @param experiment
	 * @return
	 * @throws NoSuchExperimentException
	 * @throws ExperimentStatusException
	 */
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
		ex.setDateConducted(LocalDateTime.now());
		
		Experiment updated = experimentRepo.save(ex);
		
		return updated;
	}
	
	
	/**
	 * Stops an experiment execution.
	 * Effects: 
	 *    - the related participants are disabled from login
	 *    - The Experiments EndTime field is set to current time
	 *    - The Experiments Duration field is set to the difference between startTime and EndTime
	 * 
	 * @param experiment
	 * @return
	 * @throws NoSuchExperimentException
	 * @throws ExperimentStatusException
	 */
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
				p.setLastQuery(null);
				
				if (p.getOnline()) {
					
					p.setOnline(false);
					ex.addUsageEvent(new SessionEvent(p, SessionEvent.Event.LOGOUT));
				}
			}
		}
		
		ex.setStatus(Experiment.Status.COMPLETE);
		ex.setEndTime(LocalDateTime.now());	
		
		Experiment updated = experimentRepo.save(ex);
		
		sendExperimentOverMessage();
		
		return updated;
	}

	public Experiment resetExperiment(Experiment experiment) 
			throws NoSuchExperimentException, ExperimentStatusException {
		
		int id = experiment.getId();
		
		if (! experimentRepo.existsById(id)) {
			throw new NoSuchExperimentException(id);
		}
		
		Experiment ex = experimentRepo.findById(id);
		
		Experiment.Status requiredStatus = Experiment.Status.COMPLETE;
		
		if (! ex.getStatus().equals(requiredStatus)) {
			throw new ExperimentStatusException(requiredStatus, ex.getStatus());
		}	
		
		ex.setStatus(Experiment.Status.READY);
		checkReadyStatus(ex);
		ex.clearUsageEvents();
		
		if (ex.getMode().equals(Experiment.Mode.QUALTRICS)) {
			
			for (TestGroup g : ex.getTestGroups()) {
				
				g.clearParticipants();
				testGroupRepo.save(g);
			}
		}
		
		Experiment updated = experimentRepo.save(ex);
		
		return updated;
	}
	
	
	
	// RESULT DATA PROCESSING / EXPORT
	
	public InputStream rawResultsCsv(Experiment experiment) throws NoSuchExperimentException {
		
		if (! experimentRepo.existsById(experiment.getId())) {
			throw new NoSuchExperimentException(experiment.getId());
		}
		
		return resultWriter.rawDataCsv(experiment);
	}
	
	public InputStream rawResultsJson(Experiment experiment) 
			throws JsonProcessingException, NoSuchExperimentException {
		
		if (! experimentRepo.existsById(experiment.getId())) {
			throw new NoSuchExperimentException(experiment.getId());
		}
		
		return resultWriter.rawDataJson(experiment);
	}
	
	public InputStream summaryJson(Experiment experiment) 
			throws NoSuchExperimentException, JsonProcessingException {
		
		if (! experimentRepo.existsById(experiment.getId())) {
			throw new NoSuchExperimentException(experiment.getId());
		}
		
		return resultWriter.summaryJson(experiment);
	}
	
	public InputStream userHistoriesCsv(int groupId) 
			throws IOException, NoSuchTestGroupException {
		
		if (! testGroupRepo.existsById(groupId)) {
			throw new NoSuchTestGroupException(groupId);
		}
		
		TestGroup testGroup = testGroupRepo.findById(groupId);
		
		return resultWriter.userHistoriesCsv(testGroup);
	}
	
	public InputStream userHistoriesJson(Experiment experiment) 
			throws NoSuchExperimentException, JsonProcessingException {
		
		if (! experimentRepo.existsById(experiment.getId())) {
			throw new NoSuchExperimentException(experiment.getId());
		}
		
		return resultWriter.userHistoriesJson(experiment);
	}
	
	public ExperimentSummary experimentSummary(Experiment experiment) throws ExperimentStatusException {		
		
		Experiment.Status requiredStatus = Experiment.Status.COMPLETE;
		Experiment.Status actualStatus = experiment.getStatus();
		
		if (! actualStatus.equals(requiredStatus)) {
			
			throw new ExperimentStatusException(requiredStatus, actualStatus);
		}
		
		return new ExperimentSummary(experiment, dataExtractor);
	}
	
	// PRIVATE METHODS
	
	private void sendExperimentOverMessage() {
		
		String msg = "experiment_over";
		
		simpMessagingTemplate.convertAndSend("/info", msg);
	}
	
	private void checkReadyStatus(Experiment e) {
		
		if (e.getStatus().equals(Experiment.Status.READY) ||
			e.getStatus().equals(Experiment.Status.NOT_READY) ||
			e.getStatus().equals(Experiment.Status.COMPLETE) ) {
			
			if (e.getTestGroups().isEmpty()) {
				
				e.setStatus(Experiment.Status.NOT_READY);
				return;
			}
	
			for (TestGroup g : e.getTestGroups()) {
				
				if (g.getDocCollections().isEmpty()) {
					
					e.setStatus(Experiment.Status.NOT_READY);
					return;
				}
				
				if (e.getMode().equals(Experiment.Mode.STAND_ALONE)) {
					
					if (g.getParticipants().isEmpty() || g.getDocCollections().isEmpty()) {
						
						e.setStatus(Experiment.Status.NOT_READY);
						return;
					}
				}
			}
			
			if (! e.getStatus().equals(Experiment.Status.COMPLETE)) {
				e.setStatus(Experiment.Status.READY);
			}
		}
	}
}





















