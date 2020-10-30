
$(document).ready(function() {
	
	console.log(urlLists);
		
	$("#urlListFileInput").on("change", () => {
		$("#urlListFilePreview").val($("#urlListFileInput").val().split("\\").pop());
	});
});

function submitUrlFile() {
	
	let formData = new FormData();
	
	if ($("#urlListFileInput")[0].files.length > 0) {
		
		let succMsg = m_file + " " + $("#urlListFilePreview").val() + " " + m_saved;
		
		formData.append("file", $("#urlListFileInput")[0].files[0]);
		
		$.ajax("/indexing/urlLists",
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

function showUrlListDeleteModal(fileName) {
	
	showConfirmDeleteModal(fileName, () => { 
		
		let url = "/indexing/urlLists?fileName=" + fileName;
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













