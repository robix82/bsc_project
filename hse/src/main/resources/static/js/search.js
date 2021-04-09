
var stompClient = null;

var t = timeout;
var timer;

$(document).ready(function() {
	
	if (searchResultList != null) {
		
		$("#resultPropertiesDisplay").text(searchResultList.searchResults.length + " " + m_results);
		$("#search-input").val(searchResultList.queryString);
	}
	
	connectWebSocket();
	
	showTime(t);
	timer = setInterval(onTimeStep, 1000);
});

function onTimeStep() {
	
	t -= 1;
	
	$("#tParam").val(t);
	showTime(t);
	
	if (t == 0) {
		
		location.href = baseUrl + "logout";
	}
}

function showTime(time) {
	
	let minutes = Math.floor(time / 60);
	let seconds = time % 60;
	
	let minString = "";
	let secString = "";
	
	if (minutes < 10) {
		minString += "0";
	}
	
	if (seconds < 10) {
		secString += "0";
	}
	
	minString += minutes;
	secString += seconds;
	
	$("#remainingTime").text(minString + ":" + secString);
}

function sendBrowseEvent(searchResult) {
	
	$.ajax(baseUrl + "browse",
		{
			type: "POST",
			contentType: 'application/json; charset=utf-8',
			data: JSON.stringify(searchResult),
			success: () => {

				console.log("sent browse event");
			},
			error: (err) => { 

				handleHttpError(err);
			}
		}
	);
}

function connectWebSocket() {
		
    var socket = new SockJS(baseUrl + "statusInfo");
    stompClient = Stomp.over(socket);

    stompClient.connect({}, () => {

        stompClient.subscribe("/info", function (info) {
	
			if (info.body == "experiment_over") {
				
				location.href = baseUrl + "logout";
			}
			else {
				console.log(info);
			}
        });
    });
}

function disconnectWebSocket() {
	
    if (stompClient !== null) {
        stompClient.disconnect();
    }
}





