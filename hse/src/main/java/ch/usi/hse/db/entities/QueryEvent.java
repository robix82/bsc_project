package ch.usi.hse.db.entities;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import ch.usi.hse.retrieval.SearchResultList;

@Entity(name="query_event")
public class QueryEvent extends UsageEvent {

	@Column(name="query_string")
	private String queryString;
	
	@Column(name="total_results")
	private int totalResults;
	
	@OneToMany(mappedBy="queryEvent", fetch=FetchType.EAGER, orphanRemoval=true, cascade=CascadeType.ALL)
	private Set<QueryStat> queryStats;
	
	public QueryEvent() {
		
		super();
		eventType = UsageEvent.Type.QUERY;
		queryStats = new HashSet<>();
	}
	
	public QueryEvent(Participant participant, SearchResultList resultList) {
		
		super(UsageEvent.Type.QUERY, participant);
		
		queryStats = new HashSet<>();
		
		queryString = resultList.getQueryString();
		totalResults = resultList.getSearchResults().size();
		
		for (Entry<DocCollection, Integer> e : resultList.getCollectionStats().entrySet()) {
			
			DocCollection c = e.getKey();
			QueryStat newStat = new QueryStat(c.getId(), c.getName(), e.getValue());
			newStat.setQueryEvent(this);
			queryStats.add(newStat);
		}
	}
	
	public String getQueryString() {
		return queryString;
	}
	
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}
	
	public int getTotalResults() {
		return totalResults;
	}
	
	public void setTotalResults(int totalResults) {
		this.totalResults = totalResults;
	}
	
	public Set<QueryStat> getQueryStats() {
		return queryStats;
	}
	
	public void setQueryStats(Set<QueryStat> queryStats) {
		this.queryStats = queryStats;
	}
}







