package ch.usi.hse.services;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import ch.usi.hse.db.repositories.DocCollectionRepository;
import ch.usi.hse.db.repositories.ExperimentRepository;
import ch.usi.hse.db.repositories.ExperimenterRepository;
import ch.usi.hse.db.repositories.ParticipantRepository;
import ch.usi.hse.db.repositories.TestGroupRepository;

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
	
	private ExperimentService experimentService;
	
	@BeforeEach
	public void setUp() {
		
		experimentService = new ExperimentService(experimentRepo,
												  testGroupRepo,
												  participantRepo,
												  collectionRepo,
												  experimenterRepo);
	}
	
	@Test
	public void testSetup() {
		
		assertNotNull(experimentService);
	}
}






















