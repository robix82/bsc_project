
$(document).ready(function() {
	
	console.log("RESULTS:");
	console.log(searchResultList);
	
	if (searchResultList != null) {
		
		
		
		$("#search-input").val(searchResultList.queryString);
	}
});

