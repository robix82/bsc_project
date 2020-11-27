package ch.usi.hse.experiments;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import ch.usi.hse.db.entities.Experiment;
import ch.usi.hse.db.entities.TestGroup;

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
	
	// average  stats
	private DataStats queriesPerUser, clicksPerUser, clicksPerQuery, 
					  timePerQuery, timePerClick;
	
	
	@JsonIgnore
	private EventDataExtractor dataExtractor;
	
	/**
	 * Autowired constructor for usage as Component
	 * 
	 * @param qeRepo
	 * @param ceRepo
	 */
	@Autowired
	public ExperimentSummary(EventDataExtractor dataExtractor) {
		
		this.dataExtractor = dataExtractor;
	}
	
	/**
	 * Full arguments constructor for json I/O
	 * 
	 */
	public ExperimentSummary(@JsonProperty("title") String title,
							 @JsonProperty("dateConducted")LocalDateTime dateConducted,
							 @JsonProperty("duration")Duration duration,
							 @JsonProperty("groupNames")List<String> groupNames,
							 @JsonProperty("participantsPerGroup")Map<String, Integer> participantsPerGroup,
							 @JsonProperty("participants")int participants,
							 @JsonProperty("totalQueries")int totalQueries,
							 @JsonProperty("totalClicks")int totalClicks,
							 @JsonProperty("queriesPerUser")DataStats queriesPerUser,
							 @JsonProperty("clicksPerUser")DataStats clicksPerUser,
							 @JsonProperty("clicksPerQuery")DataStats clicksPerQuery,
							 @JsonProperty("timePerQuery")DataStats timePerQuery,
							 @JsonProperty("timePerClick")DataStats timePerClick) {
		
		this.title = title;
		this.dateConducted = dateConducted;
		this.duration = duration;
		this.groupNames = groupNames;
		this.participantsPerGroup = participantsPerGroup;
		this.participants = participants;
		this.totalQueries = totalQueries;
		this.totalClicks = totalClicks;
		this.queriesPerUser = queriesPerUser;
		this.clicksPerUser = clicksPerUser;
		this.clicksPerQuery = clicksPerQuery;
		this.timePerQuery = timePerQuery;
		this.timePerClick = timePerClick;
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
		
		totalQueries = dataExtractor.queriesPerExperiment(experiment);
		totalClicks = dataExtractor.clicksPerExperiment(experiment);
		
		return this;
	}
	
	public String getTitle() {
		return title;
	}
	
	public LocalDateTime getDateConducted() {
		return dateConducted;
	}
	
	public Duration getDuration() {
		return duration;
	}
	
	public List<String> getGroupNames() {
		return groupNames;
	}
	
	public Map<String, Integer> getParticipantsPerGroup() {
		return participantsPerGroup;
	}
	
	public int getParticipants() {
		return participants;
	}
	
	public int getTotalQueries() {
		return totalQueries;
	}
	
	public int getTotalClicks() {
		return totalClicks;
	}
	
	public DataStats getQueriesPerUser() {
		return queriesPerUser;
	}
	
	public DataStats getClicksPerUser() {
		return clicksPerUser;
	}
	
	public DataStats getClicksPerQuery() {
		return clicksPerQuery;
	}
	
	public DataStats getTimePerQuery() {
		return timePerQuery;
	}
	
	public DataStats getTimePerClick() {
		return timePerClick;
	}
}












