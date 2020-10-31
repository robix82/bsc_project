package ch.usi.hse.endpoints;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
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

import ch.usi.hse.exceptions.ApiError;

@SpringBootTest
@AutoConfigureMockMvc
public class IndexingControllerIntegrationTest {

	@Value("${dir.urlLists}")
	private String urlListsDir;
	
	private String base = "/indexing";
	
	@Autowired
	private MockMvc mvc;
	
	private Path urlListsPath;
	
	private List<String> urlListNames, urls1, urls2;
	private String urlListName1, urlListName2, newUrlListName;
	private MockMultipartFile newUrlListFile;
	
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
		
		Files.list(urlListsPath).forEach(f -> {
			try {
				Files.deleteIfExists(f);
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		});
		
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
	
	@Test
	public void testGetIndexingUi() throws Exception {
		
		mvc.perform(get(base + "/ui"))
		   .andExpect(status().isOk())
		   .andExpect(model().attribute("urlLists", Matchers.iterableWithSize(urlListNames.size())))
		   .andExpect(view().name("indexing"));
	}
	
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
	
	
		///////////////////
	
	private String resString(MvcResult res) throws UnsupportedEncodingException {
	
		return res.getResponse().getContentAsString();
	}
}











