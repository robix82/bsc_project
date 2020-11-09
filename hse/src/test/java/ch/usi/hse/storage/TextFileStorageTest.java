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

public class TextFileStorageTest {

	@TempDir
	Path storageDir;
	
	private TextFileStorage tfs;
	
	private String newFileName;
	private String existingFileName;
	private Path existingFile;
	private MockMultipartFile newMpFile;
	private List<String> existingFileLines, newFileLines;
	private byte[] existingFileBytes, newFileBytes;
	
	@BeforeEach
	public void setUp() throws IOException {
		
		tfs = new TextFileStorage();
		
		newFileName = "newFile.txt";
		existingFileName = "existingFile.txt";
		
		existingFileLines = List.of("line1", "line2", "line3");
		newFileLines = List.of("line4", "line5", "line6");
		
		StringBuilder sb1 = new StringBuilder();
		
		for (String s : existingFileLines) {
			sb1.append(s).append("\n");
		}
		
		existingFileBytes = sb1.toString().getBytes();
		
		existingFile = Files.createFile(storageDir.resolve(existingFileName));
		Files.write(existingFile, existingFileBytes);
		
		StringBuilder sb2 = new StringBuilder();
		
		for (String s : newFileLines) {
			sb2.append(s).append("\n");
		}
		
		newFileBytes = sb1.toString().getBytes();
		
		newMpFile = new MockMultipartFile("textFile",
									      newFileName,
									      MediaType.TEXT_PLAIN_VALUE,
									      newFileBytes);
	}
	 
	@AfterEach
	public void cleanup() throws IOException {
		cleanFiles();
	}
	
	@Test
	public void testSetup() throws IOException {
		
		assertEquals(1, Files.list(storageDir).count());
		assertTrue(Files.exists(existingFile));
		assertArrayEquals(existingFileBytes, Files.readAllBytes(existingFile));
	}
	
	@Test
	public void testStoreTextFile() throws IOException, FileWriteException {
		
		long count = Files.list(storageDir).count();
		assertFalse(Files.exists(storageDir.resolve(newFileName)));
		
		tfs.storeTextFile(newMpFile, storageDir);
		
		assertEquals(count +1, Files.list(storageDir).count());
		assertTrue(Files.exists(storageDir.resolve(newFileName)));
		assertArrayEquals(newFileBytes, Files.readAllBytes(storageDir.resolve(newFileName)));
	}
	
	@Test
	public void testDeleteTextFile1() throws Exception {
		
		long count = Files.list(storageDir).count();
		assertTrue(Files.exists(existingFile));
		
		tfs.deleteTextFile(existingFileName, storageDir);
		
		assertEquals(count -1, Files.list(storageDir).count());
		assertFalse(Files.exists(existingFile));
	}
	
	@Test
	public void testDeleteTextFile2() throws FileDeleteException {
		
		boolean exc;
		
		try {
			
			tfs.deleteTextFile(newFileName, storageDir);
			exc = false;
		} 
		catch (NoSuchFileException e) {
			
			assertTrue(e.getMessage().contains(newFileName));
			exc = true;
		}
	
		assertTrue(exc);
	}
	
	@Test
	public void testGetTextLines1() throws Exception {
		
		List<String> lines = tfs.getTextLines(existingFileName, storageDir);
		
		assertIterableEquals(existingFileLines, lines);
	}
	
	@Test
	public void testGetTextLines2() throws Exception {
		
		boolean exc;
		
		try {
			
			tfs.getTextLines(newFileName, storageDir);
			exc = false;
		}
		catch (NoSuchFileException e) {
			
			assertTrue(e.getMessage().contains(newFileName));
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	@Test
	public void testListTextFiles() throws FileReadException {
		
		List<String> fileNames = tfs.listTextFiles(storageDir);
		
		assertEquals(1, fileNames.size());
		assertTrue(fileNames.contains(existingFileName));
	}
	
	@Test
	public void testGetTextFileAsStream() throws Exception {
		
		InputStream is = tfs.getTextFileAsStream(existingFileName, storageDir);
		
		assertNotNull(is);
		byte[] content = new byte[is.available()];
		is.read(content);
		
		assertArrayEquals(existingFileBytes, content);
	}
	
	@Test
	public void TestGetTextFileAsStream2() throws Exception {
		
		boolean exc;
		
		try {
			
			tfs.getTextFileAsStream(newFileName, storageDir);
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














