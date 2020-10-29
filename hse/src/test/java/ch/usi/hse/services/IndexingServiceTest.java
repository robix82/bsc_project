package ch.usi.hse.services;

import static org.mockito.Mockito.doThrow;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import ch.usi.hse.exceptions.FileDeleteException;
import ch.usi.hse.exceptions.FileReadException;
import ch.usi.hse.exceptions.FileWriteException;
import ch.usi.hse.exceptions.NoSuchFileException;
import ch.usi.hse.storage.UrlListStorage;

@SpringBootTest
public class IndexingServiceTest {

	@MockBean
	private UrlListStorage urlListStorage;
	
	@Autowired
	private IndexingService service;
	
	private List<String> fileList;
	private String existingName, newName, badName;
	private MockMultipartFile newFile, badFile;
	
	@BeforeEach
	public void setUp() throws FileReadException, FileWriteException, NoSuchFileException, FileDeleteException {
		
		fileList = Arrays.asList("urls1.txt", "urls2.txt", "urls3.txt");
		
		existingName = fileList.get(0);
		newName = "urls4.txt";
		badName = "bad.txt";
		
		newFile = new MockMultipartFile(newName,
										newName,
										MediaType.TEXT_PLAIN_VALUE,
										"some text".getBytes());
		
		when(urlListStorage.savedFiles()).thenReturn(fileList);
		doNothing().when(urlListStorage).store(newFile);
		doNothing().when(urlListStorage).delete(existingName);
		doThrow(FileWriteException.class).when(urlListStorage).store(badFile);
		doThrow(FileDeleteException.class).when(urlListStorage).delete(badName);
		doThrow(NoSuchFileException.class).when(urlListStorage).delete(newName);
	}
	
	@Test
	public void testSavedUrlLists() throws FileReadException {
		
		List<String> res = service.savedUrlLists();
		
		assertIterableEquals(fileList, res);
	}
	
	@Test
	public void testAddUrlList1() {
		
		boolean noexc = false;
		
		try {
			service.addUrlList(newFile);
			noexc = true;
		}
		catch (Exception e) {
			noexc = false;
		}
		
		assertTrue(noexc);
	}
	
	@Test
	public void testAddUrlList2() {
		
		boolean exc = false;
		
		try {
			service.addUrlList(badFile);
		}
		catch (FileWriteException e) {
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	@Test
	public void testRemoveUrlList1() {
		
		boolean noexc = false;
		
		try {
			service.removeUrlList(existingName);
			noexc = true;
		}
		catch (Exception e) {
			noexc = false;
		}
		
		assertTrue(noexc);
	}
	
	@Test
	public void testRemoveUrlList2() throws NoSuchFileException {
		
		boolean exc = false;
		
		try {
			service.removeUrlList(badName);
		}
		catch (FileDeleteException e) {
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	@Test
	public void testRemoveUrlList3() throws FileDeleteException {
		
		boolean exc = false;
		
		try {
			service.removeUrlList(newName);
		}
		catch (NoSuchFileException e) {
			exc = true;
		}
		
		assertTrue(exc);
	}
}

















