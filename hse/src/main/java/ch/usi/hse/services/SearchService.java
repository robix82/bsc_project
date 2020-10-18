package ch.usi.hse.services;

import org.springframework.stereotype.Service;

import static ch.usi.hse.dummie_data.SearchData.*;
import ch.usi.hse.dto.SearchResultList;

/**
 * Service class for interacting with the Lucene Search API
 * 
 * @author robert.jans@usi.ch
 *
 */
@Service
public class SearchService {

	/**
	 * Performs the index search given a query string
	 * 
	 * @param queryString
	 * @return SearchResultList 
	 */
	public SearchResultList search(String queryString) {
		
		// TODO: replace with actual Lucene search
		
		SearchResultList res = dummieSearchResultList(10);
		res.setQueryString(queryString);
		
		return res;
	}
	
}