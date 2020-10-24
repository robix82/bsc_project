package ch.usi.hse.endpoints;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.UriComponentsBuilder;

import static ch.usi.hse.dummie_data.SearchData.dummieSearchResultList;
import ch.usi.hse.dto.SearchResultList;
import ch.usi.hse.services.SearchService;

@SpringBootTest
@AutoConfigureMockMvc
public class SearchControllerUnitTest {

	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private SearchService searchService;
	
	private SearchResultList testSrl = dummieSearchResultList(10);
	
	@BeforeEach
	public void setUp() {
		
		when(searchService.search(anyString())).thenReturn(testSrl);
	}
	
	
	
	@Test
	public void testGetSearchUi() throws Exception {
		
		mvc.perform(get("/")).andExpect(status().isOk())
						   .andExpect(view().name("search"));
	}
	
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
}







