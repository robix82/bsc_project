package ch.usi.hse.endpoints;


import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;

import ch.usi.hse.db.entities.Experiment;
import ch.usi.hse.db.entities.TestGroup;
import ch.usi.hse.exceptions.ConfigParseException;
import ch.usi.hse.exceptions.ExperimentExistsException;
import ch.usi.hse.exceptions.ExperimentStatusException;
import ch.usi.hse.exceptions.FileDeleteException;
import ch.usi.hse.exceptions.FileReadException;
import ch.usi.hse.exceptions.FileWriteException;
import ch.usi.hse.exceptions.NoSuchDocCollectionException;
import ch.usi.hse.exceptions.NoSuchExperimentException;
import ch.usi.hse.exceptions.NoSuchFileException;
import ch.usi.hse.exceptions.NoSuchTestGroupException;
import ch.usi.hse.exceptions.NoSuchUserException;
import ch.usi.hse.exceptions.TestGroupExistsException;
import ch.usi.hse.exceptions.UserExistsException;
import ch.usi.hse.experiments.ExperimentSummary;
import ch.usi.hse.services.ExperimentService;
import ch.usi.hse.services.UserService;

/**
 * Controller class for the experiments UI page
 * 
 * @author robert.jans@usi.ch
 *
 */
@Controller
@CrossOrigin
@RequestMapping("/experiments")
public class ExperimentsController {

	@Autowired
	private ExperimentService experimentService;
	
	@Autowired
	private UserService userService;
	
	@Value("${baseUrl}")
	private String baseUrl;
	
	// UI CONTROLLERS
	
	/**
	 * Serves the main experiments UI
	 * 
	 * @return ModelAndView
	 */
	@GetMapping("/ui")
	public ModelAndView getExperimentsUi() {
		 
		ModelAndView mav = new ModelAndView();
		mav.setViewName("experiments");
		
		mav.addObject("baseUrl", baseUrl);
		mav.addObject("experiments", experimentService.allExperiments());
		mav.addObject("experimenters", userService.allExperimenters());
		
		return mav;
	}
	
	/**
	 * Serves the Setup page for the Experiment with the given Id
	 * 
	 * @param expId
	 * @return
	 * @throws NoSuchExperimentException 
	 * @throws FileReadException 
	 */
	@GetMapping("/setup/ui")
	public ModelAndView getExperimentsSetupUi(@RequestParam(required=true) int expId) 
			throws NoSuchExperimentException, FileReadException {
		
		ModelAndView mav = new ModelAndView();
		
		mav.setViewName("exp_setup");
		mav.addObject("baseUrl", baseUrl);
		mav.addObject("experiment", experimentService.findExperiment(expId));
		mav.addObject("docCollections", experimentService.getIndexedDocCollections());
		mav.addObject("configFiles", experimentService.savedConfigFiles());
		
		return mav;
	}
	
	/**
	 * Serves the experiment execution page for the Experiment with the given id
	 * 
	 * @param expId
	 * @return
	 * @throws NoSuchExperimentException 
	 */
	@GetMapping("/run/ui")
	public ModelAndView getExperimentsRunUi(@RequestParam(required=true) int expId) 
			throws NoSuchExperimentException {
		
		ModelAndView mav = new ModelAndView();
		mav.addObject("baseUrl", baseUrl);
		mav.setViewName("exp_run");
		mav.addObject("experiment", experimentService.findExperiment(expId));
		
		return mav;
	}
	
	/**
	 * Serves the experiment evaluation page for the Experiment with the given id
	 * 
	 * @param expId
	 * @return
	 * @throws NoSuchExperimentException 
	 * @throws ExperimentStatusException 
	 */
	@GetMapping("/eval/ui")
	public ModelAndView getExperimentsEvalUi(@RequestParam(required=true) int expId) 
			throws NoSuchExperimentException, ExperimentStatusException {
		
		Experiment experiment = experimentService.findExperiment(expId);
		ExperimentSummary summary = experimentService.experimentSummary(experiment);
		
		ModelAndView mav = new ModelAndView();
		mav.addObject("baseUrl", baseUrl);
		mav.setViewName("exp_eval");
		mav.addObject("experiment", experiment);
		mav.addObject("summary", summary);
		
		
		return mav;
	}
	
	// REST API FOR EXPERIMENT CREATION / UPDATE / DELETION
	
	
	/**
	 * add a new Experiment to the database	
	 * 
	 * @param experiment
	 * @return saved experiment
	 * @throws ExperimentExistsException
	 * @throws NoSuchUserException 
	 */
	@PostMapping("/")
	public ResponseEntity<Experiment> postExperiment(@RequestBody Experiment experiment) 
			throws ExperimentExistsException, NoSuchUserException {
		
		Experiment saved = experimentService.addExperiment(experiment);
		
		return new ResponseEntity<>(saved, HttpStatus.CREATED);
	}
	
