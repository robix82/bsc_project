package ch.usi.hse.endpoints;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

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
import ch.usi.hse.exceptions.ApiError;
import ch.usi.hse.exceptions.FileReadException;
import ch.usi.hse.services.ExperimentService;
import ch.usi.hse.storage.FileStorage;

@SpringBootTest
@AutoConfigureMockMvc
public class ExperimentsControllerIntegrationTest {

	@Autowired
	private MockMvc mvc;
	
	@Value("${dir.experimentConfig}")
	private Path configFilesPath;
	
	@Autowired
	@Qualifier("FileStorage")
	private FileStorage fileStorage;
	
	@Autowired
	private ExperimentRepository experimentRepo;
	
	@Autowired
	private DocCollectionRepository collectionRepo;
	
	@Autowired
	private ExperimenterRepository experimenterRepo;
	
	@Autowired
	private ParticipantRepository participantRepo;
	
	@Autowired
	private TestGroupRepository testGroupRepo;
	
//	@Autowired
//	private ExperimentService expService;
	
	private String base = "/experiments";
	
	private ObjectMapper mapper;
	private ObjectWriter writer;
	private MediaType json;
	
	private List<Experiment> savedExperiments;
	private List<Experimenter> savedExperimenters;
	private List<TestGroup> savedTestGroups; // belonging to savedExperiments.get(0)
	private List<DocCollection> savedDocCollections;
	
	private String validConfigName, badConfigName, newConfigName;
	private byte[] validConfigData, newConfigData, badConfigData;
	private MockMultipartFile newConfigMpFile;
	
	@BeforeEach
	public void setUp() throws Exception {
		
		mapper = new ObjectMapper();
		mapper.findAndRegisterModules();
		writer = mapper.writer().withDefaultPrettyPrinter();
		json = new MediaType(MediaType.APPLICATION_JSON.getType(), 
			       MediaType.APPLICATION_JSON.getSubtype(), 
			       Charset.forName("utf8"));
		
		if (! Files.exists(configFilesPath)) {
			Files.createDirectories(configFilesPath);
		}
		
		clearRepositories();
		
		
		// EXPERIMENTS AND EXPERIMENTERS
		
		Experimenter e1 = experimenterRepo.save(new Experimenter("e1", "pwd"));
		Experimenter e2 = experimenterRepo.save(new Experimenter("e2", "pwd"));
		
		Experiment ex1 = experimentRepo.save(new Experiment("ex1"));
		Experiment ex2 = experimentRepo.save(new Experiment("ex2"));
		Experiment ex3 = experimentRepo.save(new Experiment("ex3"));
		Experiment ex4 = experimentRepo.save(new Experiment("ex4"));
		
		TestGroup g1 = testGroupRepo.save(new TestGroup("g1"));
		TestGroup g2 = testGroupRepo.save(new TestGroup("g2"));
		
		Participant p1 = new Participant("p1", "pwd");
		Participant p2 = new Participant("p2", "pwd");
		Participant p3 = new Participant("p3", "pwd");
		Participant p4 = new Participant("p4", "pwd");
		
		g1.addParticipant(p1);
		g1.addParticipant(p2);
		g2.addParticipant(p3);
		g2.addParticipant(p4);
		
		ex1.addTestGroup(g1);
		ex1.addTestGroup(g2);
		
		e1.addExperiment(ex1);
		e1.addExperiment(ex2);
		e2.addExperiment(ex3);
		e2.addExperiment(ex4);
		
		experimenterRepo.save(e1);
		experimenterRepo.save(e2);
		
		savedExperimenters = experimenterRepo.findAll();
		savedExperiments = experimentRepo.findAll();
		
		// DOC COLLECTIONS
		
		DocCollection c1 = collectionRepo.save(new DocCollection("dc1", "urlList1"));
		DocCollection c2 = collectionRepo.save(new DocCollection("dc2", "urlList2"));
		savedDocCollections = collectionRepo.findAll();
		
		// CONFIG FILES
		
		validConfigName = "valid.txt";
		badConfigName = "bad.txt";
		newConfigName = "new.txt";
		
		String validConfigText = "group: testGroup1\n" +
								 "participant1 pwd1\n" +
								 "participant2 pwd2\n" +
								 "participant3 pwd3\n" +
								 "group: testGroup2\n" +
								 "participant4 pwd4\n" +
								 "participant5 pwd5\n";
		
		String badConfigText = "group: testGroup1\n" +
							   "participant1 pwd1\n" +
							   "participant2\n" + // missing password
							   "participant3 pwd3\n" +
							   "group: testGroup2\n" +
							   "participant4 pwd4\n" +
							   "participant5 pwd5\n";
		
		String newConfigText = "group: testGroup3\n" +
							   "participant6 pwd6\n" +
							   "participant7 pwd7\n" +
							   "participant8 pwd8\n";
		
		validConfigData = validConfigText.getBytes();
		badConfigData = badConfigText.getBytes();
		newConfigData = newConfigText.getBytes();
		
		Path f1 = Files.createFile(configFilesPath.resolve(validConfigName));
		Path f2 = Files.createFile(configFilesPath.resolve(badConfigName));
		Files.write(f1, validConfigData);
		Files.write(f2, badConfigData);
		
		newConfigMpFile = new MockMultipartFile("file",
												newConfigName,
												MediaType.TEXT_PLAIN_VALUE,
												newConfigData);
		
		
	}
	
