package ch.usi.hse.db.entities;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * db entity representing indexed document collections
 * 
 * @author robert.jans@usi.ch
 *
 */
@Entity(name="doc_collection")
public class DocCollection {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="doc_collection_id")
	private Integer id;
	
	@Column(name="name")
	private String name;
	
	@Column(name="url_list")
	private String urlList;
	
	@Column(name="index_dir")
	private String indexDir;
	
	@Column(name="indexed")
	private boolean indexed;
	
	public DocCollection() {
		
		id = 0;
		indexed = false;
	}
	
	public DocCollection(String name, String urlList) {
		
		id = 0;
		this.name = name;
		this.urlList = urlList;
		indexed = false;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getUrlList() {
		return urlList;
	}
	
	public String getIndexDir() {
		return indexDir;
	}
	
	public boolean getIndexed() {
		return indexed;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setUrlList(String urlList) {
		this.urlList = urlList;
	}
	
	public void setIndexDir(String indexDir) {
		this.indexDir = indexDir;
	}
	
	public void setIndexed(boolean indexed) {
		this.indexed = indexed;
	}
	
	@Override
	public boolean equals(Object o) {
		
		if (o == this) {
			return true;
		}
		
		if (! (o instanceof DocCollection)) {
			return false;
		}
		
		DocCollection c = (DocCollection) o;
		
		return c.id.equals(id);
	}
	
	@Override
	public int hashCode() {
		
		return Objects.hash(id);
	}
}




















