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
import java.util.List;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import ch.usi.hse.config.Language;
import ch.usi.hse.db.entities.DocCollection;
import ch.usi.hse.db.repositories.DocCollectionRepository;
import ch.usi.hse.exceptions.ApiError;
import ch.usi.hse.indexing.IndexingResult;

@SpringBootTest
@AutoConfigureMockMvc
public class IndexingControllerIntegrationTest {

	@Value("${dir.urlLists}")
	private String urlListsDir;
	
	@Value("${dir.indices}")
	private String indexDir;
	
	@Autowired
	private DocCollectionRepository collectionRepo;
	
	private String base = "/indexing";
	
	@Autowired
	private MockMvc mvc;
	
	private Path urlListsPath, indexDirPath;
	
	private List<String> urlListNames, urls1, urls2;
	private String urlListName1, urlListName2, newUrlListName;
	private MockMultipartFile newUrlListFile;
	
	private List<DocCollection> docCollections;
	private DocCollection newDocCollection;
	
	private ObjectMapper mapper;
	private ObjectWriter writer;
	private MediaType json; 
	
	 
	@BeforeEach
	public void setUp() throws IOException {
		
		mapper = new ObjectMapper();
		mapper.findAndRegisterModules();
		writer = mapper.writer().withDefaultPrettyPrinter();
		json = new MediaType(MediaType.APPLICATION_JSON.getType(), 
			       MediaType.APPLICATION_JSON.getSubtype(), 
			       Charset.forName("utf8"));
		
		urlListsPath = Paths.get(urlListsDir);
		indexDirPath = Paths.get(indexDir);
		
		urlListName1 = "urls1.txt";
		urlListName2 = "urls2.txt";
		newUrlListName = "newUrls.txt";
		urlListNames = Arrays.asList(urlListName1, urlListName2);
		
		urls1 = Arrays.asList("url1", "url2", "url3");
		urls2 = Arrays.asList("url4", "url5", "url6");
		
		newUrlListFile = new MockMultipartFile("file",
											   newUrlListName,
											   MediaType.TEXT_PLAIN_VALUE,
											   "content".getBytes());
		
		DocCollection c1 = new DocCollection("c1", urlListName1);
		DocCollection c2 = new DocCollection("c2", urlListName2);
		c1.setId(1);
		c2.setId(2);
		c1.setIndexDir(indexDir + "c1");
		c2.setIndexDir(indexDir + "c2");
		
		docCollections = List.of(collectionRepo.save(c1),
								 collectionRepo.save(c2));
		
		newDocCollection = new DocCollection("c3", urlListName2);
		newDocCollection.setIndexDir(indexDir + "c3");
		
		clearTestFiles();
		
		PrintStream ps1 = new PrintStream(new FileOutputStream(urlListsDir + urlListName1));
		
		for (String s : urls1) {
			ps1.println(s);
		}
		 
		ps1.close();
		
		PrintStream ps2 = new PrintStream(new FileOutputStream(urlListsDir + urlListName2));
		
		for (String s : urls2) {
			ps2.println(s);
		}
		
		ps2.close();
	}
	
	@AfterEach
	public void cleanup() throws IOException {
		
		clearTestFiles();
	}
	 
	@Test
	public void testGetIndexingUi() throws Exception {
		
		mvc.perform(get(base + "/ui"))
		   .andExpect(status().isOk())
		   .andExpect(model().attribute("urlLists", Matchers.iterableWithSize(urlListNames.size())))
		   .andExpect(model().attribute("docCollections", Matchers.iterableWithSize(docCollections.size())))
		   .andExpect(model().attribute("languages", is(Language.languages)))
		   .andExpect(view().name("indexing"));
	}
	
	
	// URL LISTS
	
	@Test
	public void testPostUrlList() throws Exception {
		
		String msg = "file " + newUrlListName + " uploaded";

		long countBefore = Files.list(urlListsPath).count();
		
		MvcResult res = mvc.perform(multipart(base + "/urlLists").file(newUrlListFile))
						   .andExpect(status().isCreated())
						   .andReturn();
		
		String resBody = resString(res);
		
		assertEquals(msg, resBody);
		
		long countAfter = Files.list(urlListsPath).count();
		
		assertEquals(countBefore +1, countAfter);
	}
	
