
var t = 0;
var running = false;
var timerId;

$(document).ready(function() {
	
	console.log(experiment);
	
	$("#startStopBtn").on("click", () => {
		startExperiment();
	});
	
	$("#evalBtn").hide();
});

function startExperiment() {
	
	$("#startStopBtn").off("click").on("click", () => {
		stopExperiment();
	});
	
	$("#startStopBtn").text(m_stop);
	
	$("#status-display").text(m_experimentRunning);
	
	timerId = setInterval(onTimeStep, 1000);
}

function stopExperiment() {

	clearInterval(timerId);
	
	$("#startStopBtn").prop("disabled", true);
	
	$("#status-display").text(m_experimentComplete);
	
	$("#evalBtn").show();
}

function onTimeStep() {
	
	$("#timer-display").text(tString(++t));
} 

function gotoEval() {
	
	location.href= "/experiments/eval/ui?expId=" + experiment.id;
}

function tString(sec) { 
	
	let h = parseInt(sec / 3600);
	sec = sec % 3600;
	let m = parseInt(sec / 60);
	sec = sec % 60;
	
	let tStr = "";
	
	if (h < 10) {
		tStr += "0";
	} 
	
	tStr += h + ":";
	
	if (m < 10) {
		tStr += "0";
	}
	
	tStr += m + ":";
	
	if (sec < 10) {
		tStr += "0"; 
	}
	
	tStr += parseInt(sec);
	
	return tStr;
}






