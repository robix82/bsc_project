
$(document).ready(function() {
		
	$("#urlListFileInput").on("change", () => {

		submitUrlFile();
	});
});

function submitUrlFile() {
	
	let formData = new FormData();
	
	if ($("#urlListFileInput")[0].files.length > 0) {
		
		let succMsg = m_file + " " + $("#urlListFileInput").val().split("\\").pop() + " " + m_saved;
		
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

function showDocCollectionInputModal(collection) {
	
	let method = "POST";
	
	if (collection) {
		
		method = "PUT";
		
		$("#collectionNameInput").val(collection.name);
		$("#urlListSelect").val(collection.urlListName);
		$("#collectionLanguageSelect").val(collection.language);
	}
	
	$("#submitDocCollectionBtn").on("click", () => {
		
		
		
		let name = $("#collectionNameInput").val().trim();
		let urlListName = $("#urlListSelect").val().trim();
		let language = $("#collectionLanguageSelect").val().trim();
		
		if (name == "") {
			showErrorModal(m_error, m_missingCollectionName);
			return;
		}
		
		if (! collection) {
			collection = {};
		}
		
		collection.name = name;
		collection.urlListName = urlListName;
		collection.language = language;
		
		let succMsg = m_collection + " " + name + " " + m_saved_f
		
		$.ajax("/indexing/docCollections",
		{
			type: method,
			dataType: "json",
			contentType: 'application/json; charset=utf-8',
			data: JSON.stringify(collection),
			success: () => {

				showInfoModal("", succMsg, () => { location.reload(); });
			},
			error: (err) => {  

				handleHttpError(err);
			}
		}
	);
	});
	
	$("#docCollectionInputModal").modal("show");
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

function showCollectionDeleteModal(collection) {
	
	showConfirmDeleteModal(collection.name, () => { 
		deleteCollection(collection); 
	});
}

function deleteCollection(collection) {
	
	let succMsg = m_collection + " " + collection.name + " " + m_deleted_f + ".";
	
	$.ajax("/indexing/docCollections",
		{
			type: "DELETE",
			dataType: "json",
			contentType: 'application/json; charset=utf-8',
			data: JSON.stringify(collection),
			success: () => {

				showInfoModal("", succMsg, () => { location.reload(); });
			},
			error: (err) => { 

				handleHttpError(err);
			}
		}
	);
} 











