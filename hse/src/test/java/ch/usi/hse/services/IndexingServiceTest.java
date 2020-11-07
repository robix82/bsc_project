package ch.usi.hse.services;

import static org.mockito.Mockito.doThrow;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.mockito.Mockito.*;

import java.util.List;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import ch.usi.hse.db.entities.DocCollection;
import ch.usi.hse.db.repositories.DocCollectionRepository;
import ch.usi.hse.exceptions.DocCollectionExistsException;
import ch.usi.hse.exceptions.FileDeleteException;
import ch.usi.hse.exceptions.FileReadException;
import ch.usi.hse.exceptions.FileWriteException;
import ch.usi.hse.exceptions.LanguageNotSupportedException;
import ch.usi.hse.exceptions.NoSuchDocCollectionException;
import ch.usi.hse.exceptions.NoSuchFileException;
import ch.usi.hse.indexing.IndexBuilder;
import ch.usi.hse.indexing.IndexingResult;
import ch.usi.hse.storage.FileStorage;
import ch.usi.hse.storage.UrlListStorage;

public class IndexingServiceTest {
	
	@Mock
	private UrlListStorage urlListStorage;
	
	@Mock
	private FileStorage fileStorage;
	
	@Mock
	private IndexBuilder indexBuilder;
	
	@Mock
	private DocCollectionRepository collectionRepo;

	private IndexingService indexingService;
	
	private List<String> fileList;
	private String existingUrlListName, newUrlListName, badUrlListName, existingDocCollectionName;
	private MockMultipartFile newFile, badFile;
	private byte[] savedBytes;
	
	private List<DocCollection> savedDocCollections;
	private DocCollection existingDocCollection, newDocCollection;
	
	private Path existingDirectory, nonExistingDirectory;
	
	@BeforeEach
	public void setUp() throws Exception {
		
		initMocks(this);
		
		indexingService = new IndexingService(true, true,
											  fileStorage,
											  urlListStorage,
											  indexBuilder,
											  collectionRepo);

		existingDirectory = Paths.get("validDir");
		nonExistingDirectory = Paths.get("noSuchDir");
		
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
		c1.setIndexDir(existingDirectory.toString());
		c1.setRawFilesDir(existingDirectory.toString());
		c1.setExtractionResultsDir(existingDirectory.toString());
		c1.setIndexed(true);
		
		
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
		
		doNothing().when(fileStorage).removeDirectory(existingDirectory);
		doThrow(NoSuchFileException.class).when(fileStorage).removeDirectory(nonExistingDirectory);
	} 
	
	// URL LISTS
	 
	@Test
	public void testSavedUrlLists() throws FileReadException {
		
		List<String> res = indexingService.savedUrlLists();
		
		assertIterableEquals(fileList, res);
	}
	
	@Test
	public void testAddUrlList1() {
		
		boolean noexc = false;
		
		try {
			indexingService.addUrlList(newFile);
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
			indexingService.addUrlList(badFile);
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
			indexingService.removeUrlList(existingUrlListName);
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
			indexingService.removeUrlList(badUrlListName);
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
			indexingService.removeUrlList(newUrlListName);
		}
		catch (NoSuchFileException e) {
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	@Test
	public void testGetUrlListFile1() throws NoSuchFileException, FileReadException, IOException {
		
		InputStream is = indexingService.getUrlListFile(existingUrlListName);
		
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
			indexingService.getUrlListFile(newUrlListName);
		}
		catch (NoSuchFileException e) {
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	// DOC COLLECTIONS
	
	@Test
	public void testDocCollections() {
		
		List<DocCollection> res = indexingService.docCollections();
		
		assertIterableEquals(savedDocCollections, res);
	}
	
	@Test
	public void testAddDocCollection1() throws Exception {
		
		DocCollection saved = indexingService.addDocCollection(newDocCollection);
		
		assertEquals(newDocCollection, saved);
	} 
	
	@Test
	public void testAddDocCollection2() throws Exception {
		
		boolean exc = false;
		
		try {
			indexingService.addDocCollection(existingDocCollection);
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
			indexingService.addDocCollection(newDocCollection);
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
			indexingService.addDocCollection(newDocCollection);
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
			indexingService.addDocCollection(newDocCollection);
		}
		catch (DocCollectionExistsException e) {
			
			assertTrue(e.getMessage().contains(existingDocCollectionName));
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	@Test
	public void testUpdateDocCollection1() throws Exception {
		
		String newName = "newCollection";
		existingDocCollection.setName(newName);
		
		DocCollection updated = indexingService.updateDocCollection(existingDocCollection);
		
		assertEquals(existingDocCollection, updated);
	}
	 
	@Test
	public void testUpdateDocCollection2() throws Exception {
		
		boolean exc = false;
		
		try {
			indexingService.updateDocCollection(newDocCollection);
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
			indexingService.updateDocCollection(existingDocCollection);
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
			indexingService.updateDocCollection(existingDocCollection);
		}
		catch (LanguageNotSupportedException e) {
			
			assertTrue(e.getMessage().contains(badLanguage));
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	@Test
	public void testUpdateDocCollection5() throws Exception {
		
		boolean exc = false;
		
		DocCollection c = new DocCollection(existingDocCollectionName, existingUrlListName);
		c.setId(existingDocCollection.getId());
		 
		try {
			indexingService.updateDocCollection(c);
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
			
			indexingService.removeDocCollection(existingDocCollection);
			noexc = true;
		}
		catch (Exception e) {
			noexc = false;
		}
		
		assertTrue(noexc);
	}
	
	@Test // non-existing collection
	public void testRemoveDocCollection2() throws FileDeleteException, NoSuchFileException {
		
		boolean exc = false;
		
		try {
			indexingService.removeDocCollection(newDocCollection);
		}
		catch (NoSuchDocCollectionException e) {
			 
			assertTrue(e.getMessage().contains(Integer.toString(newDocCollection.getId())));
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	@Test
	public void testBuildIndex1() throws NoSuchFileException, FileReadException, FileWriteException {
		
		IndexingResult res = indexBuilder.buildIndex(existingDocCollection);
		
		assertNotNull(res);
	}
	
	@Test
	public void testBuildIndex2() throws LanguageNotSupportedException, 
										 NoSuchFileException, 
										 FileReadException, FileWriteException {
		
		int badId = 87645;
		newDocCollection.setId(badId);
		
		boolean exc = false;
		
		try {
				indexingService.buildIndex(newDocCollection);
		}
		catch (NoSuchDocCollectionException e) {
			
			assertTrue(e.getMessage().contains(Integer.toString(badId)));
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	@Test
	public void testBuildIndex3() throws Exception {
		
		existingDocCollection.setUrlListName(newUrlListName);
		
		boolean exc = false;
		
		try {
			indexingService.buildIndex(existingDocCollection);
		}
		catch (NoSuchFileException e) {
			
			assertTrue(e.getMessage().contains(newUrlListName));
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	@Test
	public void testBuildIndex4() throws Exception {
		
		String badLanguage = "xy";
		existingDocCollection.setLanguage(badLanguage);
		
		boolean exc = false;
		
		try {
			indexingService.buildIndex(existingDocCollection);
		}
		catch (LanguageNotSupportedException e) {
			
			assertTrue(e.getMessage().contains(badLanguage));
			exc = true;
		}
		
		assertTrue(exc);
	}
}



 






 