	@AfterEach
	public void cleanup() {
		
		clearTestFiles();
		clearRepositories();
	}
	
	@Test
	public void testSetup() throws IOException, FileReadException {
		
		assertEquals(2, savedExperimenters.size());
		assertEquals(4, savedExperiments.size());
		assertEquals(2, savedDocCollections.size());
		assertEquals(2, testGroupRepo.count());
		assertEquals(4, participantRepo.count());
		assertEquals(2, Files.list(configFilesPath).count());
	}
	
	// UI 
	
	@Test
	public void testGetExperimentsUi() throws Exception {
		
		mvc.perform(get(base + "/ui"))
		   .andExpect(status().isOk())
		   .andExpect(view().name("experiments"))
		   .andExpect(model().attribute("experiments", Matchers.iterableWithSize(4)))
		   .andExpect(model().attribute("experimenters", Matchers.iterableWithSize(2)));
	}

	@Test
	public void testExperimentsSetupUi1() throws Exception {
		
		Experiment experiment = savedExperiments.get(0);
		
		String url = UriComponentsBuilder.fromUriString(base + "/setup/ui")
				 						 .queryParam("expId", experiment.getId())
				 						 .build()
				 						 .toUriString();
				 				
		
		mvc.perform(get(url))
		   .andExpect(status().isOk())
		   .andExpect(view().name("exp_setup"))
		   .andExpect(model().attribute("experiment", experiment))
		   .andExpect(model().attribute("docCollections", Matchers.iterableWithSize(2)))
		   .andExpect(model().attribute("configFiles", Matchers.iterableWithSize(2)));
	}
	
