$(document).ready(function() {
	
	console.log(experiment);
	console.log(docCollections);
	console.log(configFiles);
	
	$("#configFileInput").on("change", () => {

		submitConfigFile();
	});
}); 

function getDocCollection(collectionId) {
	
	for (let i = 0; i < docCollections.length; i++) {
		
		let collection = docCollections[i];
		
		if (collection.id == collectionId) {
			return collection;
		}
	}
	
	return null;
} 

function submitConfigFile() {
	
	let formData = new FormData();
	
	if ($("#configFileInput")[0].files.length > 0) {
		
		formData.append("file", $("#configFileInput")[0].files[0]);
		let url = baseUrl + "/experiments/testGroups/config/ul";
		
		$.ajax(url,
			   {
			    type : "POST",
			    data : formData,
			    processData: false, 
			    contentType: false,  
			    success : () => {
					location.reload();
			    },
				error: (err) => {
					handleHttpError(err);	
				}
		});
	}
}

function showConfigFileDeleteModal(fileName) {
	
	showConfirmDeleteModal(fileName, () => { 
		
		let url = baseUrl + "experiments/testGroups/config?fileName=" + fileName;
		
		$.ajax(url,
				{
					type: "DELETE",
					success: () => {
		
						location.reload();;
					},
					error: (err) => { 
		
						handleHttpError(err);
					}
				}
		);
	});
}

function showFileConfigModal() {
	
	let file = $("#configFileSelect").val();
	
	$("#configureBtn").on("click", () => {
		
		submitFileConfig(file);
	});
	
	$("#applyConfigFileModal").modal("show");
}

function submitFileConfig(file) {
	
	console.log("submitting file config " + file);
	
	let url = baseUrl + "experiments/testGroups/config?configFileName=" + file;
	
	$.ajax(url,
		{
			type: "POST",
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

function showTestGroupInputModal() {
	
	$("#submitTestGroupBtn").on("click", () => {
		
		let gName = $("#testGroupNameInput").val().trim();
		
		if (gName == "") {
			
			showErrorModal(m_error, m_missingName);
			return;
		}
		
		let newGroup = {
			name: gName,
			experimentId: experiment.id,
			experimentTitle: experiment.title		
		};
		
		submitTestGroup(newGroup, "POST");
	});
	
	$("#testGroupInputModal").modal("show");
}

function showTestGroupDeleteModal(testGroup) {
	
	showConfirmDeleteModal(testGroup.name, () => { 
		
		submitTestGroup(testGroup, "DELETE"); 
	});
}

function showDocCollectionSelectModal(testGroup) {
	
	console.log("showDocCollectionSelectModal(" + testGroup.name + ")");
	
	$("#selctCollectionBtn").on("click", () => {
		
		let collection = getDocCollection($("#docCollectionSelect").val());
		testGroup.docCollections.push(collection);
		
		submitTestGroup(testGroup, "PUT");
	});
		
	$("#selectDocCollectionModal").modal("show");
}

function showParticipantInputModal(testGroup) {

	$("#submitParticipantBtn").on("click", () => {
		
		let uName = $("#participantUserNameInput").val().trim();
		let pwd = $("#participantPasswordInput").val().trim();
		
		if (uName == "") { 		
			showErrorModal(m_error, m_missingUserName);
			return;
		}
		
		if (pwd == "") {			
			showErrorModal(m_error, m_missingPassword);
			return;
		}
		
		let newParticipant = {
			userName: uName,
			password: pwd
		}
		
		testGroup.participants.push(newParticipant);
		
		submitTestGroup(testGroup, "PUT");
	});
	
	$("#participantInputModal").modal("show");
}

function showParticipantDeleteModal(testGroup, participant) {
	
	showConfirmDeleteModal(participant.userName, () => { 
		
		let idx = 0;
		let participants = testGroup.participants;
		
		for (idx = 0; idx < participants.length; idx++) {
			
			if (participants[idx].id == participant.id) {
				break;
			}
		}
		
		participants.splice(idx, 1);
		
		submitTestGroup(testGroup, "PUT"); 
	});
}

function removeDocCollection(testGroup, collectionId) {
	 
	let collections = testGroup.docCollections;
	let idx = 0;

	for (idx = 0; idx < collections.length; idx++) {
		
		if (collections[idx].id == collectionId) {
			break;
		}
	}
	
	collections.splice(idx, 1);
	
	submitTestGroup(testGroup, "PUT");
}

function submitTestGroup(testGroup, method) {
	
	let url = baseUrl + "/experiments/testGroups";
	
	$.ajax(url,
		{
			type: method,
			dataType: "json",
			contentType: 'application/json; charset=utf-8',
			data: JSON.stringify(testGroup),
			success: () => {
				location.reload();
			},
			error: (err) => { 

				handleHttpError(err);
			}
		}
	);
}
 









