
var t = 0;
var t0;
var running = false;
var timerId;

var stompClient = null;

$(document).ready(function() {
	
//	console.log(experiment);

	connectWebSocket();
	
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
		
		if (experiment.mode == "QUALTRICS") {
			
			t0.setHours(t0.getHours() + 1);
		}
		
		onTimeStep();
		timerId = setInterval(onTimeStep, 1000);
		
		$("#startStopBtn").text(m_stop);
		$("#status-display").text(m_experimentRunning)	
				
		$("#startStopBtn").off("click").on("click", () => {
			stopExperiment();
		});
	}
	else if (experiment.status == "COMPLETE") {
		
		t = Math.round(experiment.duration);
		$("#timer-display").text(tString(t));
		
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
		
		if (experiment.mode == "QUALTRICS") {
			
			t0.setHours(t0.getHours() + 1);
		}
		
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
		
		location.reload();
	});
}

function onTimeStep() {
	
	t = (new Date() - t0) / 1000;
	
	$("#timer-display").text(tString(++t));
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

function connectWebSocket() {
		
    var socket = new SockJS(baseUrl + "statusInfo");
    stompClient = Stomp.over(socket);

    stompClient.connect({}, () => {

        stompClient.subscribe("/userActions", function (res) {

			experiment = JSON.parse(res.body);
			updateInfoTables();
        });

		stompClient.subscribe("/userAdded", () => {
			
			location.reload();
		})
    });
}

function disconnectWebSocket() {
	
    if (stompClient !== null) {
        stompClient.disconnect();
    }
}




