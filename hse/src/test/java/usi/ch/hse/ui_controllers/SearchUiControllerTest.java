package usi.ch.hse.ui_controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.UriComponentsBuilder;

import usi.ch.hse.dto.SearchResultList;
import static usi.ch.hse.dummie_data.SearchData.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SearchUiControllerTest {

	@Autowired
	private MockMvc mvc;
	
	@Test
	public void testGetSearchUi() throws Exception {
		
		mvc.perform(get("/")).andExpect(status().isOk())
						   .andExpect(view().name("search"));
	}
	
	@Test
	public void testPostQuery() throws Exception {
		
		SearchResultList expectedSrl = dummieSearchResultList(10);
		
		String url = UriComponentsBuilder.fromUriString("/")
								       .queryParam("queryString", "test query")
								       .build()
								       .toUriString();
		
		mvc.perform(post(url)).andExpect(status().isOk())
						     .andExpect(view().name("search"));
	}
}







