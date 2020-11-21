package ch.usi.hse.db.entities;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity(name="query_stat")
public class QueryStat {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id")
	private Integer id;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="query_event")
	@JsonIgnore
	protected QueryEvent queryEvent;
	
	@Column(name="collection_id")
	private int collectionId;
	
	@Column(name="collection_name")
	private String collectionName;
	
	@Column(name="result_count")
	private int resultCount;
	
	public QueryStat() {
		id = 0;
	}
	
	public QueryStat(int collectionId, String collectionName, int resultCount) {
		
		id = 0;
		this.collectionId = collectionId;
		this.collectionName = collectionName;
		this.resultCount = resultCount;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public QueryEvent getQueryEvent() {
		return queryEvent;
	}
	
	public void setQueryEvent(QueryEvent queryEvent) {
		this.queryEvent = queryEvent;
	}
	
	public int getCollectionId() {
		return collectionId;
	}
	
	public void setCollectionId(int collectionId) {
		this.collectionId = collectionId;
	}
	
	public String getCollectionName() {
		return collectionName;
	}
	
	public void setCollectionName(String collectionName) {
		this.collectionName = collectionName;
	}
	
	public int getResultCount() {
		return resultCount;
	}
	
	public void setResultCount(int resultCount) {
		this.resultCount = resultCount;
	}
	
	@Override
	public boolean equals(Object o) {
		
		if (o == this) {
			return true;
		}
		
		if (! (o instanceof QueryStat)) {
			return false;
		}
		
		QueryStat qs = (QueryStat) o;
		
		return qs.id.equals(id);
	}
	
	@Override
	public int hashCode() {
		
		return Objects.hash(id);
	}
}







