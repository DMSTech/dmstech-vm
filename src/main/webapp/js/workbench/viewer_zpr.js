function ViewerZPR(config) {
	this.config = config;
	
	this.id = this.config.id;
	
	this.zpr = null;
}

ViewerZPR.prototype.showImage = function(event, data) {
	this.zpr = new zpr('zpr_viewer', {
		imageStacksURL: data.bodyId,
		width: data.width,
		height: data.height,
		marqueeImgSize: 125
	});
};

ViewerZPR.prototype.activate = function() {
	$(this.id).html('<div id="zpr_viewer"/>');
	this.resize($(this.id).height(), $(this.id).width());
	eventManager.bind('imageSelected', $.proxy(this.showImage, this));
};

ViewerZPR.prototype.deactivate = function() {
	eventManager.unbind('imageSelected', this.showImage);
};

ViewerZPR.prototype.resize = function(height, width) {
	$('#zpr_viewer').height(height).width(width);
};