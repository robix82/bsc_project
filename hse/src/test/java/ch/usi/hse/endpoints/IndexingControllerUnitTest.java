package ch.usi.hse.endpoints;


import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
 
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import ch.usi.hse.config.Language;
import ch.usi.hse.db.entities.DocCollection;
import ch.usi.hse.exceptions.ApiError;
import ch.usi.hse.exceptions.FileDeleteException;
import ch.usi.hse.exceptions.FileReadException;
import ch.usi.hse.exceptions.FileWriteException;
import ch.usi.hse.exceptions.LanguageNotSupportedException;
import ch.usi.hse.exceptions.NoSuchFileException;
import ch.usi.hse.indexing.IndexingResult;
import ch.usi.hse.exceptions.DocCollectionExistsException;
import ch.usi.hse.exceptions.NoSuchDocCollectionException;
import ch.usi.hse.services.IndexingService;

@SpringBootTest
@AutoConfigureMockMvc
public class IndexingControllerUnitTest {

	@Autowired 
	private MockMvc mvc;
	
	@MockBean
	private IndexingService indexingService;
	
	private String base = "/indexing";
	
	private List<String> urlListNames;
	
	private String urlListName1, urlListName2, 
				   newUrlListName, badUrlListName;
	
	private MockMultipartFile newUrlListFile, badUrlListFile;
	private byte[] savedBytes;
	
	private List<DocCollection> docCollections;
	private DocCollection existingDocCollection, newDocCollection;
	
	private ObjectMapper mapper;
	private ObjectWriter writer;
	private MediaType json;
	 
	@BeforeEach
	public void setUp() 
			throws FileReadException, 
				   FileWriteException, 
				   NoSuchFileException, 
				   FileDeleteException, 
				   LanguageNotSupportedException, 
				   DocCollectionExistsException, 
				   NoSuchDocCollectionException {
		
		mapper = new ObjectMapper();
		mapper.findAndRegisterModules();
		writer = mapper.writer().withDefaultPrettyPrinter();
		json = new MediaType(MediaType.APPLICATION_JSON.getType(), 
			       MediaType.APPLICATION_JSON.getSubtype(), 
			       Charset.forName("utf8"));
		
		// URL LISTS
		
		urlListName1 = "urls1.txt";
		urlListName2 = "urls2.txt";
		newUrlListName = "newUrls.txt";
		badUrlListName = "badUrls.txt";
		urlListNames = Arrays.asList(urlListName1, urlListName2);
		
		newUrlListFile = new MockMultipartFile("file",
										       newUrlListName, 
										       MediaType.TEXT_PLAIN_VALUE, 
										       "content".getBytes());
		
		badUrlListFile = new MockMultipartFile("file",
										       newUrlListName, 
										       MediaType.TEXT_PLAIN_VALUE, 
										       "content".getBytes()); 
		
		savedBytes = "some content".getBytes();
		
		// DOC COLLECTIONS 
		
		DocCollection c1 = new DocCollection("c1", urlListName1);
		DocCollection c2 = new DocCollection("c2", urlListName2);
		DocCollection c3 = new DocCollection("c3", urlListName2);
		c1.setId(1);
		c2.setId(2);
		
		docCollections = List.of(c1, c2);
		existingDocCollection = docCollections.get(0);
		newDocCollection = c3;
		
		// MOCK RESPONSES
		 
		when(indexingService.savedUrlLists()).thenReturn(urlListNames);
		doNothing().when(indexingService).addUrlList(newUrlListFile);
		doThrow(new FileWriteException(badUrlListName)).when(indexingService).addUrlList(badUrlListFile);
		
		doNothing().when(indexingService).removeUrlList(urlListName1);
		doNothing().when(indexingService).removeUrlList(urlListName2);
		doThrow(new NoSuchFileException(newUrlListName)).when(indexingService).removeUrlList(newUrlListName);
		doThrow(new FileDeleteException(badUrlListName)).when(indexingService).removeUrlList(badUrlListName);
		
		when(indexingService.getUrlListFile(urlListName1)).thenReturn(new ByteArrayInputStream(savedBytes));
		when(indexingService.getUrlListFile(urlListName2)).thenReturn(new ByteArrayInputStream(savedBytes));
		doThrow(new NoSuchFileException(newUrlListName)).when(indexingService).getUrlListFile(newUrlListName);
		doThrow(new FileReadException(badUrlListName)).when(indexingService).getUrlListFile(badUrlListName);
		
		when(indexingService.docCollections()).thenReturn(docCollections);
		when(indexingService.addDocCollection(newDocCollection)).thenReturn(newDocCollection);
		when(indexingService.addDocCollection(existingDocCollection)).thenThrow(DocCollectionExistsException.class);
		when(indexingService.updateDocCollection(existingDocCollection)).thenReturn(existingDocCollection);
		when(indexingService.updateDocCollection(newDocCollection)).thenThrow(NoSuchDocCollectionException.class);
		doNothing().when(indexingService).removeDocCollection(existingDocCollection);
		doThrow(NoSuchDocCollectionException.class).when(indexingService).removeDocCollection(newDocCollection);
		when(indexingService.buildIndex(existingDocCollection, false, false)).thenReturn(new IndexingResult());
		when(indexingService.buildIndex(newDocCollection, false, false)).thenThrow(NoSuchDocCollectionException.class);
	}
	  
