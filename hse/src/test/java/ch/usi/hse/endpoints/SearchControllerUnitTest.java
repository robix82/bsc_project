package ch.usi.hse.endpoints;

import static ch.usi.hse.testData.SearchData.dummieSearchResultList;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import ch.usi.hse.db.entities.Participant;
import ch.usi.hse.exceptions.FileReadException;
import ch.usi.hse.retrieval.SearchResultList;
import ch.usi.hse.services.SearchService;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(authorities={"PARTICIPANT"})
public class SearchControllerUnitTest {

	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private SearchService searchService;
	
	private SearchResultList testSrl = dummieSearchResultList(10);
	
	@BeforeEach
	public void setUp() throws ParseException, FileReadException, InvalidTokenOffsetsException {
		
		when(searchService.search(anyString(), any(Participant.class))).thenReturn(testSrl);
	}
	
	
	
	@Test
	public void testGetSearchUi() throws Exception {
		
		mvc.perform(get("/")).andExpect(status().isOk())
						   .andExpect(view().name("search"));
	}
	/*
	@Test
	public void testPostQuery() throws Exception {
		
		
		
		String url = UriComponentsBuilder.fromUriString("/")
								       .queryParam("queryString", "test query")
								       .build()
								       .toUriString();
		
		mvc.perform(post(url)).andExpect(status().isOk())
						     .andExpect(view().name("search"))
						     .andExpect(model().attribute("searchResultList", testSrl));
	}
	*/
}







