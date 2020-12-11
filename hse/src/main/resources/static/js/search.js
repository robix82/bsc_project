
var stommpClient = null;

$(document).ready(function() {
	
	console.log("RESULTS:");
	console.log(searchResultList);
	
	if (searchResultList != null) {
		
		$("#resultPropertiesDisplay").text("Showing " + searchResultList.searchResults.length + " results");
		$("#search-input").val(searchResultList.queryString);
	}
	
	connectWebSocket();
});

function sendBrowseEvent(searchResult) {
	
	$.ajax("/browse",
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
	
	
	
    var socket = new SockJS("/statusInfo");
    stompClient = Stomp.over(socket);

    stompClient.connect({}, function (frame) {

        console.log('Connected: ' + frame);

        stompClient.subscribe(baseUrl + "info", function (info) {
	
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

    console.log("Disconnected");
}





