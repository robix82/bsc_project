package usi.ch.hse.services;

import org.springframework.stereotype.Service;

import static usi.ch.hse.dummie_data.SearchData.*;
import usi.ch.hse.dto.SearchResultList;

/**
 * Service class for interacting with the Lucene Searcch API
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
		return dummieSearchResultList(10);
	}
}
