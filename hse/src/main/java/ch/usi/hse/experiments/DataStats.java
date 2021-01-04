package ch.usi.hse.experiments;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
	private double standardDeviation;
	
	@JsonIgnore
	private List<Double> values;
	
	/**
	 * Full arguments constructor for json I/O
	 * 
	 * @param n
	 * @param total
	 * @param mean
	 * @param median
	 * @param standardDeviation
	 */
	public DataStats(int n, double total, double mean, 
					 double median, double standardDeviation) {
		
		this.n = n;
		this.total = total;
		this.mean = mean;
		this.median = median;
		this.standardDeviation = standardDeviation;
	}
	
	
	/**
	 * Construct from value list 
	 * (computes statistical metrics based on the given values)
	 * 
	 * @param values
	 */
	public DataStats(List<Double> values) {
		
		this.values = values;
		
		update();
	}
	
	/**
	 * default constructor: initializes all fields to 0
	 */
	public DataStats() {
		
		values = new ArrayList<>();
		n = 0;
		total = 0;
		mean = 0;
		median = 0;
		standardDeviation = 0;
	}
	
	public int getN() {
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
		return standardDeviation;
	}
	
	@JsonIgnore
	public void appendValue(double v) {
		
		values.add(v);
		update();
	}
	
	@JsonIgnore
	public void appendValues(List<Double> vs) {
		
		values.addAll(vs);
		update();
	}
	
	private void update() {
		
		n = values.size();
		
		if (n > 0) {
			
			// median
			List<Double> valuesCp = new ArrayList<>(values);
			Collections.sort(valuesCp);
			int m1 = (int) Math.floor((double) (n+1)/2) -1;
			int m2 = (int) Math.ceil((double) (n+1)/2) -1;
			median = (valuesCp.get(m1) + valuesCp.get(m2)) / 2;
			
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
			
			standardDeviation = Math.sqrt(squareSum / n);
		}
	}
	
	@Override
	public boolean equals(Object o) {
		
		if (o == this) {
			return true;
		}
		
		if (! (o instanceof DataStats)) {
			return false;
		}
		
		DataStats stats = (DataStats) o;
		
		return stats.values.equals(values);
	}
	
	@Override
	public int hashCode() {
		
		return Objects.hash(values);
	}
	

	public List<Double> getValues() {
		return values;
	}

}