	@Test
	public void testDeleteUrlList1() throws Exception {
		
		String msg = "file " + urlListName1 + " removed";
		String url = UriComponentsBuilder.fromUriString(base + "/urlLists")
				 						 .queryParam("fileName", urlListName1)
				 						 .build()
				 						 .toUriString();
		
		MvcResult res = mvc.perform(delete(url))
				 		   .andExpect(status().isOk())
				 		   .andReturn();
		
		String resBody = resString(res);
		
		assertEquals(msg, resBody);
	}
	
	@Test 
	public void testDeleteUrlList2() throws Exception {
		
		String url = UriComponentsBuilder.fromUriString(base + "/urlLists")
										 .queryParam("fileName", newUrlListName)
										 .build()
										 .toUriString();
		
		MvcResult res = mvc.perform(delete(url))
				 		   .andExpect(status().isNotFound())
				 		   .andReturn();
		
		ApiError err = mapper.readValue(resString(res), ApiError.class);
		
		assertEquals("NoSuchFileException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(newUrlListName));
	}
	
	@Test
	public void testDownloadUrlList1() throws Exception {
		
		File f = new File(urlListsDir + urlListName1);
		byte[] expectedBytes = FileUtils.readFileToByteArray(f);
		
		String url = UriComponentsBuilder.fromUriString(base + "/urlLists/dl")
				 						 .queryParam("fileName", urlListName1)
				 						 .build()
				 						 .toUriString();
		
		MvcResult res = mvc.perform(get(url))
				 		   .andExpect(status().isOk())
				 		   .andReturn();
		
		byte[] resBody = res.getResponse().getContentAsByteArray();
		
		assertArrayEquals(expectedBytes, resBody);
	}
	
	@Test
	public void testDownloadUrlList2() throws Exception {
		
		String url = UriComponentsBuilder.fromUriString(base + "/urlLists/dl")
										 .queryParam("fileName", newUrlListName)
										 .build()
										 .toUriString();
		
		MvcResult res = mvc.perform(get(url))
				 		   .andExpect(status().isNotFound())
				 		   .andReturn();
		
		ApiError err = mapper.readValue(resString(res), ApiError.class);
		
		assertEquals("NoSuchFileException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(newUrlListName));
	}
	
	// DOC COLLECTIONS
	
	@Test
	public void testAddDocCollection1() throws Exception {
		
		String jsonString = writer.writeValueAsString(newDocCollection);
		String dirName = indexDir + newDocCollection.getName();
		
		long countBefore = collectionRepo.count();
		assertFalse(Files.exists(Paths.get(dirName)));
		
		MvcResult res = mvc.perform(post(base + "/docCollections").contentType(json).content(jsonString))
				 		   .andExpect(status().isCreated())
				 		   .andReturn();
		
		DocCollection resBody = mapper.readValue(resString(res), DocCollection.class);	
		assertEquals(newDocCollection.getName(), resBody.getName());
		assertEquals(dirName, resBody.getIndexDir());
		
		long countAfter = collectionRepo.count();
		assertEquals(countBefore +1, countAfter);
		assertTrue(Files.exists(Paths.get(dirName)));
	}
	
	@Test // collection with existing id
	public void testAddDocCollection2() throws Exception {
		
		int existingId = docCollections.get(0).getId();
		newDocCollection.setId(existingId);
		String jsonString = writer.writeValueAsString(newDocCollection);
		
		MvcResult res = mvc.perform(post(base + "/docCollections").contentType(json).content(jsonString))
				 		   .andExpect(status().isUnprocessableEntity())
				 		   .andReturn();
		
		ApiError err = mapper.readValue(resString(res), ApiError.class);
		
		assertEquals("DocCollectionExistsException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(Integer.toString(existingId)));
	}
	
	@Test // collection with existing name
	public void testAddDocCollection3() throws Exception {
		
		String existingName = docCollections.get(0).getName();
		newDocCollection.setName(existingName);
		String jsonString = writer.writeValueAsString(newDocCollection);
		
		MvcResult res = mvc.perform(post(base + "/docCollections").contentType(json).content(jsonString))
				 		   .andExpect(status().isUnprocessableEntity())
				 		   .andReturn();
		
		ApiError err = mapper.readValue(resString(res), ApiError.class);
		
		assertEquals("DocCollectionExistsException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(existingName));
	}
	
	@Test // collection with non-existing urlListName
	public void testAddDocCollection4() throws Exception {
		
		newDocCollection.setUrlListName(newUrlListName);
		String jsonString = writer.writeValueAsString(newDocCollection);
		
		MvcResult res = mvc.perform(post(base + "/docCollections").contentType(json).content(jsonString))
				 		   .andExpect(status().isNotFound())
				 		   .andReturn();
		
		ApiError err = mapper.readValue(resString(res), ApiError.class);
		
		assertEquals("NoSuchFileException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(newUrlListName));
	}
	
	@Test // collection with non-supported language
	public void testAddDocCollection5() throws Exception {
		
		String badLanguage = "xy";
		newDocCollection.setLanguage(badLanguage);
		String jsonString = writer.writeValueAsString(newDocCollection);
		
		MvcResult res = mvc.perform(post(base + "/docCollections").contentType(json).content(jsonString))
				 		   .andExpect(status().isUnprocessableEntity())
				 		   .andReturn();
		
		ApiError err = mapper.readValue(resString(res), ApiError.class);
		
		assertEquals("LanguageNotSupportedException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(badLanguage));
	}
	
	@Test
	public void testUpdateDocCollection1() throws Exception {
		
		String newName = "newName";
		DocCollection existingCollection = docCollections.get(0);
		String oldDir = existingCollection.getIndexDir();
		Files.createDirectory(Paths.get(oldDir));
		String newDir = indexDir + newName;
		
		existingCollection.setName(newName);
		existingCollection.setLanguage("EN");
		existingCollection.setUrlListName(urlListName2);
		existingCollection.setIndexed(true); 
		String jsonString = writer.writeValueAsString(existingCollection);
		
		assertTrue(Files.exists(Paths.get(oldDir)));
		assertFalse(Files.exists(Paths.get(newDir)));
		
		MvcResult res = mvc.perform(put(base + "/docCollections").contentType(json).content(jsonString))
				 		   .andExpect(status().isOk())
				 		   .andReturn();
		
		DocCollection resBody = mapper.readValue(resString(res), DocCollection.class);
		assertEquals(existingCollection, resBody);

		DocCollection foundAfter = collectionRepo.findById(existingCollection.getId());
		assertEquals(newName, foundAfter.getName());
		assertEquals(existingCollection.getLanguage(), foundAfter.getLanguage());
		assertEquals(existingCollection.getUrlListName(), foundAfter.getUrlListName());
		assertEquals(newDir, foundAfter.getIndexDir());
		assertEquals(existingCollection.getIndexed(), foundAfter.getIndexed());
		
		assertFalse(Files.exists(Paths.get(oldDir)));
		assertTrue(Files.exists(Paths.get(newDir)));
	}
	
	@Test // update on non-existing id
	public void testUpdateDocCollection2() throws Exception {
		
		int badId = 724321;
		DocCollection existingCollection = docCollections.get(0);
		existingCollection.setId(badId);
		String jsonString = writer.writeValueAsString(existingCollection);
		
		MvcResult res = mvc.perform(put(base + "/docCollections").contentType(json).content(jsonString))
				 		   .andExpect(status().isNotFound())
				 		   .andReturn();
		
		ApiError err = mapper.readValue(resString(res), ApiError.class);
		
		assertEquals("NoSuchDocCollectionException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(Integer.toString(badId)));
	}
	
	@Test // update on non-existing url list
	public void testUpdate3() throws Exception {
		
		DocCollection existingCollection = docCollections.get(0);
		existingCollection.setUrlListName(newUrlListName);
		String jsonString = writer.writeValueAsString(existingCollection);
		
		MvcResult res = mvc.perform(put(base + "/docCollections").contentType(json).content(jsonString))
				  		   .andExpect(status().isNotFound())
				  		   .andReturn();
		
		ApiError err = mapper.readValue(resString(res), ApiError.class);
		
		assertEquals("NoSuchFileException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(newUrlListName));
	}
	
	@Test // update on existing name
	public void testUpdateDocCollection4() throws Exception {
		
		String existingName = docCollections.get(1).getName();
		DocCollection existingCollection = docCollections.get(0);
		existingCollection.setName(existingName);
		String jsonString = writer.writeValueAsString(existingCollection);
		
		MvcResult res = mvc.perform(put(base + "/docCollections").contentType(json).content(jsonString))
				 		   .andExpect(status().isUnprocessableEntity())
				 		   .andReturn();
		
		ApiError err = mapper.readValue(resString(res), ApiError.class);
		
		assertEquals("DocCollectionExistsException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(existingName));
	}
	
	@Test // update on non-supported language
	public void testUpdateDocCollection5() throws Exception {
		
		String badLanguage = "xy";
		DocCollection existingCollection = docCollections.get(0);
		existingCollection.setLanguage(badLanguage);
		String jsonString = writer.writeValueAsString(existingCollection);
		
		MvcResult res = mvc.perform(put(base + "/docCollections").contentType(json).content(jsonString))
				 		   .andExpect(status().isUnprocessableEntity())
				 		   .andReturn();
		
		ApiError err = mapper.readValue(resString(res), ApiError.class);
		
		assertEquals("LanguageNotSupportedException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(badLanguage));
	}
	
	@Test
	public void testDeleteDocCollection1() throws Exception {
		
		DocCollection existingCollection = docCollections.get(0);
		String dir = existingCollection.getIndexDir();
		Files.createDirectory(Paths.get(dir));
		String jsonString = writer.writeValueAsString(existingCollection);
		
		long countBefore = collectionRepo.count();
		assertTrue(Files.exists(Paths.get(dir)));
		
		assertTrue(collectionRepo.existsById(existingCollection.getId()));
		
		MvcResult res = mvc.perform(delete(base + "/docCollections").contentType(json).content(jsonString))
				 		   .andExpect(status().isOk())
				 		   .andReturn();
		
		DocCollection resBody = mapper.readValue(resString(res), DocCollection.class);
		
		assertEquals(existingCollection, resBody);
		
		assertEquals(countBefore -1, collectionRepo.count());
		assertFalse(collectionRepo.existsById(existingCollection.getId()));
		assertFalse(Files.exists(Paths.get(dir)));
	}
	
	@Test
	public void testDeleteDocCollection2() throws Exception {
		
		int badId = 724321;
		newDocCollection.setId(badId);
		String jsonString = writer.writeValueAsString(newDocCollection);
		
		MvcResult res = mvc.perform(delete(base + "/docCollections").contentType(json).content(jsonString))
				 		   .andExpect(status().isNotFound())
				 		   .andReturn();
		
		ApiError err = mapper.readValue(resString(res), ApiError.class);
		
		assertEquals("NoSuchDocCollectionException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(Integer.toString(badId)));
	}
	
	@Test
	public void testBuildIndex1() throws Exception {
		
		DocCollection existingCollection = docCollections.get(0);
		String jsonString = writer.writeValueAsString(existingCollection);
		
		MvcResult res = mvc.perform(post(base + "/buildIndex").contentType(json).content(jsonString))
				 		   .andExpect(status().isOk())
				 		   .andReturn();
		
		IndexingResult resBody = mapper.readValue(resString(res), IndexingResult.class);
		
		assertNotNull(resBody);
	}
	
	@Test
	public void testBuildIndex2() throws Exception {
		
		int badId = 87452189;
		DocCollection existingCollection = docCollections.get(0);
		existingCollection.setId(badId);
		String jsonString = writer.writeValueAsString(existingCollection);
		
		MvcResult res = mvc.perform(post(base + "/buildIndex").contentType(json).content(jsonString))
				 		   .andExpect(status().isNotFound())
				 		   .andReturn();
		
		ApiError err = mapper.readValue(resString(res), ApiError.class);
		
		assertEquals("NoSuchDocCollectionException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(Integer.toString(badId)));
	}
	
	@Test
	public void testBuildIndex3() throws Exception {
		
		DocCollection existingCollection = docCollections.get(0);
		existingCollection.setUrlListName(newUrlListName);
		String jsonString = writer.writeValueAsString(existingCollection);
		
		MvcResult res = mvc.perform(post(base + "/buildIndex").contentType(json).content(jsonString))
				 		   .andExpect(status().isNotFound())
				 		   .andReturn();
		
		ApiError err = mapper.readValue(resString(res), ApiError.class);
		
		assertEquals("NoSuchFileException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(newUrlListName));
	}
	
	@Test 
	public void testBuildIndex4() throws Exception {
		
		String badLanguage = "xy";
		DocCollection existingCollection = docCollections.get(0);
		existingCollection.setLanguage(badLanguage);
		String jsonString = writer.writeValueAsString(existingCollection);
		
		MvcResult res = mvc.perform(post(base + "/buildIndex").contentType(json).content(jsonString))
				 		   .andExpect(status().isUnprocessableEntity())
				 		   .andReturn();
		
		ApiError err = mapper.readValue(resString(res), ApiError.class);
		
		assertEquals("LanguageNotSupportedException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(badLanguage));
	}
	
	
	
		///////////////////
	
	private String resString(MvcResult res) throws UnsupportedEncodingException {
	
		return res.getResponse().getContentAsString();
	}
	
	private void clearTestFiles() throws IOException {
		
		Files.list(urlListsPath).forEach(f -> {
			try {
				Files.deleteIfExists(f);
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		});
		
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

 
 