	@Test
	public void testGetExperimentsSetupUi2() throws Exception {
		
		int badId = 999999;
		
		String url = UriComponentsBuilder.fromUriString(base + "/setup/ui")
										 .queryParam("expId", badId)
										 .build()
										 .toUriString();
		
		MvcResult res = mvc.perform(get(url))
						   .andExpect(status().isNotFound())
						   .andReturn();
		
		ApiError err = getError(res);
		
		assertEquals("NoSuchExperimentException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(Integer.toString(badId)));
	}
	
	@Test
	public void testGetExperimentsRunUi1() throws Exception {
		
		Experiment experiment = savedExperiments.get(0);
		
		String url = UriComponentsBuilder.fromUriString(base + "/run/ui")
				 						 .queryParam("expId", experiment.getId())
				 						 .build()
				 						 .toUriString();
		
		mvc.perform(get(url))
		   .andExpect(status().isOk())
		   .andExpect(view().name("exp_run"))
		   .andExpect(model().attribute("experiment", experiment));
	}
	
	@Test
	public void testGetExperimentsRunUi2() throws Exception {
		
		int badId = 999999;
		
		String url = UriComponentsBuilder.fromUriString(base + "/run/ui")
										 .queryParam("expId", badId)
										 .build()
										 .toUriString();
		
		MvcResult res = mvc.perform(get(url))
						   .andExpect(status().isNotFound())
						   .andReturn();
		
		ApiError err = getError(res);
		
		assertEquals("NoSuchExperimentException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(Integer.toString(badId)));
	}
	
	@Test
	public void testGetExperimentsEvalUi1() throws Exception {
		
		Experiment experiment = savedExperiments.get(0);
		
		String url = UriComponentsBuilder.fromUriString(base + "/eval/ui")
				 						 .queryParam("expId", experiment.getId())
				 						 .build()
				 						 .toUriString();
		
		mvc.perform(get(url))
		   .andExpect(status().isOk())
		   .andExpect(view().name("exp_eval"))
		   .andExpect(model().attribute("experiment", experiment));
	}
	
	@Test
	public void testGetExperimentsEvalUi2() throws Exception {
		
		int badId = 999999;
		
		String url = UriComponentsBuilder.fromUriString(base + "/eval/ui")
										 .queryParam("expId", badId)
										 .build()
										 .toUriString();
		
		MvcResult res = mvc.perform(get(url))
						   .andExpect(status().isNotFound())
						   .andReturn();
		
		ApiError err = getError(res);
		
		assertEquals("NoSuchExperimentException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(Integer.toString(badId)));
	}
	
	// EXPERIMENTS REST API
	
	@Test
	public void testPostExperiment1() throws Exception {
		
		Experimenter experimenter = savedExperimenters.get(0);
		Experiment experiment = new Experiment("test");
		experiment.setExperimenter(experimenter);
		
		String jsonString = writer.writeValueAsString(experiment);
		
		long count = experimentRepo.count();

		MvcResult res = mvc.perform(post(base + "/").contentType(json).content(jsonString))
				 		   .andExpect(status().isCreated())
				 		   .andReturn();
		
		Experiment resBody = mapper.readValue(resString(res), Experiment.class);
		
		assertEquals(experiment.getTitle(), resBody.getTitle());
		assertEquals(count +1, experimentRepo.count()); 
		assertTrue(experimentRepo.existsById(resBody.getId()));
		
		Experiment retrieved = experimentRepo.findById(resBody.getId()); 
		assertEquals(experimenter, retrieved.getExperimenter());
	}
	
	@Test // experiment with existing id
	public void testPostExperiment2() throws Exception {
		
		int existingId = savedExperiments.get(0).getId();
		Experiment experiment = new Experiment("test");
		experiment.setId(existingId);
		String jsonString = writer.writeValueAsString(experiment);
		
		MvcResult res = mvc.perform(post(base + "/").contentType(json).content(jsonString))
				 		   .andExpect(status().isUnprocessableEntity())
				 		   .andReturn();
		
		ApiError err = getError(res);
		
		assertEquals("ExperimentExistsException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(Integer.toString(existingId)));
	}
	
	@Test // Experiment with existing title
	public void testPostExperiment3() throws Exception {
		
		String existingTitle = savedExperiments.get(0).getTitle();
		Experiment experiment = new Experiment(existingTitle);
		String jsonString = writer.writeValueAsString(experiment);
		
		MvcResult res = mvc.perform(post(base + "/").contentType(json).content(jsonString))
				 		   .andExpect(status().isUnprocessableEntity())
				 		   .andReturn();
		
		ApiError err = getError(res);
		
		assertEquals("ExperimentExistsException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(existingTitle));
	}
	
	@Test // experiment with non-existing experimenter id
	public void testPostExperiment4() throws Exception {
		
		int badId = 999999;
		Experiment experiment = new Experiment("test");
		experiment.setExperimenterId(badId);
		String jsonString = writer.writeValueAsString(experiment);
		
		MvcResult res = mvc.perform(post(base + "/").contentType(json).content(jsonString))
				 		   .andExpect(status().isNotFound())
				 		   .andReturn();
		
		ApiError err = getError(res);
		
		assertEquals("NoSuchUserException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains("Experimenter"));
		assertTrue(err.getErrorMessage().contains(Integer.toString(badId)));
	}

	@Test
	public void testUpdateExperiment1() throws Exception {
		
		String newTitle = "newTitle";
		Experimenter newExperimenter = savedExperimenters.get(1);
		Experiment.Status newStatus = Experiment.Status.COMPLETE;
		LocalDateTime newDateCreated = LocalDateTime.of(2020, 10, 9, 0, 0);
		LocalDateTime newDateConducted = LocalDateTime.of(2020, 10, 10, 0, 0);
		LocalDateTime newStartTime = LocalDateTime.of(2020, 10, 10, 1, 0);
		LocalDateTime newEndTime = LocalDateTime.of(2020, 10, 10, 2, 0);
		
		Experiment experiment = savedExperiments.get(0);
		
		assertNotEquals(newTitle, experiment.getTitle());
		assertNotEquals(newExperimenter, experiment.getExperimenter());
		assertNotEquals(newExperimenter.getId(), experiment.getExperimenterId());
		assertNotEquals(newExperimenter.getUserName(), experiment.getExperimenterName());
		assertNotEquals(newStatus, experiment.getStatus());
		assertNotEquals(newDateCreated, experiment.getDateCreated());
		assertNotEquals(newDateConducted, experiment.getDateConducted());
		assertNotEquals(newStartTime, experiment.getStartTime());
		assertNotEquals(newEndTime, experiment.getEndTime());
		
		experiment.setTitle(newTitle);
		experiment.setExperimenter(newExperimenter);
		experiment.setStatus(newStatus);
		experiment.setDateCreated(newDateCreated);
		experiment.setDateConducted(newDateConducted);
		experiment.setStartTime(newStartTime);
		experiment.setEndTime(newEndTime);
		
		String jsonString = writer.writeValueAsString(experiment);
		
		MvcResult res = mvc.perform(put(base + "/").contentType(json).content(jsonString))
				 		   .andExpect(status().isOk())
				 		   .andReturn();
		
		Experiment resBody = mapper.readValue(resString(res), Experiment.class);
		
		assertEquals(experiment, resBody);
		
		Experiment found = experimentRepo.findById(experiment.getId());
		
		assertEquals(newTitle, found.getTitle());
		assertEquals(newExperimenter, found.getExperimenter());
		assertEquals(newExperimenter.getId(), found.getExperimenterId());
		assertEquals(newExperimenter.getUserName(), found.getExperimenterName());
		assertEquals(newStatus, found.getStatus());
		assertEquals(newDateCreated, found.getDateCreated());
		assertEquals(newDateConducted, found.getDateConducted());
		assertEquals(newStartTime, found.getStartTime());
		assertEquals(newEndTime, found.getEndTime());
	}

	@Test // non-existing Experiment id
	public void testUpdateExperiment2() throws Exception {
		
		int badId = 999999;
		Experiment experiment = savedExperiments.get(0);
		experiment.setId(badId);
		String jsonString = writer.writeValueAsString(experiment);
		
		MvcResult res = mvc.perform(put(base + "/").contentType(json).content(jsonString))
				 		   .andExpect(status().isNotFound())
				 		   .andReturn();
		
		ApiError err = getError(res);
		
		assertEquals("NoSuchExperimentException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(Integer.toString(badId)));
	}
	
	@Test // existing title
	public void testUpdateExperiment3() throws Exception {
		
		String existingTitle = savedExperiments.get(1).getTitle();
		Experiment experiment = savedExperiments.get(0);
		experiment.setTitle(existingTitle);
		String jsonString = writer.writeValueAsString(experiment);
		
		MvcResult res = mvc.perform(put(base + "/").contentType(json).content(jsonString))
				 		   .andExpect(status().isUnprocessableEntity())
				 		   .andReturn();
		
		ApiError err = getError(res);
		
		assertEquals("ExperimentExistsException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(existingTitle));
	}
	
	@Test // non-existing experimenter id
	public void testUpdateExperiment4() throws Exception {
		
		int badId = 999999;
		Experiment experiment = savedExperiments.get(0);
		experiment.setExperimenterId(badId);
		String jsonString = writer.writeValueAsString(experiment);
		
		MvcResult res = mvc.perform(put(base + "/").contentType(json).content(jsonString))
				 		   .andExpect(status().isNotFound())
				 		   .andReturn();
		
		ApiError err = getError(res);
		
		assertEquals("NoSuchUserException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains("Experimenter"));
		assertTrue(err.getErrorMessage().contains(Integer.toString(badId)));
	}
	
	@Test // non-existing experimenter name
	public void testUpdateExperiment5() throws Exception {
		
		String badName = "badName";
		Experiment experiment = savedExperiments.get(0);
		experiment.setExperimenterId(savedExperimenters.get(1).getId());
		experiment.setExperimenterName(badName);
		String jsonString = writer.writeValueAsString(experiment);
		
		MvcResult res = mvc.perform(put(base + "/").contentType(json).content(jsonString))
				 		   .andExpect(status().isNotFound())
				 		   .andReturn();
		
		ApiError err = getError(res);
		
		assertEquals("NoSuchUserException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains("Experimenter"));
		assertTrue(err.getErrorMessage().contains(badName));
	}
	
	@Test
	public void testDeleteExperiment1() throws Exception {
		
		Experiment experiment = savedExperiments.get(0);
		String jsonString = writer.writeValueAsString(experiment);
		
		assertTrue(experimentRepo.existsById(experiment.getId()));
		assertEquals(4, experimentRepo.count());
		assertEquals(2, testGroupRepo.count());
		assertEquals(4, participantRepo.count());
		
		MvcResult res = mvc.perform(delete(base + "/").contentType(json).content(jsonString))
				 		   .andExpect(status().isOk())
				 		   .andReturn();
		
		Experiment resBody = mapper.readValue(resString(res), Experiment.class);
		
		assertEquals(experiment, resBody);
		assertFalse(experimentRepo.existsById(experiment.getId()));
		assertEquals(3, experimentRepo.count());
		assertEquals(0, testGroupRepo.count());
		assertEquals(0, participantRepo.count());
	}
	
	
	///////////////////////////////////////
	
	private String resString(MvcResult res) throws UnsupportedEncodingException {
		
		return res.getResponse().getContentAsString();
	}
	
	private ApiError getError(MvcResult res) throws Exception {
		
		return mapper.readValue(resString(res), ApiError.class);
	}
	
	private void clearTestFiles()  {
		
		try {
			
			fileStorage.clearDirectory(configFilesPath);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void clearRepositories() {
				
		experimenterRepo.deleteAll();
		experimentRepo.deleteAll();	
		testGroupRepo.deleteAll();
		participantRepo.deleteAll();
		collectionRepo.deleteAll();
	}
}







