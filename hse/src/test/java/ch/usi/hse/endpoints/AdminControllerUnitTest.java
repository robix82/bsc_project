package ch.usi.hse.endpoints;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import ch.usi.hse.exceptions.NoSuchUserException;
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
	private Administrator newAdministrator, existingAdministrator, existingNameAdministrator;
	private Experimenter newExperimenter, existingExperimenter, existingNameExperimenter;
	private Participant newParticipant, existingParticipant, existingNameParticipant;
	
	@BeforeEach
	public void setUp() throws UserExistsException, NoSuchUserException {
		
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
		administrators.add(new Administrator(2, "admin2", "pwd", new HashSet<>()));
		administrators.add(new Administrator(3, "admin3", "pwd", new HashSet<>()));
		experimenters.add(new Experimenter(4, "exp1", "pwd", new HashSet<>()));
		experimenters.add(new Experimenter(5, "exp2", "pwd", new HashSet<>()));
		experimenters.add(new Experimenter(6, "exp3", "pwd", new HashSet<>()));
		participants.add(new Participant(7, "part1", "pwd", new HashSet<>(), null));
		participants.add(new Participant(8, "part2", "pwd6", new HashSet<>(), null));
		participants.add(new Participant(9, "part3", "pwd6", new HashSet<>(), null));
		
		newAdministrator= administrators.get(0);
		existingAdministrator= administrators.get(1);
		existingNameAdministrator = administrators.get(2);
		newExperimenter = experimenters.get(0);
		existingExperimenter = experimenters.get(1); 
		existingNameExperimenter = experimenters.get(2);
		newParticipant = participants.get(0);
		existingParticipant = participants.get(1);
		existingNameParticipant = participants.get(2); 
	
		when(userService.allAdministrators()).thenReturn(administrators);
		when(userService.allExperimenters()).thenReturn(experimenters);
		when(userService.allParticipants()).thenReturn(participants);
		
		when(userService.addAdministrator(newAdministrator)).thenReturn(newAdministrator);
		when(userService.addExperimenter(newExperimenter)).thenReturn(newExperimenter);
		when(userService.addParticipant(newParticipant)).thenReturn(newParticipant);
		when(userService.addAdministrator(existingAdministrator)).thenThrow(UserExistsException.class);
		when(userService.addExperimenter(existingExperimenter)).thenThrow(UserExistsException.class);
		when(userService.addParticipant(existingParticipant)).thenThrow(UserExistsException.class);
		
		when(userService.updateAdministrator(existingAdministrator)).thenReturn(existingAdministrator);
		when(userService.updateAdministrator(newAdministrator)).thenThrow(NoSuchUserException.class);
		when(userService.updateAdministrator(existingNameAdministrator)).thenThrow(UserExistsException.class);
		when(userService.updateExperimenter(existingExperimenter)).thenReturn(existingExperimenter);
		when(userService.updateExperimenter(newExperimenter)).thenThrow(NoSuchUserException.class);
		when(userService.updateExperimenter(existingNameExperimenter)).thenThrow(UserExistsException.class);
		when(userService.updateParticipant(existingParticipant)).thenReturn(existingParticipant);
		when(userService.updateParticipant(newParticipant)).thenThrow(NoSuchUserException.class);
		when(userService.updateParticipant(existingNameParticipant)).thenThrow(UserExistsException.class);
		
		doThrow(NoSuchUserException.class).when(userService).removeUser(newAdministrator.getId());
		doThrow(NoSuchUserException.class).when(userService).removeUser(newExperimenter.getId());
		doThrow(NoSuchUserException.class).when(userService).removeUser(newParticipant.getId());
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
						   .andExpect(status().isUnprocessableEntity())
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
				 		   .andExpect(status().isUnprocessableEntity())
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
				 		   .andExpect(status().isUnprocessableEntity())
				 		   .andReturn();
		
		ApiError err = mapper.readValue(resString(res), ApiError.class);
		
		assertEquals("UserExistsException", err.getErrorType());
	}
	
	@Test
	public void testUpdateAdministrator1() throws Exception {
		
		String url = base + "/administrators";
		String jsonString = writer.writeValueAsString(existingAdministrator);
		
		MvcResult res = mvc.perform(put(url).contentType(json).content(jsonString))
				 	       .andExpect(status().isOk())
				 	       .andReturn();
		
		Administrator resBody = mapper.readValue(resString(res), Administrator.class);
		
		assertEquals(existingAdministrator, resBody);
	}
	
	@Test
	public void testUpdateAdministrator2() throws Exception {
		
		String url = base + "/administrators";
		String jsonString = writer.writeValueAsString(newAdministrator);
		
		MvcResult res = mvc.perform(put(url).contentType(json).content(jsonString))
				 		   .andExpect(status().isNotFound())
				 		   .andReturn();
		
		ApiError err = mapper.readValue(resString(res), ApiError.class);
		
		assertEquals("NoSuchUserException", err.getErrorType());
	}
	
	@Test
	public void testUpdateAdministrator3() throws Exception {
		
		String url = base + "/administrators";
		String jsonString = writer.writeValueAsString(existingNameAdministrator);
		
		MvcResult res = mvc.perform(put(url).contentType(json).content(jsonString))
				 		   .andExpect(status().isUnprocessableEntity())
				 		   .andReturn();
		
		ApiError err = mapper.readValue(resString(res), ApiError.class);
		
		assertEquals("UserExistsException", err.getErrorType());
	}
	
	@Test
	public void testUpdateExperimenter1() throws Exception {
		
		String url = base + "/experimenters";
		String jsonString = writer.writeValueAsString(existingExperimenter);
		
		MvcResult res = mvc.perform(put(url).contentType(json).content(jsonString))
				 		   .andExpect(status().isOk())
				 		   .andReturn();
		
		Experimenter resBody = mapper.readValue(resString(res), Experimenter.class);
		
		assertEquals(existingExperimenter, resBody);
	}
	
	@Test
	public void testUpdateExperimenter2() throws Exception {
		
		String url = base + "/experimenters";
		String jsonString = writer.writeValueAsString(newExperimenter);
		
		MvcResult res = mvc.perform(put(url).contentType(json).content(jsonString))
				 		   .andExpect(status().isNotFound())
				 		   .andReturn();
		
		ApiError err = mapper.readValue(resString(res), ApiError.class);
		
		assertEquals("NoSuchUserException", err.getErrorType());
	}
	
	@Test
	public void testUpdateExperimenter3() throws Exception {
		
		String url = base + "/experimenters";
		String jsonString = writer.writeValueAsString(existingNameExperimenter);
		
		MvcResult res = mvc.perform(put(url).contentType(json).content(jsonString))
				 		   .andExpect(status().isUnprocessableEntity())
				 		   .andReturn();
		
		ApiError err = mapper.readValue(resString(res), ApiError.class);
		
		assertEquals("UserExistsException", err.getErrorType());
	}
	
	@Test
	public void testUpdateParticipant1() throws Exception {
		
		String url = base + "/participants";
		String jsonString = writer.writeValueAsString(existingParticipant);
		
		MvcResult res = mvc.perform(put(url).contentType(json).content(jsonString))
						   .andExpect(status().isOk())
						   .andReturn();
		
		Participant resBody = mapper.readValue(resString(res), Participant.class);
		
		assertEquals(existingParticipant, resBody);
	}
	
	@Test
	public void testUpdateParticipant2() throws Exception {
		
		String url = base + "/participants";
		String jsonString = writer.writeValueAsString(newParticipant);
		
		MvcResult res = mvc.perform(put(url).contentType(json).content(jsonString))
				 		   .andExpect(status().isNotFound())
				 		   .andReturn();
		
		ApiError err = mapper.readValue(resString(res), ApiError.class);
		
		assertEquals("NoSuchUserException", err.getErrorType());
	}
	
	@Test
	public void testUpdateParticipant3() throws Exception {
		
		String url = base + "/participants";
		String jsonString = writer.writeValueAsString(existingNameParticipant);
		
		MvcResult res = mvc.perform(put(url).contentType(json).content(jsonString))
				 		   .andExpect(status().isUnprocessableEntity())
				 		   .andReturn();
		
		ApiError err = mapper.readValue(resString(res), ApiError.class);
		
		assertEquals("UserExistsException", err.getErrorType());
	}
	
	@Test
	public void testDeleteAdministrator1() throws Exception {
		
		String url = base + "/administrators";
		String jsonString = writer.writeValueAsString(existingAdministrator);
		
		MvcResult res = mvc.perform(delete(url).contentType(json).content(jsonString))
				 		   .andExpect(status().isOk())
				 		   .andReturn();
		
		Administrator resBody = mapper.readValue(resString(res), Administrator.class);
		
		assertEquals(existingAdministrator, resBody);
	}
	
	@Test
	public void testDeleteAdministrator2() throws Exception {
		
		String url = base + "/administrators";
		String jsonString = writer.writeValueAsString(newAdministrator);
		
		MvcResult res = mvc.perform(delete(url).contentType(json).content(jsonString))
				 		   .andExpect(status().isNotFound())
				 		   .andReturn();
		
		ApiError err = mapper.readValue(resString(res), ApiError.class);
		
		assertEquals("NoSuchUserException", err.getErrorType());
	}
	
	@Test
	public void testDeleteExperimenter1() throws Exception {
		
		String url = base + "/experimenters";
		String jsonString = writer.writeValueAsString(existingExperimenter);
		
		MvcResult res = mvc.perform(delete(url).contentType(json).content(jsonString))
				 		   .andExpect(status().isOk())
				 		   .andReturn();
		
		Experimenter resBody = mapper.readValue(resString(res), Experimenter.class);
		
		assertEquals(existingExperimenter, resBody);
	}
	
	@Test
	public void testDeleteExperimenter2() throws Exception {
		
		String url = base + "/experimenters";
		String jsonString = writer.writeValueAsString(newExperimenter);
		
		MvcResult res = mvc.perform(delete(url).contentType(json).content(jsonString))
				 		   .andExpect(status().isNotFound())
				 		   .andReturn();
		
		ApiError err = mapper.readValue(resString(res), ApiError.class);
		
		assertEquals("NoSuchUserException", err.getErrorType());
	}
	
	@Test
	public void testDeleteParticipant1() throws Exception {
		
		String url = base + "/participants";
		String jsonString = writer.writeValueAsString(existingParticipant);
		
		MvcResult res = mvc.perform(delete(url).contentType(json).content(jsonString))
				 		   .andExpect(status().isOk())
				 		   .andReturn();
		
		Participant resBody = mapper.readValue(resString(res), Participant.class);
		
		assertEquals(existingParticipant, resBody);
	}
	
	@Test
	public void testDeleteParticipant2() throws Exception {
		
		String url = base + "/participants";
		String jsonString = writer.writeValueAsString(newParticipant);
		
		MvcResult res = mvc.perform(delete(url).contentType(json).content(jsonString))
				 		   .andExpect(status().isNotFound())
				 		   .andReturn();
		
		ApiError err = mapper.readValue(resString(res), ApiError.class);
		
		assertEquals("NoSuchUserException", err.getErrorType());
	}
	 
	@Test
	public void testDeleteAllExperimenters() throws Exception {
		
		String url = base + "/experimenters/all";
		
		MvcResult res = mvc.perform(delete(url))
				 		   .andExpect(status().isOk())
				 		   .andReturn();
		
		assertEquals("Experimenters Cleared", resString(res));
	}
	
	@Test
	public void testDeleteAllParticipants() throws Exception {
		
		String url = base + "/participants/all";
		
		MvcResult res = mvc.perform(delete(url))
				 		   .andExpect(status().isOk())
				 		   .andReturn();
		
		assertEquals("Participants Cleared", resString(res));
	}
	
	/////////////
	
	private String resString(MvcResult res) throws UnsupportedEncodingException {
		
		return res.getResponse().getContentAsString();
	}
}













