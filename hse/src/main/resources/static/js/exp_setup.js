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