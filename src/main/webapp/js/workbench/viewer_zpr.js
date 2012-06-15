function ViewerZPR(config) {
	this.config = config;
	
	this.id = this.config.id;
	
	this.zpr = null;
	
	this.djatokaURL = null;
	
	var host = window.location.host;
	var path = window.location.pathname.match(/^.*\//)[0];
	$.ajax({
		url: 'http://'+host+path+'sc/lookup/djatoka',
		type: 'GET',
		success: $.proxy(function (data, status, xhr) {
			this.djatokaURL = data;
		}, this),
		error: function() {
			alert('There was an error getting the djatoka URL.');
		}
	});
}

ViewerZPR.prototype.showImage = function(event, data) {
	$('#zpr_viewer').empty();
	
	var type = 'djatoka';
	if (data.imageURI.indexOf('stacks') != -1) {
		type = 'img';
	}
	
	this.zpr = new zpr('zpr_viewer', {
		imageStacksURL: data.imageURI,
		jp2URL: data.imageURI,
		djatokaURL: this.djatokaURL,
		width: data.width,
		height: data.height,
		marqueeImgSize: 125,
		type: type
	});
};

ViewerZPR.prototype.activate = function() {
	$(this.id).html('<div id="zpr_viewer"/>');
	this.resize($(this.id).height(), $(this.id).width());
	eventManager.bind('imageSelected', $.proxy(this.showImage, this));
};

ViewerZPR.prototype.deactivate = function() {
	$('#zpr_viewer').empty();
	eventManager.unbind('imageSelected', this.showImage);
};

ViewerZPR.prototype.resize = function(height, width) {
	$('#zpr_viewer').height(height).width(width);
};