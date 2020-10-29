package ch.usi.hse.endpoints;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import ch.usi.hse.exceptions.FileDeleteException;
import ch.usi.hse.exceptions.FileReadException;
import ch.usi.hse.exceptions.FileWriteException;
import ch.usi.hse.exceptions.NoSuchFileException;
import ch.usi.hse.services.IndexingService;

/**
 * Controller class for the indexing UI page
 * 
 * @author robert.jans@usi.ch
 *
 */
@Controller
@CrossOrigin
@RequestMapping("/indexing")
public class IndexingController {

	@Autowired
	private IndexingService indexingService;
	
	/**
	 * Serves the main indexing UI page
	 * 
	 * @return ModelAndView
	 * @throws FileReadException 
	 */
	@GetMapping("/ui")
	public ModelAndView getIndexingUi() throws FileReadException {
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("indexing");
		mav.addObject("urlLists", indexingService.savedUrlLists());
		
		return mav;
	}

	/**
	 * upload a new url list (text file)
	 * 
	 * @param file
	 * @return success messsage
	 * @throws FileWriteException
	 */
	@PostMapping("/urlLists")
	public ResponseEntity<String> postUrlList(@RequestParam(name="file") MultipartFile file) 
			throws FileWriteException {
		
		indexingService.addUrlList(file);
		
		String msg = "file " + file.getOriginalFilename() + " uploaded";
		
		return new ResponseEntity<>(msg, HttpStatus.CREATED);
	}
	
	/**
	 * remove a saved url list
	 * 
	 * @param fileName
	 * @return
	 * @throws NoSuchFileException
	 * @throws FileDeleteException
	 */
	@DeleteMapping("/urlLists")
	public ResponseEntity<String> deleteUrlList(@RequestParam String fileName) 
			throws NoSuchFileException, FileDeleteException {
		
		String msg = "file " + fileName + " removed";
		
		indexingService.removeUrlList(fileName);
		
		return new ResponseEntity<>(msg, HttpStatus.OK);
	}
}




 





