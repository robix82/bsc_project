package usi.ch.hse.dummie_data;

import java.util.ArrayList;
import java.util.List;

import usi.ch.hse.dto.SearchResult;
import usi.ch.hse.dto.SearchResultList;

public class SearchData {
	
	public static SearchResultList dummieSearchResultList(int n) {
		
		return new SearchResultList("Test Query", dummieSearchResults(n));
	}

	public static List<SearchResult> dummieSearchResults(int n) {
		
		List<SearchResult> res = new ArrayList<>();
		
		for (int i = 0; i < n; ++i) {
			
			res.add(new SearchResult(n, "www.test.com", "Some summary"));
		}
		
		return res;
	}
}
