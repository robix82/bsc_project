package ch.usi.hse.retrieval;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import ch.usi.hse.db.entities.DocCollection;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *  Data transfer class containing the query string and the retrieved results
 * 
 * @author robert.jans@usi.ch
 *
 */
public class SearchResultList {

	private String queryString;
	private List<SearchResult> searchResults;
	
	// map collection id to number of results belonging to the give collection
	private Map<DocCollection, Integer>  collectionStats;
	
	/**
	 * Default constructor
	 */
	public SearchResultList() {
		
		queryString = "";
		searchResults = new ArrayList<>();
		collectionStats = new HashMap<>();
	}
	
	/**
	 * Builds a SearchResultList object with the given parameters
	 * 
	 * @param queryString
	 * @param searchResults
	 */
	public SearchResultList(String queryString, List<SearchResult> searchResults) {
		
		this.queryString = queryString;
		this.searchResults = searchResults;
		collectionStats = new HashMap<>();
		computeCollectionStats();
	}
	
	public SearchResultList(String queryString) {
		
		this.queryString = queryString;
		searchResults = new ArrayList<>();
		collectionStats = new HashMap<>();
	}
	
	public String getQueryString() {
		return queryString;
	}
	
	public List<SearchResult> getSearchResults() {
		return searchResults;
	}
	
	public Map<DocCollection, Integer> getCollectionStats() {
		return collectionStats;
	}
	
	public void setCollectionStats(Map<DocCollection, Integer> collectionStats) {
		this.collectionStats = collectionStats;
	}
	
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}
	
	public void setSearchResults(List<SearchResult> searchResults) {
		
		this.searchResults = searchResults;
		computeCollectionStats();
	}
	
	public void addSearchResult(SearchResult res) {
		
		searchResults.add(res);
		
		DocCollection collection = res.getDocCollection();
		
		if (collection != null) {
			
			if (! collectionStats.containsKey(collection)) {
				
				collectionStats.put(collection, 1);
			}
			else {
				
				int n = collectionStats.get(collection);
				collectionStats.put(collection, n+1);
			}
		}
	}
	
	@Override
	public boolean equals(Object o) {
		
		if (o == this) {
			return true;
		}
		
		if (! (o instanceof SearchResultList)) {
			
			return false;
		}
		 
		SearchResultList srl = (SearchResultList) o;
		
		if (! srl.queryString.equals(queryString)) {
			return false;
		}
		
		if (! (srl.searchResults.size() == searchResults.size())) {
			return false;
		}
		
		for (int i = 0; i < searchResults.size(); ++i) {
			
			if (! searchResults.get(i).equals(srl.searchResults.get(i))) {
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public int hashCode() {
		
		return Objects.hash(queryString, searchResults);
	}
	
	private void computeCollectionStats() {
		
		collectionStats.clear();
		
		for (SearchResult res : searchResults) {
			
			DocCollection collection = res.getDocCollection();
			
			if (! collectionStats.containsKey(collection)) {
				
				collectionStats.put(collection, 1);
			}
			else {
				
				int n = collectionStats.get(collection);
				collectionStats.put(collection, n+1);
			}
		}
	}
}










