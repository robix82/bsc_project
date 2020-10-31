package ch.usi.hse.services;

import static org.mockito.Mockito.doThrow;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

import java.util.List;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import ch.usi.hse.db.entities.DocCollection;
import ch.usi.hse.db.repositories.DocCollectionRepository;
import ch.usi.hse.dto.IndexingResult;
import ch.usi.hse.exceptions.DocCollectionExistsException;
import ch.usi.hse.exceptions.FileDeleteException;
import ch.usi.hse.exceptions.FileReadException;
import ch.usi.hse.exceptions.FileWriteException;
import ch.usi.hse.exceptions.LanguageNotSupportedException;
import ch.usi.hse.exceptions.NoSuchDocCollectionException;
import ch.usi.hse.exceptions.NoSuchFileException;
import ch.usi.hse.indexing.IndexBuilder;
import ch.usi.hse.storage.UrlListStorage;

@SpringBootTest
public class IndexingServiceTest {

	@MockBean
	private UrlListStorage urlListStorage;
	
	@MockBean
	private IndexBuilder indexBuilder;
	
	@MockBean
	private DocCollectionRepository collectionRepo;
		
	@Autowired
	private IndexingService service;
	
	private List<String> fileList;
	private String existingUrlListName, newUrlListName, badUrlListName, existingDocCollectionName;
	private MockMultipartFile newFile, badFile;
	private byte[] savedBytes;
	
	private List<DocCollection> savedDocCollections;
	private DocCollection existingDocCollection, newDocCollection;
	
	@BeforeEach
	public void setUp() throws FileReadException, FileWriteException, NoSuchFileException, FileDeleteException {
		
		fileList = Arrays.asList("urls1.txt", "urls2.txt", "urls3.txt");
		savedBytes = "content".getBytes();  
		
		existingUrlListName = fileList.get(0); 
		newUrlListName = "urls4.txt";
		badUrlListName = "bad.txt";
		

		
		newFile = new MockMultipartFile(newUrlListName,
										newUrlListName,
										MediaType.TEXT_PLAIN_VALUE,
					 					"some text".getBytes());
		
		existingDocCollectionName = "existingCollection";
		
		DocCollection c1 = new DocCollection("c1", "l1");
		DocCollection c2 = new DocCollection("c2", "l2");
		DocCollection c3 = new DocCollection("c3", "l3");
		c1.setId(1);
		c2.setId(2);
		c3.setId(3);
		c1.setUrlListName(existingUrlListName);
		c2.setUrlListName(existingUrlListName);
		c3.setUrlListName(existingUrlListName);
		
		savedDocCollections = List.of(c1, c2);
		existingDocCollection = c1;
		newDocCollection = c3;
		
		when(urlListStorage.savedFiles()).thenReturn(fileList);
		doNothing().when(urlListStorage).store(newFile);
		doNothing().when(urlListStorage).delete(existingUrlListName);
		doThrow(FileWriteException.class).when(urlListStorage).store(badFile);
		doThrow(FileDeleteException.class).when(urlListStorage).delete(badUrlListName);
		doThrow(NoSuchFileException.class).when(urlListStorage).delete(newUrlListName);
		
		when(urlListStorage.getFileAsStream(existingUrlListName)).thenReturn(new ByteArrayInputStream(savedBytes));
		doThrow(NoSuchFileException.class).when(urlListStorage).getFileAsStream(newUrlListName);
		
		when(collectionRepo.findAll()).thenReturn(savedDocCollections);
		when(collectionRepo.existsById(existingDocCollection.getId())).thenReturn(true);
		when(collectionRepo.existsById(newDocCollection.getId())).thenReturn(false);
		when(collectionRepo.existsByName(anyString())).thenReturn(false);
		when(collectionRepo.existsByName(existingDocCollectionName)).thenReturn(true);
		when(collectionRepo.save(newDocCollection)).thenReturn(newDocCollection);
		when(collectionRepo.save(existingDocCollection)).thenReturn(existingDocCollection);
		when(collectionRepo.findById(existingDocCollection.getId())).thenReturn(existingDocCollection);
		doNothing().when(collectionRepo).delete(existingDocCollection);
		
		when(indexBuilder.buildIndex(any(DocCollection.class))).thenReturn(new IndexingResult());
	}
	
