$(document).ready(function() {
	
	console.log(experiment);
	console.log(docCollections);
	console.log(configFiles);
	
	$("#configFileInput").on("change", () => {

		submitConfigFile();
	});
}); 

function submitConfigFile() {
	
	let formData = new FormData();
	
	if ($("#configFileInput")[0].files.length > 0) {
		
		let succMsg = m_file + " " + $("#configFileInput").val().split("\\").pop() + " " + m_saved;
		
		formData.append("file", $("#configFileInput")[0].files[0]);
		
		$.ajax("/experiments/testGroups/config/ul",
			   {
			    type : "POST",
			    data : formData,
			    processData: false, 
			    contentType: false,  
			    success : () => {
					showInfoModal("", succMsg, () => { location.reload(); });
			    },
				error: (err) => {
					handleHttpError(err);	
				}
		});
	}
}

function showConfigFileDeleteModal(fileName) {
	
	showConfirmDeleteModal(fileName, () => { 
		
		let url = "/experiments/testGroups/config?fileName=" + fileName;
		let succMsg = m_file + " " + fileName + " " + m_deleted;
		
		$.ajax(url,
				{
					type: "DELETE",
					success: () => {
		
						showInfoModal("", succMsg, () => { location.reload(); });
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
	
	let url = "/experiments/testGroups/config?configFileName=" + file;
	
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

function showDocCollectionSelectModal(testGroup) {
	
	console.log("showDocCollectionSelectModal(" + testGroup.name + ")");
}

function showParticipantInputModal(testGroup) {
	
	console.log("showParticipantInputModal(" + testGroup.name + ")");
}









