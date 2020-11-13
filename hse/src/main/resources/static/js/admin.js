

$(document).ready(function() {
	
	console.log("Administrators:");
	console.log(administrators);
	console.log("Experimenters:");
	console.log(experimenters);
	console.log("Participants:");
	console.log(participants);
});

function showAdministratorInputModal(admin) {
	 
	let method = "POST";
	
	if (admin != null) {
		
		method = "PUT";
		$("#administratorUserNameInput").val(admin.userName);
	}

	$("#submitAdministratorBtn").on("click", () => {
		
		let uName = $("#administratorUserNameInput").val().trim();
		let pwd = $("#administratorPasswordInput").val().trim();
		
		if (uName == "") {
			
			showErrorModal(m_error, m_missingUserName);
			return;
		}
		
		if (admin == null && pwd == "") {
			
			showErrorModal(m_error, m_missingPassword);
			return;
		}

		if (! admin) {
			
			admin = {
				userName: uName,
				password: pwd
			}
		}
		else {
			
			admin.userName = uName;
			
			if (pwd != "") {
				admin.password = pwd;
			}
		}
			
		submitUser(admin, "administrators", method);
	});
	
	$("#administratorInputModal").modal("show"); 
}

function showExperimenterInputModal(experimenter) { 
	
	let method = "POST"; 
	
	if (experimenter != null) {
		 
		method = "PUT";
		$("#experimenterUserNameInput").val(experimenter.userName);
	}
	
	$("#submitExperimenterBtn").on("click", () => {
		
		let uName = $("#experimenterUserNameInput").val().trim();
		let pwd = $("#experimenterPasswordInput").val().trim();
		
		if (uName == "") {
			
			showErrorModal(m_error, m_missingUserName);
			return;
		}
		
		if (experimenter == null && pwd == "") {
			
			showErrorModal(m_error, m_missingPassword);
			return;
		}

		if (! experimenter) {
			
			experimenter = {
				userName: uName,
				password: pwd
			}
		}
		else {
			
			experimenter.userName = uName;
			
			if (pwd != "") {
				experimenter.password = pwd;
			}
		}
			
		submitUser(experimenter, "experimenters", method);
	});
	
	$("#experimenterInputModal").modal("show"); 
}

function showAdminDeleteModal(admin) {
	
	showConfirmDeleteModal(admin.userName, () => { deleteUser(admin, "administrators"); });
}

function showExperimenterDeleteModal(experimenter) {
	
	showConfirmDeleteModal(experimenter.userName, () => { deleteUser(experimenter, "experimenters"); });
}  

function submitUser(user, category, method) { 
	
	let msg = "";
	
	if (category == "administrators") {
		msg = m_administrator;
	}
	else if (category == "experimenters") {
		msg = m_experimenter;
	}
	else if (category == "participants") {
		msg = m_participant;
	}
	
	let succMsg = msg + " " + user.userName + " " + m_saved + ".";
	let url = "/admin/" + category;
	
	$.ajax(url,
		{
			type: method,
			dataType: "json",
			contentType: 'application/json; charset=utf-8',
			data: JSON.stringify(user),
			success: () => {

				showInfoModal("", succMsg, () => { location.reload(); });
			},
			error: (err) => { 

				handleHttpError(err);
			}
		}
	);
}

function deleteUser(user, category) {
	
	let msg = "";
	
	if (category == "administrators") {
		msg = m_administrator;
	}
	else if (category == "experimenters") {
		msg = m_experimenter;
	}
	else if (category == "participants") {
		msg = m_participant;
	}
	
	let succMsg = msg + " " + user.userName + " " + m_deleted + ".";
	let url = "/admin/" + category;
	
	$.ajax(url,
		{
			type: "DELETE",
			dataType: "json",
			contentType: 'application/json; charset=utf-8',
			data: JSON.stringify(user),
			success: () => {

				showInfoModal("", succMsg, () => { location.reload(); });
			},
			error: (err) => { 

				handleHttpError(err);
			}
		}
	);
}






