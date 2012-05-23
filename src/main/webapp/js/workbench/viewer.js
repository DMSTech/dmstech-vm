function Viewer(config) {
	this.config = config;
	
	this.id = this.config.id;
	
	this.current = null;
}

Viewer.prototype.showImage = function(event, data) {
	this.current = data;
	$(this.id+' iframe').attr('src', 'loading.htm');
	
//	$.ajax(data.bodyId+'.json', {
//		dataType: 'json',
//		success: function(data, status, xhr) {
//			console.log(data);
//		}
//	});
	
	setTimeout(function() {
//		var iframeWidth = $(id+' iframe').width();
//		var ratio = data.width / iframeWidth;
		$('#toolContent iframe').attr('src', data.imageURI+'_large');
//		$('#toolContent iframe').attr('height', data.height / ratio);
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