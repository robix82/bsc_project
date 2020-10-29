package ch.usi.hse.storage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import ch.usi.hse.exceptions.FileDeleteException;
import ch.usi.hse.exceptions.FileReadException;
import ch.usi.hse.exceptions.FileWriteException;
import ch.usi.hse.exceptions.NoSuchFileException;

/**
 * base class for general file storage
 * see https://github.com/spring-guides/gs-uploading-files
 * 
 * @author robert.jans@usi.ch
 *
 */
@Component
public class FileStorage {

	/**
	 * Stores the given MultiPartFile
	 * 
	 * @param file
	 * @param dirPath
	 * @throws FileWriteException
	 */
	public void store(MultipartFile file, Path dirPath) throws FileWriteException {
		
		Path filePath = dirPath.resolve(file.getOriginalFilename())
							   .normalize()
							   .toAbsolutePath();
		
		try {
			
			InputStream is = file.getInputStream();
			
			Files.copy(is, filePath, StandardCopyOption.REPLACE_EXISTING);
		}
		catch (IOException e) {
			
			throw new FileWriteException(file.getOriginalFilename());
		}
	}
	
	/**
	 * Stores the given MultiPartFile
	 * 
	 * @param file
	 * @param dirPathString
	 * @throws FileWriteException
	 */
	public void store(MultipartFile file, String dirPathString) throws FileWriteException {
		
		Path dirPath = Paths.get(dirPathString);
		
		store(file, dirPath);
	}
	
	/**
	 * deletes the given file
	 * 
	 * @param filePath
	 * @throws NoSuchFileException
	 * @throws FileDeleteException
	 */
	public void delete(Path filePath) throws NoSuchFileException,
											 FileDeleteException {
		
		String fName = filePath.getFileName().toString();
		
		if (! Files.exists(filePath)) {
			throw new NoSuchFileException(fName);
		}
		
		try {
			Files.delete(filePath);
		}
		catch (IOException e) {
			
			throw new FileDeleteException(fName);
		}
	}
	

	/**
	 * Deletes the given file
	 * 
	 * @param filePathString
	 * @throws NoSuchFileException
	 * @throws FileDeleteException
	 */
	public void delete(String filePathString) throws NoSuchFileException,
											 FileDeleteException {
		
		Path filePath = Paths.get(filePathString);
		
		delete(filePath);
	}
	
	/**
	 * returns an InputStream for reading the given file
	 * 
	 * @param filePath
	 * @return
	 * @throws NoSuchFileException
	 * @throws FileReadException
	 */
	public InputStream getInputStream(Path filePath) throws NoSuchFileException,
															FileReadException {
		
		String fName = filePath.getFileName().toString();
		
		if (! Files.exists(filePath)) {
			throw new NoSuchFileException(fName);
		}
		
		try {
			return Files.newInputStream(filePath);
		} 
		catch (IOException e) {
			
			throw new FileReadException(fName);
		}
	}
	
	/**
	 * returns an InputStream for reading the given file
	 * 
	 * @param filePathString
	 * @return
	 * @throws NoSuchFileException
	 * @throws FileReadException
	 */
	public InputStream getInputStream(String filePathString) throws NoSuchFileException,
															FileReadException {
		
		Path filePath = Paths.get(filePathString);
		
		return getInputStream(filePath);
	}
}
 









