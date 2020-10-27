
$(document).ready(function() {
    
	setupLangSelect();
});

function setupLangSelect() {
    
    $("#lang_it").click(function() {
    	updateLangSelect('it');
    });
    
    $("#lang_en").click(function() {
    	updateLangSelect('en');
    });
};

function updateLangSelect(lang) {
	
	window.location.replace('?lang=' + lang);
}