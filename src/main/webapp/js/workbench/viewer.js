function Viewer(config) {
	this.config = config;
	
	this.id = this.config.id;
}

Viewer.prototype.showImage = function(event, data) {
	var id = this.id;
	$(id+' iframe').attr('src', 'loading.htm');
	setTimeout(function() {
		var iframeWidth = $(id+' iframe').width();
		var ratio = data.width / iframeWidth;
		$('#toolContent iframe').attr('src', data.bodyId);
		$('#toolContent iframe').attr('height', data.height / ratio);
	}, 150);
};

Viewer.prototype.activate = function() {
	$(this.id).html('<iframe></iframe>');
	eventManager.bind('tree.imageSelected', $.proxy(this.showImage, this));
};

Viewer.prototype.deactivate = function() {
	eventManager.unbind('tree.imageSelected', this.showImage);
};