package ch.usi.hse.retrieval;

import java.util.ArrayList;
import java.util.List;

public class SearchData {
	
	public static SearchResultList dummieSearchResultList(int n) {
		
		return new SearchResultList("Test Query", dummieSearchResults(n));
	}

	public static List<SearchResult> dummieSearchResults(int n) {
		
		List<SearchResult> res = new ArrayList<>();
		
		for (int i = 0; i < n; ++i) {
			
			res.add(new SearchResult(n, "https://www.usi.ch", "Some summary"));
		}
		
		return res;
	}
}
 