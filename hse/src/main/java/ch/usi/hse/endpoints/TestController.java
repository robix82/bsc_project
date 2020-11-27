package ch.usi.hse.endpoints;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import ch.usi.hse.experiments.DataStats;

// temporary endpoint for testing

@Controller
@CrossOrigin
public class TestController {

	@GetMapping("/test")
	public ResponseEntity<DataStats> test() {
		
		int n = 23;
		double total = 2.5;
		double mean = 1.2;
		double median = 1.0;
		double stdDev = 0.3;
		
		DataStats stats = new DataStats(n, total, mean, median, stdDev);
		
		return new ResponseEntity<>(stats, HttpStatus.OK);
	}
}
