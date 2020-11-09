package ch.usi.hse.endpoints;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.UriComponentsBuilder;

import ch.usi.hse.db.entities.Experiment;
import ch.usi.hse.db.entities.Experimenter;
import ch.usi.hse.services.ExperimentService;
import ch.usi.hse.services.UserService;
import ch.usi.hse.exceptions.*;

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
	
	private List<Experiment> savedExperiments;
	private List<Experimenter> savedExperimenters;
	private Experiment newExperiment;
	
	
	@BeforeEach
	public void setUp() throws NoSuchExperimentException {
		
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
		e1.setId(2);
		e1.setId(3);
		e1.setId(4);
		newExperiment.setId(5);
		
		experimenter1.addExperiment(e1);
		experimenter1.addExperiment(e2);
		experimenter2.addExperiment(e3);
		experimenter2.addExperiment(e4);
		
		savedExperiments = List.of(e1, e2, e3, e4);
		savedExperimenters = List.of(experimenter1, experimenter2);
		
		when(userService.allExperimenters()).thenReturn(savedExperimenters);
		when(experimentService.allExperiments()).thenReturn(savedExperiments);
		
		when(experimentService.findExperiment(newExperiment.getId())).thenThrow(new NoSuchExperimentException(newExperiment.getId()));
		
		for (Experiment e : savedExperiments) {
			
			when(experimentService.findExperiment(e.getId())).thenReturn(e);
		}
	}
	
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
		   .andExpect(model().attribute("experiment", is(exp)));
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
}








