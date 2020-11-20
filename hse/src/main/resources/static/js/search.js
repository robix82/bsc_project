
$(document).ready(function() {
	
	console.log("RESULTS:");
	console.log(searchResultList);
	
	if (searchResultList != null) {
		
		$("#resultPropertiesDisplay").text("Showing " + searchResultList.searchResults.length + " results");
		$("#search-input").val(searchResultList.queryString);
	}
});

