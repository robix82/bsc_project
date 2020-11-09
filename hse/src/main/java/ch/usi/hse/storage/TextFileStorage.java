package ch.usi.hse.storage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import ch.usi.hse.exceptions.FileDeleteException;
import ch.usi.hse.exceptions.FileReadException;
import ch.usi.hse.exceptions.FileWriteException;
import ch.usi.hse.exceptions.NoSuchFileException;

@Component("TextFileStorage")
public class TextFileStorage extends FileStorage {

	/**
	 * stores the given file
	 * 
	 * @param file
	 * @throws FileWriteException
	 */
	public void storeTextFile(MultipartFile file, Path directory) 
			throws FileWriteException {

		store(file, directory);
	}
	
	/**
	 * deletes the file with the given name
	 * 
	 * @param fileName
	 * @throws NoSuchFileException
	 * @throws FileDeleteException
	 */
	public void deleteTextFile(String fileName, Path directory) 
			throws FileDeleteException, NoSuchFileException {
		
		Path filePath = directory.resolve(fileName);
		delete(filePath);
	}
	
	/**
	 * returns the lines from the file with the given name
	 * 
	 * @param fileName
	 * @return
	 * @throws NoSuchFileException
	 * @throws FileReadException
	 */
	public List<String> getTextLines(String fileName, Path directory) 
			throws NoSuchFileException, FileReadException {
		
		Path filePath = directory.resolve(fileName);
		
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
	 * returns the names of the files in the given directory
	 * 
	 * @return
	 * @throws FileReadException
	 */
	public List<String> listTextFiles(Path directory) throws FileReadException {
		
		try {
			
			List<String> files = Files.list(directory).map(f -> f.getFileName().toString())
											     	  .collect(Collectors.toList());
			
			return files;
		}
		catch (IOException e) {
			throw new FileReadException(directory.toString());
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
	public InputStream getTextFileAsStream(String fileName, Path directory) 
			throws NoSuchFileException, FileReadException {
		
		Path filePath = directory.resolve(fileName);
		
		return getInputStream(filePath);
	}
}















