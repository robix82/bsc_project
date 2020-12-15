
var t = 0;
var t0;
var running = false;
var timerId;

$(document).ready(function() {
	
	console.log(experiment);
	
	t = 0;
	$("#timer-display").text(tString(t));
	
	if (experiment.status == "READY") {
		
		$("#startStopBtn").on("click", () => {
			startExperiment();
		});
		
		$("#status-display").text(m_experimentReady);
	}
	else if (experiment.status == "RUNNING") {
		
		t0 = new Date(experiment.startTime);
		onTimeStep();
		timerId = setInterval(onTimeStep, 1000);
		
		$("#startStopBtn").text(m_stop);
		$("#status-display").text(m_experimentRunning)	
				
		$("#startStopBtn").off("click").on("click", () => {
			stopExperiment();
		});
	}
	else if (experiment.status == "COMPLETE") {
		
		$("#startStopBtn").text(m_reset);
		
		$("#startStopBtn").on("click", () => {
			resetExperiment();
		});
		
		$("#status-display").text(m_experimentComplete);
	}
	else {
		
		showErrorModal(m_error, m_experimentNotReady, () => {
			location.href = baseUrl + "experiments/setup/ui?expId=" + experiment.id;
		});
	}	
	
	updateInfoTables();
});

function updateInfoTables() {
	
	experiment.testGroups.forEach((group) => {
		
		group.participants.forEach((participant) => {
			
			let pId = participant.id;
			let status = "offline";
			$("#p_status_" + pId).css("color", "red");
			
			if (participant.online) {
				status = "online";
				$("#p_status_" + pId).css("color", "green");
			}
			
			$("#p_status_" + pId).text(status);
			$("#p_queries_" + pId).text(participant.queryCount);
			$("#p_clicks_" + pId).text(participant.clickCount);
		});
	});
}

function startExperiment() {
	
	submitExperiment("start", (res) => {
	
		experiment = res;
		t0 = new Date(experiment.startTime);
		t0.setSeconds(t0.getSeconds() + 1);
		timerId = setInterval(onTimeStep, 1000);
		
		$("#startStopBtn").text(m_stop);
		$("#status-display").text(m_experimentRunning)	
				
		$("#startStopBtn").off("click").on("click", () => {
			stopExperiment();
		});
	});
}

function stopExperiment() { 
	
	submitExperiment("stop", (res) => {
		
		experiment= res;
		updateInfoTables();
		clearInterval(timerId);
	
		$("#startStopBtn").text(m_reset);
		
		$("#startStopBtn").off("click").on("click", () => {
			showWarningModal(m_experimentResetWarning, resetExperiment);
		});
		
		$("#status-display").text(m_experimentComplete);	
		$("#evalBtn").show();
	});	
	
	setTimeout(pollExperimentUpdate, 500);
}

function resetExperiment() {

	submitExperiment("reset", (res) => {
		
		experiment = res;
		updateInfoTables();
		t = 0;
		$("#timer-display").text(tString(t));
		
		$("#startStopBtn").text(m_start);	
		
		$("#startStopBtn").off("click").on("click", () => {
				startExperiment();
		});
		
		$("#status-display").text(m_experimentReady);	
		$("#evalBtn").hide();
		
		updateInfoTables();
	});
}

function onTimeStep() {
	
	t = (new Date() - t0) / 1000;
	
	$("#timer-display").text(tString(++t));
	pollExperimentUpdate();
} 

function submitExperiment(action, onSuccess) {
	
	let url = baseUrl + "experiments/" + action;
	
	$.ajax(url,
		{
			type: "POST",
			dataType: "json",
			contentType: 'application/json; charset=utf-8',
			data: JSON.stringify(experiment),
			success: (res) => {

				onSuccess(res);
			},
			error: () => { 

				console.log("unable to fetch update");
			}
		}
	);
}

function pollExperimentUpdate() {
	
	let url = baseUrl + "experiments/?id=" + experiment.id;
	
	$.ajax(url,
		{
			type: "GET",
			dataType: "json",

			success: (res) => {

				experiment = res;
				updateInfoTables();
			},
			error: (err) => { 

				handleHttpError(err);
			}
		}
	);
}






