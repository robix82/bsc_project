package ch.usi.hse.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.usi.hse.db.entities.Experiment;
import ch.usi.hse.db.entities.Experimenter;
import ch.usi.hse.db.entities.TestGroup;
import ch.usi.hse.db.repositories.DocCollectionRepository;
import ch.usi.hse.db.repositories.ExperimentRepository;
import ch.usi.hse.db.repositories.ExperimenterRepository;
import ch.usi.hse.db.repositories.ParticipantRepository;
import ch.usi.hse.db.repositories.TestGroupRepository;
import ch.usi.hse.exceptions.ExperimentExistsException;
import ch.usi.hse.exceptions.NoSuchExperimentException;
import ch.usi.hse.exceptions.NoSuchTestGroupException;
import ch.usi.hse.exceptions.NoSuchUserException;


@Service
public class ExperimentService {

	private ExperimentRepository experimentRepo;
	private TestGroupRepository testGroupRepo;
//	private ParticipantRepository participantRepo;
//	private DocCollectionRepository collectionRepo;
	private ExperimenterRepository experimenterRepo;
	
	@Autowired
	public ExperimentService(ExperimentRepository experimentRepo,
							 TestGroupRepository testGroupRepo,
							 ParticipantRepository participantRepo,
							 DocCollectionRepository collectionRepo,
							 ExperimenterRepository experimenterRepo) {
		
		this.experimentRepo = experimentRepo;
		this.testGroupRepo = testGroupRepo;
//		this.participantRepo = participantRepo;
//		this.collectionRepo = collectionRepo;
		this.experimenterRepo = experimenterRepo;
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
	
	public Experiment addExperiment(Experiment experiment) // TODO: use config file instead
			throws ExperimentExistsException {
		
		int id = experiment.getId();
		String title = experiment.getTitle();
		
		if (experimentRepo.existsById(id)) {
			throw new ExperimentExistsException(id);
		}
		
		if (experimentRepo.existsByTitle(title)) {
			throw new ExperimentExistsException(title);
		}
		
		experiment.setDateCreated(LocalDateTime.now());
		
		Experiment saved = experimentRepo.save(experiment);
		
		return saved;
	}
	
	public Experiment updateExperiment(Experiment experiment) 
			throws NoSuchExperimentException, 
				   ExperimentExistsException, 
				   NoSuchUserException, 
				   NoSuchTestGroupException {
		
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
		found.setDateConducted(experiment.getDateConducted());
		found.setStartTime(experiment.getStartTime());
		found.setEndTime(experiment.getEndTime());
		
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
	
	// TODO: unit tests from here
	
	// EXPERIMENT SETUP
	/*
	public List<Participant> createParticipants(List<Participant> participants) 
			throws UserExistsException {
		
		List<Participant> saved = new ArrayList<>();
		
		for (Participant p : participants) {
			
			p.setActive(false);
			saved.add(userService.addParticipant(p));
		}
		
		return saved;
	}
	*/
	
	public TestGroup updateTestGroup(TestGroup testGroup) 
			throws NoSuchTestGroupException,
				   NoSuchUserException {
		
		int groupId = testGroup.getId();
		
		if (! testGroupRepo.existsById(groupId)) {
			throw new NoSuchTestGroupException(groupId);
		}
		
		TestGroup found = testGroupRepo.findById(groupId);
		
		// TODO: update fields, check participants
		
		return found;
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
}





















