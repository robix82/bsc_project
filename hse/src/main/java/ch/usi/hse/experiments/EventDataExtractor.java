package ch.usi.hse.experiments;

import java.util.ArrayList;
import java.util.List;
import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
	
	public int queriesPerExperiment(Experiment experiment) {
		
		return qeRepo.findByExperiment(experiment).size();
	}
	
	public int clicksPerExperiment(Experiment experiment) {
		
		return qeRepo.findByExperiment(experiment).size();
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
		
		List<Double> queryCounts = new ArrayList<>();
		
		for (int id : participantIds(experiment)) {
			
			queryCounts.add((double) ceRepo.findByUserId(id).size());
		}
		
		return new DataStats(queryCounts);
	}
	
	public DataStats clicksPerUser(TestGroup testGroup) {
		
		List<Double> queryCounts = new ArrayList<>();
		
		for (int id : participantIds(testGroup)) {
			
			queryCounts.add((double) ceRepo.findByUserId(id).size());
		}
		
		return new DataStats(queryCounts);
	}
	
	public DataStats clicksPerQuery(Experiment experiment) {
		
		List<Double> data = new ArrayList<>();
		
		for (int id : participantIds(experiment)) {
			
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
	
	public DataStats clicksPerQuery(TestGroup testGroup) {
		
		List<Double> data = new ArrayList<>();
		
		for (int id : participantIds(testGroup)) {
			
			data.addAll(clicksPerQuery(id));
		}
		
		return new DataStats(data);
	}
	
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
		
		List<UsageEvent> history = ueRepo.findByUserId(userId);		
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
		
		List<UsageEvent> history = ueRepo.findByUserId(userId);		
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
		
		List<UsageEvent> history = ueRepo.findByUserId(userId);		
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
}





