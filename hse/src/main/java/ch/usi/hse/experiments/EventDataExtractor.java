package ch.usi.hse.experiments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

import ch.usi.hse.db.entities.DocClickEvent;
import ch.usi.hse.db.entities.DocCollection;
import ch.usi.hse.db.entities.Experiment;
import ch.usi.hse.db.entities.Participant;
import ch.usi.hse.db.entities.SessionEvent;
import ch.usi.hse.db.entities.TestGroup;
import ch.usi.hse.db.entities.UsageEvent;
import ch.usi.hse.db.repositories.DocClickEventRepository;
import ch.usi.hse.db.repositories.QueryEventRepository;
import ch.usi.hse.db.repositories.UsageEventRepository;

/**
 * utilities for extracting and processing
 * information from the raw data collected during experiments 
 * 
 * @author robert.jans@usi.ch
 *
 */
@Component
public class EventDataExtractor {

	private UsageEventRepository ueRepo;
	private QueryEventRepository qeRepo;
	private DocClickEventRepository ceRepo;
	
	@Autowired
	public EventDataExtractor(UsageEventRepository ueRepo,
							  QueryEventRepository qeRepo,
							  DocClickEventRepository ceRepo) {
		
		this.ueRepo = ueRepo;
		this.qeRepo = qeRepo;
		this.ceRepo = ceRepo;
	}
	
	/**
	 * returns the number of participants who actually logged in
	 * 
	 * @param experiment
	 * @return
	 */
	public int effectiveUserCount(Experiment experiment) {
		
		return participantIds(experiment).size();
	}
	
	/**
	 * returns the number of participants who actually logged in
	 * 
	 * @param testGroup
	 * @return
	 */
	public int effectiveUserCount(TestGroup testGroup) {
		
		return participantIds(testGroup).size();
	}
	
	/**
	 * returns the total of all queries entered
	 * 
	 * @param experiment
	 * @return
	 */
	public int totalQueries(Experiment experiment) {
		
		return qeRepo.findByExperiment(experiment).size();
	}
	
	/**
	 * returns the total of all queries entered
	 * 
	 * @param experiment
	 * @return
	 */
	public int totalQueries(TestGroup testGroup) {
		
		return qeRepo.findByGroupId(testGroup.getId()).size();
	}
	
	/**
	 * returns the total of all documents accessed
	 * 
	 * @param experiemnt
	 * @return
	 */
	public int totalClicks(Experiment experiment) {
		
		return ceRepo.findByExperiment(experiment).size();
	}
	
	/**
	 * returns the total of all documents accessed
	 * 
	 * @param testGroup
	 * @return
	 */
	public int totalClicks(TestGroup testGroup) {
	
		return ceRepo.findByGroupId(testGroup.getId()).size();
	}
		
	/**
	 * per-experiment query statistics (averaged over users)
	 * 
	 * @param experiment
	 * @return
	 */
	public DataStats queriesPerUser(Experiment experiment) {
		
		DataStats stats = new DataStats();
		
		for (int id : participantIds(experiment)) {
	
			stats.appendValue(qeRepo.findByUserId(id).size());
		}
		
		return stats;
	}
	
	/**
	 * per-testGroup query statistics (averaged over users)
	 * 
	 * @param testGroup
	 * @return
	 */
	public DataStats queriesPerUser(TestGroup testGroup) {
		
		DataStats stats = new DataStats();
		
		for (int id : participantIds(testGroup)) {
				
			stats.appendValue(qeRepo.findByUserId(id).size());
		}
		
		return stats;
	}
	
	/**
	 * per-experiment document access statistics (averaged over users)
	 * 
	 * @param experiment
	 * @return
	 */
	public DataStats clicksPerUser(Experiment experiment) {
		
		DataStats stats = new DataStats();
		
		for (int id : participantIds(experiment)) {
				
			stats.appendValue(ceRepo.findByUserId(id).size());
		}
		
		return stats;
	}
	
	/**
	 * testGroup level document access statistics (averaged over users)
	 * 
	 * @param testGroup
	 * @return
	 */
	public DataStats clicksPerUser(TestGroup testGroup) {
		
		DataStats stats = new DataStats();
		
		for (int id : participantIds(testGroup)) {

			stats.appendValue(ceRepo.findByUserId(id).size());
		}
		
		return stats;
	}
	
	/**
	 * experiment level per-query document access statistics (averaged over users)
	 * 
	 * @param experiment
	 * @return
	 */
	public DataStats clicksPerQuery(Experiment experiment) {
		
		DataStats stats = new DataStats();
		
		for (int id : participantIds(experiment)) {
				
			stats.appendValues(clicksPerQuery(id));
		}
		
		return stats;
	}
	
