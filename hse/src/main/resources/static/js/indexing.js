
$(document).ready(function() {
		
	$("#urlListFileInput").on("change", () => {

		submitUrlFile();
	});
});

function submitUrlFile() {
	
	let formData = new FormData();
	
	if ($("#urlListFileInput")[0].files.length > 0) {
		
		formData.append("file", $("#urlListFileInput")[0].files[0]);
		let url = baseUrl + "indexing/urlLists";
		
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
		
		let url = baseUrl + "indexing/docCollections";
		
		$.ajax(url,
			{
				type: method,
				dataType: "json",
				contentType: 'application/json; charset=utf-8',
				data: JSON.stringify(collection),
				success: () => {
	
					location.reload(); 
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
		
		let url = baseUrl + "indexing/urlLists?fileName=" + fileName;
		
		$.ajax(url,
				{
					type: "DELETE",
					success: () => {
		
						location.reload();
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
	
	let url = baseUrl + "indexing/docCollections";
	
	$.ajax(url,
		{
			type: "DELETE",
			dataType: "json",
			contentType: 'application/json; charset=utf-8',
			data: JSON.stringify(collection),
			success: () => {
				location.reload();
			},
			error: (err) => { 

				handleHttpError(err);
			}
		}
	);
} 

function doIndex(collection) {
	
	$("#processingModal").modal("show");
	
	let url = baseUrl + "indexing/buildIndex";
	
	$.ajax(url,
			{
				type: "POST",
				dataType: "json",
				contentType: 'application/json; charset=utf-8',
				data: JSON.stringify(collection),
				success: (res) => {
	
					$("#processingModal").modal("hide");
					showIndexingResult(res);
				},
				error: (err) => {  
	
					$("#processingModal").modal("hide");
					handleHttpError(err);
				}
			}
	);
}

function showIndexingResult(res) {
	
	// set fields of indexingResultModal
	
	$("#irCollectionName").text(res.collectionName);
	$("#irUrlListName").text(res.urlListName);
	$("#irProcessed").text(res.processedUrls);
	$("#irIndexed").text(res.indexed);
	$("#irSkipped").text(res.skipped);
	$("#irTime").text(res.timeElapsed);
	
	$("#irOkBtn").on("click", () => { location.reload(); });
	
	$("#indexingResultModal").modal("show");
}









