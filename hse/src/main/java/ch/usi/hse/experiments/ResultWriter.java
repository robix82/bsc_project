package ch.usi.hse.experiments;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import ch.usi.hse.db.entities.Experiment;
import ch.usi.hse.db.entities.Participant;
import ch.usi.hse.db.entities.TestGroup;
import ch.usi.hse.db.entities.UsageEvent;
import ch.usi.hse.exceptions.NoSuchExperimentException;

@Component
public class ResultWriter {

	private CsvWriter csvWriter;
	private ObjectWriter jsonWriter;
	private EventDataExtractor dataExtractor;
	
	@Autowired
	public ResultWriter(CsvWriter csvWriter, EventDataExtractor dataExtractor) {
		
		this.csvWriter = csvWriter;
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.findAndRegisterModules();
		jsonWriter = mapper.writer().withDefaultPrettyPrinter();
		
		this.dataExtractor = dataExtractor;
	}
	
	public InputStream rawDataCsv(Experiment experiment) throws NoSuchExperimentException {
		
		String csvString = csvWriter.writeExperimentData(experiment);
		byte[] csvData = csvString.getBytes();
		
		return new ByteArrayInputStream(csvData);
	}
	
	public InputStream rawDataJson(Experiment experiment) throws JsonProcessingException {
		
		byte[] jsonData = jsonWriter.writeValueAsBytes(experiment.getUsageEvents());
		
		return new ByteArrayInputStream(jsonData);
	}
	
	public InputStream userHistoriesCsv(TestGroup testGroup) throws IOException {
		
		ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
		ZipOutputStream zip = new ZipOutputStream(bytesOut);
		
		for (Participant p : testGroup.getParticipants()) {
			
			String fName = testGroup.getName() + "_" + p.getId() + ".csv";
			String csv = csvWriter.writeUserData(p);
	
			zip.putNextEntry(new ZipEntry(fName));	
			zip.write(csv.getBytes());
			zip.closeEntry();
		}
		
		zip.finish();

		return new ByteArrayInputStream(bytesOut.toByteArray());
	}
	
	public InputStream userHistoriesJson(Experiment experiment) throws JsonProcessingException {
		
		Map<String, Map<Integer, List<UsageEvent>>> perGroupData = new HashMap<>();
		
		for (TestGroup group : experiment.getTestGroups()) {
			
			perGroupData.put(group.getName(), dataExtractor.userHistories(group));
		}
		
		byte[] jsonData = jsonWriter.writeValueAsBytes(perGroupData);
		
		return new ByteArrayInputStream(jsonData);
	}
}








