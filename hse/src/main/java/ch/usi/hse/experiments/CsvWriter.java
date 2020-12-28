package ch.usi.hse.experiments;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ch.usi.hse.db.entities.DocClickEvent;
import ch.usi.hse.db.entities.Experiment;
import ch.usi.hse.db.entities.Participant;
import ch.usi.hse.db.entities.QueryEvent;
import ch.usi.hse.db.entities.QueryStat;
import ch.usi.hse.db.entities.SessionEvent;
import ch.usi.hse.db.entities.UsageEvent;
import ch.usi.hse.db.repositories.DocClickEventRepository;
import ch.usi.hse.db.repositories.ExperimentRepository;
import ch.usi.hse.db.repositories.QueryEventRepository;
import ch.usi.hse.db.repositories.SessionEventRepository;
import ch.usi.hse.db.repositories.UsageEventRepository;
import ch.usi.hse.exceptions.NoSuchExperimentException;

@Component
public class CsvWriter {
	
	
	private ExperimentRepository experimentRepo;
	private UsageEventRepository usageEventRepo;
	private SessionEventRepository sessionEventRepo;
	private QueryEventRepository queryEventRepo;
	private DocClickEventRepository docClickEventRepo;
	
	private final String notAssigned =  "N.A.";
	private StringBuilder sb;
	
	@Autowired
	public CsvWriter(ExperimentRepository experimentRepo,
						UsageEventRepository usageEventRepo,
						SessionEventRepository sessionEventRepo,
						QueryEventRepository queryEventRepo,
						DocClickEventRepository docClickEventRepo) {
		
		this.experimentRepo = experimentRepo;
		this.usageEventRepo = usageEventRepo;
		this.sessionEventRepo = sessionEventRepo;
		this.queryEventRepo = queryEventRepo;
		this.docClickEventRepo = docClickEventRepo;
	}

	public String writeExperimentData(Experiment experiment) 
			throws NoSuchExperimentException {
		
		if (! experimentRepo.existsById(experiment.getId())) {
			throw new NoSuchExperimentException(experiment.getId());
		}
		
		sb = new StringBuilder();
				
		List<UsageEvent> events = usageEventRepo.findByExperiment(experiment);
		Collections.sort(events, byTimeStampComparator);

		addCsvHeader();
		
		for (UsageEvent event : events) {
			
			addCsvLine(event);
		}
				
		return sb.toString();
	}
	
	public String writeUserData(Participant participant) {
				
		sb = new StringBuilder();
		
		List<UsageEvent> events = usageEventRepo.findByUserId(participant.getId());
		Collections.sort(events, byTimeStampComparator);
		
		addCsvHeader();
		
		for (UsageEvent event : events) {
			
			addCsvLine(event);
		}
		
		return sb.toString();
	}
	
	private void addCsvHeader() {
		
		// base properties
		sb.append("eventId,eventType,timestamp,userId,groupId,groupName,");
				
		// SessionEvent properties
		sb.append("action,");
				
		// QueryEvent properties
		sb.append("queryString,resultCount,docDistribution,");
		
		// DocClickEvent properties
		sb.append("url,documentId,documentRank,collectionId,collectionName\n");
	}
	
	private void addCsvLine(UsageEvent event) {
		
		int eventId = event.getId();
		addBaseProperties(event);
		
		if (sessionEventRepo.existsById(eventId)) {
			
			SessionEvent sessionEvent = sessionEventRepo.findById(eventId);
			addSessionEventProperties(sessionEvent);
			fillNotAssigned(8);
		}
		else if (queryEventRepo.existsById(eventId)) {
							
			QueryEvent queryEvent = queryEventRepo.findById(eventId);
			fillNotAssigned(1);
			addQueryEventProperties(queryEvent);
			fillNotAssigned(5);
		}
		else if (docClickEventRepo.existsById(eventId)) {
			
			DocClickEvent docClickEvent = docClickEventRepo.findById(eventId);
			fillNotAssigned(4);
			addDocClickEventProperties(docClickEvent);
		}
		
		sb.setLength(sb.length() - 1);
		sb.append("\n");
	}
	
	private void addBaseProperties(UsageEvent e) {
		
		sb.append(e.getId()).append(",")
		  .append(e.getEventType().toString()).append(",")
		  .append(e.getTimestamp().toString()).append(",")
		  .append(e.getUserId()).append(",")
		  .append(e.getGroupId()).append(",")
		  .append(e.getGroupName()).append(",");
	}
	
	private void addSessionEventProperties(SessionEvent e) {
		
		sb.append(e.getEvent().toString()).append(",");
	}
	
	private void addQueryEventProperties(QueryEvent e) {
		
		sb.append(e.getQueryString()).append(",")
		  .append(e.getTotalResults()).append(",");
		
		for (QueryStat qs : e.getQueryStats()) {
			
			sb.append("[")
			  .append(qs.getCollectionName()).append(":")
			  .append(qs.getResultCount())
			  .append("]");
		}
		
		sb.append(",");
	}
	
	private void addDocClickEventProperties(DocClickEvent e) {

		sb.append(e.getUrl()).append(",")
		  .append(e.getDocumentId()).append(",")
		  .append(e.getDocumentRank()).append(",")
		  .append(e.getCollectionId()).append(",")
		  .append(e.getCollectionName()).append(",");
	}
	
	private void fillNotAssigned(int columns) {
		
		for (int i = 0; i < columns; ++i) {
			
			sb.append(notAssigned).append(",");
		}
	}
	
	private Comparator<UsageEvent> byTimeStampComparator = new Comparator<UsageEvent>() {
		
		public int compare(UsageEvent e1, UsageEvent e2) {
			
			return e1.getTimestamp().compareTo(e2.getTimestamp());
		}
	};
}







