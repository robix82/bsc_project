
var stompClient = null;

$(document).ready(function() {

	connectWebSocket();
});

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