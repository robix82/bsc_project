package ch.usi.hse.endpoints;



import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.util.FileCopyUtils;

import ch.usi.hse.config.Language;
import ch.usi.hse.db.entities.DocCollection;
import ch.usi.hse.exceptions.DocCollectionExistsException;
import ch.usi.hse.exceptions.FileDeleteException;
import ch.usi.hse.exceptions.FileReadException;
import ch.usi.hse.exceptions.FileWriteException;
import ch.usi.hse.exceptions.LanguageNotSupportedException;
import ch.usi.hse.exceptions.NoSuchDocCollectionException;
import ch.usi.hse.exceptions.NoSuchFileException;
import ch.usi.hse.indexing.IndexingResult;
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
		mav.addObject("docCollections", indexingService.docCollections());
		mav.addObject("languages", Language.languages);
		
		return mav; 
	}
 
	/**
	 * upload a new url list (text file)
	 * 
	 * @param file
	 * @return success message
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
	
	/**
	 * download a saved url list
	 * 
	 * @param response
	 * @param fileName
	 * @throws NoSuchFileException
	 * @throws FileReadException
	 */
	@GetMapping("/urlLists/dl")
	public void downloadUrlList(HttpServletResponse response, @RequestParam String fileName) 
			throws NoSuchFileException, FileReadException {
		 
		InputStream is = indexingService.getUrlListFile(fileName);
		
		response.setContentType(MediaType.TEXT_PLAIN);
		response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
		
		try {
			FileCopyUtils.copy(is, response.getOutputStream());
		}
		catch (IOException e) {
			throw new FileReadException(fileName);
		}
	}
	 
	/**
	 * Add a new DocCollection entry to the database
	 * 
	 * @param docCollection
	 * @return
	 * @throws LanguageNotSupportedException
	 * @throws DocCollectionExistsException
	 * @throws NoSuchFileException
	 * @throws FileReadException
	 * @throws FileWriteException 
	 */
	@PostMapping("/docCollections")
	public ResponseEntity<DocCollection> postDocCollection(@RequestBody DocCollection docCollection) 
			throws LanguageNotSupportedException, 
				   DocCollectionExistsException, 
				   NoSuchFileException, 
				   FileReadException, 
				   FileWriteException {
		
		DocCollection saved = indexingService.addDocCollection(docCollection);
		
		return new ResponseEntity<>(saved, HttpStatus.CREATED);
	}
	
	/**
	 * update the database entry of an existing DocCollection
	 * 
	 * @param docCollection
	 * @return
	 * @throws NoSuchDocCollectionException
	 * @throws NoSuchFileException
	 * @throws LanguageNotSupportedException
	 * @throws FileReadException
	 * @throws DocCollectionExistsException
	 * @throws FileDeleteException 
	 * @throws FileWriteException 
	 */
	@PutMapping("/docCollections")
	public ResponseEntity<DocCollection> updateDocCollection(@RequestBody DocCollection docCollection) 
			throws NoSuchDocCollectionException, 
				   NoSuchFileException, 
				   LanguageNotSupportedException, 
				   FileReadException, 
				   DocCollectionExistsException, 
				   FileDeleteException, 
				   FileWriteException {
		 
		DocCollection updated = indexingService.updateDocCollection(docCollection);
		
		return new ResponseEntity<>(updated, HttpStatus.OK);
	}
	
	/**
	 * removes the give DocCollection entry from the database
	 * 
	 * @param docCollection
	 * @return
	 * @throws NoSuchDocCollectionException
	 * @throws FileDeleteException 
	 */
	@DeleteMapping("/docCollections")
	public ResponseEntity<DocCollection> deleteDocCollection(@RequestBody DocCollection docCollection) 
			throws NoSuchDocCollectionException, FileDeleteException {
		
		indexingService.removeDocCollection(docCollection);
		
		return new ResponseEntity<>(docCollection, HttpStatus.OK);
	}
	 
	/**
	 * initiate the indexing process for the given DocCollection 
	 * 
	 * @param docCollection
	 * @return
	 * @throws NoSuchDocCollectionException
	 * @throws LanguageNotSupportedException
	 * @throws NoSuchFileException
	 * @throws FileReadException
	 */
	@PostMapping("/buildIndex")
	public ResponseEntity<IndexingResult> buildIndex(@RequestBody DocCollection docCollection) 
			throws NoSuchDocCollectionException, 
				   LanguageNotSupportedException, 
				   NoSuchFileException, 
				   FileReadException {
		
		IndexingResult result = indexingService.buildIndex(docCollection);
		
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
}




 





