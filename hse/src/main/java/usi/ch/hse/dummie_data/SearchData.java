package usi.ch.hse.dummie_data;

import java.util.ArrayList;
import java.util.List;

import usi.ch.hse.dto.SearchResult;

public class SearchData {

	public static List<SearchResult> searchResults(int n) {
		
		List<SearchResult> res = new ArrayList<>();
		
		for (int i = 0; i < n; ++i) {
			
			res.add(new SearchResult(n, "www.test.com", "Some summary"));
		}
		
		return res;
	}
}
