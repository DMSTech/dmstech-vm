function Workbench() {
	
	this.currentTool = null;
	
	$(document).ready($.proxy(this.init, this));
}

Workbench.prototype.init = function() {
	$('#search button').button().click($.proxy(function() {
		this.searchTrees($('#search input')[0].value);
	}, this));
	$('#search input').keyup($.proxy(function(event) {
		if (event.which == 13) {
			this.searchTrees($('#search input')[0].value);
		}
	}, this));
	
	this.localTree = new Tree({id: '#collectionsLocal'});
	this.remoteTree = new Tree({id: '#collectionsRemote'});
	
	this.viewer = new Viewer({id: '#toolContent'});
	this.viewerZPR = new ViewerZPR({id: '#toolContent'});
	
	this.tools = [{
		id: 'viewer',
		label: 'Viewer',
		tool: this.viewer
	},{
		id: 'tpen',
		label: 'TPen',
		tool: null
	},{
		id: 'dm',
		label: 'DM',
		tool: null
	},{
		id: 'enhancedViewer',
		label: 'Enhanced Viewer',
		tool: this.viewerZPR
	}];
	
	for (var i = 0; i < this.tools.length; i++) {
		var tool = this.tools[i];
		$('#tools').append('<input type="radio" id="'+tool.id+'Radio" name="toolsRadio" /><label for="'+tool.id+'Radio">'+tool.label+'</label>');
		$('#tools input:last').data('tool', tool.tool);
	}
	
	$('#tools').buttonset();
	$('#tools input').click($.proxy(this.toolChange, this));
	
	$('#tools input:first').trigger('click');
	
	eventManager.bind('tree.imageSelected', $.proxy(function(event, data) {
		if (this.currentTool == 'viewer') {
			this.viewer.showImage(event, data);
		} else if (this.currentTool == 'enhanced') {
			this.viewerZPR.showImage(event, data);
		}
	}, this));
};

Workbench.prototype.toolChange = function(event) {
	if (this.currentTool != null) {
		this.currentTool.deactivate();
	}
	this.currentTool = $(event.target).data('tool');
	this.currentTool.activate();
};

Workbench.prototype.searchTrees = function(query) {
	this.localTree.tree.jstree('search', query);
	this.remoteTree.tree.jstree('search', query);
};

workbench = new Workbench();