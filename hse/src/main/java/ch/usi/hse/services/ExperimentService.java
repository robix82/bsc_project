package ch.usi.hse.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.usi.hse.db.entities.Experiment;
import ch.usi.hse.db.entities.Experimenter;
import ch.usi.hse.db.repositories.DocCollectionRepository;
import ch.usi.hse.db.repositories.ExperimentRepository;
import ch.usi.hse.db.repositories.ExperimenterRepository;
import ch.usi.hse.db.repositories.ParticipantRepository;
import ch.usi.hse.db.repositories.TestGroupRepository;
import ch.usi.hse.exceptions.NoSuchExperimentException;
import ch.usi.hse.exceptions.NoSuchUserException;

@Service
public class ExperimentService {

	private ExperimentRepository experimentRepo;
	private TestGroupRepository testGroupRepo;
	private ParticipantRepository participantRepo;
	private DocCollectionRepository collectionRepo;
	private ExperimenterRepository experimenterRepo;
	
	@Autowired
	public ExperimentService(ExperimentRepository experimentRepo,
							 TestGroupRepository testGroupRepo,
							 ParticipantRepository participantRepo,
							 DocCollectionRepository collectionRepo,
							 ExperimenterRepository experimenterRepo) {
		
		this.experimentRepo = experimentRepo;
		this.testGroupRepo = testGroupRepo;
		this.participantRepo = participantRepo;
		this.collectionRepo = collectionRepo;
		this.experimenterRepo = experimenterRepo;
	}
	
	public List<Experiment> findAll() {
		
		return experimentRepo.findAll();
	}
	
	public Experiment find(int id) throws NoSuchExperimentException {
		
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
	
	public Experiment addExperiment(Experiment experiment) {
		
		Experiment saved = experimentRepo.save(experiment);
		
		return saved;
	}
}








