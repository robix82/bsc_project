package ch.usi.hse.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.stereotype.Component;

import ch.usi.hse.exceptions.FileReadException;
import ch.usi.hse.exceptions.NoSuchFileException;

/**
 * Utility class for handling text files
 * 
 * @author robert.jans@usi.ch
 *
 */
@Component
public class TextFileStorage extends FileStorage {


	/**
	 * returns a list containing all lines from the given text file
	 * 
	 * @param filePath (path to a text file)
	 * @return
	 * @throws NoSuchFileException
	 * @throws FileReadException
	 */
	public List<String> getLines(Path filePath) throws NoSuchFileException,
													   FileReadException {
		
		if (! Files.exists(filePath)) {
			throw new NoSuchFileException(filePath.getFileName().toString());
		}
		
		try {
			return Files.readAllLines(filePath);
		}
		catch (IOException e) {
			
			throw new FileReadException(filePath.getFileName().toString());
		}
	}

	public List<String> getLines(String directory, String fileName)
		throws NoSuchFileException, FileReadException {
		
		Path dirPath = Paths.get(directory);
		Path filePath = dirPath.resolve(fileName);
		
		return getLines(filePath);
	}
}








