function Workbench() {
	this.host = window.location.host;
	this.path = window.location.pathname.match(/^.*\//)[0];
	
	this.currentCollection = null;
	this.currentTool = null;
	
	this.localPagingWizard = null;
	this.remotePagingWizard = null;
	
	$(window).resize($.proxy(this.doResize, this));
	
	$.ajax({
		url: 'http://'+this.host+this.path+'sc/lookup/remote',
		type: 'GET',
		success: $.proxy(function(data, status, xhr) {
			this.remoteRepositoriesURL = data;
			this.init();
		}, this),
		error: function() {
			alert('There was an error getting the remote repositories URL.');
			this.init();
		}
	});
}

Workbench.prototype.init = function() {	
	$('#collectionsSelector').buttonset();
	$('#collectionsSelector input').click($.proxy(this.collectionsChange, this));
	
	var manifest = getParameterByName('manifest');
	var canvas = getParameterByName('canvas');
	this.localPagingWizard = new PagingWizard({
		type: 'local',
		id: '#collectionsLocal',
		url: 'http://'+this.host+this.path+'sc/Repository.xml',
		manifest: manifest,
		canvas: canvas
	});
	this.remotePagingWizard = new PagingWizard({
		type: 'remote',
		id: '#collectionsRemote',
		url: this.remoteRepositoriesURL
	});
	
	$('#collections input:first').trigger('click');
	
	this.viewer = new Viewer({id: '#toolContent'});
	this.viewerZPR = new ViewerZPR({id: '#toolContent'});
	this.orderer = new Orderer({id: '#toolContent'});
	this.htmlSerializer = new HTMLSerializer({id: '#toolContent'});
	
	this.tools = [{
		id: 'viewer',
		label: 'Viewer',
		tool: this.viewer
	},{
		id: 'orderer',
		label: 'Orderer',
		tool: this.orderer
	},{
		id: 'enhancedViewer',
		label: 'Enhanced Viewer',
		tool: this.viewerZPR
	},{
		id: 'htmlSerializer',
		label: 'RDF',
		tool: this.htmlSerializer
	}];
	
	for (var i = 0; i < this.tools.length; i++) {
		var tool = this.tools[i];
		$('#tools').append('<input type="radio" id="'+tool.id+'Radio" name="toolsRadio" /><label for="'+tool.id+'Radio">'+tool.label+'</label>');
		$('#tools input:last').data('tool', tool);
	}
	
	$('#tools').buttonset();
	$('#tools input').click($.proxy(this.toolChange, this));
	
	$('#tools input:first').trigger('click');
	
	this.doResize();
};

Workbench.prototype.collectionsChange = function(event) {
	if ($(event.target).attr('id').match('local')) {
		this.currentCollection = this.localPagingWizard;
		this.remotePagingWizard.hide();
	} else {
		this.currentCollection = this.remotePagingWizard;
		this.localPagingWizard.hide();
	}
	this.currentCollection.show();
};

Workbench.prototype.toolChange = function(event) {
	if (this.currentTool != null) {
		this.currentTool.deactivate();
	}
	var toolInfo = $(event.target).data('tool');
	this.currentTool = toolInfo.tool;
	var data = null;
	if (toolInfo.id == 'orderer' && this.currentCollection.currentStep == this.currentCollection.steps.length - 1) {
		var step = this.currentCollection.getCurrentStep();
		data = {items: step.cache, uris: step.data};
	}
	this.currentTool.activate(data);
};

Workbench.prototype.doResize = function() {
	var height = $(window).height() - 104;
	$('#toolContent').height(height);
	this.currentTool.resize(height, $('#toolContent').width());
};

// general utility functions
var setPrefixesForDatabank = function(data, databank) {
	var root = $(data).children()[0];
	for (var i = 0; i < root.attributes.length; i++) {
		var att = root.attributes[i];
		databank.prefix(att.nodeName.split(':')[1], att.nodeValue);
	}
};

workbench = new Workbench();