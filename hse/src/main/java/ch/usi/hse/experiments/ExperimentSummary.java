package ch.usi.hse.experiments;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ch.usi.hse.db.entities.Experiment;
import ch.usi.hse.db.entities.TestGroup;
import ch.usi.hse.db.repositories.DocClickEventRepository;
import ch.usi.hse.db.repositories.QueryEventRepository;

@Component
public class ExperimentSummary {
	
	// general
	private String title;
	private LocalDateTime dateConducted;
	private Duration duration;
	private List<String> groupNames;
	private Map<String, Integer> participantsPerGroup;
	
	// totals
	private int participants, totalQueries, totalClicks;
	
	// average stats
	private DataStats queriesPerUser, clicksPerUser, clicksPerQuery, 
					  timePerQuery, timePerClick;
	
	
	
	private QueryEventRepository qeRepo;
	private DocClickEventRepository ceRepo;
	
	@Autowired
	public ExperimentSummary(QueryEventRepository qeRepo,
							  DocClickEventRepository ceRepo) {
		
		this.qeRepo = qeRepo;
		this.ceRepo = ceRepo;
	}

	public ExperimentSummary fromExperiment(Experiment experiment) {
		
		title = experiment.getTitle();
		dateConducted = experiment.getDateConducted();
		duration = experiment.getDuration();
		
		groupNames = new ArrayList<>();
		participantsPerGroup = new HashMap<>();
		participants = 0;
		
		for (TestGroup g : experiment.getTestGroups()) {
			
			String gName = g.getName();
			int gParticipants = g.getParticipants().size();
			
			groupNames.add(gName);
			participantsPerGroup.put(gName, gParticipants);
			participants += gParticipants;
		}
		
		totalQueries = qeRepo.findByExperiment(experiment).size();
		totalClicks = ceRepo.findByExperiment(experiment).size();
		
		return this;
	}
	
	// GETTERS AND SETTERS (for json i/o)
	
	// general
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public LocalDateTime getDateConducted() {
		return dateConducted;
	}
	
	public void setDateConducted(LocalDateTime dateConducted) {
		this.dateConducted = dateConducted;
	}
	
	public Duration getDuration() {
		return duration;
	}
	
	public void setDuration(Duration duration) {
		this.duration = duration;
	}
	
	public List<String> getGroupNammes() {
		return groupNames;
	}
	
	public void setGroupNames(List<String> groupNames) {
		this.groupNames = groupNames;
	}
	
	public Map<String, Integer> getParticipantsPerGroup() {
		return participantsPerGroup;
	}
	
	public void setParticipantsPerGroup(Map<String, Integer> participantsPerGroup) {
		this.participantsPerGroup = participantsPerGroup;
	}
	
	// totals
	
	public int getParticipants() {
		return participants;
	}
	
	public void setParticipants(int participants) {
		this.participants = participants;
	}
	
	public int getTotalQueries() {
		return totalQueries;
	}
	
	public void setTotalQueries(int totalQueries) {
		this.totalQueries = totalQueries;
	}
	
	public int getTotalClicks() {
		return totalClicks;
	}
	
	public void setTotalClicks(int totalClicks) {
		this.totalClicks = totalClicks;
	}
	
	// average stats
	
	public DataStats getQueriesPerUser() {
		return queriesPerUser;
	}
	
	public void setQueriesPerUser(DataStats queriesPerUser) {
		this.queriesPerUser = queriesPerUser;
	}
	
	public DataStats getClicksPerUser() {
		return clicksPerUser;
	}
	
	public void setClicksPerUser(DataStats clicksPerUser) {
		this.clicksPerUser = clicksPerUser;
	}
	
	public DataStats getClicksPerQuery() {
		return clicksPerQuery;
	}
	
	public  void setClicksPerQuery(DataStats clicksPerQuery) {
		this.clicksPerQuery = clicksPerQuery; 
	}
	
	public DataStats getTimePerQuery() {
		return timePerQuery;
	}
	
	public void setTimePerQuery(DataStats timePerQuery) {
		this.timePerQuery = timePerQuery;
	}
	
	public DataStats getTimePerClick() {
		return timePerClick;
	}
	
	public void setTimePerClick(DataStats timePerClick) {
		this.timePerClick = timePerClick;
	}
}












