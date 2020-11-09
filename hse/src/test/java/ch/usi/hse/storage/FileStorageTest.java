package ch.usi.hse.storage;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import ch.usi.hse.exceptions.FileDeleteException;
import ch.usi.hse.exceptions.FileReadException;
import ch.usi.hse.exceptions.FileWriteException;
import ch.usi.hse.exceptions.NoSuchFileException;

public class FileStorageTest {

	@TempDir
	Path testDir;
	
	private FileStorage fileStorage;
	
	private String newFileName;
	private String existingFileName;
	private MockMultipartFile testMpFile;
	private String testString;
	private byte[] testBytes;
	
	@BeforeEach 
	public void setUp() throws IOException {
		 
		fileStorage = new FileStorage();
		
		testString = "some content";
		testBytes = testString.getBytes();  
		
		newFileName = "newFile.txt";
		existingFileName = "existingFile.txt";

		testMpFile = new MockMultipartFile("file",
										   newFileName,
										   MediaType.TEXT_PLAIN_VALUE,
										   testBytes);
		
		Files.writeString(testDir.resolve(existingFileName), testString);
	}
	
	@AfterEach
	public void cleanup() throws IOException {
		cleanFiles();
	}
	
	@Test // store MultipartFile on Path
	public void testStore1() throws IOException, FileWriteException {
		
		assertEquals(1, Files.list(testDir).count());
		Path fPath = testDir.resolve(newFileName);
		
		fileStorage.store(testMpFile, testDir);
		
		// check file is created
		assertEquals(2, Files.list(testDir).count());
		assertTrue(Files.exists(fPath));
		
		// check content
		byte[] res = Files.readAllBytes(fPath);
		assertArrayEquals(testBytes, res);
	}
	
	@Test // store MultipartFile on Path String
	public void tetStore2() throws IOException, FileWriteException {
		
		assertEquals(1, Files.list(testDir).count());
		Path fPath = testDir.resolve(newFileName);
		
		fileStorage.store(testMpFile, testDir.toString());
		
		// check file is created
		assertEquals(2, Files.list(testDir).count());
		assertTrue(Files.exists(fPath));
		
		// check content
		byte[] res = Files.readAllBytes(fPath);
		assertArrayEquals(testBytes, res);
	}
	
	@Test // Store InputStream on Path
	public void testStore3() throws IOException, FileWriteException {
		
		assertEquals(1, Files.list(testDir).count());
		
		byte[] data = "new content".getBytes();
		InputStream is = new ByteArrayInputStream(data);
		Path fPath = testDir.resolve(newFileName);
	
		fileStorage.store(is, fPath);
		
		//check file is created
		assertEquals(2, Files.list(testDir).count());
		assertTrue(Files.exists(fPath));
		
		// check content
		byte[] res = Files.readAllBytes(fPath);
		assertArrayEquals(data, res);
	}
	
	@Test // Store String on path
	public void testStrore4() throws IOException, FileWriteException {
		
		String testString = "some string";
		Path fPath = testDir.resolve(newFileName);
		
		assertEquals(1, Files.list(testDir).count());
		
		fileStorage.store(testString,  fPath);
		
		//check file is created
		assertEquals(2, Files.list(testDir).count());
		assertTrue(Files.exists(fPath));
				
		// check content
		byte[] res = Files.readAllBytes(fPath);
		assertArrayEquals(testString.getBytes(), res);
	}
	
	@Test
	public void testDelete1() throws IOException, NoSuchFileException, FileDeleteException {
		
		assertEquals(1, Files.list(testDir).count());
		
		fileStorage.delete(testDir.resolve(existingFileName));
		
		assertEquals(0, Files.list(testDir).count());
	}
	
	@Test
	public void testDelete3() throws IOException, FileDeleteException {
		
		boolean exc = false;
		
		assertEquals(1, Files.list(testDir).count());
		
		try {
			
			fileStorage.delete(testDir.resolve(newFileName));
		}
		catch (NoSuchFileException e) {
			
			assertTrue(e.getMessage().contains(newFileName));
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	@Test
	public void testGetInputStream1() throws NoSuchFileException, FileReadException, IOException {
		
		InputStream is = fileStorage.getInputStream(testDir.resolve(existingFileName));
		
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
			fileStorage.getInputStream(testDir.resolve(newFileName));
		}
		catch (NoSuchFileException e) {
			
			assertTrue(e.getMessage().contains(newFileName));
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	@Test
	public void testRemoveDirctory1() throws IOException, NoSuchFileException, FileDeleteException {
		
		Path d0 = testDir.resolve("d0");
		Path d1 = d0.resolve("d1");
		Path f1 = d0.resolve("f1");
		Path f2 = d1.resolve("f2");
		
		Files.createDirectory(d0);
		Files.createDirectory(d1);
		Files.createFile(f1);
		Files.createFile(f2);
		
		assertTrue(Files.exists(d0));
		assertTrue(Files.exists(d1));
		assertTrue(Files.exists(f1));
		assertTrue(Files.exists(f2));
		
		fileStorage.removeDirectory(d0);
		
		assertFalse(Files.exists(d0));
		assertFalse(Files.exists(d1));
		assertFalse(Files.exists(f1));
		assertFalse(Files.exists(f2));
	}
	
	@Test
	public void testRemoveDirectory2() throws FileDeleteException {
		
		Path noSuchDir = testDir.resolve("noSuchDir");
		assertFalse(Files.exists(noSuchDir));
		
		boolean exc = false;
		
		try {
			fileStorage.removeDirectory(noSuchDir);
		}
		catch (NoSuchFileException e) {
			
			assertTrue(e.getMessage().contains(noSuchDir.toString()));
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	@Test
	public void testClearDirectory1() throws IOException, NoSuchFileException, FileDeleteException {
		
		Path d0 = testDir.resolve("d0");
		Path d1 = d0.resolve("d1");
		Path f1 = d0.resolve("f1");
		Path f2 = d1.resolve("f2");
		
		Files.createDirectory(d0);
		Files.createDirectory(d1);
		Files.createFile(f1);
		Files.createFile(f2);
		
		assertNotEquals(0, Files.list(d0).count());
		
		fileStorage.clearDirectory(d0);
		
		assertEquals(0, Files.list(d0).count());
		
		Files.delete(d0);
	}
	
	@Test 
	public void testClearDirectory2() throws FileDeleteException {
		
		Path noSuchDir = testDir.resolve("noSuchDir");
		assertFalse(Files.exists(noSuchDir));
		
		boolean exc = false;
		
		try {
			fileStorage.clearDirectory(noSuchDir);
		}
		catch (NoSuchFileException e) {
			
			assertTrue(e.getMessage().contains(noSuchDir.toString()));
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	private void cleanFiles() throws IOException {
		
		Files.list(testDir).forEach(f -> {
			try {
				Files.deleteIfExists(f);
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
}


















