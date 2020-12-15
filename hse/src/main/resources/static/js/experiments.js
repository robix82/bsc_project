
$(document).ready(function() {
	
//	console.log(experiments);
//	console.log(experimenters);
//  console.log(administrators);
	
	experiments.forEach((experiment) => {
		
		setupButtons(experiment);
		
		let tdId = "exp_creationDate_" + experiment.id;
		let dateString = new Date(experiment.dateCreated).toLocaleDateString();
		let td = document.getElementById(tdId);
		td.innerText = dateString;
	});
});



function setupButtons(experiment) {
	
	let expId = experiment.id;
	let expStatus = experiment.status;
	let configBtnId = "#exp_configBtn_" + expId;
	let runBtnId = "#exp_runBtn_" + expId;
	let evalBtnId = "#exp_evalBtn_" + expId;
	
	$(runBtnId).prop("disabled", true);
	$(evalBtnId).prop("disabled", true);
	
	$(configBtnId).on("click", () => {
		location.href= baseUrl + "experiments/setup/ui?expId=" + expId;
	});
	
	if (expStatus == "READY" ||expStatus == "RUNNING" || expStatus == "COMPLETE") {
		
		$(runBtnId).prop("disabled", false);
		
		$(runBtnId).on("click", () => {
			location.href= baseUrl + "experiments/run/ui?expId=" + expId;
		});
	}
	
	if (expStatus == "COMPLETE") {
		
		$(evalBtnId).prop("disabled", false);
	
		$(evalBtnId).on("click", () => {
			location.href= baseUrl + "experiments/eval/ui?expId=" + expId;
		});
	}
}

function getExperimenter(id) {
	
	for (let i = 0; i < experimenters.length; i++) {
		
		let experimenter = experimenters[i];
		
		if (experimenter.id == id) {
			return experimenter;
		}
	}
	
	return null;
}

function showExperimentInputModal(experiment) {
	
	if (experimenters.length == 0) {
		
		showErrorModal(m_error, m_noExperimenter);
		return;
	}
	
	let method = "POST";
	
	if (experiment) {
		
		method = "PUT";
		
		$("#experimentTitleInput").val(experiment.title);
		$("#experimenterSelect").val(experiment.experimenterId);
	}
	else {
		experiment = {};
	}
	
	$("#submitExperimentBtn").on("click", () => {
		
		let title = $("#experimentTitleInput").val().trim();
		
		if (title == "") {
			
			showErrorModal(m_error, m_missingTitle);
			return;
		}
		
		experiment.title = title;
		experiment.mode = $("#modeSelect").val();
		let experimenter = getExperimenter($("#experimenterSelect").val());
		experiment.experimenterId = experimenter.id;
		experiment.experimenterName = experimenter.userName;
		submitExperiment(experiment, method);
	});
	
	$("#experimentInputModal").modal("show");
}

function showExperimentDeleteModal(experiment) {
	
	showConfirmDeleteModal(experiment.title, () => { 
		
		deleteExperiment(experiment); 
	});
}

function submitExperiment(experiment, method) {

	let url = baseUrl + "experiments/";
	
	$.ajax(url,
		{
			type: method,
			dataType: "json",
			contentType: 'application/json; charset=utf-8',
			data: JSON.stringify(experiment),
			success: () => {

				location.reload();
			},
			error: (err) => { 

				handleHttpError(err);
			}
		}
	);
}

function deleteExperiment(experiment) {

	let url = baseUrl + "experiments/"
	
	$.ajax(url,
		{
			type: "DELETE",
			dataType: "json",
			contentType: 'application/json; charset=utf-8',
			data: JSON.stringify(experiment),
			success: () => {

				location.reload();
			},
			error: (err) => { 

				handleHttpError(err);
			}
		}
	);
}
















