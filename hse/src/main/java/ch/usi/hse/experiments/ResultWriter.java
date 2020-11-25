package ch.usi.hse.experiments;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import ch.usi.hse.db.entities.Experiment;
import ch.usi.hse.exceptions.NoSuchExperimentException;

@Component
public class ResultWriter {

	private CsvWriter csvWriter;
	private ObjectWriter jsonWriter;
	
	@Autowired
	public ResultWriter(CsvWriter csvWriter) {
		
		this.csvWriter = csvWriter;
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.findAndRegisterModules();
		jsonWriter = mapper.writer().withDefaultPrettyPrinter();
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
}








