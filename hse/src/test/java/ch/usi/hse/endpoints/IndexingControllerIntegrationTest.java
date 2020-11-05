package ch.usi.hse.endpoints;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.tomcat.jni.Status;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import ch.usi.hse.config.Language;
import ch.usi.hse.db.entities.DocCollection;
import ch.usi.hse.db.repositories.DocCollectionRepository;
import ch.usi.hse.exceptions.ApiError;

@SpringBootTest
@AutoConfigureMockMvc
public class IndexingControllerIntegrationTest {

	@Value("${dir.urlLists}")
	private Path urlListsPath;
	
	@Value("${dir.indices}")
	private Path indexDirPath;
	
	@Autowired
	private DocCollectionRepository collectionRepo;
	
	private String base = "/indexing";
	
	@Autowired
	private MockMvc mvc;
	
	private List<String> urlListNames;
	private String urlListName1, urlListName2, urlListName3;
	private MockMultipartFile newUrlListFile;
	private byte[] urlListData1, urlListData2, urlListData3;
	
	private List<DocCollection> savedDocCollections;
	private DocCollection newDocCollection;
	
	private ObjectMapper mapper;
	private ObjectWriter writer;
	private MediaType json; 
	
	 
	@BeforeEach
	public void setUp() throws IOException {
		
		//   Set up existing urlLists urlListName1 (urlListData1) and urlListName2 (urlListData2),
		//   MockMultipartFile urlListName3 (urlListData3) for upload
		
		mapper = new ObjectMapper();
		mapper.findAndRegisterModules();
		writer = mapper.writer().withDefaultPrettyPrinter();
		json = new MediaType(MediaType.APPLICATION_JSON.getType(), 
			       MediaType.APPLICATION_JSON.getSubtype(), 
			       Charset.forName("utf8"));
		
		urlListName1 = "urls1.txt";
		urlListName2 = "urls2.txt";
		urlListName3 = "newUrls.txt";
		urlListNames = List.of(urlListName1, urlListName2);
		
		List<String> urls1 = List.of("url1", "url2", "url3");
		List<String> urls2 = List.of("url4", "url5", "url6");
		List<String> urls3 = List.of("url7", "url8", "url9");
				
		StringBuilder sb1 = new StringBuilder();
		
		for (String s : urls1) {
			sb1.append(s).append("\n");
		}
		
		StringBuilder sb2 = new StringBuilder();
		
		for (String s : urls2) {
			sb2.append(s).append("\n");
		}
		
		StringBuilder sb3 = new StringBuilder();
		
		for (String s : urls3) {
			sb3.append(s).append("\n");
		}
		
		urlListData1 = sb1.toString().getBytes();
		urlListData2 = sb2.toString().getBytes();
		urlListData3 = sb3.toString().getBytes();
		
		newUrlListFile = new MockMultipartFile("file",
											   urlListName3,
											   MediaType.TEXT_PLAIN_VALUE,
											   urlListData3);
		
		// set up existing DocCollections c1 (indexed) and c2 (not indexed)
		
		DocCollection c1 = new DocCollection("c1", urlListName1);
		DocCollection c2 = new DocCollection("c2", urlListName2);
		c1.setId(1);
		c2.setId(2);
		c1.setIndexed(true);
		c1.setIndexDir(indexDirPath.resolve("c1").toString());
			
		collectionRepo.deleteAll();
		
		savedDocCollections = List.of(collectionRepo.save(c1),
									  collectionRepo.save(c2));

		newDocCollection = new DocCollection("c3", urlListName2);
		newDocCollection.setIndexDir(indexDirPath.resolve("c3").toString());
		
		// write test directory contents
		
		clearTestFiles();
		
		if (! Files.exists(urlListsPath)) {
			Files.createDirectories(urlListsPath);
		}
		
		if (! Files.exists(indexDirPath)) {
			Files.createDirectories(indexDirPath);
		}
				
		Path f1 = Files.createFile(urlListsPath.resolve(urlListName1));
		Path f2 = Files.createFile(urlListsPath.resolve(urlListName2));
				
		Files.write(f1, urlListData1, StandardOpenOption.CREATE);	
		Files.write(f2, urlListData2, StandardOpenOption.CREATE);
	}
	
	@AfterEach
	public void cleanup() throws IOException {
		
		clearTestFiles();
	}
	
	@Test
	public void setupTest() throws IOException {
		
		assertEquals(2, Files.list(urlListsPath).count());
		assertEquals(2, collectionRepo.count());
	}
	 
