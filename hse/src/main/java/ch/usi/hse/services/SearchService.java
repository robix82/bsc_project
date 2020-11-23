package ch.usi.hse.services;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.usi.hse.db.entities.DocCollection;
import ch.usi.hse.db.entities.Experiment;
import ch.usi.hse.db.entities.HseUser;
import ch.usi.hse.db.entities.Participant;
import ch.usi.hse.db.entities.QueryEvent;
import ch.usi.hse.db.repositories.DocCollectionRepository;
import ch.usi.hse.db.repositories.ExperimentRepository;
import ch.usi.hse.exceptions.FileReadException;
import ch.usi.hse.exceptions.NoSuchExperimentException;
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
	private ExperimentRepository experimentRepo;
	private SearchAssembler searchAssembler;
	
	@Autowired
	public SearchService(DocCollectionRepository collectionRepo,
						 ExperimentRepository experimentRepo,
						 SearchAssembler searchAssembler) {
		
		this.collectionRepo = collectionRepo;
		this.experimentRepo = experimentRepo;
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
	 * @throws NoSuchExperimentException 
	 */
	public SearchResultList search(String queryString, HseUser user) 
			throws ParseException, 
				   FileReadException, 
				   InvalidTokenOffsetsException, 
				   NoSuchExperimentException {
		
		List<DocCollection> collections;
		
		if (user instanceof Participant) {
			
			Participant p = (Participant) user;
			int experimentId = p.getExperimentId();
			
			if (! experimentRepo.existsById(experimentId)) {
				throw new NoSuchExperimentException(experimentId);
			}
			
			collections = new ArrayList<>(p.getTestGroup().getDocCollections());
			SearchResultList srl = searchAssembler.getSearchResults(queryString, collections);
			
			Experiment experiment = experimentRepo.findById(p.getExperimentId());
			experiment.addUsageEvent(new QueryEvent(p, srl));
			experimentRepo.save(experiment);
			
			return srl;
		}
		else {
			
			collections = collectionRepo.findAll();
			return searchAssembler.getSearchResults(queryString, collections);
		}		
	}
	
	
}





