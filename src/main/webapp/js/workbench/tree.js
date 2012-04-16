function Tree(config) {
	this.config = config;
	
	this.id = this.config.id;
	
	var initData = config.data || [{
		data: 'Loading...'
	}];
	
	this.treeConfig = this.config.treeConfig != null ? this.config.treeConfig : {
		plugins: ['themes', 'json_data', 'ui', 'search'],
		themes: {
			theme: 'default'
		},
		ui: {
			select_limit: 1
		},
		search: {
			case_insensitive: true
		},
		json_data: {
			progressive_render: true,
			progressive_unload: true,
			data: initData
		}
	};
	
	if (config.data == null) {
		$(this.id).bind('loaded.jstree', $.proxy(function() {
			this.tree.find('a').addClass('jstree-loading');
		}, this));
	}
	
	this.tree = $(this.id).jstree(this.treeConfig);
	this.tree.bind('select_node.jstree', $.proxy(this.selectHandler, this));
}

Tree.prototype.populateTree = function(jsonNodes) {
	this.tree.children('ul').children('li').each($.proxy(function(index, el) {
		this.tree.jstree('delete_node', el);
	}, this));
	
	for (var i = 0; i < jsonNodes.length; i++) {
		this.tree.jstree('create_node', this.tree, 'last', jsonNodes[i]);
	}
};

Tree.prototype.selectHandler = function(event, data) {
	var node = data.rslt.obj;
	var mdata = node.data();
	if (mdata.type == 'repository') {
		
	} else if (mdata.type == 'collection') {
		node.children('a').addClass('jstree-loading');
		this.fetchManifests(mdata.uris, node);
	} else if (mdata.type == 'manifest') {
		node.children('a').addClass('jstree-loading');
		this.fetchSequence(mdata.iaUri, node);
	} else if (mdata.bodyId) {
		eventManager.trigger('tree.imageSelected', mdata);
	}
};

Tree.prototype.fetchManifests = function(uris, node) {
	var length = uris.length;//Math.min(uris.length, 10);
	for (var i = 0; i < length; i++) {
		var uri = uris[i];
		$.ajax({
			url: 'http://'+window.location.host+proxyString,
			data: {
				url: uri
			},
			success: $.proxy(function(data, status, xhr) {
				var manifest = $.rdf.databank();
				manifest.load(data);
				setPrefixesForDatabank(data, manifest);
				
				var title = $.rdf({databank: manifest}).where('?manifest dc:title ?title').get(0).title.value;

				var nsUri = $.rdf({databank: manifest})
				.where('?ns rdf:type ?type')
				.filter('type', /ns\/Sequence/)
				.where('?ns ore:isDescribedBy ?uri').get(0).uri.value.toString();
				
				var iaUri = $.rdf({databank: manifest})
				.where('?ns rdf:type ?type')
				.filter('type', /ns\/ImageAnnotationList/)
				.where('?ns ore:isDescribedBy ?uri').get(0).uri.value.toString();
				
				this.tree.jstree('create_node', node, 'last', {
					data: title,
					metadata: {
						type: 'manifest',
						nsUri: nsUri,
						iaUri: iaUri
					}
				});
				
				if (i == length) {
					this.tree.jstree('open_node', node);
				}
				
			}, this)
		});
	}
};

Tree.prototype.fetchSequence = function(iaUri, node) {
	var qry = $.rdf(opts);
	function parseAnnotations(qry, uri) {
		var annotations = buildAllAnnos(qry);
		eventManager.trigger('tree.sequenceSelected', [annotations]);
		this.addAnnotationsToNode(node, annotations);
	}
	fetchTriples('http://'+window.location.host+proxyString+'?url='+iaUri, qry, $.proxy(parseAnnotations, this));
};

Tree.prototype.addAnnotationsToNode = function(node, annotations) {
	for (var i = 0; i < annotations.length; i++) {
		var a = annotations[i];
		this.tree.jstree('create_node', node, 'last', {
			data: a.targets[0].title,
			metadata: {
				bodyId: a.body.id,
				width: a.body.width,
				height: a.body.height
			}
		});
	}
	this.tree.jstree('open_node', node);
};