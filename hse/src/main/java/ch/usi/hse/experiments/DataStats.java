package ch.usi.hse.experiments;

import java.util.List;

/**
 * Utility class including methods
 * for means, medians and standard deviations
 * 
 * @author robert.jans@usi.ch
 *
 */
public class DataStats {

	private int n;
	private double total;
	private double mean;
	private double median;
	private double stdDev;
	
	public DataStats(List<Double> values) {
		
		n = values.size();
		
		// median
		int m1 = (int) Math.floor((double) (n+1)/2) -1;
		int m2 = (int) Math.ceil((double) (n+1)/2) -1;
		median = (values.get(m1) + values.get(m2)) / 2;
		
		// total and mean
		for (Double v : values) {			
			total += v;
		}
		
		mean = total / n;
		
		// standard deviation
		
		double squareSum = 0;
		
		for (double v : values) {
			squareSum += (v - mean) * (v - mean);
		}
		
		stdDev = Math.sqrt(squareSum / n);
	}
	
	public int getValueCount() {
		return n;
	}
	
	public double getTotal() {
		return total;
	}
	
	public double getMean() {
		return mean;
	}
	
	public double getMedian() {
		return median;
	}
	
	public double getStandardDeviation() {
		return stdDev;
	}
}







