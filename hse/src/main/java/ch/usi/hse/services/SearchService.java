package ch.usi.hse.services;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import ch.usi.hse.db.entities.DocClickEvent;
import ch.usi.hse.db.entities.DocCollection;
import ch.usi.hse.db.entities.Experiment;
import ch.usi.hse.db.entities.Participant;
import ch.usi.hse.db.entities.QueryEvent;
import ch.usi.hse.db.entities.TestGroup;
import ch.usi.hse.db.repositories.ExperimentRepository;
import ch.usi.hse.db.repositories.ParticipantRepository;
import ch.usi.hse.db.repositories.TestGroupRepository;
import ch.usi.hse.exceptions.FileReadException;
import ch.usi.hse.exceptions.NoSuchExperimentException;
import ch.usi.hse.exceptions.NoSuchTestGroupException;
import ch.usi.hse.exceptions.NoSuchUserException;
import ch.usi.hse.retrieval.SearchResultList;
import ch.usi.hse.retrieval.SearchAssembler;
import ch.usi.hse.retrieval.SearchResult;

import java.io.IOException;
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
 
	private ExperimentRepository experimentRepo;
	private ParticipantRepository participantRepo;
	private TestGroupRepository groupRepo;
	private SearchAssembler searchAssembler;
	private SimpMessagingTemplate simpMessagingTemplate;
	
	@Autowired
	public SearchService(ExperimentRepository experimentRepo,
						 ParticipantRepository participantRepo,
						 TestGroupRepository groupRepo,
						 SearchAssembler searchAssembler,
						 SimpMessagingTemplate simpMessagingTemplate) {
		
		this.experimentRepo = experimentRepo;
		this.participantRepo =  participantRepo;
		this.groupRepo = groupRepo;
		this.searchAssembler = searchAssembler;
		this.simpMessagingTemplate = simpMessagingTemplate;
	}
	
	
	
	public SearchResultList handleNewQuery(String query, Participant participant) 
			throws NoSuchUserException, 
				   NoSuchTestGroupException, 
				   NoSuchExperimentException, 
				   IOException, 
				   ParseException, 
				   InvalidTokenOffsetsException, 
				   FileReadException {
		
		checkDataConsistency(participant);
		
		SearchResultList srl;
		
		if (query.equals(participant.getFirstQuery())) {			
			srl = getFirstQueryList(query, participant);
		}
		else {			
			srl = search(query, participant);
		}
		
		participant.setLastQuery(query);
		participant.incQueryCount();
		participantRepo.save(participant);
		
		sendNewQueryMessage(participant, srl);
		
		return srl;
	}

	public SearchResultList handleRepeatedQuery(String query, Participant participant) 
			throws NoSuchUserException, 
				   NoSuchTestGroupException, 
				   NoSuchExperimentException, 
				   IOException, ParseException, 
				   InvalidTokenOffsetsException, 
				   FileReadException {
		
		checkDataConsistency(participant);
		
		SearchResultList srl;
		
		if (query.equals(participant.getFirstQuery())) {			
			srl = getFirstQueryList(query, participant);
		}
		else {			
			srl = search(query, participant);
		}
		
		return srl;
	}
	
	public SearchResultList handleFirstQuery(String query, Participant participant) 
			throws NoSuchUserException, 
			       NoSuchTestGroupException, 
			       NoSuchExperimentException, 
			       IOException, 
			       ParseException, 
			       InvalidTokenOffsetsException, 
			       FileReadException {
		
		checkDataConsistency(participant);
		
		SearchResultList srl = getFirstQueryList(query, participant);
		
		participant.setFirstQuery(query);
		participant.setLastQuery(query);
		participant.incQueryCount();
		participantRepo.save(participant);
			
		sendNewQueryMessage(participant, srl);
		
		return srl;
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
	
	private SearchResultList search(String query, Participant p) 
			throws ParseException, 
			       FileReadException, 
			       InvalidTokenOffsetsException {
		
		TestGroup g = groupRepo.findById(p.getTestGroupId());
		List<DocCollection> collections = new ArrayList<>(g.getDocCollections());
		
		return searchAssembler.getSearchResults(query, collections);
	}
	
	private SearchResultList getFirstQueryList(String query, Participant p) 
			throws IOException, 
				   ParseException, 
				   InvalidTokenOffsetsException, 
				   FileReadException {
		
		TestGroup g = groupRepo.findById(p.getTestGroupId());
		DocCollection fqCollection = g.getFirstQueryCollection();
		
		if (fqCollection != null) {
			
			return searchAssembler.getFirstQueryList(fqCollection, query);
		}
		else {
			
			return search(query, p);
		}
	}
	
	private void sendNewQueryMessage(Participant p, SearchResultList srl) {
		
		Experiment experiment = experimentRepo.findById(p.getExperimentId());
		experiment.addUsageEvent(new QueryEvent(p, srl));
		experimentRepo.save(experiment);
		
		simpMessagingTemplate.convertAndSend("/userActions", experiment);
	}
	
	private void checkDataConsistency(Participant p) 
			throws NoSuchUserException, NoSuchTestGroupException, NoSuchExperimentException {
		
		int participantId = p.getId();
		int groupId = p.getTestGroupId();
		int experimentId = p.getExperimentId();
		
		if (! participantRepo.existsById(participantId)) {
			throw new NoSuchUserException("participant", participantId);
		}
		
		if (! groupRepo.existsById(groupId)) {
			throw new NoSuchTestGroupException(groupId);
		}
		
		if (! experimentRepo.existsById(experimentId)) {
			throw new NoSuchExperimentException(experimentId);
		}
	}
}























