


$(document).ready(function() {
    
	setupLangSelect();
});

// LANGUAGE SETTINGS

function setupLangSelect() {
    
    $("#lang_it").click(function() {
    	updateLangSelect('it');
    });
    
    $("#lang_en").click(function() {
    	updateLangSelect('en');
    });
};

function updateLangSelect(lang) {
	
	window.location.replace('?lang=' + lang);
}

// MODAL SHOW FUNCTIONS

function showInfoModal(header, text) {
	
	$("#infoHeader").text(header);
	$("#infoText").text(text);
	$("#infoModal").modal("show");
}

function showErrorModal(header, text) {
	
	$("#errorHeader").text(header);
	$("#errorText").text(text);
	$("#errorModal").modal("show");
}







