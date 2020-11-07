package ch.usi.hse.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.usi.hse.db.entities.Experiment;
import ch.usi.hse.db.entities.Experimenter;
import ch.usi.hse.db.entities.Participant;
import ch.usi.hse.db.entities.TestGroup;
import ch.usi.hse.db.repositories.DocCollectionRepository;
import ch.usi.hse.db.repositories.ExperimentRepository;
import ch.usi.hse.db.repositories.ExperimenterRepository;
import ch.usi.hse.db.repositories.ParticipantRepository;
import ch.usi.hse.db.repositories.TestGroupRepository;
import ch.usi.hse.exceptions.ExperimentExistsException;
import ch.usi.hse.exceptions.NoSuchExperimentException;
import ch.usi.hse.exceptions.NoSuchUserException;
import ch.usi.hse.exceptions.UserExistsException;

@Service
public class ExperimentService {

	private ExperimentRepository experimentRepo;
//	private TestGroupRepository testGroupRepo;
//	private ParticipantRepository participantRepo;
//	private DocCollectionRepository collectionRepo;
	private ExperimenterRepository experimenterRepo;
	private UserService userService;
	
	@Autowired
	public ExperimentService(ExperimentRepository experimentRepo,
							 TestGroupRepository testGroupRepo,
							 ParticipantRepository participantRepo,
							 DocCollectionRepository collectionRepo,
							 ExperimenterRepository experimenterRepo,
							 UserService userService) {
		
		this.experimentRepo = experimentRepo;
//		this.testGroupRepo = testGroupRepo;
//		this.participantRepo = participantRepo;
//		this.collectionRepo = collectionRepo;
		this.experimenterRepo = experimenterRepo;
		this.userService = userService;
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
	
	public List<Experimenter> allExperimenters() {
		
		return experimenterRepo.findAll();
	}
	
	public List<Experiment> findByExperimenter(Experimenter experimenter) throws NoSuchUserException {
		
		if (! experimenterRepo.existsById(experimenter.getId())) {
			throw new NoSuchUserException("Experimenter", experimenter.getId());
		}
		
		return experimentRepo.findByExperimenter(experimenter);
	}
	
	public Experiment addExperiment(Experiment experiment) 
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
		found.setDateConducted(experiment.getDateConducted());
		
		Experiment updated = experimentRepo.save(found);
		
		return updated;
	}
	
	public void removeExperiment(Experiment experiment) 
			throws NoSuchExperimentException {
		
		if (! experimentRepo.existsById(experiment.getId())) {
			throw new NoSuchExperimentException(experiment.getId());
		}
		
		experimentRepo.delete(experiment);
	}
	
	// EXPERIMENT SETUP
	
	public List<Participant> createParticipants(List<Participant> participants) 
			throws UserExistsException {
		
		List<Participant> saved = new ArrayList<>();
		
		for (Participant p : participants) {
			
			p.setActive(false);
			saved.add(userService.addParticipant(p));
		}
		
		return saved;
	}
	
	public Experiment addTestGroup(int experimentId, TestGroup testGroup) 
			throws NoSuchExperimentException {
		
		if (! experimentRepo.existsById(experimentId)) {
			throw new NoSuchExperimentException(experimentId);
		}
		
		Experiment experiment = experimentRepo.findById(experimentId);
		
		experiment.addTestGroup(testGroup);
		
		Experiment updated = experimentRepo.save(experiment);
		
		return updated;
 	}
}








