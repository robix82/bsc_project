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

@Service
public class IndexingService {

	@Autowired
	private UrlListStorage urlListStorage;
	
	public List<String> savedUrlLists() throws FileReadException {
		
		return urlListStorage.savedFiles();
	}
	
	public void addUrlList(MultipartFile file) throws FileWriteException {
		
		urlListStorage.store(file);
	}
	
	public void removeUrlList(String fileName) throws NoSuchFileException, 
						 							  FileDeleteException {
		
		urlListStorage.delete(fileName);
	}
}







