package ch.usi.hse.storage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

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
public class UrlListStorage extends FileStorage {

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
	public void store(MultipartFile file) throws FileWriteException {

		store(file, storagePath);
	}
	
	/**
	 * deletes the file with the given name
	 * 
	 * @param fileName
	 * @throws NoSuchFileException
	 * @throws FileDeleteException
	 */
	public void delete(String fileName) throws NoSuchFileException, FileDeleteException {
		
		Path filePath = storagePath.resolve(fileName);
		delete(filePath);
	}
	
	/**
	 * returns the lines in from the file with the given name
	 * 
	 * @param fileName
	 * @return
	 * @throws NoSuchFileException
	 * @throws FileReadException
	 */
	public List<String> getLines(String fileName) throws NoSuchFileException, FileReadException {
		
		Path filePath = storagePath.resolve(fileName);
		
		if (! Files.exists(filePath)) {
			throw new NoSuchFileException(fileName);
		}
		
		try {
			return Files.readAllLines(filePath);
		}
		catch (IOException e) {
			throw new FileReadException(fileName);
		}
	}
	
	/**
	 * returns the file names of the saved files
	 * 
	 * @return
	 * @throws FileReadException
	 */
	public List<String> savedFiles() throws FileReadException {
		
		try {
			
			List<String> files = Files.list(storagePath).map(f -> f.getFileName().toString())
											     	   .collect(Collectors.toList());
			
			return files;
		}
		catch (IOException e) {
			throw new FileReadException(storagePath.toString());
		}
	}
	
	/**
	 * returns an InputStream for reading the given file
	 * 
	 * @param fileName
	 * @return
	 * @throws NoSuchFileException
	 * @throws FileReadException
	 */
	public InputStream getFileAsStream(String fileName) 
			throws NoSuchFileException, FileReadException {
		
		Path filePath = storagePath.resolve(fileName);
		
		return getInputStream(filePath);
	}
}