	@Test
	public void testGetIndexingUi() throws Exception {
		
		mvc.perform(get(base + "/ui"))
		   .andExpect(status().isOk())
		   .andExpect(model().attribute("urlLists", Matchers.iterableWithSize(urlListNames.size())))
		   .andExpect(model().attribute("docCollections", Matchers.iterableWithSize(savedDocCollections.size())))
		   .andExpect(model().attribute("languages", is(Language.languages)))
		   .andExpect(view().name("indexing"));
	}
	
	
	// URL LISTS
	
	@Test 
	public void testPostUrlList() throws Exception {
		
		String msg = "file " + urlListName3 + " uploaded";
		
		long countBefore = Files.list(urlListsPath).count();
		assertFalse(Files.exists(urlListsPath.resolve(urlListName3)));
		
		MvcResult res = mvc.perform(multipart(base + "/urlLists").file(newUrlListFile))
				 	       .andExpect(status().isCreated())
				 	       .andReturn();
		
		assertEquals(msg, resString(res));
		
		assertEquals(countBefore +1, Files.list(urlListsPath).count());
		assertTrue(Files.exists(urlListsPath.resolve(urlListName3)));
	}
	
	@Test
	public void testDeleteUrlList1() throws Exception {
		
		String msg = "file " + urlListName1 + " removed";
		
		String url = UriComponentsBuilder.fromUriString(base + "/urlLists")
				 					     .queryParam("fileName", urlListName1)
				 					     .build()
				 					     .toUriString();
		
		long countBefore = Files.list(urlListsPath).count();
		assertTrue(Files.exists(urlListsPath.resolve(urlListName1)));
		
		MvcResult res = mvc.perform(delete(url))
				 		   .andExpect(status().isOk())
				 		   .andReturn();
		
		assertEquals(msg, resString(res));
		assertEquals(countBefore -1, Files.list(urlListsPath).count());
		assertFalse(Files.exists(urlListsPath.resolve(urlListName1)));
	}
	
