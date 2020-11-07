package ch.usi.hse.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.usi.hse.db.repositories.DocCollectionRepository;
import ch.usi.hse.db.repositories.ExperimentRepository;
import ch.usi.hse.db.repositories.ExperimenterRepository;
import ch.usi.hse.db.repositories.ParticipantRepository;
import ch.usi.hse.db.repositories.TestGroupRepository;

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
}