	// URL LISTS
	
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
			service.removeUrlList(existingUrlListName);
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
			service.removeUrlList(badUrlListName);
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
			service.removeUrlList(newUrlListName);
		}
		catch (NoSuchFileException e) {
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	@Test
	public void testGetUrlListFile1() throws NoSuchFileException, FileReadException, IOException {
		
		InputStream is = service.getUrlListFile(existingUrlListName);
		
		assertNotNull(is);
		
		byte[] bf = new byte[is.available()];
		is.read(bf);
		is.close();
		
		assertArrayEquals(savedBytes, bf);
	}
	
	@Test
	public void testGetUrlListFile2() throws FileReadException {
		
		boolean exc = false;
		
		try {
			service.getUrlListFile(newUrlListName);
		}
		catch (NoSuchFileException e) {
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	// DOC COLLECTIONS
	
	@Test
	public void testDocCollections() {
		
		List<DocCollection> res = service.docCollections();
		
		assertIterableEquals(savedDocCollections, res);
	}
	
	@Test
	public void testAddDocCollection1() throws Exception {
		
		DocCollection saved = service.addDocCollection(newDocCollection);
		
		assertEquals(newDocCollection, saved);
	}
	
	@Test
	public void testAddDocCollection2() throws Exception {
		
		boolean exc = false;
		
		try {
			service.addDocCollection(existingDocCollection);
		}
		catch (DocCollectionExistsException e) {
			
			assertTrue(e.getMessage().contains(Integer.toString(existingDocCollection.getId())));
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	@Test
	public void testAddDocCollection3() throws Exception {
		
		boolean exc = false;
		
		newDocCollection.setUrlListName(newUrlListName);
		
		try {
			service.addDocCollection(newDocCollection);
		}
		catch (NoSuchFileException e) {
			assertTrue(e.getMessage().contains(newUrlListName));
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	@Test
	public void testAddDocCollection4() throws Exception {
		
		boolean exc = false; 
		
		String badLanguage = "XY";
		newDocCollection.setLanguage(badLanguage);
		
		try {
			service.addDocCollection(newDocCollection);
		}
		catch (LanguageNotSupportedException e) {
			
			assertTrue(e.getMessage().contains(badLanguage));
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	@Test
	public void testAddDocCollection5() throws Exception {
		
		boolean exc = false;
		newDocCollection.setName(existingDocCollectionName);
		
		try {
			service.addDocCollection(newDocCollection);
		}
		catch (DocCollectionExistsException e) {
			
			assertTrue(e.getMessage().contains(existingDocCollectionName));
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	@Test
	public void testUpdateDocCollection1() throws Exception {
		
		existingDocCollection.setIndexDirName("newName");
		
		DocCollection updated = service.updateDocCollection(existingDocCollection);
		
		assertEquals(existingDocCollection, updated);
	}
	
	@Test
	public void testUpdateDocCollection2() throws Exception {
		
		boolean exc = false;
		
		try {
			service.updateDocCollection(newDocCollection);
		}
		catch (NoSuchDocCollectionException e) {
			
			assertTrue(e.getMessage().contains(Integer.toString(newDocCollection.getId())));
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	@Test
	public void testUpdateDocCollection3() throws Exception {
		
		boolean exc = false;
		
		existingDocCollection.setUrlListName(newUrlListName);
		
		try {
			service.updateDocCollection(existingDocCollection);
		}
		catch (NoSuchFileException e) {
			
			assertTrue(e.getMessage().contains(existingDocCollection.getUrlListName()));
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	@Test
	public void testUpdateDocCollection4() throws Exception {
		
		boolean exc = false;
		
		String badLanguage = "XY";
		
		existingDocCollection.setLanguage(badLanguage);
		
		try {
			service.updateDocCollection(existingDocCollection);
		}
		catch (LanguageNotSupportedException e) {
			
			assertTrue(e.getMessage().contains(badLanguage));
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	@Test
	public void testUpdateDocCollecction5() throws Exception {
		
		boolean exc = false;
		
		DocCollection c = new DocCollection(existingDocCollectionName, existingUrlListName);
		c.setId(existingDocCollection.getId());
		
		try {
			service.updateDocCollection(c);
		}
		catch (DocCollectionExistsException e) {
			
			assertTrue(e.getMessage().contains(c.getName()));
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	@Test 
	public void testRemoveDocCollection1() {
		
		boolean noexc = false;
		
		try {
			
			service.removeDocCollection(existingDocCollection);
			noexc = true;
		}
		catch (Exception e) {
			noexc = false;
		}
		
		assertTrue(noexc);
	}
	
	@Test
	public void testRemoveDocCollection2() {
		
		boolean exc = false;
		
		try {
			service.removeDocCollection(newDocCollection);
		}
		catch (NoSuchDocCollectionException e) {
			
			assertTrue(e.getMessage().contains(Integer.toString(newDocCollection.getId())));
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	@Test
	public void testBuildIndex() {
		
		IndexingResult res = indexBuilder.buildIndex(existingDocCollection);
		
		assertNotNull(res);
	}
}

















