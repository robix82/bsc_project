package ch.usi.hse.storage;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import ch.usi.hse.exceptions.FileReadException;
import ch.usi.hse.exceptions.NoSuchFileException;

@SpringBootTest
public class TextFileStorageTest {

	@Value("${test_file_dir}")
	private String testDir;
	
	@Autowired
	private TextFileStorage fileStorage;
	
	private Path testDirPath;
	private String newFileName;
	private String existingFileName;
	private List<String> textLines;
	
	@BeforeEach
	public void setUp() throws FileNotFoundException {
		
		testDirPath = Paths.get(testDir);
		newFileName = "new.txt";
		existingFileName = "existing.txt";
		
		textLines = Arrays.asList("line1", "line2", "line3");
		
		PrintStream ps = new PrintStream(new FileOutputStream(testDir + existingFileName));
		
		for (String s : textLines) {
			ps.println(s);
		}
		
		ps.close();
	}
	
	@Test
	public void testSetup() {
		
		assertTrue(Files.exists(testDirPath.resolve(existingFileName)));
		assertFalse(Files.exists(testDirPath.resolve(newFileName)));
	}
	
	@Test
	public void testGetLines1() throws NoSuchFileException, FileReadException {
		
		List<String> res = fileStorage.getLines(testDirPath.resolve(existingFileName));
		
		assertIterableEquals(textLines, res);
	}
	
	@Test
	public void testGetLines2() throws NoSuchFileException, FileReadException {
		
		List<String> res = fileStorage.getLines(testDir, existingFileName);
		
		assertIterableEquals(textLines, res);
	}
	
	@Test
	public void testGetLines3() throws FileReadException {
		
		boolean exc = false;
		
		try {
			fileStorage.getLines(testDirPath.resolve(newFileName));
		}
		catch (NoSuchFileException e) {
			
			assertTrue(e.getMessage().contains(newFileName));
			exc = true;
		}
		
		assertTrue(exc);
	}
}








