package ch.usi.hse.experiments;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ch.usi.hse.db.entities.Experiment;
import ch.usi.hse.db.entities.Participant;
import ch.usi.hse.db.entities.TestGroup;
import ch.usi.hse.db.entities.UsageEvent;
import ch.usi.hse.db.repositories.DocClickEventRepository;
import ch.usi.hse.db.repositories.QueryEventRepository;
import ch.usi.hse.db.repositories.UsageEventRepository;

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
	

	private double clicksPerQuery(int userId) {
		
		int queries = qeRepo.findByUserId(userId).size();
		int clicks = ceRepo.findByUserId(userId).size();
		
		return (double) clicks / queries;
	}
}