	/**
	 * update an existing experiment
	 * warning: TestGroups are to be updated using the 
	 * specific API ("/experiments/testGroups")
	 * 
	 * @param experiment
	 * @return updated experiment
	 * @throws NoSuchExperimentException
	 * @throws ExperimentExistsException
	 * @throws NoSuchUserException
	 * @throws NoSuchTestGroupException
	 * @throws UserExistsException
	 * @throws NoSuchDocCollectionException
	 */
	@PutMapping("/")
	public ResponseEntity<Experiment> updateExperiment(@RequestBody Experiment experiment) 
			throws NoSuchExperimentException, 
				   ExperimentExistsException, 
				   NoSuchUserException {
		
		Experiment updated = experimentService.updateExperiment(experiment);
		
		return new ResponseEntity<>(updated, HttpStatus.OK);
	}
	
	/**
	 * deletes the given experiment from the database
	 * warning: all associated TestGroups and their Participants
	 * are deleted as well
	 * 
	 * @param experiment
	 * @return deleted experiment
	 * @throws NoSuchExperimentException
	 * @throws NoSuchUserException 
	 */
	@DeleteMapping("/")
	public ResponseEntity<Experiment> deleteExperiment(@RequestBody Experiment experiment) 
			throws NoSuchExperimentException, NoSuchUserException {
		
		experimentService.deleteExperiment(experiment);
		
		return new ResponseEntity<>(experiment, HttpStatus.OK);
	}
	
	/**
	 * returns the saved Experiment instancce corresponding to the given id
	 * (to be used for polling data during experiment execution)
	 * 
	 * @param id
	 * @return
	 * @throws NoSuchExperimentException
	 */
	@GetMapping("/")
	public ResponseEntity<Experiment> getExperiment(@RequestParam(required=true) int id) 
			throws NoSuchExperimentException {
		
		Experiment found = experimentService.findExperiment(id);
		
		return new ResponseEntity<>(found, HttpStatus.OK);
	}
	
	// REST API FOR TEST GROUP CONFIGURATION
	
	/**
	 * add a new TestGroup to the Experiment
	 * specified in the TestGroups experimentId field
	 * 
	 * @param testGroup
	 * @return
	 * @throws NoSuchExperimentException
	 * @throws TestGroupExistsException 
	 */
	@PostMapping("/testGroups")
	public ResponseEntity<TestGroup> postTestGroup(@RequestBody TestGroup testGroup) 
			throws NoSuchExperimentException, TestGroupExistsException {
		
		TestGroup saved = experimentService.addTestGroup(testGroup);
		
		return new ResponseEntity<>(saved, HttpStatus.CREATED);
	}
	
	/**
	 * update an existing given testGroup
	 * 
	 * @param testGroup
	 * @return
	 * @throws NoSuchTestGroupException 
	 * @throws NoSuchExperimentException 
	 * @throws NoSuchDocCollectionException 
	 * @throws UserExistsException 
	 */
	@PutMapping("/testGroups")
	public ResponseEntity<TestGroup> updateTestGroup(@RequestBody TestGroup testGroup) 
			throws NoSuchTestGroupException, 
				   NoSuchExperimentException, 
				   NoSuchDocCollectionException, 
				   UserExistsException {
		
		TestGroup updated = experimentService.updateTestGroup(testGroup);
		
		return new ResponseEntity<>(updated, HttpStatus.OK);
	}
	
	/**
	 * delete an existing TestGroup
	 * warning: all associated Participants will be deleted as well
	 * 
	 * @param testGroup
	 * @return
	 * @throws NoSuchExperimentException
	 * @throws NoSuchTestGroupException
	 */
	@DeleteMapping("/testGroups")
	public ResponseEntity<TestGroup> deleteTestGroup(@RequestBody TestGroup testGroup) 
			throws NoSuchExperimentException, NoSuchTestGroupException {
		
		experimentService.deleteTestGroup(testGroup);
		
		return new ResponseEntity<>(testGroup, HttpStatus.OK);
	}
	
	/**
	 * configure an Experiments TestGroups using the given configuration file
	 * 
	 * @param configFileName
	 * @param experiment
	 * @return
	 * @throws NoSuchExperimentException
	 * @throws NoSuchFileException
	 * @throws FileReadException
	 * @throws ConfigParseException
	 */
	@PostMapping("/testGroups/config")
	public ResponseEntity<Experiment> configureExperiment(@RequestParam String configFileName,
														  @RequestBody Experiment experiment) 
		throws NoSuchExperimentException, 
			   NoSuchFileException, 
			   FileReadException, 
			   ConfigParseException {
		
		Experiment configured = experimentService.configureTestGroups(experiment, configFileName);
		
		return new ResponseEntity<>(configured, HttpStatus.OK);
	}
	
	// REST API FOR CONFIGURATION FILE MANAGEMENT
	