	@Test 
	public void testDeleteUrlList2() throws Exception {
		
		String url = UriComponentsBuilder.fromUriString(base + "/urlLists")
				 						 .queryParam("fileName", urlListName3)
				 						 .build()
				 						 .toUriString();
		
		long countBefore = Files.list(urlListsPath).count();
		
		MvcResult res = mvc.perform(delete(url))
				 		   .andExpect(status().isNotFound())
				 		   .andReturn();
		
		ApiError err = getApiError(res);
		
		assertEquals("NoSuchFileException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(urlListName3));
		assertEquals(countBefore, Files.list(urlListsPath).count());
	}
	
	@Test
	public void testDownloadUrlList1() throws Exception {
		
		String url = UriComponentsBuilder.fromUriString(base + "/urlLists/dl")
				 						 .queryParam("fileName", urlListName1)
				 						 .build()
				 						 .toUriString();
		
		MvcResult res = mvc.perform(get(url))
				 		   .andExpect(status().isOk())
				 		   .andReturn();
		
		byte[] resBody = res.getResponse().getContentAsByteArray();
		
		assertArrayEquals(urlListData1, resBody);
	}
	
	@Test
	public void testDownloadUrlList2() throws Exception {
		
		String url = UriComponentsBuilder.fromUriString(base + "/urlLists/dl")
				 						 .queryParam("fileName", urlListName3)
				 						 .build()
				 						 .toUriString();
		
		MvcResult res = mvc.perform(get(url))
				 		   .andExpect(status().isNotFound())
				 		   .andReturn();
		
		ApiError err = getApiError(res);
		
		assertEquals("NoSuchFileException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(urlListName3));
	}
	
	// DOC COLLECTIONS
	
	@Test
	public void testPostDocCollection1() throws Exception {
		
		String jsonString = getJson(newDocCollection);
		
		long countBefore = collectionRepo.count();
		assertFalse(collectionRepo.existsByName(newDocCollection.getName()));
		
		MvcResult res = mvc.perform(post(base + "/docCollections").contentType(json).content(jsonString))
				 		   .andExpect(status().isCreated())
				 		   .andReturn();
		
		DocCollection resBody = mapper.readValue(resString(res), DocCollection.class);
		assertEquals(newDocCollection.getName(), resBody.getName());
		
		assertEquals(countBefore +1, collectionRepo.count());
		assertTrue(collectionRepo.existsByName(newDocCollection.getName()));
	}
	
	@Test // unsupported language
	public void testPostDocCollection2() throws Exception {
		
		String badLanguage = "xy";
		newDocCollection.setLanguage(badLanguage);
		String jsonString = getJson(newDocCollection);
		
		long countBefore = collectionRepo.count();
		
		MvcResult res = mvc.perform(post(base + "/docCollections").contentType(json).content(jsonString))
				 		   .andExpect(status().isUnprocessableEntity())
				 		   .andReturn();

		ApiError err = getApiError(res);
		
		assertEquals("LanguageNotSupportedException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(badLanguage));
		assertEquals(countBefore, collectionRepo.count());
	}
	
	@Test // collection id already exists
	public void testPostDocCollection3() throws Exception {
		
		int existingId = savedDocCollections.get(0).getId();
		newDocCollection.setId(existingId);
		String jsonString = getJson(newDocCollection);
		
		long countBefore = collectionRepo.count();
		
		MvcResult res = mvc.perform(post(base + "/docCollections").contentType(json).content(jsonString))
				 		   .andExpect(status().isUnprocessableEntity())
				 		   .andReturn();
		
		ApiError err = getApiError(res);
		
		assertEquals("DocCollectionExistsException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(Integer.toString(existingId)));
		assertEquals(countBefore, collectionRepo.count());
	}
	
	@Test // collection name already exists
	public void testPostDocCollection4() throws Exception {
		
		String existingName = savedDocCollections.get(0).getName();
		newDocCollection.setName(existingName);
		String jsonString = getJson(newDocCollection);
		
		long countBefore = collectionRepo.count();
		
		MvcResult res = mvc.perform(post(base + "/docCollections").contentType(json).content(jsonString))
				 		   .andExpect(status().isUnprocessableEntity())
				 		   .andReturn();
		
		ApiError err = getApiError(res);
		
		assertEquals("DocCollectionExistsException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(existingName));
		assertEquals(countBefore, collectionRepo.count());
	}
	
	@Test // collection with non-existing url list
	public void testPostCollection5() throws Exception {
		
		newDocCollection.setUrlListName(urlListName3);
		String jsonString = getJson(newDocCollection);
		
		long countBefore = collectionRepo.count();
		
		MvcResult res = mvc.perform(post(base + "/docCollections").contentType(json).content(jsonString))
				 		   .andExpect(status().isNotFound())
				 		   .andReturn();
		
		ApiError err = getApiError(res);
		
		assertEquals("NoSuchFileException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(urlListName3));
		assertEquals(countBefore, collectionRepo.count());
	}
	
	@Test
	public void testUpdateDocCollection1() throws Exception {
		
		DocCollection existingCollection = savedDocCollections.get(1);
		
		int id = existingCollection.getId();
		String newName = "newName"; 
		String newLanguage = "EN";
		String newUrlList = urlListName1;
		String newIndexDir = "newIndexDir";
		existingCollection.setName(newName);
		existingCollection.setLanguage(newLanguage);
		existingCollection.setUrlListName(newUrlList);
		existingCollection.setIndexDir(newIndexDir);
		existingCollection.setIndexed(true);
		
		String jsonString = getJson(existingCollection);
		
		DocCollection retrievedBefore = collectionRepo.findById(id);
		assertNotEquals(newName, retrievedBefore.getName());
		assertNotEquals(newLanguage, retrievedBefore.getLanguage());
		assertNotEquals(newUrlList, retrievedBefore.getUrlListName());
		assertNotEquals(newIndexDir, retrievedBefore.getIndexDir());
		assertFalse(retrievedBefore.getIndexed());
		
		MvcResult res = mvc.perform(put(base + "/docCollections").contentType(json).content(jsonString))
				 		   .andExpect(status().isOk())
				 		   .andReturn();
		
		DocCollection resBody = mapper.readValue(resString(res), DocCollection.class);
		assertEquals(existingCollection, resBody);
		
		DocCollection retrievedAfter = collectionRepo.findById(id);
		assertEquals(newName, retrievedAfter.getName());
		assertEquals(newLanguage, retrievedAfter.getLanguage());
		assertEquals(newUrlList, retrievedAfter.getUrlListName());
		assertEquals(newIndexDir, retrievedAfter.getIndexDir());
		assertTrue(retrievedAfter.getIndexed());
	}
	
	///////////////////
	
	private String resString(MvcResult res) throws UnsupportedEncodingException {
	
		return res.getResponse().getContentAsString();
	}
	
	private ApiError getApiError(MvcResult res) throws Exception{
		
		return mapper.readValue(resString(res), ApiError.class);
	}
	
	private String getJson(Object o) throws JsonProcessingException {
		
		return writer.writeValueAsString(o);
	}
	
	private void clearTestFiles() throws IOException {
		
		if (Files.exists(urlListsPath)) {
			
			Files.list(urlListsPath).forEach(f -> {
				try {
					Files.deleteIfExists(f);
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
			});
		}
		
		if (Files.exists(indexDirPath)) {
		
			Files.list(indexDirPath).forEach(f -> {
				
				try {
					
					Files.list(f).forEach(x -> { try {
														Files.deleteIfExists(x);
											  		} 
											  		catch (IOException e) {
											  			e.printStackTrace();
											  		}
											}); 
					
					Files.deleteIfExists(f);
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
			});
		}
	}
}

 
 








