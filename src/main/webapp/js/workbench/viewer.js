function Viewer(config) {
	this.config = config;
	
	this.id = this.config.id;
}

Viewer.prototype.showImage = function(event, data) {
	var id = this.id;
	$(id+' iframe').attr('src', 'loading.htm');
	
//	$.ajax(data.bodyId+'.json', {
//		dataType: 'json',
//		success: function(data, status, xhr) {
//			console.log(data);
//		}
//	});
	
	setTimeout(function() {
//		var iframeWidth = $(id+' iframe').width();
//		var ratio = data.width / iframeWidth;
		$('#toolContent iframe').attr('src', data.bodyId+'_large');
//		$('#toolContent iframe').attr('height', data.height / ratio);
	}, 150);
};

Viewer.prototype.activate = function() {
	$(this.id).html('<iframe></iframe>');
	this.resize($(this.id).height(), $(this.id).width());
	eventManager.bind('tree.imageSelected', $.proxy(this.showImage, this));
};

Viewer.prototype.deactivate = function() {
	eventManager.unbind('tree.imageSelected', this.showImage);
};

Viewer.prototype.resize = function(height, width) {
	$(this.id+' iframe').height(height).width(width);
};