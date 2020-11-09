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
 * in the url lists directory defined in application.properties
 * 
 * @author robert.jans@usi.ch
 *
 */
@Component("UrlListStorage")
public class UrlListStorage extends TextFileStorage {

	private Path storagePath;
	
	@Autowired
	public UrlListStorage(@Value("${dir.urlLists}") Path storagePath) 
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
	public void storeUrlList(MultipartFile file) throws FileWriteException {

		storeTextFile(file, storagePath);
	}
	
	/**
	 * deletes the file with the given name
	 * 
	 * @param fileName
	 * @throws NoSuchFileException
	 * @throws FileDeleteException
	 */
	public void deleteUrlList(String fileName) throws NoSuchFileException, FileDeleteException {
		
		deleteTextFile(fileName, storagePath);
	}
	
	/**
	 * returns the lines from the file with the given name
	 * 
	 * @param fileName
	 * @return
	 * @throws NoSuchFileException
	 * @throws FileReadException
	 */
	public List<String> getUrlLines(String fileName) 
			throws NoSuchFileException, FileReadException {
		
		List<String> urlLines = new ArrayList<>();
		
		for (String s : getTextLines(fileName, storagePath)) {
			
			if (! s.isEmpty() && ! s.isBlank()) {
				urlLines.add(s);
			}
		}
		
		return urlLines;
	}
	 
	/**
	 * returns the file names of the saved files
	 * 
	 * @return
	 * @throws FileReadException
	 */
	public List<String> listUrlFiles() throws FileReadException {
		
		return listTextFiles(storagePath);
	}
	
	/**
	 * returns an InputStream for reading the given file
	 * 
	 * @param fileName
	 * @return
	 * @throws NoSuchFileException
	 * @throws FileReadException
	 */
	public InputStream getUrlFileAsStream(String fileName) 
			throws NoSuchFileException, FileReadException {
		
		return getTextFileAsStream(fileName, storagePath);
	}
}












