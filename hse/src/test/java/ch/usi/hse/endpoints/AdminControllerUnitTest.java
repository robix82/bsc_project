package ch.usi.hse.endpoints;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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

import ch.usi.hse.db.entities.Administrator;
import ch.usi.hse.db.entities.Experimenter;
import ch.usi.hse.db.entities.Participant;
import ch.usi.hse.services.UserService;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(authorities="ADMIN")
public class AdminControllerUnitTest {

	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private UserService userService;
	
	private List<Administrator> administrators;
	private List<Experimenter> experimenters; 
	private List<Participant> participants;
	
	@BeforeEach
	public void setUp() {
		
		administrators = new ArrayList<>();
		experimenters = new ArrayList<>();
		participants = new ArrayList<>();
		
		administrators.add(new Administrator(1, "admin1", "pwd1", new HashSet<>()));
		administrators.add(new Administrator(1, "admin2", "pwd2", new HashSet<>()));
		experimenters.add(new Experimenter(1, "exp1", "pwd3", new HashSet<>()));
		experimenters.add(new Experimenter(1, "exp2", "pwd4", new HashSet<>()));
		participants.add(new Participant(1, "part1", "pwd5", new HashSet<>()));
		participants.add(new Participant(1, "part2", "pwd6", new HashSet<>()));
		
		when(userService.allAdministrators()).thenReturn(administrators);
		when(userService.allExperimenters()).thenReturn(experimenters);
		when(userService.allParticipants()).thenReturn(participants);
	}
	
	@Test
	public void testGetAdminUi() throws Exception {
		
		mvc.perform(get("/admin"))
		   .andExpect(status().isOk())
		   .andExpect(view().name("admin"))
		   .andExpect(model().attribute("administrators", is(administrators)))
		   .andExpect(model().attribute("experimenters", is(experimenters)))
		   .andExpect(model().attribute("participants", is(participants)));
	}
}
