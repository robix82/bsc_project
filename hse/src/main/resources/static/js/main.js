


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
	
	let url = window.location.href;
	let langParam = "lang=" + lang;
	
	if (url.includes("lang=")) {
		url = url.substr(0, url.length - 8);
	}
	
	if (url.includes("?")) {
		url += "&";
	}
	else {
		url += "?";
	}
	
	url += langParam;
	
	window.location.href = url;
}

// UTILITIES

function tString(sec) { 
	
	let h = parseInt(sec / 3600);
	sec = sec % 3600;
	let m = parseInt(sec / 60);
	sec = sec % 60;
	
	let tStr = "";
	
	if (h < 10) {
		tStr += "0";
	} 
	
	tStr += h + ":";
	
	if (m < 10) {
		tStr += "0";
	}
	
	tStr += m + ":";
	
	if (sec < 10) {
		tStr += "0"; 
	}
	
	tStr += parseInt(sec);
	
	return tStr;
}

// MODAL SHOW FUNCTIONS

function showInfoModal(header, text, onOk) {
	
	$("#infoHeader").text(header);
	$("#infoText").text(text);
	$("#infoModal").modal("show");
	
	if (onOk) {
		
		$("#infoOkBtn").off("click").on("click", onOk);
	}
}

function showErrorModal(header, text, next) {
	
	$("#errorHeader").text(header);
	$("#errorText").text(text);
	$("#errorModal").modal("show");
	
	if (next) {
		
		$("#errorOkBtn").off("click").on("click", () => {
			next();
		}) 
	}
}

function showWarningModal(text, onConfirm) {
	
	$("#warningText").text(text);
	
	$("#warningText").off("click").on("click", () => {
		onConfirm();
	})
}

function showConfirmDeleteModal(obj, onYes) { 
	
	console.log("show confirm delete " + obj);
	
	$("#deleteWarningText").text(m_delete + " " + obj + "?");
	$("#deleteYesBtn").off("click").on("click", onYes);
	
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
	
	let errType = errJson.errorType;
	
	if (errType == "UserExistsException") {
		
		showErrorModal(m_error, m_userExists);
	}
	else if (errType == "NoSuchUserException") {
		
		showErrorModal(m_error, m_userNotFound);
	}
	else if (errType == "FileReadException") {
		
		showErrorModal(m_error, m_fileReadError);
	}
	else if (errType == "FileWriteException") {
		
		showErrorModal(m_error, m_uploadError);
	}
	else if (errType == "NoSuchFileException") {
		
		showErrorModal(m_error, m_fileNotFound);
	}
	else if (errType == "DocCollectionExistsException") {
		
		showErrorModal(m_error, m_collectionExists);
	}
	else if (errType == "NoSuchCollectionException") {
		
		showErrorModal(m_error, m_collectionNotFound);
	}
	else if (errType == "ExperimentExistsException") {
		
		showErrorModal(m_error, m_experimentExists);
	}
	else if (errType == "NoSuchExperimentException") {
		
		showErrorModal(m_error, m_experimentNotFound);
	}
	else if (errType == "ConfigParseExeption") {
		
		showErrorModal(m_error, m_invalidConfigFile + ": " + errJson.errorMessage);
	}
	
	else {

		console.log(err);
		showErrorModal(m_error, m_generalError);
	}
}







