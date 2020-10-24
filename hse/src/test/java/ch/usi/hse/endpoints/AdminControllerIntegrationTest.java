package ch.usi.hse.endpoints;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import ch.usi.hse.db.entities.Administrator;
import ch.usi.hse.db.entities.Experimenter;
import ch.usi.hse.db.entities.Participant;
import ch.usi.hse.db.entities.Role;
import ch.usi.hse.db.repositories.AdministratorRepository;
import ch.usi.hse.db.repositories.ExperimenterRepository;
import ch.usi.hse.db.repositories.ParticipantRepository;
import ch.usi.hse.db.repositories.RoleRepository;
import ch.usi.hse.db.repositories.UserRepository;

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
	
	private List<Administrator> administrators;
	private List<Experimenter> experimenters; 
	private List<Participant> participants;
	
	@BeforeEach
	public void setUp() {
		
		Set<Role> adminRoles = new HashSet<>();
		Set<Role> experimenterRoles = new HashSet<>();
		Set<Role> participantRoles = new HashSet<>();
		adminRoles.add(roleRepo.findByRole("ADMIN"));
		experimenterRoles.add(roleRepo.findByRole("EXPERIMENTER"));
		participantRoles.add(roleRepo.findByRole("PARTICIPANT"));
		
		administrators = new ArrayList<>();
		experimenters = new ArrayList<>();
		participants = new ArrayList<>();
		
		administrators.add(new Administrator("admin1", "pwd1", adminRoles));
		administrators.add(new Administrator("admin2", "pwd2", adminRoles));
		experimenters.add(new Experimenter("exp1", "pwd3", experimenterRoles));
		experimenters.add(new Experimenter("exp2", "pwd4", experimenterRoles));
		participants.add(new Participant("part1", "pwd5", participantRoles));
		participants.add(new Participant("part2", "pwd6", participantRoles));
		
		userRepo.deleteAll();
		
		for (Administrator a : administrators) {
			administratorRepo.save(a);
		}
		
		for (Experimenter e : experimenters) {
			experimenterRepo.save(e);
		}
		
		for (Participant p : participants) {
			participantRepo.save(p);
		}
	}
	
	@Test
	public void testGetAdminUi() throws Exception {
		
		mvc.perform(get("/admin"))
		   .andExpect(status().isOk())
		   .andExpect(view().name("admin"))
		   .andExpect(model().attribute("administrators", Matchers.iterableWithSize(administrators.size())))
		   .andExpect(model().attribute("experimenters", Matchers.iterableWithSize(experimenters.size())))
		   .andExpect(model().attribute("participants", Matchers.iterableWithSize(participants.size())));
	}
}










