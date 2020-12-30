$(document).ready(function() {
	
	console.log("EXPERIMENT:");
	console.log(experiment);
	console.log("SUMMARY:");
	console.log(summary);
	
	$("#dateDisp").text(m_date + ": " + new Date(summary.dateConducted).toLocaleDateString());
	$("#durationDisp").text(m_duration + ": " + tString(summary.duration));
	
	displayGeneralStats();
	displayPerGroupStats();
});

function displayGeneralStats() {
	
	let parentDiv = document.getElementById("generalStats")
	
	addStatsDiv(summary.queriesPerUser, m_queriesPerUser, parentDiv);
	addStatsDiv(summary.clicksPerUser, m_clicksPerUser, parentDiv);
	addStatsDiv(summary.clicksPerQuery, m_clicksPerQuery, parentDiv);
	addStatsDiv(summary.timePerQuery, m_timePerQuery, parentDiv);
	addStatsDiv(summary.timePerClick, m_timePerClick, parentDiv);
}

function displayPerGroupStats() {
	
	
}

function addStatsDiv(stats, title, parent) {
	
	let container = document.createElement("div");
	container.classList.add("stats-div");
	
	let leftDiv = document.createElement("div");
	leftDiv.classList.add("stats-title");
	let rightDiv = document.createElement("div");
	rightDiv.classList.add("stats-details");
		
	let header = document.createElement("span");
	header.innerText = title + ": ";
	leftDiv.appendChild(header);
	
	let meanSpan = document.createElement("span");
	meanSpan.innerText = m_mean + ": " + stats.mean.toFixed(4);
	rightDiv.appendChild(meanSpan);
	
	let medianSpan = document.createElement("span");
	medianSpan.innerText = m_median + ": " + stats.median;
	rightDiv.appendChild(medianSpan);
	
	let stdDevSpan = document.createElement("span");
	stdDevSpan.innerText = m_stdDev + ": " + stats.standardDeviation.toFixed(4);
	rightDiv.appendChild(stdDevSpan);
	
	container.appendChild(leftDiv);
	container.appendChild(rightDiv);
	
	parent.appendChild(container);
}






