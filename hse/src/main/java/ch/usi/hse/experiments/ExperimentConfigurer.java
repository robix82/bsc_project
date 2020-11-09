package ch.usi.hse.experiments;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import ch.usi.hse.db.entities.Experiment;
import ch.usi.hse.db.entities.Participant;
import ch.usi.hse.db.entities.TestGroup;
import ch.usi.hse.exceptions.ConfigParseException;
import ch.usi.hse.exceptions.FileReadException;
import ch.usi.hse.exceptions.NoSuchFileException;
import ch.usi.hse.storage.ExperimentConfigStorage;

@Component
public class ExperimentConfigurer {

	private ExperimentConfigStorage configStorage;
	
	private String fileName;
	private List<String> configLines;
	
	@Autowired
	public ExperimentConfigurer(@Qualifier("ExperimentConfigStorage")
								ExperimentConfigStorage configStorage) {
		
		
		this.configStorage = configStorage;
	}

	public Experiment configureTestGroups(Experiment experiment, String configFileName) 
			throws NoSuchFileException, 
				   FileReadException,
				   ConfigParseException {
		
		fileName = configFileName;
		configLines = configStorage.getConfigLines(fileName);

		experiment.clearTestGroups();
		
		int idx = 0;
		int end = configLines.size();
		
		while (idx < end) {
			
			String[] words = getWords(idx++);		
			System.out.println("outer loop reading " + Arrays.toString(words));
			
			if (words.length != 2 || ! words[0].equals("group:")) {
				System.out.println("Error on group def");
				throwParseException(idx);
			}
			
			System.out.println("idx " + idx + ": adding group " + words[1]);
			TestGroup g = new TestGroup(words[1]);
			
			while (idx < end) {
				
				words = getWords(idx++);
				System.out.println("inner loop reading " + Arrays.toString(words));
				
				if (words.length != 2) {
					System.out.println("Error on participant def");
					throwParseException(idx);
				}
				
				if (words[0].equals("group:")) {
					--idx;
					break;
				}
				
				System.out.println("idx " + idx + ": adding participant " + words[0] + " " + words[1]);
				g.addParticipant(new Participant(words[0], words[1]));
			}
			
			System.out.println("Adding to Experiment: " + g.getName());
			experiment.addTestGroup(g);
		}
		
		
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















