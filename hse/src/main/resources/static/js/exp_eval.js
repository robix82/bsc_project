$(document).ready(function() {
	
	console.log("EXPERIMENT:");
	console.log(experiment);
	console.log("SUMMARY:");
	console.log(summary);
	
	$("#dateDisp").text(m_date + ": " + new Date(summary.dateConducted).toLocaleDateString());
	$("#durationDisp").text(m_duration + ": " + tString(summary.duration));
});


