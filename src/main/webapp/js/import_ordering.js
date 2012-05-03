$(document).ready(function() {
	var host = window.location.host;
	var path = window.location.pathname.match(/^.*\//)[0];
	
	var orderer = new Orderer({id: '#orderParent'});
	
	function doResize() {
		var height = $(window).height() - 105;
		$('#orderParent').height(height);
		orderer.resize(height);
	}
	$(window).resize(doResize);
	
	orderer.activate();
	doResize();
	
	var qry = $.rdf(opts);
	fetchTriples('http://'+host+path+'proxy.jsp?url=http://dms-data.stanford.edu/Stanford/kq131cs7229/ImageAnnotations.xml', qry, function() {
		var annotations = buildAllAnnos(qry);
		eventManager.trigger('sequenceSelected', [annotations]);
	});
});