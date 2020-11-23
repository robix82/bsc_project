
$(document).ready(function() {
	
	console.log("RESULTS:");
	console.log(searchResultList);
	
	if (searchResultList != null) {
		
		$("#resultPropertiesDisplay").text("Showing " + searchResultList.searchResults.length + " results");
		$("#search-input").val(searchResultList.queryString);
	}
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