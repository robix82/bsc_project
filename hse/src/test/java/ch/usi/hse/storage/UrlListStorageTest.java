package ch.usi.hse.storage;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
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
public class UrlListStorageTest {

	@Value("${dir.urlLists}")
	private String storageDir;
	
	@Autowired
	private UrlListStorage listStorage;
	
	private Path storagePath;
	
	private List<String> savedList1, savedList2, newList, listNames;
	private String newName;
	private byte[] savedBytes; 
	
	@BeforeEach
	public void setUp() throws IOException {
		
		
		storagePath = Paths.get(storageDir);
		
		String name1 = "list1.txt";
		String name2 = "list2.txt"; 
		listNames = Arrays.asList(name1, name2); 
		
		savedList1 = Arrays.asList("url1", "url2", "url3");
		savedList2 = Arrays.asList("url4", "url5", "url6");
		newList = Arrays.asList("url7", "url8", "url9");
		newName = "newList.txt";
		
		Files.list(storagePath).forEach(f -> {
			try {
				Files.deleteIfExists(f);
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		});
		
		PrintStream ps1 = new PrintStream(new FileOutputStream(storageDir + name1));
		
		for (String s : savedList1) {
			ps1.println(s);
		}
		 
		ps1.close();
		
		PrintStream ps2 = new PrintStream(new FileOutputStream(storageDir + name2));
		
		for (String s : savedList2) {
			ps2.println(s);
		}
		
		ps2.close(); 
		
		File f = new File(storageDir + name1);
		savedBytes = FileUtils.readFileToByteArray(f);
	}
	
	@Test
	public void testSetup() throws IOException {
		
		assertEquals(2, Files.list(storagePath).count());
	}
	
	@Test
	public void testStore() throws IOException, FileWriteException {
		
		StringBuilder sb = new StringBuilder();
		
		for (String s : newList) {
			
			sb.append(s).append("\n");
		}
		
		MockMultipartFile file = new MockMultipartFile(newName,
													   newName,
													   MediaType.TEXT_PLAIN_VALUE,
													   sb.toString().getBytes());
		
		assertEquals(2, Files.list(storagePath).count());
		assertFalse(Files.exists(storagePath.resolve(newName)));
		
		listStorage.store(file);
		
		assertEquals(3, Files.list(storagePath).count());
		assertTrue(Files.exists(storagePath.resolve(newName)));
	}
	
	@Test
	public void testDelete1() throws IOException, NoSuchFileException, FileDeleteException {
		
		String name = listNames.get(0);
		
		assertEquals(2, Files.list(storagePath).count());
		assertTrue(Files.exists(storagePath.resolve(name)));
		
		listStorage.delete(name);
		
		assertEquals(1, Files.list(storagePath).count());
		assertFalse(Files.exists(storagePath.resolve(name)));
	}
	
	@Test
	public void testDelete2() throws IOException, FileDeleteException {
		
		boolean exc = false;
		
		assertEquals(2, Files.list(storagePath).count());
		
		try {
			listStorage.delete(newName);
		}
		catch (NoSuchFileException e) {
			
			assertTrue(e.getMessage().contains(newName));
			exc = true;
		}
		
		assertEquals(2, Files.list(storagePath).count());
		assertTrue(exc);
	}
	
	@Test
	public void testGetLines1() throws NoSuchFileException, FileReadException {
		
		List<String> res = listStorage.getLines(listNames.get(0));

		assertIterableEquals(savedList1, res);
	}
	
	@Test
	public void testGetLines2() throws FileReadException {
		
		boolean exc = false;
		
		try {
			listStorage.getLines(newName);
		}
		catch (NoSuchFileException e) {
			
			assertTrue(e.getMessage().contains(newName));
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	@Test
	public void testSavedFiles() throws FileReadException {
		
		List<String> fileNames = listStorage.savedFiles();
		
		assertEquals(2, fileNames.size());
		assertTrue(fileNames.contains(listNames.get(0)));
		assertTrue(fileNames.contains(listNames.get(1)));
	}
	
	@Test
	public void testgGetFileAsStream1() throws NoSuchFileException, FileReadException, IOException {
		
		InputStream is = listStorage.getFileAsStream(listNames.get(0));
		
		assertNotNull(is);
		
		byte[] bf = new byte[is.available()];
		is.read(bf);
		is.close();
		
		assertArrayEquals(savedBytes, bf);
	}
	
	@Test
	public void testGetFileAsStream2() throws FileReadException, IOException {
		
		boolean exc = false;
		
		try {
			listStorage.getFileAsStream(newName);
		}
		catch (NoSuchFileException e) {
			
			assertTrue(e.getMessage().contains(newName));
			exc = true;
		}
		
		assertTrue(exc);
	}
}











