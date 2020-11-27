package ch.usi.hse.experiments;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class DataStatsTest {

	private static ObjectMapper mapper;
	private static ObjectWriter writer;
	
	@BeforeAll
	public static void init() {
		
		mapper = new ObjectMapper();
		mapper.findAndRegisterModules();
		writer = mapper.writer().withDefaultPrettyPrinter();
	}
	
	@Test
	public void testConstructor1() {
		
		int n = 23;
		double total = 2.5;
		double mean = 1.2;
		double median = 1.0;
		double stdDev = 0.3;
		
		DataStats stats = new DataStats(n, total, mean, median, stdDev);
		
		double eps = 0.0001;
		
		assertEquals(n, stats.getN());
		assertEquals(total, stats.getTotal(), eps);
		assertEquals(mean, stats.getMean(), eps);
		assertEquals(median, stats.getMedian(), eps);
		assertEquals(stdDev, stats.getStandardDeviation());
	}
	
	@Test 
	public void testConstructor2() {
		
		// example from https://en.wikipedia.org/wiki/Standard_deviation
		
		List<Double> testData = List.of(2.0, 4.0, 4.0, 4.0, 5.0, 5.0, 7.0, 9.0);
		double expectedTotal = 40;
		double expectedMean = 5;
		double expectedMedian = 4.5;
		double expectedStdDev = 2.0;
		
		DataStats stats = new DataStats(testData);
		
		double eps = 0.0001;
		
		assertEquals(expectedTotal, stats.getTotal(), eps);
		assertEquals(expectedMean, stats.getMean(), eps);
		assertEquals(expectedMedian, stats.getMedian(), eps);
		assertEquals(expectedStdDev, stats.getStandardDeviation(), eps);
	}
	
	@Test
	public void testJsonIo() throws Exception {
		
		int n = 23;
		double total = 2.5;
		double mean = 1.2;
		double median = 1.0;
		double stdDev = 0.3;
		
		DataStats stats = new DataStats(n, total, mean, median, stdDev);
		
		String jsonString = writer.writeValueAsString(stats);
		
		DataStats reconstructed = mapper.readValue(jsonString, DataStats.class);
		
		assertEquals(n, reconstructed.getN());
	}
}










