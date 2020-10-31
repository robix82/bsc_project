package ch.usi.hse.db.entities;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import ch.usi.hse.config.Language;

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
	
	@Column(name="language")
	private String language;
	
	@Column(name="url_list")
	private String urlListName;
	
	@Column(name="index_dir")
	private String indexDirName;
	
	@Column(name="indexed")
	private boolean indexed;
	 
	public DocCollection() {
		
		id = 0;
		indexed = false;
		language = Language.IT;
	}
	
	public DocCollection(String name, String urlList) {
		
		id = 0;
		indexed = false;
		language = Language.IT;
		this.name = name;
		this.urlListName = urlList;
	}
	
	public int getId() { 
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getLanguage() {
		return language;
	} 
	
	public String getUrlListName() {
		return urlListName;
	}
	
	public String getIndexDirName() {
		return indexDirName;
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
	
	public void setLanguage(String language) {
		this.language = language;
	}
	
	public void setUrlListName(String urlListName) {
		this.urlListName = urlListName;
	}
	
	public void setIndexDirName(String indexDirName) {
		this.indexDirName = indexDirName;
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




















