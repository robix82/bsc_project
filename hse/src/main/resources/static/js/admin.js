

$(document).ready(function() {
	
	console.log("Administrators:");
	console.log(administrators);
	console.log("Experimenters:");
	console.log(experimenters);
	console.log("Participants:");
	console.log(participants);
	
	$("#newAdminBtn").on("click", () => {
		showAddAdministratorModal();
	});

});

function showAddAdministratorModal() {
	
	$("#submitAdministratorBtn").on("click", () => {
		
		let uName = $("#administratorUserNameInput").val().trim();
		let pwd = $("#administratorPasswordInput").val().trim();
		
		if (uName == "") {
			
			showErrorModal(m_error, m_missingUserName);
			return;
		}
		
		if (pwd == "") {
			
			showErrorModal(m_error, m_missingPassword);
			return;
		}

		let newAdmin = {
			userName: uName,
			password: pwd
		};
			
		postAdministrator(newAdmin);

	})
	
	$("#administratorInputModal").modal("show"); 
}

function showAdminEditModal(admin) {
	
	console.log("show edit modal");
}

function showAdminDeleteModal(admin) {
	
	showConfirmDeleteModal(admin.userName, () =>{ deleteAdministrator(admin); })
}

function postAdministrator(administrator) {
	
	let succMsg = m_administrator + " " + administrator.userName + " " + m_saved + ".";
	
	$.ajax("/admin/administrators",
		{
			type: "POST",
			dataType: "json",
			contentType: 'application/json; charset=utf-8',
			data: JSON.stringify(administrator),
			success: () => {

				showInfoModal("", succMsg, () => { location.reload(); });
			},
			error: (err) => { 

				handleHttpError(err);
			}
		}
	);
}

function deleteAdministrator(administrator) {
	
	let succMsg = m_administrator + " " + administrator.userName + " " + m_deleted + ".";
	
	$.ajax("/admin/administrators",
		{
			type: "DELETE",
			dataType: "json",
			contentType: 'application/json; charset=utf-8',
			data: JSON.stringify(administrator),
			success: () => {

				showInfoModal("", succMsg, () => { location.reload(); });
			},
			error: (err) => { 

				handleHttpError(err);
			}
		}
	);
}






