function Viewer(config) {
	this.config = config;
	
	this.id = this.config.id;
	
	this.current = null;
	
	this.host = window.location.host;
	this.path = window.location.pathname.match(/^.*\//)[0];
	
	this.djatokaURL = null;
	$.ajax({
		url: 'http://'+this.host+this.path+'sc/lookup/djatoka',
		type: 'GET',
		success: $.proxy(function (data, status, xhr) {
			this.djatokaURL = data;
		}, this),
		error: function() {
			alert('There was an error getting the djatoka URL.');
		}
	});
}

Viewer.prototype.showImage = function(event, data) {
	this.current = data;
	$(this.id+' iframe').attr('src', 'loading.htm');
	
	setTimeout(function() {
		var uri = data.imageURI;
		if (uri.indexOf('stacks') != -1) {
			uri += '_large';
		} else if (uri.indexOf('.jp2')) {
			var width = $(this.id+' iframe').width();
			var height = $(this.id+' iframe').height();
			uri = this.djatokaURL+
				'?url_ver=Z39.88-2004'+
				'&rft_id='+uri+
				'&svc_id=info:lanl-repo/svc/getRegion'+
				'&svc_val_fmt=info:ofi/fmt:kev:mtx:jpeg2000'+
				'&svc.scale='+width+','+height;
		}
		$('#toolContent iframe').attr('src', uri);
	}, 150);
};

Viewer.prototype.activate = function() {
	$(this.id).html(''+
	'<iframe></iframe>'+
	'<div class="bottomButtons clear">'+
		'<button id="tpenTranscribe">Transcribe in TPen</button>'+
	'</div>');
	$('#tpenTranscribe').button().click($.proxy(function() {
		var url = 'http://'+window.location.host+'/TPEN/parkerRedirect.jsp?canvas='+this.current.canvasURI;
		window.open(url, 'tpen');
	}, this));
	
	this.resize($(this.id).height(), $(this.id).width());
	eventManager.bind('imageSelected', $.proxy(this.showImage, this));
};

Viewer.prototype.deactivate = function() {
	eventManager.unbind('imageSelected', this.showImage);
};

Viewer.prototype.resize = function(height, width) {
	$(this.id+' iframe').height(height-40).width(width);
};