$(document).ready(function() {
	var orderer = new Orderer({id: '#orderParent'});
	
	function doResize() {
		var height = $(window).height() - 105;
		$('#orderParent').height(height);
		orderer.resize(height);
	}
	$(window).resize(doResize);
	
	orderer.activate();
	doResize();
	
	var proxyString = '/dmstech/proxy.jsp';
	var qry = $.rdf(opts);
	fetchTriples('http://'+window.location.host+proxyString+'?url=http://dms-data.stanford.edu/Stanford/kq131cs7229/ImageAnnotations.xml', qry, function() {
		var annotations = buildAllAnnos(qry);
		eventManager.trigger('tree.sequenceSelected', [annotations]);
	});
});