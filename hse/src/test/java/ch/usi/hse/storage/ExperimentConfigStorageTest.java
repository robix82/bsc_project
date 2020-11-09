package ch.usi.hse.storage;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import ch.usi.hse.exceptions.*;

public class ExperimentConfigStorageTest {

	@TempDir
	Path storageDir;
	
	private ExperimentConfigStorage cnfs;
	
	private String existingFileName, newFileName;
	private List<String> existingConfigLines, newConfigLines;
	private Path existingFile;
	private MockMultipartFile newMpFile;
	private byte[] existingFileBytes, newFileBytes;
	
	@BeforeEach
	public void setUp() throws IOException, FileWriteException {
		
		cnfs = new ExperimentConfigStorage(storageDir);
		
		existingFileName = "existingFile";
		newFileName = "newFile";
		
		existingConfigLines = List.of("line1", "line2", "line3");
		newConfigLines = List.of("line4", "line5", "line6");
		
		StringBuilder sb1 = new StringBuilder();
		
		for (String s : existingConfigLines) {
			
			sb1.append("   \n")   // blank line to be ignored
			   .append("# comment\n") // comment to be ignored
			   .append(s).append("\n");
		}
		
		existingFileBytes = sb1.toString().getBytes();
		
		existingFile = Files.createFile(storageDir.resolve(existingFileName));
		Files.write(existingFile, existingFileBytes);
		
		StringBuilder sb2 = new StringBuilder();
		
		for (String s : newConfigLines) {
			
			sb2.append("   \n")   // blank line to be ignored
			   .append("# comment\n") // comment to be ignored
			   .append(s).append("\n");
		}
		
		newFileBytes = sb2.toString().getBytes();
		
		newMpFile = new MockMultipartFile("configFile",
								          newFileName,
								          MediaType.TEXT_PLAIN_VALUE,
								          newFileBytes);
	}
	
	@AfterEach
	public void cleanUp() throws IOException {
		
		cleanFiles();
	}
	
	@Test
	public void testSetUp() throws IOException {
		
		assertEquals(1, Files.list(storageDir).count());
		assertTrue(Files.exists(existingFile));
		assertArrayEquals(existingFileBytes, Files.readAllBytes(existingFile));
	}
	
	@Test
	public void testStoreConfigFile() throws Exception {
		
		long count = Files.list(storageDir).count();
		assertFalse(Files.exists(storageDir.resolve(newFileName)));
		
		cnfs.storeConfigFile(newMpFile);
		
		assertEquals(count +1, Files.list(storageDir).count());
		assertTrue(Files.exists(storageDir.resolve(newFileName)));
		assertArrayEquals(newFileBytes, Files.readAllBytes(storageDir.resolve(newFileName)));
	}
	
	@Test
	public void testDeleteConfigFile1() throws Exception {
		
		long count = Files.list(storageDir).count();
		assertTrue(Files.exists(existingFile));
		
		cnfs.deleteConfigFile(existingFileName);
		
		assertEquals(count -1, Files.list(storageDir).count());
		assertFalse(Files.exists(existingFile));
	}
	
	@Test
	public void testDeleteConfigFile2() throws Exception {
		
		boolean exc;
		
		try {
			
			cnfs.deleteConfigFile(newFileName);
			exc = false;
		}
		catch (NoSuchFileException e) {
			
			assertTrue(e.getMessage().contains(newFileName));
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	@Test
	public void testGetConfigLines1() throws Exception {
		
		List<String> lines = cnfs.getConfigLines(existingFileName);
		
		assertIterableEquals(existingConfigLines, lines);
	}
	
	@Test
	public void testGetConfigLines2() throws Exception {
		
		boolean exc;
		
		try {
			
			cnfs.getConfigLines(newFileName);
			exc = false;
		}
		catch (NoSuchFileException e) {
			
			assertTrue(e.getMessage().contains(newFileName));
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	@Test
	public void testGetConfigFileAsStream1() throws Exception {
		
		InputStream is = cnfs.getConfigFileAsStream(existingFileName);
		
		assertNotNull(is);
		byte[] content = new byte[is.available()];
		is.read(content);
		assertArrayEquals(existingFileBytes, content);
	}
	
	@Test
	public void testGetConfigFileAsStream2() throws Exception {
		
		boolean exc;
		
		try {
			
			cnfs.getConfigFileAsStream(newFileName);
			exc = false;
		}
		catch (NoSuchFileException e) {
			
			assertTrue(e.getMessage().contains(newFileName));
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	
	///////////////////////
	
	private void cleanFiles() throws IOException {

		Files.list(storageDir).forEach(f -> {
			try {
				Files.deleteIfExists(f);
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
}















