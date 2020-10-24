package ch.usi.hse.endpoints;


import static org.junit.jupiter.api.Assertions.*;
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
import java.util.Set;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import ch.usi.hse.db.entities.Administrator;
import ch.usi.hse.db.entities.Experimenter;
import ch.usi.hse.db.entities.Participant;
import ch.usi.hse.db.entities.Role;
import ch.usi.hse.db.repositories.AdministratorRepository;
import ch.usi.hse.db.repositories.ExperimenterRepository;
import ch.usi.hse.db.repositories.ParticipantRepository;
import ch.usi.hse.db.repositories.RoleRepository;
import ch.usi.hse.db.repositories.UserRepository;
import ch.usi.hse.exceptions.ApiError;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(authorities="ADMIN")
public class AdminControllerIntegrationTest {

	@Autowired
	private MockMvc mvc;
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private AdministratorRepository administratorRepo;
	
	@Autowired
	private ExperimenterRepository experimenterRepo;
	
	@Autowired
	private ParticipantRepository participantRepo;
	
	@Autowired
	private RoleRepository roleRepo;
	
	private ObjectMapper mapper;
	private ObjectWriter writer;
	private MediaType json;
	
	private String base = "/admin";
	
	private List<Administrator> administrators;
	private List<Experimenter> experimenters; 
	private List<Participant> participants;
	
	private Set<Role> adminRoles, experimenterRoles, participantRoles;
	
	@BeforeEach
	public void setUp() {
		
		mapper = new ObjectMapper();
		mapper.findAndRegisterModules();
		writer = mapper.writer().withDefaultPrettyPrinter();
		json = new MediaType(MediaType.APPLICATION_JSON.getType(), 
			       MediaType.APPLICATION_JSON.getSubtype(), 
			       Charset.forName("utf8"));
		
		adminRoles = new HashSet<>();
		experimenterRoles = new HashSet<>();
		participantRoles = new HashSet<>();
		adminRoles.add(roleRepo.findByRole("ADMIN"));
		experimenterRoles.add(roleRepo.findByRole("EXPERIMENTER"));
		participantRoles.add(roleRepo.findByRole("PARTICIPANT"));
		
		administrators = new ArrayList<>();
		experimenters = new ArrayList<>();
		participants = new ArrayList<>();
		
		administrators.add(new Administrator("admin1", "pwd1"));
		administrators.add(new Administrator("admin2", "pwd2"));
		experimenters.add(new Experimenter("exp1", "pwd3"));
		experimenters.add(new Experimenter("exp2", "pwd"));
		participants.add(new Participant("part1", "pwd5"));
		participants.add(new Participant("part2", "pwd6"));
		
		userRepo.deleteAll();
		
		for (Administrator a : administrators) {
			
			a.setRoles(adminRoles);
			administratorRepo.save(a);
		}
		
		for (Experimenter e : experimenters) {
			
			e.setRoles(experimenterRoles);
			experimenterRepo.save(e);
		}
		
		for (Participant p : participants) {
			
			p.setRoles(participantRoles);
			participantRepo.save(p);
		}
	}
	
	@Test
	public void testGetAdminUi() throws Exception {
		
		mvc.perform(get(base + "/ui"))
		   .andExpect(status().isOk())
		   .andExpect(view().name("admin"))
		   .andExpect(model().attribute("administrators", Matchers.iterableWithSize(administrators.size())))
		   .andExpect(model().attribute("experimenters", Matchers.iterableWithSize(experimenters.size())))
		   .andExpect(model().attribute("participants", Matchers.iterableWithSize(participants.size())));
	}
 
	@Test // add a new Administrator
	public void testAddAdministrator() throws Exception {
		
		String url = base + "/administrators";
		Administrator newAdministrator = new Administrator("newAdmin", "pwd");
		String jsonString = writer.writeValueAsString(newAdministrator);
		
		MvcResult res = mvc.perform(post(url).contentType(json).content(jsonString))
				 			 .andExpect(status().isCreated())
				 			 .andReturn();
		
		Administrator resBody = mapper.readValue(resString(res), Administrator.class);
		
		assertEquals(newAdministrator, resBody);
		assertIterableEquals(adminRoles, resBody.getRoles());
	}
	
	@Test // attempt adding an Administrator with existing userName
	public void testAddAdministrator2() throws Exception {
		
		String url = base + "/administrators";
		String existingName = participants.get(0).getUserName();
		Administrator badAdmin = new Administrator(existingName, "pwd");
		String jsonString = writer.writeValueAsString(badAdmin);
		
		MvcResult res = mvc.perform(post(url).contentType(json).content(jsonString))
				 		   .andExpect(status().isBadRequest())
				 		   .andReturn();
		
		ApiError err = mapper.readValue(resString(res), ApiError.class);
		
		assertEquals("UserExistsException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(existingName));
	}
	
	@Test // add a new Experimenter
	public void testAddExperimenter1() throws Exception {
		
		String url = base + "/experimenters";
		Experimenter newExperimenter = new Experimenter("newName", "pwd");
		String jsonString = writer.writeValueAsString(newExperimenter);
		
		MvcResult res = mvc.perform(post(url).contentType(json).content(jsonString))
						   .andExpect(status().isCreated())
						   .andReturn();
		
		Experimenter resBody = mapper.readValue(resString(res), Experimenter.class);
		
		assertEquals(newExperimenter, resBody);
		assertIterableEquals(experimenterRoles, resBody.getRoles());
	}
	
	@Test // attempt adding an Experimenter with existing userName
	public void testAddExperimenter2() throws Exception {
		
		String url = base + "/experimenters";
		String existingName = administrators.get(0).getUserName();
		Experimenter badExperimenter = new Experimenter(existingName, "pwd");
		String jsonString = writer.writeValueAsString(badExperimenter);
		
		MvcResult res = mvc.perform(post(url).contentType(json).content(jsonString))
				 		   .andExpect(status().isBadRequest())
				 		   .andReturn();
		
		ApiError err = mapper.readValue(resString(res), ApiError.class);
		
		assertEquals("UserExistsException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(existingName));
	}
	
	@Test // add a new Participant
	public void testAddParticipant1() throws Exception {
		
		String url = base + "/participants";
		Participant newParticipant = new Participant("newName", "pwd");
		String jsonString = writer.writeValueAsString(newParticipant);
		
		MvcResult res = mvc.perform(post(url).contentType(json).content(jsonString))
				 		   .andExpect(status().isCreated())
				 		   .andReturn();
		
		Participant resBody = mapper.readValue(resString(res), Participant.class);
		
		assertEquals(newParticipant, resBody);
		assertIterableEquals(participantRoles, resBody.getRoles());
	}
	
	@Test // attempt adding a Participant with existing userName
	public void testAddParticipant2() throws Exception {
		
		String url = base + "/participants";
		String existingName = administrators.get(0).getUserName();
		Participant badParticipant = new Participant(existingName, "pwd");
		String jsonString = writer.writeValueAsString(badParticipant);
		
		MvcResult res = mvc.perform(post(url).contentType(json).content(jsonString))
				 		   .andExpect(status().isBadRequest())
				 		   .andReturn();
		
		ApiError err = mapper.readValue(resString(res), ApiError.class);
		
		assertEquals("UserExistsException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(existingName));
	}

	private String resString(MvcResult res) throws UnsupportedEncodingException {
		
		System.out.println("RESULT STRING:");
		System.out.println(res.getResponse().getContentAsString());
		
		return res.getResponse().getContentAsString();
	}
}










