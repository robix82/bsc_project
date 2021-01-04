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
	
	public int effectiveUserCount(Experiment experiment) {
		
		return participantIds(experiment).size();
	}
	
	public int effectiveUserCount(TestGroup testGroup) {
		
		return participantIds(testGroup).size();
	}
	
	public int totalQueries(Experiment experiment) {
		
		return qeRepo.findByExperiment(experiment).size();
	}
	
	public int totalQueries(TestGroup testGroup) {
		
		return qeRepo.findByGroupId(testGroup.getId()).size();
	}
	
	public int totalClicks(Experiment experiment) {
		
		return ceRepo.findByExperiment(experiment).size();
	}
	
	public int totalClicks(TestGroup testGroup) {
	
		return ceRepo.findByGroupId(testGroup.getId()).size();
	}
		
	public DataStats queriesPerUser(Experiment experiment) {
		
		DataStats stats = new DataStats();
		
		for (int id : participantIds(experiment)) {
	
			stats.appendValue(qeRepo.findByUserId(id).size());
		}
		
		return stats;
	}
	
	public DataStats queriesPerUser(TestGroup testGroup) {
		
		DataStats stats = new DataStats();
		
		for (int id : participantIds(testGroup)) {
				
			stats.appendValue(qeRepo.findByUserId(id).size());
		}
		
		return stats;
	}
	
	public DataStats clicksPerUser(Experiment experiment) {
		
		DataStats stats = new DataStats();
		
		for (int id : participantIds(experiment)) {
				
			stats.appendValue(ceRepo.findByUserId(id).size());
		}
		
		return stats;
	}
	
	public DataStats clicksPerUser(TestGroup testGroup) {
		
		DataStats stats = new DataStats();
		
		for (int id : participantIds(testGroup)) {

			stats.appendValue(ceRepo.findByUserId(id).size());
		}
		
		return stats;
	}
	
	public DataStats clicksPerQuery(Experiment experiment) {
		
		DataStats stats = new DataStats();
		
		for (int id : participantIds(experiment)) {
				
			stats.appendValues(clicksPerQuery(id));
		}
		
		return stats;
	}
	
	public DataStats clicksPerQuery(TestGroup testGroup) {
		
		DataStats stats = new DataStats();
		
		for (int id : participantIds(testGroup)) {
				
			stats.appendValues(clicksPerQuery(id));
		}
		
		return stats;
	}
	
	public DataStats timePerQuery(Experiment experiment) {
		
		DataStats stats = new DataStats();
		
		for (int id : participantIds(experiment)) {

			stats.appendValues(timePerQuery(id));
		}
		
		return stats;
	}
	
	public DataStats timePerQuery(TestGroup testGroup) {
		
		DataStats stats = new DataStats();
		
		for (int id : participantIds(testGroup)) {

			stats.appendValues(timePerQuery(id));
		}
		
		return stats;
	}
	
	public DataStats timePerClick(Experiment experiment) {
		
		DataStats stats = new DataStats();
		
		for (int id : participantIds(experiment)) {

			stats.appendValues(timePerClick(id));
		}
		
		return stats;
	}
	
	public DataStats timePerClick(TestGroup testGroup) {
		
		DataStats stats = new DataStats();
		
		for (int id : participantIds(testGroup)) {

			stats.appendValues(timePerClick(id));
		}
		
		return stats;
	}
	
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
					evt = history.get(++idx);
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





