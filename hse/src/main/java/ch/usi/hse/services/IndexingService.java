package ch.usi.hse.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import ch.usi.hse.exceptions.FileDeleteException;
import ch.usi.hse.exceptions.FileReadException;
import ch.usi.hse.exceptions.FileWriteException;
import ch.usi.hse.exceptions.NoSuchFileException;
import ch.usi.hse.storage.UrlListStorage;

/**
 * Service class for handling the indexing process
 * 
 * @author robert.jans@usi.ch
 *
 */
@Service
public class IndexingService {

	@Autowired
	private UrlListStorage urlListStorage;
	
	/**
	 * 
	 * @return names of the saved url list files
	 * @throws FileReadException
	 */
	public List<String> savedUrlLists() throws FileReadException {
		
		return urlListStorage.savedFiles();
	}
	
	/**
	 * add a new url list file (text file)
	 * 
	 * @param file
	 * @throws FileWriteException
	 */
	public void addUrlList(MultipartFile file) throws FileWriteException {
		
		urlListStorage.store(file);
	}
	
	
	/**
	 * remove the file with the given file name
	 * 
	 * @param fileName
	 * @throws NoSuchFileException
	 * @throws FileDeleteException
	 */
	public void removeUrlList(String fileName) throws NoSuchFileException, 
						 							  FileDeleteException {
		
		urlListStorage.delete(fileName);
	}
}







