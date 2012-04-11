var proxyString = '/dmstech/proxy.jsp';

function Workbench() {
	
	this.currentTool = null;
	
	$(document).ready($.proxy(this.init, this));
	
	$(window).resize($.proxy(this.doResize, this));
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
	
	this.pagingWizard = new PagingWizard({id: '#collectionsLocal'});
	
	this.viewer = new Viewer({id: '#toolContent'});
	this.viewerZPR = new ViewerZPR({id: '#toolContent'});
	this.orderer = new Orderer({id: '#toolContent'});
	
	this.tools = [{
		id: 'viewer',
		label: 'Viewer',
		tool: this.viewer
	},{
		id: 'orderer',
		label: 'Orderer',
		tool: this.orderer
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
		$('#tools input:last').data('tool', tool);
	}
	
	$('#tools').buttonset();
	$('#tools input').click($.proxy(this.toolChange, this));
	
	$('#tools input:first').trigger('click');
	
	this.doResize();
};

Workbench.prototype.toolChange = function(event) {
	if (this.currentTool != null) {
		this.currentTool.deactivate();
	}
	var toolInfo = $(event.target).data('tool');
	this.currentTool = toolInfo.tool;
	this.currentTool.activate();
};

Workbench.prototype.searchTrees = function(query) {
	this.localTree.tree.jstree('search', query);
	this.remoteTree.tree.jstree('search', query);
};

Workbench.prototype.doResize = function() {
	var height = $(window).height() - 80;
	$('#toolContent').height(height);
	this.currentTool.resize(height, $('#toolContent').width());
};

// general utility function
var setPrefixesForDatabank = function(data, databank) {
	var root = $(data).children()[0];
	for (var i = 0; i < root.attributes.length; i++) {
		var att = root.attributes[i];
		databank.prefix(att.nodeName.split(':')[1], att.nodeValue);
	}
};

workbench = new Workbench();