


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

function showInfoModal(header, text, onOk) {
	
	$("#infoHeader").text(header);
	$("#infoText").text(text);
	$("#infoModal").modal("show");
	
	if (onOk) {
		
		$("#infoOkBtn").on("click", onOk);
	}
}

function showErrorModal(header, text) {
	
	$("#errorHeader").text(header);
	$("#errorText").text(text);
	$("#errorModal").modal("show");
}

function showConfirmDeleteModal(obj, onYes) { 
	
	console.log("show confirm delete " + obj);
	
	$("#deleteWarningText").text(m_delete + " " + obj + "?");
	$("#deleteYesBtn").on("click", onYes);
	
	$("#confirmDeleteModal").modal("show");
}

// HTTP ERROR HANDLING

function handleHttpError(err) {
	
	
	
	if (err.responseJSON &&    // check error is instance of ApiError
		err.responseJSON.status && 
		err.responseJSON.errorType) { 
		
		handleApiError(err.responseJSON);
	}
	else {

		console.log(err);
		showErrorModal(m_error, m_generalError);
	}
}

function handleApiError(errJson) {
	
	if (errJson.errorType == "UserExistsException") {
		
		showErrorModal(m_error, m_userExists);
	}
	else if (errJson.errorType == "NoSuchUserException") {
		
		showErrorModal(m_error, m_userNotFound);
	}
	else {

		console.log(err);
		showErrorModal(m_error, m_generalError);
	}
}







