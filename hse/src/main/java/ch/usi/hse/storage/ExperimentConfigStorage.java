package ch.usi.hse.storage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import ch.usi.hse.exceptions.FileDeleteException;
import ch.usi.hse.exceptions.FileReadException;
import ch.usi.hse.exceptions.FileWriteException;
import ch.usi.hse.exceptions.NoSuchFileException;

/**
 * utility class for storing and retrieving files
 * in the experimentConfig directory defined in application.properties
 * 
 * @author robert.jans@usi.ch
 *
 */
@Component("ExperimentConfigStorage")
public class ExperimentConfigStorage extends TextFileStorage {

	private Path storagePath;
	
	@Autowired
	public ExperimentConfigStorage(@Value("${dir.experimentConfig") Path storagePath) 
			throws FileWriteException {
		
		this.storagePath = storagePath;
	
		if (! Files.exists(storagePath)) {
			
			try { 
				Files.createDirectories(storagePath);
			} 
			catch (IOException e) {
	
				throw new FileWriteException(storagePath.toString());
			}
		}
	}
	
	/**
	 * stores the given file
	 * 
	 * @param file
	 * @throws FileWriteException
	 */
	public void storeConfigFile(MultipartFile file) throws FileWriteException {

		store(file, storagePath);
	}
	
	/**
	 * deletes the file with the given name
	 * 
	 * @param fileName
	 * @throws NoSuchFileException
	 * @throws FileDeleteException
	 */
	public void deleteConfigFile(String fileName) throws NoSuchFileException, FileDeleteException {
		
		Path filePath = storagePath.resolve(fileName);
		delete(filePath);
	}
	
	public List<String> getConfigLines(String fileName) throws NoSuchFileException, FileReadException {
		
		List<String> configLines = new ArrayList<>();
		
		for (String s : getTextLines(fileName, storagePath)) {
			
			if (! s.isEmpty() &&
				! s.isBlank() &&
				! s.startsWith("#")) {
				
				configLines.add(s);
			}
		}
		
		return configLines;
	}
	
	public InputStream getConfigFileAsStream(String fileName) 
			throws NoSuchFileException, FileReadException {
		
		return getTextFileAsStream(fileName, storagePath);
	}
}














