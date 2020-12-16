package ch.usi.hse.services;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import ch.usi.hse.db.entities.DocClickEvent;
import ch.usi.hse.db.entities.DocCollection;
import ch.usi.hse.db.entities.Experiment;
import ch.usi.hse.db.entities.HseUser;
import ch.usi.hse.db.entities.Participant;
import ch.usi.hse.db.entities.QueryEvent;
import ch.usi.hse.db.entities.TestGroup;
import ch.usi.hse.db.repositories.DocCollectionRepository;
import ch.usi.hse.db.repositories.ExperimentRepository;
import ch.usi.hse.db.repositories.ParticipantRepository;
import ch.usi.hse.exceptions.FileReadException;
import ch.usi.hse.exceptions.NoSuchExperimentException;
import ch.usi.hse.exceptions.NoSuchFileException;
import ch.usi.hse.retrieval.SearchResultList;
import ch.usi.hse.storage.UrlListStorage;
import ch.usi.hse.retrieval.SearchAssembler;
import ch.usi.hse.retrieval.SearchResult;

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
	private ParticipantRepository participantRepo;
	private SearchAssembler searchAssembler;
	private SimpMessagingTemplate simpMessagingTemplate;
	private UrlListStorage urlListStorage;
	
	@Autowired
	public SearchService(DocCollectionRepository collectionRepo,
						 ExperimentRepository experimentRepo,
						 ParticipantRepository participantRepo,
						 SearchAssembler searchAssembler,
						 SimpMessagingTemplate simpMessagingTemplate,
						 UrlListStorage urlListStorage) {
		
		this.collectionRepo = collectionRepo;
		this.experimentRepo = experimentRepo;
		this.participantRepo =  participantRepo;
		this.searchAssembler = searchAssembler;
		this.simpMessagingTemplate = simpMessagingTemplate;
		this.urlListStorage = urlListStorage;
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
	 * @throws NoSuchFileException 
	 */
	public SearchResultList search(String queryString, HseUser user) 
			throws ParseException, 
				   FileReadException, 
				   InvalidTokenOffsetsException, 
				   NoSuchExperimentException, 
				   NoSuchFileException {
		
		List<DocCollection> collections;
		
		if (user instanceof Participant) {
			
			String query = queryString.trim().toLowerCase();
			
			Participant p = (Participant) user;
			int experimentId = p.getExperimentId();
			p.incQueryCount();
			
			if (p.getQueryCount() == 1) {
				p.setFirstQuery(query);
			}
			
			participantRepo.save(p);
			
			if (! experimentRepo.existsById(experimentId)) {
				throw new NoSuchExperimentException(experimentId);
			}
			
			TestGroup group = p.getTestGroup();
			SearchResultList srl;
			
			if (query.equals(p.getFirstQuery())) {
				
				List<String> urls = urlListStorage.getUrlLines(group.getFirstQueryList());
				srl = searchAssembler.getFirstQueryList(urls, queryString);
			}
			else {
			
				collections = new ArrayList<>(group.getDocCollections());
				srl = searchAssembler.getSearchResults(queryString, collections);
			}
			
			Experiment experiment = experimentRepo.findById(p.getExperimentId());
			experiment.addUsageEvent(new QueryEvent(p, srl));
			experimentRepo.save(experiment);
			
			simpMessagingTemplate.convertAndSend("/userActions", experiment);
			
			return srl;
		}
		else {
			
			collections = collectionRepo.findAll();
			return searchAssembler.getSearchResults(queryString, collections);
		}		
	}
	
	public void addDocClickEvent(SearchResult searchResult, Participant participant) 
			throws NoSuchExperimentException {
		
		int experimentId = participant.getExperimentId();
		
		if (! experimentRepo.existsById(experimentId)) {
			throw new NoSuchExperimentException(experimentId);
		}
		
		participant.incClickCount();
		participantRepo.save(participant);
		Experiment experiment = experimentRepo.findById(experimentId);
		experiment.addUsageEvent(new DocClickEvent(participant, searchResult));	
		experimentRepo.save(experiment);
		simpMessagingTemplate.convertAndSend("/userActions", experiment);
	}
}























