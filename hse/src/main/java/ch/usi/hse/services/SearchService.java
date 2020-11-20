package ch.usi.hse.services;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.usi.hse.db.entities.DocCollection;
import ch.usi.hse.db.entities.HseUser;
import ch.usi.hse.db.entities.Participant;
import ch.usi.hse.db.repositories.DocCollectionRepository;
import ch.usi.hse.exceptions.FileReadException;
import ch.usi.hse.retrieval.SearchResultList;
import ch.usi.hse.retrieval.SearchAssembler;

import java.util.ArrayList;
import java.util.List;

/**
 * Service class for interacting with the Lucene Search API
 * 
 * @author robert.jans@usi.ch
 *
 */
@Service
public class SearchService {

	private DocCollectionRepository collectionRepo;
	private SearchAssembler searchAssembler;
	
	@Autowired
	public SearchService(DocCollectionRepository collectionRepo,
						 SearchAssembler searchAssembler) {
		
		this.collectionRepo = collectionRepo;
		this.searchAssembler = searchAssembler;
	}
	
	/**
	 * Performs the index search given a query string
	 * and the user who  is performing the query
	 * 
	 * @param queryString
	 * @return SearchResultList 
	 * @throws FileReadException 
	 * @throws ParseException 
	 * @throws InvalidTokenOffsetsException 
	 */
	public SearchResultList search(String queryString, HseUser user) throws ParseException, FileReadException, InvalidTokenOffsetsException {
		
		List<DocCollection> collections;
		
		if (user instanceof Participant) {
			
			Participant p = (Participant) user;
			collections = new ArrayList<>(p.getTestGroup().getDocCollections());
		}
		else {
			collections = collectionRepo.findAll();
		}
		
		return searchAssembler.getSearchResults(queryString, collections);
	}
	
	
}





