package ch.usi.hse.endpoints;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import org.springframework.http.MediaType;

import ch.usi.hse.db.entities.Administrator;
import ch.usi.hse.db.entities.Experimenter;
import ch.usi.hse.db.entities.Participant;
import ch.usi.hse.exceptions.ApiError;
import ch.usi.hse.exceptions.UserExistsException;
import ch.usi.hse.services.UserService;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(authorities="ADMIN")
public class AdminControllerUnitTest {

	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private UserService userService;
	
	private String base = "/admin";
	
	private ObjectMapper mapper;
	private ObjectWriter writer;
	private MediaType json;
	
	private List<Administrator> administrators;
	private List<Experimenter> experimenters; 
	private List<Participant> participants;
	private Administrator newAdministrator, existingAdministrator;
	private Experimenter newExperimenter, existingExperimenter;
	private Participant newParticipant, existingParticipant;
	
	@BeforeEach
	public void setUp() throws UserExistsException {
		
		mapper = new ObjectMapper();
		mapper.findAndRegisterModules();
		writer = mapper.writer().withDefaultPrettyPrinter();
		json = new MediaType(MediaType.APPLICATION_JSON.getType(), 
			       MediaType.APPLICATION_JSON.getSubtype(), 
			       Charset.forName("utf8"));
		
		administrators = new ArrayList<>();
		experimenters = new ArrayList<>();
		participants = new ArrayList<>();
		
		administrators.add(new Administrator(1, "admin1", "pwd1", new HashSet<>()));
		administrators.add(new Administrator(1, "admin2", "pwd2", new HashSet<>()));
		experimenters.add(new Experimenter(1, "exp1", "pwd3", new HashSet<>()));
		experimenters.add(new Experimenter(1, "exp2", "pwd4", new HashSet<>()));
		participants.add(new Participant(1, "part1", "pwd5", new HashSet<>()));
		participants.add(new Participant(1, "part2", "pwd6", new HashSet<>()));
		
		newAdministrator= administrators.get(0);
		existingAdministrator= administrators.get(1);
		newExperimenter = experimenters.get(0);
		existingExperimenter = experimenters.get(1); 
		newParticipant = participants.get(0);
		existingParticipant =participants.get(1);
		
		when(userService.allAdministrators()).thenReturn(administrators);
		when(userService.allExperimenters()).thenReturn(experimenters);
		when(userService.allParticipants()).thenReturn(participants);
		when(userService.addAdministrator(newAdministrator)).thenReturn(newAdministrator);
		when(userService.addExperimenter(newExperimenter)).thenReturn(newExperimenter);
		when(userService.addParticipant(newParticipant)).thenReturn(newParticipant);
		when(userService.addAdministrator(existingAdministrator)).thenThrow(UserExistsException.class);
		when(userService.addExperimenter(existingExperimenter)).thenThrow(UserExistsException.class);
		when(userService.addParticipant(existingParticipant)).thenThrow(UserExistsException.class);
	}
	
	@Test
	public void testGetAdminUi() throws Exception {
		
		mvc.perform(get(base + "/ui"))
		   .andExpect(status().isOk())
		   .andExpect(view().name("admin"))
		   .andExpect(model().attribute("administrators", is(administrators)))
		   .andExpect(model().attribute("experimenters", is(experimenters)))
		   .andExpect(model().attribute("participants", is(participants)));
	}
	
	@Test
	public void testPostAdministrator1() throws Exception {
		
		String url = base + "/administrators";
		String jsonString = writer.writeValueAsString(newAdministrator);
		
		MvcResult res = mvc.perform(post(url).contentType(json).content(jsonString))
						   .andExpect(status().isCreated())
						   .andReturn();
		
		Administrator resBody = mapper.readValue(resString(res), Administrator.class);
		
		assertEquals(newAdministrator, resBody);
	}
	
	@Test
	public void testAddAdministrator2() throws Exception {
		
		String url = base + "/administrators";
		String jsonString = writer.writeValueAsString(existingAdministrator);
		
		MvcResult res = mvc.perform(post(url).contentType(json).content(jsonString))
						   .andExpect(status().isBadRequest())
						   .andReturn();
		
		ApiError err = mapper.readValue(resString(res), ApiError.class);
		
		assertEquals("UserExistsException", err.getErrorType());
	}
	
	@Test
	public void testAddExperimenter1() throws Exception {
		
		String url = base + "/experimenters";
		String jsonString = writer.writeValueAsString(newExperimenter);
		
		MvcResult res = mvc.perform(post(url).contentType(json).content(jsonString))
				 		   .andExpect(status().isCreated())
				 		   .andReturn();
		
		Experimenter resBody = mapper.readValue(resString(res), Experimenter.class);
		
		assertEquals(newExperimenter, resBody);
	}
	
	@Test
	public void testAddExperimenter2() throws Exception {
		
		String url = base + "/experimenters";
		String jsonString = writer.writeValueAsString(existingExperimenter);
		
		MvcResult res = mvc.perform(post(url).contentType(json).content(jsonString))
				 		   .andExpect(status().isBadRequest())
				 		   .andReturn();
		
		ApiError err = mapper.readValue(resString(res), ApiError.class);
		assertEquals("UserExistsException", err.getErrorType());
	}
	
	@Test
	public void testAddPArticipant1() throws Exception {
		
		String url = base + "/participants";
		String jsonString = writer.writeValueAsString(newParticipant);
		
		MvcResult res = mvc.perform(post(url).contentType(json).content(jsonString))
				 		   .andExpect(status().isCreated())
				 		   .andReturn();
		
		Participant resBody = mapper.readValue(resString(res), Participant.class);
		
		assertEquals(newParticipant, resBody);
	}
	
	@Test
	public void testAddParticipant2() throws Exception {
		
		String url = base + "/participants";
		String jsonString = writer.writeValueAsString(existingParticipant);
		
		MvcResult res = mvc.perform(post(url).contentType(json).content(jsonString))
				 		   .andExpect(status().isBadRequest())
				 		   .andReturn();
		
		ApiError err = mapper.readValue(resString(res), ApiError.class);
		
		assertEquals("UserExistsException", err.getErrorType());
	}
	
	private String resString(MvcResult res) throws UnsupportedEncodingException {
		
		System.out.println("RESULT STRING:");
		System.out.println(res.getResponse().getContentAsString());
		
		return res.getResponse().getContentAsString();
	}
}













