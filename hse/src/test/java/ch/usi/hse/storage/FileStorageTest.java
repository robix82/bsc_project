package ch.usi.hse.storage;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import ch.usi.hse.exceptions.FileDeleteException;
import ch.usi.hse.exceptions.FileReadException;
import ch.usi.hse.exceptions.FileWriteException;
import ch.usi.hse.exceptions.NoSuchFileException;


@SpringBootTest
public class FileStorageTest {

	@Value("${test_file_dir}")
	private String testDir;
	
	@Autowired
	private FileStorage fileStorage;
	
	private Path testDirPath;
	private String newFileName;
	private String existingFileName;
	private MockMultipartFile testMpFile;
	private String testString;
	private byte[] testBytes;
	
	@BeforeEach
	public void setUp() throws IOException {
		
		testString = "some content";
		testBytes = testString.getBytes();
		testDirPath = Paths.get(testDir);
		
		newFileName = "newFile.txt";
		existingFileName = "existingFile.txt";

		testMpFile = new MockMultipartFile(newFileName,
										   newFileName,
										   MediaType.TEXT_PLAIN_VALUE,
										   testBytes);
		
		Files.list(testDirPath).forEach(f -> {
			try {
				Files.deleteIfExists(f);
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		});
		
		PrintStream ps = new PrintStream(new FileOutputStream(testDir + existingFileName));
		ps.print(testString);
		ps.close();
	}
	
	@Test
	public void setupTest() {
		
		assertNotNull(testDir);
		assertNotNull(testDirPath);
	}
	
	@Test
	public void testStore() throws IOException, FileWriteException {
		
		assertEquals(1, Files.list(testDirPath).count());
		
		fileStorage.store(testMpFile, testDirPath);
		
		assertEquals(2, Files.list(testDirPath).count());
		assertTrue(Files.exists(testDirPath.resolve(newFileName)));
	}
	
	@Test
	public void testDelete1() throws IOException, NoSuchFileException, FileDeleteException {
		
		assertEquals(1, Files.list(testDirPath).count());
		
		fileStorage.delete(testDirPath.resolve(existingFileName));
		
		assertEquals(0, Files.list(testDirPath).count());
	}
	
	@Test
	public void testDelete2() throws IOException, FileDeleteException {
		
		boolean exc = false;
		
		assertEquals(1, Files.list(testDirPath).count());
		
		try {
			
			fileStorage.delete(testDirPath.resolve(newFileName));
		}
		catch (NoSuchFileException e) {
			
			assertTrue(e.getMessage().contains(newFileName));
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	@Test
	public void testGetInputStream1() throws NoSuchFileException, FileReadException, IOException {
		
		InputStream is = fileStorage.getInputStream(testDirPath.resolve(existingFileName));
		
		assertNotNull(is);
		
		byte[] bf = new byte[is.available()];
		is.read(bf);
		
		assertArrayEquals(testBytes, bf);
		
		is.close();
	}
	
	@Test
	public void testGetInputStream2() throws FileReadException {
		
		boolean exc = false;
		
		try {
			fileStorage.getInputStream(testDirPath.resolve(newFileName));
		}
		catch (NoSuchFileException e) {
			
			assertTrue(e.getMessage().contains(newFileName));
			exc = true;
		}
		
		assertTrue(exc);
	}
}






