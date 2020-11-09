package ch.usi.hse.experiments;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import ch.usi.hse.db.entities.Experiment;
import ch.usi.hse.db.entities.TestGroup;
import ch.usi.hse.exceptions.FileReadException;
import ch.usi.hse.exceptions.NoSuchFileException;
import ch.usi.hse.storage.ExperimentConfigStorage;

public class ExperimentConfigurerTest {

	@Mock
	private ExperimentConfigStorage configStorage;
	
	private String existingFileName, nonExistingFileName;
	private ExperimentConfigurer configurer;
	private List<String> configLines;
	
	@BeforeEach
	public void setUp() throws NoSuchFileException, FileReadException {
		
		initMocks(this);
		
		configurer = new ExperimentConfigurer(configStorage);
		
		existingFileName = "existing.txt";
		nonExistingFileName = "nonExisting.txt";
		
		configLines = new ArrayList<>();
		configLines.add("group: testGroup1");
		configLines.add("user1 pwd1");
		configLines.add("user2 pwd2");
		configLines.add("user3 pwd3");
		configLines.add("group: testGroup2");
		configLines.add("user4 pwd4");
		configLines.add("user5 pwd5");
		
		when(configStorage.getConfigLines(existingFileName)).thenReturn(configLines);
		when(configStorage.getConfigLines(nonExistingFileName)).thenThrow(new NoSuchFileException(nonExistingFileName));
	}
	
	@Test
	public void testConfigureTestGroups1() throws Exception {
		
		Experiment experiment = new Experiment("e1");
		
		assertEquals(0, experiment.getTestGroups().size());
		
		Experiment configured = configurer.configureTestGroups(experiment, existingFileName);
		
		Set<TestGroup> groups = configured.getTestGroups();
		assertEquals(2, groups.size());
	}
}











