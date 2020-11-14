package ch.usi.hse.endpoints;


import static org.junit.jupiter.api.Assertions.*;
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
import ch.usi.hse.db.entities.TestGroup;
import ch.usi.hse.db.repositories.AdministratorRepository;
import ch.usi.hse.db.repositories.ExperimenterRepository;
import ch.usi.hse.db.repositories.ParticipantRepository;
import ch.usi.hse.db.repositories.RoleRepository;
import ch.usi.hse.db.repositories.TestGroupRepository;
import ch.usi.hse.db.repositories.UserRepository;
import ch.usi.hse.exceptions.ApiError;
import ch.usi.hse.exceptions.UserExistsException;
import ch.usi.hse.services.UserService;

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
	private TestGroupRepository groupRepo;
	
	@Autowired
	private RoleRepository roleRepo;
	
	@Autowired
	private UserService userService;
	
	private ObjectMapper mapper;
	private ObjectWriter writer;
	private MediaType json;
	
	private String base = "/admin";
	
	private List<Administrator> administrators;
	private List<Experimenter> experimenters; 
	private List<Participant> participants;
	 
	private Set<Role> adminRoles, experimenterRoles, participantRoles;
	
	private TestGroup testGroup;
	
	@BeforeEach
	public void setUp() throws UserExistsException {
		
		mapper = new ObjectMapper();
		mapper.findAndRegisterModules();
		writer = mapper.writer().withDefaultPrettyPrinter();
		json = new MediaType(MediaType.APPLICATION_JSON.getType(), 
			       MediaType.APPLICATION_JSON.getSubtype(), 
			       Charset.forName("utf8"));
		
		adminRoles = new HashSet<>();
		experimenterRoles = new HashSet<>();
		participantRoles = new HashSet<>();
		adminRoles.add(roleRepo.save(new Role(1, "ADMIN")));
		experimenterRoles.add(roleRepo.save(new Role(2, "EXPERIMENTER")));
		participantRoles.add(roleRepo.save(new Role(3, "PARTICIPANT")));
		
		List<Administrator> _administrators = new ArrayList<>();
		List<Experimenter> _experimenters = new ArrayList<>();
		List<Participant> _participants = new ArrayList<>();
		TestGroup _testGroup = new TestGroup("g");
		
		_administrators.add(new Administrator("admin1", "pwd1"));
		_administrators.add(new Administrator("admin2", "pwd2"));
		_experimenters.add(new Experimenter("exp1", "pwd3"));
		_experimenters.add(new Experimenter("exp2", "pwd"));
		_participants.add(new Participant("part1", "pwd5"));
		_participants.add(new Participant("part2", "pwd6"));
		
		userRepo.deleteAll();
		groupRepo.deleteAll();

		administrators = new ArrayList<>();
		experimenters = new ArrayList<>();
		participants = new ArrayList<>(); 

		for (Administrator a : _administrators) {

			administrators.add(userService.addAdministrator(a));
		}
		
		for (Experimenter e : _experimenters) {
 
			experimenters.add(userService.addExperimenter(e));
		}
		
		for (Participant p : _participants) {

			participants.add(userService.addParticipant(p));
		}
	
	//	_testGroup.setParticipants(new HashSet<>(participants));
		
		testGroup = groupRepo.save(_testGroup);
		testGroup.setParticipants(new HashSet<>(participants));
		groupRepo.save(testGroup);
	}
	
	@Test
	public void testSetup() {
		
		assertIterableEquals(administrators, administratorRepo.findAll());
		assertIterableEquals(experimenters, experimenterRepo.findAll());
		assertIterableEquals(participants, participantRepo.findAll());
		assertIterableEquals(participants, testGroup.getParticipants());
		
		for (Administrator a : administrators) {
			assertIterableEquals(adminRoles, a.getRoles());
		}
		
		for (Experimenter e : experimenters) {
			assertIterableEquals(experimenterRoles, e.getRoles());
		}
		
		for (Participant p : participants) {
			assertIterableEquals(participantRoles, p.getRoles());
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
	public void testAddAdministrator1() throws Exception {
		
		long oldCount = administratorRepo.count();
		
		String url = base + "/administrators";
		Administrator newAdministrator = new Administrator("newAdmin", "pwd");
		String jsonString = writer.writeValueAsString(newAdministrator);
		
		MvcResult res = mvc.perform(post(url).contentType(json).content(jsonString))
				 			 .andExpect(status().isCreated())
				 			 .andReturn();
		
		Administrator resBody = mapper.readValue(resString(res), Administrator.class);
		
		assertEquals(newAdministrator, resBody);
		assertIterableEquals(adminRoles, resBody.getRoles());
		
		long newCount = administratorRepo.count();
		
		assertEquals(oldCount + 1, newCount);
		assertEquals(newAdministrator, administratorRepo.findByUserName(newAdministrator.getUserName()));
	}
	
	@Test // attempt adding an Administrator with existing userName
	public void testAddAdministrator2() throws Exception {
		
		long oldCount = administratorRepo.count();
		
		String url = base + "/administrators";
		String existingName = participants.get(0).getUserName();
		Administrator badAdmin = new Administrator(existingName, "pwd");
		String jsonString = writer.writeValueAsString(badAdmin);
		
		MvcResult res = mvc.perform(post(url).contentType(json).content(jsonString))
				 		   .andExpect(status().isUnprocessableEntity())
				 		   .andReturn();
		
		ApiError err = mapper.readValue(resString(res), ApiError.class);
		
		assertEquals("UserExistsException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(existingName));
		
		long newCount = administratorRepo.count();
		
		assertEquals(oldCount, newCount);
	}
	
	@Test // add a new Experimenter
	public void testAddExperimenter1() throws Exception {
		
		long oldCount = experimenterRepo.count();
		
		String url = base + "/experimenters";
		Experimenter newExperimenter = new Experimenter("newName", "pwd");
		String jsonString = writer.writeValueAsString(newExperimenter);
		
		MvcResult res = mvc.perform(post(url).contentType(json).content(jsonString))
						   .andExpect(status().isCreated())
						   .andReturn();
		
		Experimenter resBody = mapper.readValue(resString(res), Experimenter.class);
		
		assertEquals(newExperimenter, resBody);
		assertIterableEquals(experimenterRoles, resBody.getRoles());
		
		long newCount = experimenterRepo.count();
		
		assertEquals(oldCount +1, newCount);
		assertEquals(newExperimenter, experimenterRepo.findByUserName(newExperimenter.getUserName()));
	}
	
	@Test // attempt adding an Experimenter with existing userName
	public void testAddExperimenter2() throws Exception {
		
		long oldCount = experimenterRepo.count();
		
		String url = base + "/experimenters";
		String existingName = administrators.get(0).getUserName();
		Experimenter badExperimenter = new Experimenter(existingName, "pwd");
		String jsonString = writer.writeValueAsString(badExperimenter);
		
		MvcResult res = mvc.perform(post(url).contentType(json).content(jsonString))
				 		   .andExpect(status().isUnprocessableEntity())
				 		   .andReturn();
		
		ApiError err = mapper.readValue(resString(res), ApiError.class);
		
		assertEquals("UserExistsException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(existingName));
		
		long newCount = experimenterRepo.count();
		
		assertEquals(oldCount, newCount);
	}
	
	@Test // add a new Participant
	public void testAddParticipant1() throws Exception {
		
		long oldCount = participantRepo.count();
		
		String url = base + "/participants";
		Participant newParticipant = new Participant("newName", "pwd");
		String jsonString = writer.writeValueAsString(newParticipant);
		
		MvcResult res = mvc.perform(post(url).contentType(json).content(jsonString))
				 		   .andExpect(status().isCreated())
				 		   .andReturn();
		
		Participant resBody = mapper.readValue(resString(res), Participant.class);
		
		assertEquals(newParticipant, resBody);
		assertIterableEquals(participantRoles, resBody.getRoles());
		
		long newCount = participantRepo.count();
		
		assertEquals(oldCount +1, newCount);
		assertEquals(newParticipant, participantRepo.findByUserName(newParticipant.getUserName()));
	}
	
	@Test // attempt adding a Participant with existing userName
	public void testAddParticipant2() throws Exception {
		
		long oldCount = participantRepo.count();
		
		String url = base + "/participants";
		String existingName = administrators.get(0).getUserName();
		Participant badParticipant = new Participant(existingName, "pwd");
		String jsonString = writer.writeValueAsString(badParticipant);
		
		MvcResult res = mvc.perform(post(url).contentType(json).content(jsonString))
				 		   .andExpect(status().isUnprocessableEntity())
				 		   .andReturn();
		
		ApiError err = mapper.readValue(resString(res), ApiError.class);
		
		assertEquals("UserExistsException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(existingName));
		
		long newCount = participantRepo.count();
		
		assertEquals(oldCount, newCount);
	}
	
	@Test
	public void testUpdateAdministrator1() throws Exception {
		
		
		String url = base + "/administrators";
		
		Administrator admin = administrators.get(0);
		String oldName = admin.getUserName();
		String newName = "newName";
		admin.setUserName(newName);
		String jsonString = writer.writeValueAsString(admin);
		
		assertTrue(administratorRepo.existsByUserName(oldName));
		assertFalse(administratorRepo.existsByUserName(newName));
		
		MvcResult res = mvc.perform(put(url).contentType(json).content(jsonString))
				 		   .andExpect(status().isOk())
				 		   .andReturn();
		
		Administrator resBody = mapper.readValue(resString(res), Administrator.class);
		
		assertEquals(newName, resBody.getUserName());
		assertIterableEquals(adminRoles, admin.getRoles());
		
		assertFalse(administratorRepo.existsByUserName(oldName));
		assertTrue(administratorRepo.existsByUserName(newName));
	}
	
	@Test
	public void testUpdateAdministrator2() throws Exception {
		
		String url = base + "/administrators";
		
		Administrator admin = administrators.get(0);
		String oldName = admin.getUserName();
		String newName = participants.get(0).getUserName();
		admin.setUserName(newName);
		String jsonString = writer.writeValueAsString(admin);
		
		assertTrue(administratorRepo.existsByUserName(oldName));
		
		MvcResult res = mvc.perform(put(url).contentType(json).content(jsonString))
				 		   .andExpect(status().isUnprocessableEntity())
				 		   .andReturn();
		
		ApiError err = mapper.readValue(resString(res), ApiError.class);
		
		assertEquals("UserExistsException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(newName));
		
		assertTrue(administratorRepo.existsByUserName(oldName));
	}
	
	@Test
	public void testUpdateAdministrator3() throws Exception {
		
		String url = base + "/administrators";
		int badId = administrators.get(0).getId() + 999;
		Administrator admin = new Administrator(badId, "newAdmin", "pwd", adminRoles);
		String jsonString = writer.writeValueAsString(admin);
		
		assertFalse(userRepo.existsById(badId));

		MvcResult res = mvc.perform(put(url).contentType(json).content(jsonString))
				 		   .andExpect(status().isNotFound())
				 		   .andReturn();
		
		ApiError err = mapper.readValue(resString(res), ApiError.class);
		
		assertEquals("NoSuchUserException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(Integer.toString(badId)));
		
		assertFalse(userRepo.existsById(badId));
	}

	@Test
	public void testUpdateExperimenter1() throws Exception {
		
		String url = base + "/experimenters";
		
		Experimenter experimenter = experimenters.get(0);
		String oldName = experimenter.getUserName();
		String newName = "newName";
		experimenter.setUserName(newName);
		String jsonString = writer.writeValueAsString(experimenter);
		
		assertTrue(experimenterRepo.existsByUserName(oldName));
		assertFalse(experimenterRepo.existsByUserName(newName));
		
		MvcResult res = mvc.perform(put(url).contentType(json).content(jsonString))
					 	   .andExpect(status().isOk())
					 	   .andReturn();
		
		Experimenter resBody = mapper.readValue(resString(res), Experimenter.class);
		
		assertEquals(experimenter, resBody);
		assertIterableEquals(experimenterRoles, resBody.getRoles());
		
		assertFalse(experimenterRepo.existsByUserName(oldName));
		assertTrue(experimenterRepo.existsByUserName(newName));
	}
	
	@Test
	public void testUpdateExperimenter2() throws Exception {
		
		String url = base + "/experimenters";
		
		Experimenter experimenter = experimenters.get(0);
		String oldName = experimenter.getUserName();
		String newName = participants.get(0).getUserName();
		experimenter.setUserName(newName);
		String jsonString = writer.writeValueAsString(experimenter);
		
		assertTrue(experimenterRepo.existsByUserName(oldName));
		
		MvcResult res = mvc.perform(put(url).contentType(json).content(jsonString))
				 		   .andExpect(status().isUnprocessableEntity())
				 		   .andReturn();
		
		ApiError err = mapper.readValue(resString(res), ApiError.class);
		
		assertEquals("UserExistsException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(newName));
		
		assertTrue(experimenterRepo.existsByUserName(oldName));
	}
	
	@Test
	public void testUpdateExperimenter3() throws Exception {
		
		String url = base + "/experimenters";
		int badId = administrators.get(0).getId() + 999;
		Experimenter experimenter = new Experimenter(badId, "exp", "pwd", experimenterRoles);
		String jsonString = writer.writeValueAsString(experimenter);
		
		assertFalse(experimenterRepo.existsById(badId));
		
		MvcResult res = mvc.perform(put(url).contentType(json).content(jsonString))
				 		   .andExpect(status().isNotFound())
				 		   .andReturn();
		
		ApiError err = mapper.readValue(resString(res), ApiError.class);
		
		assertEquals("NoSuchUserException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(Integer.toString(badId)));
		
		assertFalse(experimenterRepo.existsById(badId));
	}
	
	@Test
	public void testUpdateParticipant1() throws Exception {
		
		String url = base + "/participants";
		
		Participant participant = participants.get(0);
		String oldName = participant.getUserName();
		String newName = "newName";
		participant.setUserName(newName);
		String jsonString = writer.writeValueAsString(participant);
		
		assertTrue(participantRepo.existsByUserName(oldName));
		assertFalse(participantRepo.existsByUserName(newName));
		
		MvcResult res = mvc.perform(put(url).contentType(json).content(jsonString))
				 		   .andExpect(status().isOk())
				 		   .andReturn();
		
		Participant resBody = mapper.readValue(resString(res), Participant.class);
		
		assertEquals(participant, resBody);
		assertIterableEquals(participantRoles, resBody.getRoles());
		
		assertFalse(participantRepo.existsByUserName(oldName));
		assertTrue(participantRepo.existsByUserName(newName));
	}
	
	@Test
	public void testUpdateParticipant2() throws Exception {
		
		String url = base + "/participants";
		
		Participant participant = participants.get(0);
		String oldName = participant.getUserName();
		String newName = administrators.get(0).getUserName();
		participant.setUserName(newName);
		String jsonString = writer.writeValueAsString(participant);
		
		assertTrue(participantRepo.existsByUserName(oldName));
		
		MvcResult res = mvc.perform(put(url).contentType(json).content(jsonString))
						   .andExpect(status().isUnprocessableEntity())
						   .andReturn();
		
		ApiError err = mapper.readValue(resString(res), ApiError.class);
		
		assertEquals("UserExistsException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(newName));
		
		assertTrue(participantRepo.existsByUserName(oldName));
	}
	
	@Test
	public void testUpdateParticipant3() throws Exception {
		
		String url = base + "/participants";
		int badId = participants.get(0).getId() + 999;
		Participant participant = new Participant(badId, "p", "pwd", participantRoles, null);
		String jsonString = writer.writeValueAsString(participant);
		
		assertFalse(participantRepo.existsById(badId));
		
		MvcResult res = mvc.perform(put(url).contentType(json).content(jsonString))
				 		   .andExpect(status().isNotFound())
				 		   .andReturn();
		
		ApiError err = mapper.readValue(resString(res), ApiError.class);
		
		assertEquals("NoSuchUserException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(Integer.toString(badId)));
		
		assertFalse(participantRepo.existsById(badId));
	}

	@Test
	public void testDeleteAdministrator1() throws Exception {
		
		String url = base + "/administrators";
		
		Administrator administrator = administrators.get(0);
		int id = administrator.getId();
		String jsonString = writer.writeValueAsString(administrator);
		
		long oldCount = administratorRepo.count();
		
		assertTrue(administratorRepo.existsById(id));
		
		MvcResult res = mvc.perform(delete(url).contentType(json).content(jsonString))
				 		   .andExpect(status().isOk())
				 		   .andReturn();
		
		Administrator resBody = mapper.readValue(resString(res), Administrator.class);
		assertEquals(administrator, resBody);
		
		long newCount = administratorRepo.count();
		assertEquals(oldCount -1, newCount);
		assertFalse(administratorRepo.existsById(id));
	}
	
	@Test
	public void testDeleteAdministrator2() throws Exception {
		
		String url = base + "/administrators";
		
		Administrator administrator = administrators.get(0);
		int id = administrator.getId() + 999;
		administrator.setId(id);
		String jsonString = writer.writeValueAsString(administrator);
		
		long oldCount = administratorRepo.count();
		
		assertTrue(administratorRepo.existsByUserName(administrator.getUserName()));
		
		MvcResult res = mvc.perform(delete(url).contentType(json).content(jsonString))
				 		   .andExpect(status().isNotFound())
				 		   .andReturn();
		
		ApiError err = mapper.readValue(resString(res), ApiError.class);
		
		assertEquals("NoSuchUserException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(Integer.toString(id)));
		
		long newCount = administratorRepo.count();
		
		assertEquals(oldCount, newCount);
		assertTrue(administratorRepo.existsByUserName(administrator.getUserName()));
	}
	
	@Test
	public void testDeleteExperimenter1() throws Exception {
		
		String url = base + "/experimenters";
		
		Experimenter experimenter = experimenters.get(0);
		int id = experimenter.getId();
		String jsonString = writer.writeValueAsString(experimenter);
		
		long oldCount = experimenterRepo.count();
		
		assertTrue(experimenterRepo.existsById(id));
		
		MvcResult res = mvc.perform(delete(url).contentType(json).content(jsonString))
				   	 	   .andExpect(status().isOk())
				   	 	   .andReturn();
		
		Experimenter resBody = mapper.readValue(resString(res), Experimenter.class);
		
		assertEquals(experimenter, resBody);
		
		long newCount = experimenterRepo.count();
		
		assertEquals(oldCount -1, newCount);
		assertFalse(experimenterRepo.existsById(id));
	}
	
	@Test
	public void testDeleteExperimenter2() throws Exception {
		
		String url = base + "/experimenters";
		
		Experimenter experimenter = experimenters.get(0);
		int id = experimenter.getId() + 999;
		experimenter.setId(id);
		String jsonString = writer.writeValueAsString(experimenter);
		
		long oldCount = experimenterRepo.count();
		
		assertTrue(experimenterRepo.existsByUserName(experimenter.getUserName()));
		
		MvcResult res = mvc.perform(delete(url).contentType(json).content(jsonString))
				 		   .andExpect(status().isNotFound())
				 		   .andReturn();
		
		ApiError err = mapper.readValue(resString(res), ApiError.class);
		
		assertEquals("NoSuchUserException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(Integer.toString(id)));
		
		long newCount = experimenterRepo.count();
		
		assertEquals(oldCount, newCount);
		assertTrue(experimenterRepo.existsByUserName(experimenter.getUserName()));
	}

	@Test
	public void testDeleteParticipan1() throws Exception {
		
		String url = base + "/participants";
		
		Participant participant = participantRepo.findById(participants.get(0).getId());
		int id = participant.getId();
		String jsonString = writer.writeValueAsString(participant);
		
		long oldCount = participantRepo.count();
		
		assertTrue(participantRepo.existsById(id));

		assertTrue(groupRepo.findById(participant.getTestGroupId()).getParticipants().contains(participant));
		
		MvcResult res = mvc.perform(delete(url).contentType(json).content(jsonString))
				 		   .andExpect(status().isOk())
				 		   .andReturn(); 
		
		Participant resBody = mapper.readValue(resString(res), Participant.class);
		
		assertEquals(participant, resBody);
		
		long newCount = participantRepo.count();
		
		assertEquals(oldCount -1, newCount);
		assertFalse(participantRepo.existsById(id));
		assertFalse(groupRepo.findById(participant.getTestGroupId()).getParticipants().contains(participant));
	}
	
	@Test
	public void testDeleteParticipant2() throws Exception {
		
		String url = base + "/participants";
		
		Participant participant = participants.get(0);
		int id = participant.getId() + 999;
		participant.setId(id);
		String jsonString = writer.writeValueAsString(participant);
		
		long oldCount = participantRepo.count();
		assertTrue(participantRepo.existsByUserName(participant.getUserName()));
		
		MvcResult res = mvc.perform(delete(url).contentType(json).content(jsonString))
				 		   .andExpect(status().isNotFound())
				 		   .andReturn();
		
		ApiError err = mapper.readValue(resString(res), ApiError.class);
		
		assertEquals("NoSuchUserException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(Integer.toString(id)));
		
		long newCount = participantRepo.count();
		
		assertEquals(oldCount, newCount);
		assertTrue(participantRepo.existsByUserName(participant.getUserName()));
	}
	
	@Test
	public void testDeleteAllExperimenters() throws Exception {
		
		String url = base + "/experimenters/all";
		
		assertNotEquals(0, experimenterRepo.count());
		
		MvcResult res = mvc.perform(delete(url))
				 		   .andExpect(status().isOk())
				 		   .andReturn();
		
		assertEquals("Experimenters Cleared", resString(res));
		
		assertEquals(0, experimenterRepo.count());
	}
	
	@Test
	public void testDeleteAllParticipants() throws Exception {
		
		String url = base + "/participants/all";
		
		assertNotEquals(0, participantRepo.count());	
		assertNotEquals(0, groupRepo.findById(testGroup.getId()).getParticipants().size());
		
		MvcResult res = mvc.perform(delete(url))
				 		   .andExpect(status().isOk())
				 		   .andReturn();
		 
		assertEquals("Participants Cleared", resString(res));
		
		assertEquals(0, participantRepo.count());
		assertEquals(0, groupRepo.findById(testGroup.getId()).getParticipants().size());
	}

	/////////////////

	private String resString(MvcResult res) throws UnsupportedEncodingException {
		
		
		return res.getResponse().getContentAsString();
	}
}










