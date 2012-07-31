var getParameterByName = function(name) {
	name = name.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
	var regexS = "[\\?&]" + name + "=([^&#]*)";
	var regex = new RegExp(regexS);
	var results = regex.exec(window.location.search);
	if (results == null) {
		return null;
	} else {
		return decodeURIComponent(results[1].replace(/\+/g, " "));
	}
};

var xmlToString = function(xmlData) {
	var xmlString = '';
	try {
		if (window.ActiveXObject) {
			xmlString = xmlData.xml;
		} else {
			xmlString = (new XMLSerializer()).serializeToString(xmlData);
		}
	} catch (e) {
		return '';
	}
	return xmlString;
};