	/**
	 * testGroup level per-query document access statistics (averaged over users)
	 * 
	 * @param testGroup
	 * @return
	 */
	public DataStats clicksPerQuery(TestGroup testGroup) {
		
		DataStats stats = new DataStats();
		
		for (int id : participantIds(testGroup)) {
				
			stats.appendValues(clicksPerQuery(id));
		}
		
		return stats;
	}
	
	/**
	 * experiment level time spent between successive queries (averaged over all queries)
	 * 
	 * @param experiment
	 * @return
	 */
	public DataStats timePerQuery(Experiment experiment) {
		
		DataStats stats = new DataStats();
		
		for (int id : participantIds(experiment)) {

			stats.appendValues(timePerQuery(id));
		}
		
		return stats;
	}
	
	/**
	 * test group level time spent between successive queries (averaged over all queries)
	 * 
	 * @param testGroup
	 * @return
	 */
	public DataStats timePerQuery(TestGroup testGroup) {
		
		DataStats stats = new DataStats();
		
		for (int id : participantIds(testGroup)) {

			stats.appendValues(timePerQuery(id));
		}
		
		return stats;
	}
	
	/**
	 * experiment level time spent viewing a document (averaged over all document access events)
	 * 
	 * @param experiment
	 * @return
	 */
	public DataStats timePerClick(Experiment experiment) {
		
		DataStats stats = new DataStats();
		
		for (int id : participantIds(experiment)) {

			stats.appendValues(timePerClick(id));
		}
		
		return stats;
	}
	
	/**
	 * test group level time spent viewing a document (averaged over all document access events)
	 * 
	 * @param testGroup
	 * @return
	 */
	public DataStats timePerClick(TestGroup testGroup) {
		
		DataStats stats = new DataStats();
		
		for (int id : participantIds(testGroup)) {

			stats.appendValues(timePerClick(id));
		}
		
		return stats;
	}
	
	/**
	 * distribution of document access events over document collections (averaged over users)
	 * 
	 * @param testGroup
	 * @return
	 */
	public Map<String, DataStats> clicksPerDocCollection(TestGroup testGroup) {
		
		Map<String, DataStats> res = new HashMap<>();
		
		for (DocCollection c : testGroup.getDocCollections()) {

			res.put(c.getName(), new DataStats());
		}
		
		for (int id : participantIds(testGroup)) {
			
			List<UsageEvent> h = userHistory(id);
		
			for (String cName : res.keySet()) {
					
				res.get(cName).appendValue(clicksPerCollection(h, cName));
			}
		}
		
		return res;
	}
	
	/**
	 * distribution of time spent over document collections (averaged over users)
	 * 
	 * @param testGroup
	 * @return
	 */
	public Map<String, DataStats> timePerDocCollection(TestGroup testGroup) {
		
		Map<String, DataStats> res = new HashMap<>();
		
		for (DocCollection c : testGroup.getDocCollections()) {

			res.put(c.getName(), new DataStats());
		}
		
		for (int id : participantIds(testGroup)) {
			
			List<UsageEvent> h = userHistory(id);

			for (String cName : res.keySet()) {
			
				res.get(cName).appendValue(timePerCollection(h, cName));
			}
		}
		
		return res;
	}
	
	/**
	 * chronologically sorted list of usage events for each participant 
	 * 
	 * @param group
	 * @return
	 */
	public Map<Integer, List<UsageEvent>> userHistories(TestGroup group) {
		
		Map<Integer, List<UsageEvent>> histories = new HashMap<>();
		
		for (int id : participantIds(group)) {
			
			histories.put(id, userHistory(id));
		}
		
		return histories;
	}
	
	// PRIVATE UTILITY METHODS
	
	private List<Integer> participantIds(Experiment experiment) {
		
		List<Integer> ids = new ArrayList<>();
		
		for (TestGroup g : experiment.getTestGroups()) {
			
			ids.addAll(participantIds(g));
		}
		
		return ids;
	}
	
	private List<Integer> participantIds(TestGroup group) {
		
		List<Integer> ids = new ArrayList<>();
		
		for (Participant p : group.getParticipants()) {
			
			int id = p.getId();
			
			if (ueRepo.findByUserId(id).size() > 0) {
				ids.add(id);
			}
		}
		
		return ids;
	}
	