	@Test
	public void testGetIndexingUi() throws Exception {
		
		mvc.perform(get(base + "/ui"))
		   .andExpect(status().isOk())
		   .andExpect(model().attribute("urlLists", is(urlListNames)))
		   .andExpect(model().attribute("docCollections", is(docCollections)))
		   .andExpect(model().attribute("languages", is(Language.languages)))
		   .andExpect(view().name("indexing"));
	}
	
	// URL LISTS
	
	@Test
	public void testPostUrlList1() throws Exception {
		
		String msg = "file " + newUrlListName + " uploaded";

		MvcResult res = mvc.perform(multipart(base + "/urlLists").file(newUrlListFile))
						   .andExpect(status().isCreated())
						   .andReturn();
		
		String resBody = resString(res);
		
		assertEquals(msg, resBody);
	}
	
	@Test
	public void testPostUrlList2() throws Exception {

		MvcResult res = mvc.perform(multipart(base + "/urlLists").file(badUrlListFile))
						   .andExpect(status().isInternalServerError())
						   .andReturn();
		
		ApiError err = mapper.readValue(resString(res), ApiError.class);
		
		assertEquals("FileWriteException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(badUrlListName));
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
	public void testDeleteUrlList3() throws Exception {
		
		String url = UriComponentsBuilder.fromUriString(base + "/urlLists")
										 .queryParam("fileName", badUrlListName)
										 .build()
										 .toUriString();
		
		MvcResult res = mvc.perform(delete(url))
				 		   .andExpect(status().isInternalServerError())
				 		   .andReturn();
		
		ApiError err = mapper.readValue(resString(res), ApiError.class);
		
		assertEquals("FileDeleteException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(badUrlListName));
	}
	
	@Test
	public void testDownloadUrlList1() throws Exception {
		
		String url = UriComponentsBuilder.fromUriString(base + "/urlLists/dl")
				 						 .queryParam("fileName", urlListName1)
				 						 .build().toUriString();
		
		MvcResult res = mvc.perform(get(url))
				 		   .andExpect(status().isOk())
				 		   .andReturn();
		
		byte[] resBody = res.getResponse().getContentAsByteArray();
		
		assertArrayEquals(savedBytes, resBody);
	}
	
	@Test
	public void testDownloadUrlList2() throws Exception {
		
		String url = UriComponentsBuilder.fromUriString(base + "/urlLists/dl")
				 						 .queryParam("fileName", newUrlListName)
				 						 .build().toUriString();
		
		MvcResult res = mvc.perform(get(url))
				 		   .andExpect(status().isNotFound())
				 		   .andReturn();
		
		ApiError err = mapper.readValue(resString(res), ApiError.class);
		
		assertEquals("NoSuchFileException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(newUrlListName));
	}
	
	@Test
	public void testDownloadUrlList3() throws Exception {
		
		String url = UriComponentsBuilder.fromUriString(base + "/urlLists/dl")
				 						 .queryParam("fileName", badUrlListName)
				 						 .build().toUriString();
		
		MvcResult res = mvc.perform(get(url))
				 		   .andExpect(status().isInternalServerError())
				 		   .andReturn();
		
		ApiError err = mapper.readValue(resString(res), ApiError.class);
		
		assertEquals("FileReadException", err.getErrorType());
		assertTrue(err.getErrorMessage().contains(badUrlListName));
	}
	
	// DOC COLLECTIONS
	
	@Test
	public void testAddDocCollection1() throws Exception {
		
		String jsonString = writer.writeValueAsString(newDocCollection);
		
		MvcResult res = mvc.perform(post(base + "/docCollections").contentType(json).content(jsonString))
				 		   .andExpect(status().isCreated())
				 		   .andReturn();
		
		DocCollection resBody = mapper.readValue(resString(res), DocCollection.class);
		
		assertEquals(newDocCollection, resBody);
	}
	
	@Test
	public void testAddDocCollection2() throws Exception {
		
		String jsonString = writer.writeValueAsString(existingDocCollection);
		
		MvcResult res = mvc.perform(post(base + "/docCollections").contentType(json).content(jsonString))
				 		   .andExpect(status().isUnprocessableEntity())
				 		   .andReturn();
		
		ApiError err = mapper.readValue(resString(res), ApiError.class);
				 		
		assertEquals("DocCollectionExistsException", err.getErrorType());
	}
	
	@Test
	public void testUpdateDocCollection1() throws Exception {
		
		String jsonString = writer.writeValueAsString(existingDocCollection);
		
		MvcResult res = mvc.perform(put(base + "/docCollections").contentType(json).content(jsonString))
				 		   .andExpect(status().isOk())
				 		   .andReturn();
		
		DocCollection resBody = mapper.readValue(resString(res), DocCollection.class);
		
		assertEquals(existingDocCollection, resBody);
	}
	
	@Test
	public void testUpdateDocCollection2() throws Exception {
		
		String jsonString = writer.writeValueAsString(newDocCollection);
		
		MvcResult res = mvc.perform(put(base + "/docCollections").contentType(json).content(jsonString))
				 		   .andExpect(status().isNotFound())
				 		   .andReturn();
		
		ApiError err = mapper.readValue(resString(res), ApiError.class);
		
		assertEquals("NoSuchDocCollectionException", err.getErrorType());
	}
	
	@Test
	public void testDeleteDocCollection1() throws Exception {
		
		String jsonString = writer.writeValueAsString(existingDocCollection);
		
		MvcResult res = mvc.perform(delete(base + "/docCollections").contentType(json).content(jsonString))
				 		   .andExpect(status().isOk())
				 		   .andReturn();
		
		DocCollection resBody = mapper.readValue(resString(res), DocCollection.class);
		
		assertEquals(existingDocCollection, resBody);
	}
	
	@Test
	public void testDeleteDocCollection2() throws Exception {
		
		String jsonString = writer.writeValueAsString(newDocCollection);
		
		MvcResult res = mvc.perform(delete(base + "/docCollections").contentType(json).content(jsonString))
				 		   .andExpect(status().isNotFound())
				 		   .andReturn();
		
		ApiError err = mapper.readValue(resString(res), ApiError.class);
		
		assertEquals("NoSuchDocCollectionException", err.getErrorType());
	}
	/*
	@Test
	public void testBuildIndex1() throws Exception {
		
		String jsonString = writer.writeValueAsString(existingDocCollection);
		
		MvcResult res = mvc.perform(post(base + "/buildIndex").contentType(json).content(jsonString))
				 		   .andExpect(status().isOk())
				 		   .andReturn();
		
		IndexingResult resBody = mapper.readValue(resString(res), IndexingResult.class);
		
		assertNotNull(resBody);
	}
	
	@Test
	public void testBuildIndex2() throws Exception {
		
		String jsonString = writer.writeValueAsString(newDocCollection);
		
		MvcResult res = mvc.perform(post(base + "/buildIndex").contentType(json).content(jsonString))
				 		   .andExpect(status().isNotFound())
				 		   .andReturn();
		
		ApiError err = mapper.readValue(resString(res), ApiError.class);
		
		assertEquals("NoSuchDocCollectionException", err.getErrorType());
	}
	*/
	
	///////////////////
	
	private String resString(MvcResult res) throws UnsupportedEncodingException {
		
		return res.getResponse().getContentAsString();
	}
}







 