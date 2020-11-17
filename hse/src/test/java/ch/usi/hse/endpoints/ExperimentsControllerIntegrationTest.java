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
import java.util.HashSet;
import java.util.List;

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
import org.springframework.security.test.context.support.WithMockUser;
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
import ch.usi.hse.storage.FileStorage;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(authorities={"ADMIN", "EXPERIMENTER"})
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
	
	private String base = "/experiments";
	
	private ObjectMapper mapper;
	private ObjectWriter writer;
	private MediaType json;
	
	private List<Experiment> savedExperiments;
	private List<Experimenter> savedExperimenters;
	private List<TestGroup> savedTestGroups; // belonging to savedExperiments.get(0)
	private List<DocCollection> indexedDocCollections;
	
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
		
		// DOC COLLECTIONS
		
		DocCollection c1 = collectionRepo.save(new DocCollection("dc1", "urlList1"));
		DocCollection c2 = collectionRepo.save(new DocCollection("dc1", "urlList1"));
		DocCollection c3 = collectionRepo.save(new DocCollection("dc1", "urlList1"));
		c1.setIndexed(true);
		c2.setIndexed(true);
		c3.setIndexed(false);	
		collectionRepo.save(c1);
		collectionRepo.save(c2);
		collectionRepo.save(c3);
		
		indexedDocCollections = collectionRepo.findByIndexed(true);
		
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
		g1.addDocCollection(c1);
		g2.addDocCollection(c1);
		
		ex1.addTestGroup(g1);
		ex1.addTestGroup(g2);
		ex1.setStatus(Experiment.Status.READY);
		
		e1.addExperiment(ex1);
		e1.addExperiment(ex2);
		e2.addExperiment(ex3);
		e2.addExperiment(ex4);
		
		experimenterRepo.save(e1);
		experimenterRepo.save(e2);
		
		savedExperimenters = experimenterRepo.findAll();
		savedExperiments = experimentRepo.findAll();
		savedTestGroups = testGroupRepo.findAll();
		
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
		assertEquals(2, indexedDocCollections.size());
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
	
	@Test // non-existing Experiment
	public void testDeleteExperiment2() throws Exception {
		
		int badId = 999999;
		Experiment experiment = savedExperiments.get(0);
		experiment.setId(badId);
		String jsonString = writer.writeValueAsString(experiment);
		
		MvcResult res = mvc.perform(delete(base + "/").contentType(json).content(jsonString))
				 		   .andExpect(status().isNotFound())
				 		   .andReturn();
		
		ApiError err = getError(res);
		
		assertEquals("NoSuchExperimentException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(Integer.toString(badId)));
	}
	
	@Test
	public void testGetExperiment1() throws Exception {
		
		Experiment ex = savedExperiments.get(0);
		int existingId = ex.getId();
		
		String url = UriComponentsBuilder.fromUriString(base + "/")
				 						 .queryParam("id", existingId)
				 						 .build()
				 						 .toUriString();
		
		MvcResult res = mvc.perform(get(url))
				 		   .andExpect(status().isOk())
				 		   .andReturn();
		
		Experiment resBody = mapper.readValue(resString(res), Experiment.class);
		
		assertEquals(ex, resBody);
	}
	
	@Test
	public void testGetExperiment2() throws Exception {
		
		int badId = 999999;
		
		String url = UriComponentsBuilder.fromUriString(base + "/")
				 .queryParam("id", badId)
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
	public void testPostTestGroup1() throws Exception {
		
		TestGroup newTestGroup = new TestGroup("newTestGroup");
		newTestGroup.addParticipant(new Participant("np1", "pwd"));
		newTestGroup.addParticipant(new Participant("np2", "pwd"));
		
		Experiment experiment = savedExperiments.get(0);
		newTestGroup.setExperimentId(experiment.getId());
		
		String jsonString = writer.writeValueAsString(newTestGroup);
		
		assertEquals(2, testGroupRepo.count());
		assertEquals(2, experiment.getTestGroups().size());
		assertEquals(4, participantRepo.count());
		
		MvcResult res = mvc.perform(post(base + "/testGroups").contentType(json).content(jsonString))
				 	       .andExpect(status().isCreated())
				 	       .andReturn();
		
		TestGroup resBody = mapper.readValue(resString(res), TestGroup.class);
		assertEquals(newTestGroup.getName(), resBody.getName());
		
		assertEquals(experiment.getId(), resBody.getExperimentId());
		assertEquals(experiment.getTitle(), resBody.getExperimentTitle());
		assertEquals(3, testGroupRepo.count());
		assertEquals(3, experimentRepo.findById(experiment.getId()).getTestGroups().size());
		assertEquals(6, participantRepo.count());
	}
	
	@Test // non-existing Experiment id
	public void testAddTestGroup2() throws Exception {
		
		int badId = 999999;
		TestGroup newTestGroup = new TestGroup("newTestGroup");
		newTestGroup.addParticipant(new Participant("np1", "pwd"));
		newTestGroup.addParticipant(new Participant("np2", "pwd"));
		newTestGroup.setExperimentId(badId);
		String jsonString = writer.writeValueAsString(newTestGroup);
		
		MvcResult res = mvc.perform(post(base + "/testGroups").contentType(json).content(jsonString))
				 		   .andExpect(status().isNotFound())
				 		   .andReturn();
		
		ApiError err = getError(res);
		
		assertEquals("NoSuchExperimentException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(Integer.toString(badId)));
	}
	
	@Test // update name
	public void testUpdateTestGroup1() throws Exception {
		
		TestGroup testGroup = savedTestGroups.get(0);
		
		String newName = "newName";
		
		assertNotEquals(newName, testGroup.getName());
				
		testGroup.setName(newName);
		
		String jsonString = writer.writeValueAsString(testGroup);
		
		MvcResult res = mvc.perform(put(base + "/testGroups").contentType(json).content(jsonString))
				 		   .andExpect(status().isOk())
				 		   .andReturn();
		
		TestGroup resBody = mapper.readValue(resString(res), TestGroup.class);
		
		assertEquals(testGroup, resBody);
		assertEquals(newName, resBody.getName());
		assertEquals(newName, testGroupRepo.findById(testGroup.getId()).getName());
	}
	
	@Test // add existing DocCollections
	public void testUpdateTestGroup2() throws Exception {
		
		TestGroup testGroup = savedTestGroups.get(0);
		testGroup.clearDocCollections();
		testGroupRepo.save(testGroup); 
		int groupId = testGroup.getId();
			
		testGroup.setDocCollections(new HashSet<>(indexedDocCollections));
		String jsonString = writer.writeValueAsString(testGroup);
		
		assertEquals(0, testGroupRepo.findById(groupId).getDocCollections().size());
		
		MvcResult res = mvc.perform(put(base + "/testGroups").contentType(json).content(jsonString))
				 		   .andExpect(status().isOk())
				 		   .andReturn();
		
		TestGroup resBody = mapper.readValue(resString(res), TestGroup.class);
		assertEquals(testGroup, resBody);
		assertEquals(2, testGroupRepo.findById(groupId).getDocCollections().size());
	}
	
	@Test // add non-existing DocCollection
	public void testUpdateTestGroup3() throws Exception {
		
		DocCollection newDc = new DocCollection("newDc", "someList");
		TestGroup testGroup = savedTestGroups.get(0);
		testGroup.addDocCollection(newDc);
		String jsonString = writer.writeValueAsString(testGroup);
		
		MvcResult res = mvc.perform(put(base + "/testGroups").contentType(json).content(jsonString))
				 		   .andExpect(status().isNotFound())
				 		   .andReturn();
		
		ApiError err = getError(res);
		
		assertEquals("NoSuchDocCollectionException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(Integer.toString(newDc.getId())));
	}
	
	@Test // add new Participant
	public void testUpdateTestGroup4() throws Exception {
		
		Participant newParticipant = new Participant("newParticipant", "pwd");
		TestGroup testGroup = savedTestGroups.get(0);
		int groupId = testGroup.getId();
		testGroup.addParticipant(newParticipant);
		String jsonString = writer.writeValueAsString(testGroup);
		
		assertEquals(4, participantRepo.count());
		assertFalse(testGroupRepo.findById(groupId).getParticipants().contains(newParticipant));
		
		MvcResult res = mvc.perform(put(base + "/testGroups").contentType(json).content(jsonString))
				 		   .andExpect(status().isOk())
				 		   .andReturn();
		
		TestGroup resBody = mapper.readValue(resString(res), TestGroup.class);
		assertEquals(testGroup, resBody);
		
		assertEquals(5, participantRepo.count());
		assertTrue(testGroupRepo.findById(groupId).getParticipants().contains(newParticipant));
	}
	
	@Test // add Participant with existing name
	public void testUpdateTestGroup5() throws Exception {
		
		String existingName = participantRepo.findAll().get(2).getUserName();
		TestGroup testGroup = savedTestGroups.get(0);
		testGroup.addParticipant(new Participant(existingName, "pwd"));
		String jsonString = writer.writeValueAsString(testGroup);
		
		MvcResult res = mvc.perform(put(base + "/testGroups").contentType(json).content(jsonString))
				 		   .andExpect(status().isUnprocessableEntity())
				 		   .andReturn();
		
		ApiError err = getError(res);
		
		assertEquals("UserExistsException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(existingName));
	}
	
	@Test // remove participant
	public void testUpdateTestGroup6() throws Exception {
		
		TestGroup testGroup = savedTestGroups.get(0);
		Participant p = (Participant) testGroup.getParticipants().toArray()[0];
		testGroup.removeParticipant(p);
		String jsonString = writer.writeValueAsString(testGroup);
		
		assertEquals(4, participantRepo.count());
		assertTrue(participantRepo.existsById(p.getId()));
		
		MvcResult res = mvc.perform(put(base + "/testGroups").contentType(json).content(jsonString))
				 		   .andExpect(status().isOk())
				 		   .andReturn();
		
		TestGroup resBody = mapper.readValue(resString(res), TestGroup.class);
		assertEquals(testGroup, resBody);
		
		assertEquals(3, participantRepo.count());
		assertFalse(participantRepo.existsById(p.getId()));
	}
	
	@Test // set non-existing experiment
	public void testUpdateTestGroup7() throws Exception {
		
		Experiment newExp = new Experiment("newExp");
		TestGroup testGroup = savedTestGroups.get(0);
		testGroup.setExperiment(newExp);
		String jsonString = writer.writeValueAsString(testGroup);
		
		MvcResult res = mvc.perform(put(base + "/testGroups").contentType(json).content(jsonString))
				 	       .andExpect(status().isNotFound())
				 	       .andReturn();
		
		ApiError err = getError(res);
		
		assertEquals("NoSuchExperimentException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(Integer.toString(newExp.getId())));
	}
	
	@Test // update non-existing TestGroup
	public void testUpdateTextGroup8() throws Exception {
		
		int badId = 999999;
		TestGroup testGroup = savedTestGroups.get(0);
		testGroup.setId(badId);
		String jsonString = writer.writeValueAsString(testGroup);
		
		MvcResult res = mvc.perform(put(base + "/testGroups").contentType(json).content(jsonString))
				 		   .andExpect(status().isNotFound())
				 		   .andReturn();
		
		ApiError err = getError(res);
		
		assertEquals("NoSuchTestGroupException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(Integer.toString(badId)));
	}
	
	@Test
	public void testDeleteTestGroup1() throws Exception {
		
		TestGroup testGroup = savedTestGroups.get(0);
		int experimentId = testGroup.getExperimentId();
		String jsonString = writer.writeValueAsString(testGroup);
		
		assertEquals(2, testGroupRepo.count());
		assertTrue(testGroupRepo.existsById(testGroup.getId()));
		assertEquals(2, experimentRepo.findById(experimentId).getTestGroups().size());
		assertEquals(4, participantRepo.count());
		
		MvcResult res = mvc.perform(delete(base + "/testGroups").contentType(json).content(jsonString))
				 		   .andExpect(status().isOk())
				 		   .andReturn();
		
		TestGroup resBody = mapper.readValue(resString(res), TestGroup.class);
		assertEquals(testGroup, resBody);
		
		assertEquals(1, testGroupRepo.count());
		assertFalse(testGroupRepo.existsById(testGroup.getId()));
		assertEquals(1, experimentRepo.findById(experimentId).getTestGroups().size());
		assertEquals(2, participantRepo.count());
	}
	
	@Test // non-existing TestGroup
	public void testDeleteTestGroup2() throws Exception {
		
		int badId = 999999;
		TestGroup testGroup = savedTestGroups.get(0);
		testGroup.setId(badId);
		String jsonString = writer.writeValueAsString(testGroup);
		
		MvcResult res = mvc.perform(delete(base + "/testGroups").contentType(json).content(jsonString))
				 		   .andExpect(status().isNotFound())
				 		   .andReturn();
		
		ApiError err = getError(res);
		
		assertEquals("NoSuchTestGroupException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(Integer.toString(badId)));
	}
	
	@Test // non-existing experiment
	public void testDeleteTestGroup3() throws Exception {
		
		int badId = 999999;
		TestGroup testGroup = savedTestGroups.get(0);
		testGroup.setExperimentId(badId);
		String jsonString = writer.writeValueAsString(testGroup);
		
		MvcResult res = mvc.perform(delete(base + "/testGroups").contentType(json).content(jsonString))
				 		   .andExpect(status().isNotFound())
				 		   .andReturn();
		
		ApiError err = getError(res);
		
		assertEquals("NoSuchExperimentException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(Integer.toString(badId)));
	}

	@Test // creating new TestGroups
	public void testConfigureTestGroups() throws Exception {
		
		Experiment experiment = savedExperiments.get(1);
		int experimentId = experiment.getId();
		assertEquals(0, experiment.getTestGroups().size());
		String jsonString = writer.writeValueAsString(experiment);
		
		assertEquals(0, experimentRepo.findById(experimentId).getTestGroups().size());
		assertEquals(2, testGroupRepo.count());
		assertEquals(4, participantRepo.count());
		
		String url = UriComponentsBuilder.fromUriString(base + "/testGroups/config")
				 						 .queryParam("configFileName", validConfigName)
				 						 .build()
				 						 .toUriString();
		
		MvcResult res = mvc.perform(post(url).contentType(json).content(jsonString))
				 		   .andExpect(status().isOk())
				 		   .andReturn();
		
		Experiment resBody = mapper.readValue(resString(res), Experiment.class);
		assertEquals(experiment, resBody);
		
		assertEquals(2, experimentRepo.findById(experimentId).getTestGroups().size());
		assertEquals(4, testGroupRepo.count());
		assertEquals(9, participantRepo.count());
	}

	@Test // non-existing experiment
	public void testConfigureTestGroups2() throws Exception {
		
		int badId = 999999;
		Experiment experiment = savedExperiments.get(1);
		experiment.setId(badId);
		String jsonString = writer.writeValueAsString(experiment);
		
		String url = UriComponentsBuilder.fromUriString(base + "/testGroups/config")
										 .queryParam("configFileName", validConfigName)
										 .build()
										 .toUriString();
		
		MvcResult res = mvc.perform(post(url).contentType(json).content(jsonString))
				 		   .andExpect(status().isNotFound())
				 		   .andReturn();
		
		ApiError err = getError(res);
		
		assertEquals("NoSuchExperimentException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(Integer.toString(badId)));
	}

	@Test // non-existing config file
	public void testConfigureTestGroups3() throws Exception {
		
		Experiment experiment = savedExperiments.get(1);
		String jsonString = writer.writeValueAsString(experiment);
		
		String url = UriComponentsBuilder.fromUriString(base + "/testGroups/config")
										 .queryParam("configFileName", newConfigName)
										 .build()
										 .toUriString();
		
		MvcResult res = mvc.perform(post(url).contentType(json).content(jsonString))
				 		   .andExpect(status().isNotFound())
				 		   .andReturn();
		
		ApiError err = getError(res);
		
		assertEquals("NoSuchFileException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(newConfigName));
	}
	
	@Test // config file with errors
	public void testConfigureTestGroups4() throws Exception {
		
		Experiment experiment = savedExperiments.get(1);
		String jsonString = writer.writeValueAsString(experiment);
		
		String url = UriComponentsBuilder.fromUriString(base + "/testGroups/config")
										 .queryParam("configFileName", badConfigName)
										 .build()
										 .toUriString();
		
		MvcResult res = mvc.perform(post(url).contentType(json).content(jsonString))
				 		   .andExpect(status().isUnprocessableEntity())
				 		   .andReturn();
		
		ApiError err = getError(res);
		
		assertEquals("ConfigParseException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains("participant2"));
	}

	@Test
	public void testUploadConfigFile() throws Exception {
	
		assertEquals(2, Files.list(configFilesPath).count());
		assertFalse(Files.exists(configFilesPath.resolve(newConfigName)));
		
		String expectedMsg = "file " + newConfigName + " uploaded";
		
		MvcResult res = mvc.perform(multipart(base + "/testGroups/config/ul").file(newConfigMpFile))
				 		   .andExpect(status().isCreated())
				 		   .andReturn();
		
		assertEquals(expectedMsg, resString(res));
		
		assertEquals(3, Files.list(configFilesPath).count());
		assertTrue(Files.exists(configFilesPath.resolve(newConfigName)));
		assertArrayEquals(newConfigData, Files.readAllBytes(configFilesPath.resolve(newConfigName))); 
	}

	@Test
	public void testDeleteConfigFile1() throws Exception {
		
		String url = UriComponentsBuilder.fromUriString(base + "/testGroups/config")
				 						 .queryParam("fileName", validConfigName)
				 						 .build()
				 						 .toUriString();
		
		String expectedMsg = "file " + validConfigName + " deleted";
		
		assertEquals(2, Files.list(configFilesPath).count());
		assertTrue(Files.exists(configFilesPath.resolve(validConfigName)));
		
		MvcResult res = mvc.perform(delete(url))
				 		   .andExpect(status().isOk())
				 		   .andReturn();
		
		assertEquals(expectedMsg, resString(res));
		
		assertEquals(1, Files.list(configFilesPath).count());
		assertFalse(Files.exists(configFilesPath.resolve(validConfigName)));
	}
	
	@Test
	public void testDeleteConfigFile2() throws Exception {
		
		String url = UriComponentsBuilder.fromUriString(base + "/testGroups/config")
										 .queryParam("fileName", newConfigName)
										 .build()
										 .toUriString();
		
		MvcResult res = mvc.perform(delete(url))
				 		   .andExpect(status().isNotFound())
				 		   .andReturn();
		
		ApiError err = getError(res);
		
		assertEquals("NoSuchFileException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(newConfigName));
	}
	
	@Test
	public void testDownloadConfigFile1() throws Exception {
		
		String url = UriComponentsBuilder.fromUriString(base + "/testGroups/config/dl")
										 .queryParam("fileName", validConfigName)
										 .build()
										 .toUriString();
		
		MvcResult res = mvc.perform(get(url))
				 		   .andExpect(status().isOk())
				 		   .andReturn();
		
		assertArrayEquals(validConfigData, res.getResponse().getContentAsByteArray());
	}

	@Test
	public void testDownloadConfigFile2() throws Exception {
		
		String url = UriComponentsBuilder.fromUriString(base + "/testGroups/config/dl")
										 .queryParam("fileName", newConfigName)
										 .build()
										 .toUriString();
		
		MvcResult res = mvc.perform(get(url))
				 		   .andExpect(status().isNotFound())
				 		   .andReturn();
		
		ApiError err = getError(res);
		
		assertEquals("NoSuchFileException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(newConfigName));
	}
	
	@Test
	public void testStartExperiment1() throws Exception {
		
		// setup
		
		Experiment ex = savedExperiments.get(0);
		LocalDateTime now = LocalDateTime.now();
		
		assertFalse(ex.getTestGroups().isEmpty());
		assertEquals(Experiment.Status.READY, ex.getStatus());
		assertFalse(timeApproxEquals(now, ex.getStartTime()));
		
		for (TestGroup g : ex.getTestGroups()) {
			
			assertFalse(g.getParticipants().isEmpty());
			
			for (Participant p : g.getParticipants()) {
				
				assertFalse(p.getActive());
			}
		}
		
		// perform request
		
		String jsonString = writer.writeValueAsString(ex);
		
		MvcResult res = mvc.perform(post(base+ "/start").contentType(json).content(jsonString))
				 		   .andExpect(status().isOk())
				 		   .andReturn();
		
		Experiment resBody = mapper.readValue(resString(res), Experiment.class);
		
		// check response body
		
		assertEquals(Experiment.Status.RUNNING, resBody.getStatus());
		assertTrue(timeApproxEquals(now, resBody.getStartTime()));
		
		for (TestGroup g : resBody.getTestGroups()) {		
			for (Participant p : g.getParticipants()) {			
				assertTrue(p.getActive());
			}
		}
		
		// check saved entity
		
		Experiment found = experimentRepo.findById(ex.getId());
		
		assertEquals(Experiment.Status.RUNNING, found.getStatus());
		assertTrue(timeApproxEquals(now, found.getStartTime()));
				
		for (TestGroup g : found.getTestGroups()) {		
			for (Participant p : g.getParticipants()) {			
				assertTrue(p.getActive());
			}
		}
	}

	
	@Test
	public void testStartExperiment2() throws Exception {
		
		Experiment ex = savedExperiments.get(0);
		ex.setStatus(Experiment.Status.NOT_READY);
		experimentRepo.save(ex);
		
		String jsonString = writer.writeValueAsString(ex);
		
		MvcResult res = mvc.perform(post(base + "/start").contentType(json).content(jsonString))
				 		   .andExpect(status().isUnprocessableEntity())
				 		   .andReturn();
		
		ApiError err = getError(res);
		
		assertEquals("ExperimentStatusException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(Experiment.Status.NOT_READY.toString()));
		assertTrue(err.getErrorMessage().contains(Experiment.Status.READY.toString()));
	}
	
	@Test
	public void testStartExperiment3() throws Exception {
		
		int badId = 999999;
		Experiment ex = savedExperiments.get(0);
		ex.setId(badId);
		String jsonString = writer.writeValueAsString(ex);
		
		MvcResult res = mvc.perform(post(base + "/start").contentType(json).content(jsonString))
				 		   .andExpect(status().isNotFound())
				 		   .andReturn();
		
		ApiError err = getError(res);
		
		assertEquals("NoSuchExperimentException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(Integer.toString(badId)));
	}
	
	@Test
	public void testStopExperiment1() throws Exception {
		
		Experiment ex = savedExperiments.get(0);
		LocalDateTime t0 = LocalDateTime.now();
		long dt = 2000; // ms
		
		// start experiment
		
		String jsonString = writer.writeValueAsString(ex);
		
		MvcResult res1 = mvc.perform(post(base + "/start").contentType(json).content(jsonString))
							.andExpect(status().isOk())
							.andReturn();
		
		Experiment started = mapper.readValue(resString(res1), Experiment.class);
		
		assertEquals(Experiment.Status.RUNNING, started.getStatus());
		assertTrue(timeApproxEquals(t0, started.getStartTime()));
		
		for (TestGroup g : started.getTestGroups()) {
			for (Participant p : g.getParticipants()) {
				assertTrue(p.getActive());
			}
		}
		
		Thread.sleep(dt);
		
		LocalDateTime t1 = LocalDateTime.now();
		
		// stop experiment
		
		jsonString = writer.writeValueAsString(started);
		
		MvcResult res2 = mvc.perform(post(base + "/stop").contentType(json).content(jsonString))
				  		    .andExpect(status().isOk())
				  		    .andReturn();
		
		Experiment stopped = mapper.readValue(resString(res2), Experiment.class);
		
		assertEquals(Experiment.Status.COMPLETE, stopped.getStatus());
		assertTrue(timeApproxEquals(t1, stopped.getEndTime()));
		assertTrue(Math.abs(dt - stopped.getDuration().toMillis()) < 100);
		
		for (TestGroup g : stopped.getTestGroups()) {
			for (Participant p : g.getParticipants()) {
				assertFalse(p.getActive());
			}
		}
	}
	
	@Test
	public void testStopExperiment2() throws Exception {
		
		Experiment ex = savedExperiments.get(0);
		Experiment.Status exStatus = ex.getStatus();
		assertNotEquals(Experiment.Status.RUNNING, exStatus);
		String jsonString = writer.writeValueAsString(ex);
		
		MvcResult res = mvc.perform(post(base + "/stop").contentType(json).content(jsonString))
				 		   .andExpect(status().isUnprocessableEntity())
				 		   .andReturn();
		
		ApiError err = getError(res);
		
		assertEquals("ExperimentStatusException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(Experiment.Status.RUNNING.toString()));
		assertTrue(err.getErrorMessage().contains(exStatus.toString()));
	}
	
	@Test
	public void testStopExperiment3() throws Exception {
		
		int badId = 999999;
		Experiment ex = savedExperiments.get(0);
		ex.setId(badId);
		String jsonString = writer.writeValueAsString(ex);
		
		MvcResult res = mvc.perform(post(base + "/stop").contentType(json).content(jsonString))
				 		   .andExpect(status().isNotFound())
				 		   .andReturn();
		
		ApiError err = getError(res);
		
		assertEquals("NoSuchExperimentException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(Integer.toString(badId)));
	}
	
	@Test
	public void testResetExperiment1() throws Exception {
		
		Experiment ex = savedExperiments.get(0);
		ex.setStatus(Experiment.Status.COMPLETE);
		experimentRepo.save(ex);
		String jsonString = writer.writeValueAsString(ex);
		
		MvcResult res = mvc.perform(post(base + "/reset").contentType(json).content(jsonString))
				 		   .andExpect(status().isOk())
				 		   .andReturn();
		
		Experiment resBody = mapper.readValue(resString(res), Experiment.class);
		
		assertEquals(Experiment.Status.READY, resBody.getStatus());
		
		Experiment found = experimentRepo.findById(ex.getId());
		assertEquals(Experiment.Status.READY, found.getStatus());
	}
	
	@Test
	public void testResetExperiment2() throws Exception {
		
		Experiment ex = savedExperiments.get(0);
		ex.setStatus(Experiment.Status.READY);
		experimentRepo.save(ex);
		String jsonString = writer.writeValueAsString(ex);
		
		MvcResult res = mvc.perform(post(base + "/reset").contentType(json).content(jsonString))
				 		   .andExpect(status().isUnprocessableEntity())
				 		   .andReturn();
		
		ApiError err = getError(res);
		
		assertEquals("ExperimentStatusException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(Experiment.Status.COMPLETE.toString()));
		assertTrue(err.getErrorMessage().contains(Experiment.Status.READY.toString()));
	}
	
	@Test
	public void testResetExperiment3() throws Exception {
		
		int badId = 999999;
		Experiment ex = savedExperiments.get(0);
		ex.setId(badId);
		String jsonString = writer.writeValueAsString(ex);
		
		MvcResult res = mvc.perform(post(base + "/reset").contentType(json).content(jsonString))
						   .andExpect(status().isNotFound())
						   .andReturn();
		
		ApiError err = getError(res);
		
		assertEquals("NoSuchExperimentException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(Integer.toString(badId)));
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
	
	private boolean timeApproxEquals(LocalDateTime t1, LocalDateTime t2) {
		
		if (t1 == null && t2 == null) {
			return true;
		}
		
		if (t1 == null || t2 == null) {
			return false;
		}
		
		return t1.getYear() == t2.getYear() &&
			   t1.getMonth() == t2.getMonth() &&
			   t1.getDayOfMonth() == t2.getDayOfMonth() &&
			   t1.getHour() == t2.getHour() &&
			   t1.getMinute() == t2.getMinute() &&
			   t1.getSecond() == t2.getSecond();
	}
}