	private List<Double> clicksPerQuery(int userId) {
		
		List<UsageEvent> history = userHistory(userId);		
		List<Double> res = new ArrayList<>();
		
		if (history.isEmpty()) {
			return res;
		}
		
		int idx = 0;
		int clickCount = 0;
		UsageEvent evt; 
		
		while (idx < history.size()) {
			
			evt = history.get(idx++);
			
			if (evt.getEventType().equals(UsageEvent.Type.SESSION)) {
				
				SessionEvent se = (SessionEvent) evt;
				
				if (se.getEvent().equals(SessionEvent.Event.LOGOUT)) {
					return res;
				}
			}
			else if (evt.getEventType().equals(UsageEvent.Type.QUERY)) {
				
				evt = history.get(idx);
				clickCount = 0;
				
				while (evt.getEventType().equals(UsageEvent.Type.DOC_CLICK)) {
					
					++clickCount;
					
					try {
						evt = history.get(++idx);
					}
					catch(Exception e) {
						System.out.println("INDE) ERROR");
						break;
					}
				}
				
				res.add((double) clickCount);
			}
		}

		return res;
	}
	
	private List<Double> timePerQuery(int userId) {
		
		List<UsageEvent> history = userHistory(userId);		
		List<Double> res = new ArrayList<>();
		
		if (history.isEmpty()) {
			return res;
		}
		
		int idx = 0;
		UsageEvent evt = history.get(idx++);
		
		while (idx < history.size()) {
						
			while (! evt.getEventType().equals(UsageEvent.Type.QUERY)) {
				
				evt = history.get(idx++);
				
				if (evt.getEventType().equals(UsageEvent.Type.SESSION)) {
					
					SessionEvent se = (SessionEvent) evt;
					
					if (se.getEvent().equals(SessionEvent.Event.LOGOUT)) {
						return res;
					}
				}
			}
			
			if (evt.getEventType().equals(UsageEvent.Type.QUERY)) {
				
				LocalDateTime t0 = evt.getTimestamp();
				evt = history.get(idx++);			
				
				while (evt.getEventType().equals(UsageEvent.Type.DOC_CLICK)) {
					
					evt = history.get(idx++);
				}
				
				LocalDateTime t1 = evt.getTimestamp();
				
				Duration dt = Duration.between(t0,  t1);
				
				res.add((double) dt.getSeconds());
			}
		}
		
		return res;
	}
	
	private List<Double> timePerClick(int userId) {
		
		List<UsageEvent> history = userHistory(userId);		
		List<Double> res = new ArrayList<>();
		
		if (history.isEmpty()) {
			return res;
		}
		
		int idx = 0;
		UsageEvent evt = history.get(idx++);
		
		while (idx < history.size()) {
			
			while (! evt.getEventType().equals(UsageEvent.Type.DOC_CLICK)) {
				
				evt = history.get(idx++);
				
				if (evt.getEventType().equals(UsageEvent.Type.SESSION)) {
					
					SessionEvent se = (SessionEvent) evt;
					
					if (se.getEvent().equals(SessionEvent.Event.LOGOUT)) {
						return res;
					}
				}
			}
			
			if (evt.getEventType().equals(UsageEvent.Type.DOC_CLICK)) {
				
				LocalDateTime t0 = evt.getTimestamp();
				
				evt = history.get(idx++);
				
				LocalDateTime t1 = evt.getTimestamp();
				Duration dt = Duration.between(t0,  t1);
				res.add((double) dt.getSeconds());
			}
		}
		
		return res;
	}
	
	
	private List<UsageEvent> userHistory(int userId) {
		
		List<UsageEvent> events = ueRepo.findByUserId(userId);
		
		Collections.sort(events,  byTimeStampComparator);
		
		return events;
	}
	
	private double clicksPerCollection(List<UsageEvent> history, String collectionName) {
		
		int total = 0;
		
		for (UsageEvent evt : history) {
			
			if (evt instanceof DocClickEvent) {
				
				if (evt.getEventType().equals(UsageEvent.Type.DOC_CLICK)) {
					
					DocClickEvent clickEvent = (DocClickEvent) evt;
					
					if (clickEvent.getCollectionName().equals(collectionName)) {
						
						++total;
					}
				}
			}
		}
		
		return total;
	}
	
	private double timePerCollection(List<UsageEvent> history, String collectionName) {
		
		double total = 0;
		
		for (int i = 0; i < history.size(); ++i) {
			
			UsageEvent evt = history.get(i);
			
			if (evt.getEventType().equals(UsageEvent.Type.DOC_CLICK)) {
				
				DocClickEvent clickEvent = (DocClickEvent) evt;
				
				if (clickEvent.getCollectionName().equals(collectionName) 
				    && i < history.size() -1) {
					
					UsageEvent nextEvent = history.get(i + 1);
					
					Duration dt = Duration.between(clickEvent.getTimestamp(), nextEvent.getTimestamp());
					total += dt.getSeconds();
				}
			}
		}
		
		return total;
	}
	
	private Comparator<UsageEvent> byTimeStampComparator = new Comparator<UsageEvent>() {
		
		public int compare(UsageEvent e1, UsageEvent e2) {
			
			return e1.getTimestamp().compareTo(e2.getTimestamp());
		}
	};
}





