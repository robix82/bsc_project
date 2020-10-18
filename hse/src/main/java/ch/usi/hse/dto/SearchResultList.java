package ch.usi.hse.dto;

import java.util.List;
import java.util.Objects;

import org.apache.lucene.search.TopDocs;

import java.util.ArrayList;

/**
 *  Data transfer class containing the query string and the retrieved results
 * 
 * @author robert.jans@usi.ch
 *
 */
public class SearchResultList {

	private String queryString;
	private List<SearchResult> searchResults;
	
	/**
	 * Default constructor
	 */
	public SearchResultList() {
		
		queryString = "";
		searchResults = new ArrayList<>();
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
	}
	
	/**
	 * Builds a SearchResult object with the given the query string and a Lucene TopDocs instance
	 * 
	 * @param queryString
	 * @param docs
	 */
	public SearchResultList(String queryString, TopDocs docs) {
		
		// TODO
	}
	
	public String getQueryString() {
		return queryString;
	}
	
	public List<SearchResult> getSearchResults() {
		return searchResults;
	}
	
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}
	
	public void setSearchResults(List<SearchResult> searchResults) {
		this.searchResults = searchResults;
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
}









