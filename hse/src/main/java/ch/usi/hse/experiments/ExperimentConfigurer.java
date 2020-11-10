package ch.usi.hse.experiments;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import ch.usi.hse.db.entities.Experiment;
import ch.usi.hse.db.entities.Participant;
import ch.usi.hse.db.entities.TestGroup;
import ch.usi.hse.db.repositories.ExperimentRepository;
import ch.usi.hse.db.repositories.ParticipantRepository;
import ch.usi.hse.db.repositories.TestGroupRepository;
import ch.usi.hse.exceptions.ConfigParseException;
import ch.usi.hse.exceptions.FileReadException;
import ch.usi.hse.exceptions.NoSuchFileException;
import ch.usi.hse.storage.ExperimentConfigStorage;

/**
 * File based TestGgroup and Participant configuration
 * 
 * @author robert.jans@usi.ch
 *
 */
@Component
public class ExperimentConfigurer {

	private ExperimentConfigStorage configStorage;
	private ParticipantRepository participantRepo;
	private TestGroupRepository testGroupRepo;
	private ExperimentRepository experimentRepo;
	
	private String fileName;
	private List<String> configLines;
	
	@Autowired
	public ExperimentConfigurer(@Qualifier("ExperimentConfigStorage")
								ExperimentConfigStorage configStorage,
								ParticipantRepository participantRepo,
								TestGroupRepository testGroupRepo,
								ExperimentRepository experimentRepo) {
		
		
		this.configStorage = configStorage;
		this.participantRepo = participantRepo;
		this.testGroupRepo = testGroupRepo;
		this.experimentRepo = experimentRepo;
	}

	public Experiment configureTestGroups(Experiment experiment, String configFileName) 
			throws NoSuchFileException, 
				   FileReadException,
				   ConfigParseException {
		
		fileName = configFileName;
		configLines = configStorage.getConfigLines(fileName);
		Set<String> groupNames = new HashSet<>();
		Set<String> userNames = new HashSet<>();

		experiment.clearTestGroups();
		
		int idx = 0;
		int end = configLines.size();
		
		while (idx < end) {
			
			String[] words = getWords(idx++);		
			
			if (words.length != 2 || ! words[0].equals("group:")) {
				throwParseException(idx -1);
			}
			
			String groupName = words[1];
			
			if (groupNames.contains(groupName)) {
				throwParseException(idx -1);
			}
			
			groupNames.add(groupName);
			TestGroup g = testGroupRepo.save(new TestGroup(groupName));
			
			while (idx < end) {
				
				words = getWords(idx++);
				
				if (words.length != 2) {
					throwParseException(idx -1);
				}
				
				if (words[0].equals("group:")) {
					--idx;
					break;
				}
				
				String userName = words[0];
				
				if (userNames.contains(userName)) {
					throwParseException(idx -1);
				}
				
				userNames.add(userName);
				Participant p = participantRepo.save(new Participant(userName, words[1]));
				g.addParticipant(p);
			}
			
			testGroupRepo.save(g);
			experiment.addTestGroup(g);
		}
		
		experimentRepo.save(experiment);
		
		return experiment; 
	}
	
	private String[] getWords(int idx) {
		
		String line = configLines.get(idx);
		return line.split("\\s+");
	}
	
	private void throwParseException(int idx) throws ConfigParseException {
		
		throw new ConfigParseException(fileName, configLines.get(idx));
	}
}















