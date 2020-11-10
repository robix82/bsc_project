package ch.usi.hse.endpoints;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import ch.usi.hse.services.ExperimentService;
import ch.usi.hse.services.UserService;
import ch.usi.hse.exceptions.*;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class ExperimentsControllerUnitTest {

	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private ExperimentService experimentService;
	
	@MockBean
	private UserService userService;
	
	private String base = "/experiments";
	
	private ObjectMapper mapper;
	private ObjectWriter writer;
	private MediaType json;
	
	private List<Experiment> savedExperiments;
	private List<Experimenter> savedExperimenters;
	private Experiment newExperiment;
	
	private List<DocCollection> docCollections;
	private List<String> configFileNames;
	private String newConfigFileName;
	private byte[] configData1, configData2, newConfigData;
	private MockMultipartFile newConfigFile;
	
	@BeforeEach
	public void setUp() throws Exception {
		
		mapper = new ObjectMapper();
		mapper.findAndRegisterModules();
		writer = mapper.writer().withDefaultPrettyPrinter();
		json = new MediaType(MediaType.APPLICATION_JSON.getType(), 
			       MediaType.APPLICATION_JSON.getSubtype(), 
			       Charset.forName("utf8"));
		
		configFileNames = List.of("config1", "config2");
		newConfigFileName = "newConfig";
		
		// EXPERIMENTS AND EXPERIMENTERS
		
		Experimenter experimenter1 = new Experimenter("experimenter1", "pwd");
		Experimenter experimenter2 = new Experimenter("experimenter2", "pwd");
		experimenter1.setId(1);
		experimenter2.setId(2);
		
		Experiment e1 = new Experiment("e1");
		Experiment e2 = new Experiment("e2");
		Experiment e3 = new Experiment("e3");
		Experiment e4 = new Experiment("e4");
		newExperiment = new Experiment("newExperiment");
		e1.setId(1);
		e2.setId(2);
		e3.setId(3);
		e4.setId(4);
		newExperiment.setId(5);
		
		experimenter1.addExperiment(e1);
		experimenter1.addExperiment(e2);
		experimenter2.addExperiment(e3);
		experimenter2.addExperiment(e4);
		
		savedExperiments = List.of(e1, e2, e3, e4);
		savedExperimenters = List.of(experimenter1, experimenter2);
		
		when(userService.allExperimenters()).thenReturn(savedExperimenters);
		when(experimentService.allExperiments()).thenReturn(savedExperiments);
		when(experimentService.addExperiment(newExperiment)).thenReturn(newExperiment);
		
		when(experimentService.findExperiment(newExperiment.getId())).thenThrow(
				new NoSuchExperimentException(newExperiment.getId()));
		
		when(experimentService.updateExperiment(newExperiment)).thenThrow(
				new NoSuchExperimentException(newExperiment.getId()));
		
		doThrow(new NoSuchExperimentException(newExperiment.getId()))
			.when(experimentService).deleteExperiment(newExperiment);
	
		when(experimentService.configureTestGroups(newExperiment, newConfigFileName))
				.thenThrow(new NoSuchFileException(newConfigFileName));
		
		when(experimentService.configureTestGroups(newExperiment, configFileNames.get(0)))
		.thenThrow(new NoSuchExperimentException(newExperiment.getId()));
		
		for (Experiment e : savedExperiments) {
			
			when(experimentService.findExperiment(e.getId())).thenReturn(e);	
			when(experimentService.addExperiment(e)).thenThrow(new ExperimentExistsException(e.getId()));
			when(experimentService.updateExperiment(e)).thenReturn(e);
			doNothing().when(experimentService).deleteExperiment(e);
			
			when(experimentService.configureTestGroups(e, newConfigFileName))
				.thenThrow(new NoSuchFileException(newConfigFileName));
			when(experimentService.configureTestGroups(e, configFileNames.get(0))).thenReturn(e);
		}
		
		// DOC COLLECTIONS
		
		DocCollection dc1 = new DocCollection("dc1", "list1");
		DocCollection dc2 = new DocCollection("dc2", "list2");
		dc1.setId(1);
		dc2.setId(2);
		docCollections = List.of(dc1, dc2);
		
		when(experimentService.getDocCollections()).thenReturn(docCollections);
		
		// CONFIG FILES
		
		
		
		configData1 = "data1".getBytes();
		configData2 = "data2".getBytes();
		newConfigData = "newData".getBytes();
		
		newConfigFile = new MockMultipartFile("file",
											  newConfigFileName,
											  MediaType.TEXT_PLAIN_VALUE,
											  newConfigData);
		
		when(experimentService.savedConfigFiles()).thenReturn(configFileNames);		
		doNothing().when(experimentService).addConfigFile(any(MockMultipartFile.class));
		doNothing().when(experimentService).removeConfigFile("config1");
		doNothing().when(experimentService).removeConfigFile("config1");
		doThrow(new NoSuchFileException(newConfigFileName)).when(experimentService).removeConfigFile(newConfigFileName);
		when(experimentService.getConfigFile("config1")).thenReturn(new ByteArrayInputStream(configData1));
		when(experimentService.getConfigFile("config2")).thenReturn(new ByteArrayInputStream(configData2));
		when(experimentService.getConfigFile(newConfigFileName)).thenThrow(new NoSuchFileException(newConfigFileName));
		
		
	}
	
	// UI CONTROLLERS
	
	@Test
	public void testGetExperimentsUi() throws Exception {
		
		mvc.perform(get(base + "/ui"))
		   .andExpect(status().isOk())
		   .andExpect(view().name("experiments"))
		   .andExpect(model().attribute("experiments", is(savedExperiments)))
		   .andExpect(model().attribute("experimenters", is(savedExperimenters)));
	}
	
	@Test
	public void testGetExperimentsSetupUi() throws Exception {
		
		Experiment exp = savedExperiments.get(0);
		
		String url = UriComponentsBuilder.fromUriString(base + "/setup/ui")
				 						 .queryParam("expId", exp.getId())
				 						 .build()
				 						 .toUriString();
		
		mvc.perform(get(url))
		   .andExpect(status().isOk())
		   .andExpect(view().name("exp_setup"))
		   .andExpect(model().attribute("experiment", is(exp)))
		   .andExpect(model().attribute("docCollections", is(docCollections)))
		   .andExpect(model().attribute("configFiles", is(configFileNames)));
	}
	
	@Test
	public void testGetExperimentsRunUi() throws Exception {
		
		Experiment exp = savedExperiments.get(0);
		
		String url = UriComponentsBuilder.fromUriString(base + "/run/ui")
				 						 .queryParam("expId", exp.getId())
				 						 .build()
				 						 .toUriString();
		
		mvc.perform(get(url))
		   .andExpect(status().isOk())
		   .andExpect(view().name("exp_run"))
		   .andExpect(model().attribute("experiment", is(exp)));
	}
	
	@Test
	public void testGetExperimentsEvalUi() throws Exception {
		
		Experiment exp = savedExperiments.get(0);
		
		String url = UriComponentsBuilder.fromUriString(base + "/eval/ui")
				 						 .queryParam("expId", exp.getId())
				 						 .build()
				 						 .toUriString();
		
		mvc.perform(get(url))
		   .andExpect(status().isOk())
		   .andExpect(view().name("exp_eval"))
		   .andExpect(model().attribute("experiment", is(exp)));
	}
	
	// EXPERIMENTS REST API
	
	@Test
	public void testPostExperiment1() throws Exception {

		String jsonString = writer.writeValueAsString(newExperiment);
		
		MvcResult res = mvc.perform(post(base + "/").contentType(json).content(jsonString))
						   .andExpect(status().isCreated())
						   .andReturn();
		
		Experiment resBody = mapper.readValue(resString(res), Experiment.class);
		
		assertEquals(newExperiment, resBody); 
	}

	@Test
	public void testAddExperiment2() throws Exception {
		
		int existingId = savedExperiments.get(0).getId();
		newExperiment.setId(existingId);
		String jsonString = writer.writeValueAsString(newExperiment);
		
		MvcResult res = mvc.perform(post(base + "/").contentType(json).content(jsonString))
				 		   .andExpect(status().isUnprocessableEntity())
				 		   .andReturn();
		
		ApiError err = getError(res);
		
		assertEquals("ExperimentExistsException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(Integer.toString(existingId)));
	}
	
	@Test
	public void testUpdateExperiment1() throws Exception {
		
		String jsonString = writer.writeValueAsString(savedExperiments.get(0));
		
		MvcResult res = mvc.perform(put(base + "/").contentType(json).content(jsonString))
				 		   .andExpect(status().isOk())
				 		   .andReturn();
		
		Experiment resBody = mapper.readValue(resString(res), Experiment.class);
		
		assertEquals(savedExperiments.get(0), resBody);
	}
	
	@Test
	public void testUpdateExperiment2() throws Exception {
		
		String jsonString = writer.writeValueAsString(newExperiment);
		
		MvcResult res = mvc.perform(put(base + "/").contentType(json).content(jsonString))
				 		   .andExpect(status().isNotFound())
				 		   .andReturn();
		
		ApiError err = getError(res);
		
		assertEquals("NoSuchExperimentException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(Integer.toString(newExperiment.getId())));
	}
	
	@Test
	public void testDeleteExperiment1() throws Exception {
		
		String jsonString = writer.writeValueAsString(savedExperiments.get(0));
		
		MvcResult res = mvc.perform(delete(base + "/").contentType(json).content(jsonString))
				 		   .andExpect(status().isOk())
				 		   .andReturn();
		
		Experiment resBody = mapper.readValue(resString(res), Experiment.class);
		
		assertEquals(savedExperiments.get(0), resBody);
	}
	
	@Test
	public void testDeleteExperiment2() throws Exception {
		
		String jsonString = writer.writeValueAsString(newExperiment);
		
		MvcResult res = mvc.perform(delete(base + "/").contentType(json).content(jsonString))
				 		   .andExpect(status().isNotFound())
				 		   .andReturn();
		
		ApiError err = getError(res);
		
		assertEquals("NoSuchExperimentException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(Integer.toString(newExperiment.getId())));
	}
	
	// CONFIG FILE REST API
	
	@Test
	public void testUploadConfigFile() throws Exception {
		
		String expectedMsg = "file " + newConfigFileName + " uploaded";
		
		MvcResult res = mvc.perform(multipart(base + "/config").file(newConfigFile))
				 		   .andExpect(status().isCreated())
				 		   .andReturn();
		
		assertEquals(expectedMsg, resString(res));
	}
	
	@Test
	public void testDeleteConfigFile1() throws Exception {
		
		String expectedMsg = "file " + configFileNames.get(0) + " deleted";
		
		String url = UriComponentsBuilder.fromUriString(base + "/config")
				 						 .queryParam("fileName", configFileNames.get(0))
				 						 .build()
				 						 .toUriString();
		
		MvcResult res = mvc.perform(delete(url))
				 		   .andExpect(status().isOk())
				 		   .andReturn();
		
		assertEquals(expectedMsg, resString(res));
	}
	
	@Test
	public void testDeleteConfigFile2() throws Exception {
		
		String url = UriComponentsBuilder.fromUriString(base + "/config")
										 .queryParam("fileName", newConfigFileName)
										 .build()
										 .toUriString();
		
		MvcResult res = mvc.perform(delete(url))
				 		   .andExpect(status().isNotFound())
				 		   .andReturn();
		
		ApiError err = getError(res);
		
		assertEquals("NoSuchFileException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(newConfigFileName));
	}
	
	@Test
	public void testDownloadConfigFile1() throws Exception {
		
		String url = UriComponentsBuilder.fromUriString(base + "/config/dl")
									     .queryParam("fileName", configFileNames.get(0))
									     .build()
									     .toUriString();
		
		MvcResult res = mvc.perform(get(url))
				   		   .andExpect(status().isOk())
				   		   .andReturn();
		
		byte[] resBody = res.getResponse().getContentAsByteArray();
		
		assertArrayEquals(configData1, resBody);
	}
	
	@Test
	public void testDownloadConfigFile2() throws Exception {
		
		String url = UriComponentsBuilder.fromUriString(base + "/config/dl")
									     .queryParam("fileName", newConfigFileName)
									     .build()
									     .toUriString();
		
		MvcResult res = mvc.perform(get(url))
				 		   .andExpect(status().isNotFound())
				 		   .andReturn();
		
		ApiError err = getError(res);
		
		assertEquals("NoSuchFileException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(newConfigFileName));
	}
	
	@Test
	public void testConfigureExperiment1() throws Exception {
		
		Experiment experiment = savedExperiments.get(0);
		String fileName = configFileNames.get(0);
		
		String url = UriComponentsBuilder.fromUriString(base + "/testGroups")
				 						 .queryParam("configFileName", fileName)
				 						 .build()
				 						 .toUriString();
		
		String jsonString = writer.writeValueAsString(experiment);
		
		MvcResult res = mvc.perform(post(url).contentType(json).content(jsonString))
				 		   .andExpect(status().isOk())
				 		   .andReturn();
		
		Experiment resBody = mapper.readValue(resString(res), Experiment.class);
		
		assertEquals(experiment, resBody);
	}
	
	@Test
	public void testConfigureExperiment2() throws Exception {
		
		String url = UriComponentsBuilder.fromUriString(base + "/testGroups")
										 .queryParam("configFileName", newConfigFileName)
										 .build()
										 .toUriString();

		String jsonString = writer.writeValueAsString(savedExperiments.get(0));
		
		MvcResult res = mvc.perform(post(url).contentType(json).content(jsonString))
				 		   .andExpect(status().isNotFound())
				 		   .andReturn();
		
		ApiError err = getError(res);
		
		assertEquals("NoSuchFileException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(newConfigFileName));
	}
	
	@Test
	public void testConfigureExperiment3() throws Exception {
		
		String fileName = configFileNames.get(0);
		
		String url = UriComponentsBuilder.fromUriString(base + "/testGroups")
				 						 .queryParam("configFileName", fileName)
				 						 .build()
				 						 .toUriString();
		
		String jsonString = writer.writeValueAsString(newExperiment);
		
		MvcResult res = mvc.perform(post(url).contentType(json).content(jsonString))
				 		   .andExpect(status().isNotFound())
				 		   .andReturn();
		
		ApiError err = getError(res);
		
		assertEquals("NoSuchExperimentException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(Integer.toString(newExperiment.getId())));
	}
	
	//////////////////////
	
	private String resString(MvcResult res) throws UnsupportedEncodingException {
		
		return res.getResponse().getContentAsString();
	}
	
	private ApiError getError(MvcResult res) throws Exception {
		
		return mapper.readValue(resString(res), ApiError.class);
	}
}








