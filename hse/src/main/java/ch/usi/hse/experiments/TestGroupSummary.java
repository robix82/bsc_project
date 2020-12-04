package ch.usi.hse.experiments;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import ch.usi.hse.db.entities.DocCollection;
import ch.usi.hse.db.entities.TestGroup;

/**
 * Data class storing per-testgroup statistics
 * 
 * @author robert.jans@usi.ch
 *
 */
public class TestGroupSummary {

	private String groupName;
	private List<String> collectionNames;
	
	// totals
	private int participants, totalQueries, totalClicks;
	
	// average stats
	private DataStats queriesPerUser, clicksPerUser, clicksPerQuery, 
					  timePerQuery, timePerClick;
	
	// per docCollection stats (Collection name : data)
	private Map<String, DataStats> clicksPerDocCollection, timePerDocCollection;
	
	@JsonIgnore
	private EventDataExtractor dataExtractor;
	
	
	/**
	 * Construct from TestGroup
	 * 
	 * @param dataExtractor
	 * @param testGroup
	 */
	public TestGroupSummary(EventDataExtractor dataExtractor, TestGroup testGroup) {
		
		this.dataExtractor = dataExtractor;
		
		groupName = testGroup.getName();
		
		collectionNames = new ArrayList<>();
		
		for (DocCollection c : testGroup.getDocCollections()) {
			collectionNames.add(c.getName());
		}
		
		participants = testGroup.getParticipants().size();
		totalQueries = dataExtractor.totalQueries(testGroup);
		totalClicks = dataExtractor.totalClicks(testGroup);
		queriesPerUser = dataExtractor.queriesPerUser(testGroup);
		clicksPerUser = dataExtractor.clicksPerUser(testGroup);
		clicksPerQuery = dataExtractor.clicksPerQuery(testGroup);
		timePerQuery = dataExtractor.timePerQuery(testGroup);
		timePerClick = dataExtractor.timePerClick(testGroup);
		clicksPerDocCollection = dataExtractor.clicksPerDocCollection(testGroup);
		timePerDocCollection = dataExtractor.timePerDocCollection(testGroup);
	}
	
	/**
	 * Full arguments constructor for json I/O
	 * 
	 * @param groupName
	 * @param collectionNames
	 * @param participants
	 * @param totalQueries
	 * @param totalClicks
	 * @param queriesPerUser
	 * @param clicksPerUser
	 * @param clicksPerQuery
	 * @param timePerQuery
	 * @param timePerClick
	 * @param clicksPerDocCollection
	 * @param timePerDocColletion
	 */
	public TestGroupSummary(@JsonProperty("groupName") String groupName,
							@JsonProperty("collectionNames") List<String> collectionNames,
							@JsonProperty("participants") int participants,
							@JsonProperty("totalQueries") int totalQueries,
							@JsonProperty("totalClicks") int totalClicks,
							@JsonProperty("queriesPerUser") DataStats queriesPerUser,
							@JsonProperty("clicksPerUser") DataStats clicksPerUser,
							@JsonProperty("clicksPerQuery") DataStats clicksPerQuery,
							@JsonProperty("timePerQuery") DataStats timePerQuery,
							@JsonProperty("timePerClick") DataStats timePerClick,
							@JsonProperty("clicksPerDocCollection") Map<String, DataStats> clicksPerDocCollection,
							@JsonProperty("timePerDocCollection") Map<String, DataStats> timePerDocCollection) {
		
		
		this.groupName = groupName;
		this.collectionNames = collectionNames;
		this.participants = participants;
		this.totalQueries = totalQueries;
		this.totalClicks = totalClicks;
		this.queriesPerUser = queriesPerUser;
		this.clicksPerUser = clicksPerUser;
		this.clicksPerQuery = clicksPerQuery;
		this.timePerQuery = timePerQuery;
		this.timePerClick = timePerClick;
		this.clicksPerDocCollection = clicksPerDocCollection;
		this.timePerDocCollection = timePerDocCollection;
	}
	
	// getters
	
	public String getGroupName() {
		return groupName;
	}
	
	public List<String> getCollectionNames() {
		return collectionNames;
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
	
	public Map<String, DataStats> getClicksPerDocCollection() {
		return clicksPerDocCollection;
	}
	
	public Map<String, DataStats> getTimePerDocCollection() {
		return timePerDocCollection;
	}
}















