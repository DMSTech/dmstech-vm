function Tree(config) {
	this.config = config;
	
	this.id = this.config.id;
	
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
			data: [{
				data: 'Collection 1',
				metadata: {collectionId: 'collectionId1'}
			}]
		}
	};
	
	this.tree = $(this.id).jstree(this.treeConfig);
	this.tree.bind('select_node.jstree', $.proxy(this.selectHandler, this));
}

Tree.prototype.selectHandler = function(event, data) {
	var node = data.rslt.obj;
	var mdata = node.data();
	if (mdata.collectionId) {
		this.fetchCollection(mdata.collectionId, node);
	} else if (mdata.manuscriptId) {
		this.fetchManuscript(mdata.manuscriptId, node);
	} else if (mdata.bodyId) {
		eventManager.trigger('tree.imageSelected', mdata);
	}
};

Tree.prototype.fetchCollection = function(id, node) {
	var qry = $.rdf(opts);
	function parseManifest(query, uri) {
		query.reset();
		var manifests = [];
		query
			.where('?manifest rdf:type dms:Manifest')
			.where('?manifest dc:title ?title')
			.each(function() {
				manifests.push({
					id: this.manifest.value.toString(),
					title: this.title.value.toString()
				});
			});
		query.reset();
		this.addManifestsToNode(node, manifests);
	}
	fetchTriples('xml/Manifest.xml', qry, $.proxy(parseManifest, this));
};

Tree.prototype.fetchManuscript = function(id, node) {
	var qry = $.rdf(opts);
	function parseAnnotations(qry, uri) {
		var annotations = buildAllAnnos(qry);
		this.addAnnotationsToNode(node, annotations);
	}
	fetchTriples('xml/ImageAnnotations.xml', qry, $.proxy(parseAnnotations, this));
};

Tree.prototype.addManifestsToNode = function(node, manifests) {
	for (var i = 0; i < manifests.length; i++) {
		var m = manifests[i];
		this.tree.jstree('create_node', node, 'last', {
			data: m.title,
			metadata: {
				manuscriptId: m.id
			}
		});
	}
	this.tree.jstree('open_node', node);
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