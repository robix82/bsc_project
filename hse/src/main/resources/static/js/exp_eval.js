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
	
	let mainDiv = document.getElementById("perGroupStats");
	
	summary.groupSummaries.forEach((gs) => {
		
		let groupDiv = document.createElement("div");
		groupDiv.classList.add("eval-secton-content");
		
		
		
		let header = document.createElement("h3");
		header.innerText = gs.groupName + " (" + gs.participants + " " + m_nParticipants + ")"; 
		
		let statsDiv = document.createElement("div");
		statsDiv.classList.add("eval-secton-content");
		
		let queriesDiv = document.createElement("div");
		let clicksDiv = document.createElement("div");
		queriesDiv.innerText = m_totalQueries + ": " + gs.totalQueries;
		clicksDiv.innerText = m_totalClicks + ": " + gs.totalClicks;
		statsDiv.appendChild(queriesDiv);
		statsDiv.appendChild(clicksDiv);
		
		addStatsDiv(gs.queriesPerUser, m_queriesPerUser, statsDiv);
		addStatsDiv(gs.clicksPerUser, m_clicksPerUser, statsDiv);
		addStatsDiv(gs.clicksPerQuery, m_clicksPerQuery, statsDiv);
		addStatsDiv(gs.timePerQuery, m_timePerQuery, statsDiv);
		addStatsDiv(gs.timePerClick, m_timePerClick, statsDiv);
		
		let subHeader = document.createElement("h4");
		subHeader.innerText = m_collectionDistributions + ":";
		
		statsDiv.appendChild(subHeader);
		
		let docDistributionsContainer = document.createElement("div");
		docDistributionsContainer.classList.add("doc-distributions-div");
		
		gs.collectionNames.forEach((collection) => {
			
			let docDistributionItem = document.createElement("div");
			docDistributionItem.classList.add("doc-distributions-item");
			
			let collectionHeader = document.createElement("h5");
			collectionHeader.innerText = collection;
			
			docDistributionItem.appendChild(collectionHeader);
			
			let clickStats = gs.clicksPerDocCollection[collection];
			let timeStats = gs.timePerDocCollection[collection];
			
			addStatsDiv(clickStats, m_clicks, docDistributionItem);
			addStatsDiv(timeStats, m_totalTime, docDistributionItem);
			
			docDistributionsContainer.appendChild(docDistributionItem);
		})
		
		statsDiv.appendChild(docDistributionsContainer);
		
		groupDiv.appendChild(header);
		groupDiv.appendChild(statsDiv);
		mainDiv.appendChild(groupDiv);
	});
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






