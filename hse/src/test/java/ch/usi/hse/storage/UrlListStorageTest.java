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

import ch.usi.hse.exceptions.FileWriteException;
import ch.usi.hse.exceptions.NoSuchFileException;

public class UrlListStorageTest {

	@TempDir
	Path storageDir;

	private UrlListStorage uls;
	
	private String existingFileName, newFileName;
	private List<String> existingUrls, newUrls;
	private Path existingFile;
	private MockMultipartFile newMpFile;
	private byte[] existingFileBytes, newFileBytes;
	
	@BeforeEach
	public void setUp() throws FileWriteException, IOException {
		
		uls = new UrlListStorage(storageDir);
		
		newFileName = "newFile";
		existingFileName = "existingFile";
		
		existingUrls = List.of("url1", "url2", "url3");
		newUrls = List.of("url4", "url5", "url6");
		
		StringBuilder sb1 = new StringBuilder();
		
		for (String s : existingUrls) {
			
			sb1.append("   \n")   // blank line to be ignored
			   .append(s).append("\n");
		}
		
		existingFileBytes = sb1.toString().getBytes();
		
		existingFile = Files.createFile(storageDir.resolve(existingFileName));
		Files.write(existingFile, existingFileBytes);
		
		StringBuilder sb2 = new StringBuilder();
		
		for (String s : newUrls) {
			
			sb2.append("   \n")   // blank line to be ignored
			   .append(s).append("\n");
		}
		
		newFileBytes = sb2.toString().getBytes();
		
		newMpFile = new MockMultipartFile("urlFile",
										  newFileName,
										  MediaType.TEXT_PLAIN_VALUE,
										  newFileBytes);
	}
	
	@AfterEach
	public void cleanUp() throws IOException {
		
		cleanFiles();
	}
	
	@Test
	public void testSetup() throws IOException {
		
		assertEquals(1, Files.list(storageDir).count());
		assertTrue(Files.exists(existingFile));
		assertArrayEquals(existingFileBytes, Files.readAllBytes(existingFile));
	}
	
	@Test
	public void testStoreUrlList() throws Exception {
		
		long count = Files.list(storageDir).count();
		assertFalse(Files.exists(storageDir.resolve(newFileName)));
		
		uls.storeUrlList(newMpFile);
		
		assertEquals(count +1, Files.list(storageDir).count());
		assertTrue(Files.exists(storageDir.resolve(newFileName)));
		assertArrayEquals(newFileBytes, Files.readAllBytes(storageDir.resolve(newFileName)));
	}
	
	@Test
	public void testDeleteUrlList1() throws Exception {
		
		long count = Files.list(storageDir).count();
		assertTrue(Files.exists(existingFile));
		
		uls.deleteUrlList(existingFileName);
		
		assertEquals(count -1, Files.list(storageDir).count());
		assertFalse(Files.exists(existingFile));
	}
	
	@Test
	public void testDeleteUrlList2() throws Exception {
		
		boolean exc;
		
		try {
			
			uls.deleteUrlList(newFileName);
			exc = false;
		}
		catch (NoSuchFileException e) {
			
			assertTrue(e.getMessage().contains(newFileName));
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	@Test
	public void testGetUrlLines1() throws Exception {
		
		List<String> lines = uls.getUrlLines(existingFileName);
		
		assertIterableEquals(existingUrls, lines);
	}
	
	@Test
	public void testGetUrlLines2() throws Exception {
		
		boolean exc;
		
		try {
			
			uls.getUrlLines(newFileName);
			exc = false;
		}
		catch (NoSuchFileException e) {
			
			assertTrue(e.getMessage().contains(newFileName));
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	@Test
	public void testListUrlFiles() throws Exception {
		
		List<String> files = uls.listUrlFiles();
		
		assertEquals(1, files.size());
		assertTrue(files.contains(existingFileName));
	}
	
	@Test
	public void testGetUrlFileAsStream1() throws Exception {
		
		InputStream is = uls.getUrlFileAsStream(existingFileName);
		
		assertNotNull(is);
		byte[] content = new byte[is.available()];
		is.read(content);
		assertArrayEquals(existingFileBytes, content);
	}
	
	@Test
	public void testGetUrlFileAsStream2() throws Exception {
		
		boolean exc;
		
		try {
			
			uls.getUrlFileAsStream(newFileName);
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











