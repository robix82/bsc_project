
var stompClient = null;

$(document).ready(function() {
	
	if (searchResultList != null) {
		
		$("#resultPropertiesDisplay").text("Showing " + searchResultList.searchResults.length + " results");
		$("#search-input").val(searchResultList.queryString);
	}
	
	connectWebSocket();
});

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





