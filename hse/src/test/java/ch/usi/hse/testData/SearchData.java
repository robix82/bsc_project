package ch.usi.hse.testData;

import java.util.ArrayList;
import java.util.List;

import ch.usi.hse.db.entities.DocCollection;
import ch.usi.hse.retrieval.SearchResult;
import ch.usi.hse.retrieval.SearchResultList;

public class SearchData {
	
	public static SearchResultList dummieSearchResultList(int n) {
		
		return new SearchResultList("Test Query", dummieSearchResults(n));
	}

	public static List<SearchResult> dummieSearchResults(int n) {
		
		List<SearchResult> res = new ArrayList<>();
		DocCollection collection = new DocCollection("example_collection", "some list");
		
		for (int i = 0; i < n; ++i) {
			
			SearchResult r = new SearchResult(n, "https://www.usi.ch", "Some summary");
			r.setDocCollection(collection);
			
			res.add(r);	
		}
		
		return res;
	}
}
 