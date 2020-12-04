package ch.usi.hse.experiments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
		
		List<Double> queryCounts = new ArrayList<>();
		
		for (int id : participantIds(experiment)) {
			
			queryCounts.add((double) qeRepo.findByUserId(id).size());
		}
		
		return new DataStats(queryCounts);
	}
	
	public DataStats queriesPerUser(TestGroup testGroup) {
		
		List<Double> queryCounts = new ArrayList<>();
		
		for (int id : participantIds(testGroup)) {
			
			queryCounts.add((double) qeRepo.findByUserId(id).size());
		}
		
		return new DataStats(queryCounts);
	}
	
	public DataStats clicksPerUser(Experiment experiment) {
		
		List<Double> clickCounts = new ArrayList<>();
		
		for (int id : participantIds(experiment)) {
			
			clickCounts.add((double) ceRepo.findByUserId(id).size());
		}
		
		return new DataStats(clickCounts);
	}
	
	public DataStats clicksPerUser(TestGroup testGroup) {
		
		List<Double> clickCounts = new ArrayList<>();
		
		for (int id : participantIds(testGroup)) {
			
			clickCounts.add((double) ceRepo.findByUserId(id).size());
		}
		
		return new DataStats(clickCounts);
	}
	
	public DataStats clicksPerQuery(Experiment experiment) {
		
		List<Double> data = new ArrayList<>();
		
		for (int id : participantIds(experiment)) {
			
			data.addAll(clicksPerQuery(id));
		}
		
		return new DataStats(data);
	}
	
	public DataStats clicksPerQuery(TestGroup testGroup) {
		
		List<Double> data = new ArrayList<>();
		
		for (int id : participantIds(testGroup)) {
			
			data.addAll(clicksPerQuery(id));
		}
		
		return new DataStats(data);
	}
	
	public DataStats timePerQuery(Experiment experiment) {
		
		List<Double> data = new ArrayList<>();
		
		for (int id : participantIds(experiment)) {
			
			data.addAll(timePerQuery(id));
		}
		
		return new DataStats(data);
	}
	
	public DataStats timePerQuery(TestGroup testGroup) {
		
		List<Double> data = new ArrayList<>();
		
		for (int id : participantIds(testGroup)) {
			
			data.addAll(timePerQuery(id));
		}
		
		return new DataStats(data);
	}
	
	public DataStats timePerClick(Experiment experiment) {
		
		List<Double> data = new ArrayList<>();
		
		for (int id : participantIds(experiment)) {
			
			data.addAll(timePerClick(id));
		}
		
		return new DataStats(data);
	}
	
	public DataStats timePerClick(TestGroup testGroup) {
		
		List<Double> data = new ArrayList<>();
		
		for (int id : participantIds(testGroup)) {
			
			data.addAll(timePerClick(id));
		}
		
		return new DataStats(data);
	}
	
	public Map<String, DataStats> clicksPerDocCollection(TestGroup testGroup) {
		
		Map<String, DataStats> res = new HashMap<>(); 
		Map<String, List<Double>> tmpData = new HashMap<>();
		List<String> collectionNames = new ArrayList<>();
		
		for (DocCollection c : testGroup.getDocCollections()) {
			collectionNames.add(c.getName());
			tmpData.put(c.getName(), new ArrayList<>());
		}
		
		for (int userId : participantIds(testGroup)) {
			
			Map<String, Double> perUserData = clicksPerDocCollection(collectionNames, userId);
			
			for (Entry<String, Double> e : perUserData.entrySet()) {

				tmpData.get(e.getKey()).add(e.getValue());
			}
		}
		
		for (Entry<String, List<Double>> e : tmpData.entrySet()) {
			
			res.put(e.getKey(), new DataStats(e.getValue()));
		}
		
		return res;
	}
	
	public Map<String, DataStats> timePerDocCollection(TestGroup testGroup) {
		
		Map<String, DataStats> res= new HashMap<>();
		Map<String, List<Double>> tmpData = new HashMap<>();
		List<String> collectionNames = new ArrayList<>();
		
		for (DocCollection c : testGroup.getDocCollections()) {
			collectionNames.add(c.getName());
			tmpData.put(c.getName(), new ArrayList<>());
		}
		
		for (int userId : participantIds(testGroup)) {
			
			Map<String, Double> perUserData = timePerDocCollection(collectionNames, userId);
			
			for (Entry<String, Double> e : perUserData.entrySet()) {
			
				tmpData.get(e.getKey()).add(e.getValue());
			}
		}
		
		for (Entry<String, List<Double>> e : tmpData.entrySet()) {
			
			res.put(e.getKey(), new DataStats(e.getValue()));
		}
		
		return res;
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
			ids.add(p.getId());
		}
		
		return ids;
	}
	

	private List<Double> clicksPerQuery(int userId) {
		
		List<UsageEvent> history = userHistory(userId);		
		List<Double> res = new ArrayList<>();
		
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
	
	private Map<String, Double> clicksPerDocCollection(List<String> collectionNames, int userId) {
		
		List<UsageEvent> history = userHistory(userId);
		Map<String, Double> res = new HashMap<>();
		
		for (String name : collectionNames) {
			res.put(name,  0.0);
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
				
				DocClickEvent click = (DocClickEvent) evt;
				String collectionName = click.getCollectionName();

				double count = res.get(collectionName) + 1.0;
				res.put(collectionName, count);
			}
			
			evt = history.get(idx++);
		}
		
		return res;
	}
	
	private Map<String, Double> timePerDocCollection(List<String> collectionNames, int userId) {
		
		List<UsageEvent> history = userHistory(userId);
		Map<String, Double> res = new HashMap<>();
		
		for (String name : collectionNames) {
			res.put(name,  0.0);
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
				
				DocClickEvent click = (DocClickEvent) evt;
				String collectionName = click.getCollectionName();
				
				LocalDateTime t0 = evt.getTimestamp();
				
				evt = history.get(idx++);
				
				LocalDateTime t1 = evt.getTimestamp();
				Duration dt = Duration.between(t0,  t1);
				
				Double newTotal = res.get(collectionName) + TimeUnit.SECONDS.convert(dt);
				res.put(collectionName, newTotal);
			}
			else {
				evt = history.get(idx++);
			}
		}
		
		return res;
	}
	
	private List<UsageEvent> userHistory(int userId) {
		
		List<UsageEvent> events = ueRepo.findByUserId(userId);
		
		Collections.sort(events,  byTimeStampComparator);
		
		return events;
	}
	
	private Comparator<UsageEvent> byTimeStampComparator = new Comparator<UsageEvent>() {
		
		public int compare(UsageEvent e1, UsageEvent e2) {
			
			return e1.getTimestamp().compareTo(e2.getTimestamp());
		}
	};
}





