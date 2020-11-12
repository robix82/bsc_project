
$(document).ready(function() {
	
	console.log(experiments);
	console.log(experimenters);
	
	experiments.forEach((experiment) => {
		
		enableButtons(experiment);
		
		let tdId = "exp_creationDate_" + experiment.id;
		let dateString = new Date(experiment.dateCreated).toLocaleDateString();
		let td = document.getElementById(tdId);
		td.innerText = dateString;
	});
});



function enableButtons(experiment) {
	
	
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
	
	let succMsg = m_experiment + " " + experiment.title + " " + m_saved + ".";
	
	$.ajax("/experiments/",
		{
			type: method,
			dataType: "json",
			contentType: 'application/json; charset=utf-8',
			data: JSON.stringify(experiment),
			success: () => {

				showInfoModal("", succMsg, () => { location.reload(); });
			},
			error: (err) => { 

				handleHttpError(err);
			}
		}
	);
}

function deleteExperiment(experiment) {
	
	let succMsg = m_experiment + " " + experiment.title + " " + m_deleted + ".";

	
	$.ajax("/experiments/",
		{
			type: "DELETE",
			dataType: "json",
			contentType: 'application/json; charset=utf-8',
			data: JSON.stringify(experiment),
			success: () => {

				showInfoModal("", succMsg, () => { location.reload(); });
			},
			error: (err) => { 

				handleHttpError(err);
			}
		}
	);
}
















