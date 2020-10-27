
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
			
			showErrorModal($("#m_error").text(), $("#m_missingUserName").text());
			return;
		}
		
		if (pwd == "") {
			
			showErrorModal($("#m_error").text(), $("#m_missingPassword").text());
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

function postAdministrator(administrator) {
	
	console.log("POST");
	console.log(administrator);
}






