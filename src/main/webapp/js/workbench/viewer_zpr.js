function ViewerZPR(config) {
	this.config = config;
	
	this.id = this.config.id;
	
	this.zpr = null;
}

ViewerZPR.prototype.showImage = function(event, data) {
	this.zpr = new zpr('zpr_viewer', {
		imageStacksURL: data.bodyId,
		djatokaURL: 'http://parker-test.stanford.edu/adore-djatoka/resolver?url_ver=Z39.88-2004&svc_id=info:lanl-repo/svc/getRegion&svc_val_fmt=info:ofi/fmt:kev:mtx:jpeg2000&svc.format=image/jpeg',
		width: data.width,
		height: data.height,
		marqueeImgSize: 125
	});
};

ViewerZPR.prototype.activate = function() {
	$(this.id).html('<div id="zpr_viewer"/>');
	eventManager.bind('tree.imageSelected', $.proxy(this.showImage, this));
};

ViewerZPR.prototype.deactivate = function() {
	eventManager.unbind('tree.imageSelected', this.showImage);
};