	/**
	 * upload a new configuration file
	 * 
	 * @param file
	 * @return success message
	 * @throws FileWriteException
	 */
	@PostMapping("/testGroups/config/ul")
	public ResponseEntity<String> uploadConfigFile(@RequestParam(name="file") MultipartFile file) 
			throws FileWriteException {
		
		String msg = "file " + file.getOriginalFilename() + " uploaded";
		
		experimentService.addConfigFile(file);
		
		return new ResponseEntity<>(msg, HttpStatus.CREATED);
	}
	
	/**
	 * deletes the config file with the given file name
	 * 
	 * @param fileName
	 * @return success message
	 * @throws NoSuchFileException
	 * @throws FileDeleteException
	 */
	@DeleteMapping("/testGroups/config")
	public ResponseEntity<String> deleteConfigFile(@RequestParam String fileName) 
		throws NoSuchFileException, FileDeleteException {
		
		String msg = "file " + fileName + " deleted";
		
		experimentService.removeConfigFile(fileName);
		
		return new ResponseEntity<>(msg, HttpStatus.OK);
	}
	
	/**
	 * download the given config file
	 * 
	 * @param response
	 * @param fileName
	 * @throws NoSuchFileException
	 * @throws FileReadException
	 */
	@GetMapping("/testGroups/config/dl")
	public void downloadConfigFile(HttpServletResponse response, @RequestParam String fileName) 
			throws NoSuchFileException, FileReadException {
		 
		InputStream is = experimentService.getConfigFile(fileName);
		
		response.setContentType(MediaType.TEXT_PLAIN);
		
		try {
			FileCopyUtils.copy(is, response.getOutputStream());
		}
		catch (IOException e) {
			throw new FileReadException(fileName);
		}
	}
	
	// REST API for experiment execution
	
	/**
	 * Start the given experiment's execution.
	 * The experiment's status must be 'READY'
	 * 
	 * @param experiment
	 * @return The experiment with updated fields
	 * @throws NoSuchExperimentException
	 * @throws ExperimentStatusException
	 */
	@PostMapping("/start")
	public ResponseEntity<Experiment> startExperiment(@RequestBody Experiment experiment) 
			throws NoSuchExperimentException, ExperimentStatusException {
		
		Experiment started = experimentService.startExperiment(experiment);
		
		return new ResponseEntity<>(started, HttpStatus.OK);
	}
	
	/**
	 * Stops the given experiment's execution.
	 * The experiment's status must be 'RUNNING'
	 * 
	 * @param experiment
	 * @return The experiment with updated fields
	 * @throws NoSuchExperimentException
	 * @throws ExperimentStatusException
	 */
	@PostMapping("/stop")
	public ResponseEntity<Experiment> stopExperiment(@RequestBody Experiment experiment) 
			throws NoSuchExperimentException, ExperimentStatusException {
		
		Experiment stopped = experimentService.stopExperiment(experiment);
		
		return new ResponseEntity<>(stopped, HttpStatus.OK);
	}
	
	/**
	 * Reset a completed experiment
	 * effect: all collected data is deleted and the Experiment status is set to READY
	 * 
	 * @param experiment
	 * @return
	 * @throws NoSuchExperimentException
	 * @throws ExperimentStatusException
	 */
	@PostMapping("/reset")
	public ResponseEntity<Experiment> resetExperiment(@RequestBody Experiment experiment) 
			throws NoSuchExperimentException, ExperimentStatusException {
		
		Experiment updated = experimentService.resetExperiment(experiment);
		
		return new ResponseEntity<>(updated, HttpStatus.OK);
	}
	
	// RESULT DATA EXPORT
	
	@GetMapping("/eval/export/raw_csv")
	public void exportRawCsv(HttpServletResponse response, @RequestParam(required=true) int expId) 
			throws NoSuchExperimentException, FileWriteException {
		
		Experiment experiment = experimentService.findExperiment(expId);
		
		InputStream is = experimentService.rawResultsCsv(experiment);
		String fileName = experiment.getTitle() + ".csv";
		
		response.setContentType(MediaType.TEXT_PLAIN);
		response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
		
		try {
			FileCopyUtils.copy(is, response.getOutputStream());
		}
		catch (IOException e) {
			throw new FileWriteException(fileName);
		}
	}
	
	@GetMapping("/eval/export/raw_json")
	public void exportRawJson(HttpServletResponse response, @RequestParam(required=true) int expId) 
			throws NoSuchExperimentException, FileWriteException, JsonProcessingException {
		
		
		Experiment experiment = experimentService.findExperiment(expId);
		
		InputStream is = experimentService.rawResultsJson(experiment);
		String fileName = experiment.getTitle() + ".json";
		
		response.setContentType(MediaType.TEXT_PLAIN);
		response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
		
		try {
			FileCopyUtils.copy(is, response.getOutputStream());
		}
		catch (IOException e) {
			throw new FileWriteException(fileName);
		}
	}
}






