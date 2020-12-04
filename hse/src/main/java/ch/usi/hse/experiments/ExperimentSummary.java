package ch.usi.hse.experiments;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import ch.usi.hse.db.entities.Experiment;
import ch.usi.hse.db.entities.TestGroup;

/**
 * Data class storing per-experiment usage statistics
 * 
 * @author robert.jans@usi.ch
 *
 */
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
	
	private List<TestGroupSummary> groupSummaries;
	
	
	/**
	 * Construct from Experiment and EventDataExtractor
	 * 
	 * @param experiment
	 * @param dataExtractor
	 */
	public ExperimentSummary(Experiment experiment, EventDataExtractor dataExtractor) {
		
		// general and totals
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
				
		totalQueries = dataExtractor.totalQueries(experiment);
		totalClicks = dataExtractor.totalClicks(experiment);
				
		// average stats
				
		queriesPerUser = dataExtractor.queriesPerUser(experiment);
		clicksPerUser = dataExtractor.clicksPerUser(experiment);
		clicksPerQuery = dataExtractor.clicksPerQuery(experiment);
		timePerQuery = dataExtractor.timePerQuery(experiment);
		timePerClick = dataExtractor.timePerClick(experiment);
		
		// group summaries
		
		groupSummaries = new ArrayList<>();
		
		for (TestGroup g : experiment.getTestGroups()) {
			
			groupSummaries.add(new TestGroupSummary(g, dataExtractor));
		}
	}
	
	/**
	 * Full arguments constructor for json I/O
	 * 
	 * @param title
	 * @param dateConducted
	 * @param duration
	 * @param groupNames
	 * @param participantsPerGroup
	 * @param participants
	 * @param totalQueries
	 * @param totalClicks
	 * @param queriesPerUser
	 * @param clicksPerUser
	 * @param clicksPerQuery
	 * @param timePerQuery
	 * @param timePerClick
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
							 @JsonProperty("timePerClick")DataStats timePerClick,
							 @JsonProperty("groupSummaries") List<TestGroupSummary> groupSummaries) {
		
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
		this.groupSummaries = groupSummaries;
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
	
	public List<TestGroupSummary> getGroupSummaries() {
		return groupSummaries;
	}
}












