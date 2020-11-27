package ch.usi.hse.experiments;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;

public class DataStatsTest {

	@Test 
	public void testConstrucctorAndGetters() {
		
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
}










