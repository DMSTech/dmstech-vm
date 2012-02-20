$(document).ready(function() {
	$('#search button').button().click(function() {
		searchTrees($('#search input')[0].value);
	});
	$('#search input').keyup(function(event) {
		if (event.which == 13) {
			searchTrees($('#search input')[0].value);
		}
	});
	
	
	$('#tools').buttonset();
	
	var treeConfig = {
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
	
	$('#collectionsLocal').jstree(treeConfig);
	$('#collectionsLocal').bind('select_node.jstree', selectHandler);
	
	$('#collectionsRemote').jstree(treeConfig);
	$('#collectionsRemote').bind('select_node.jstree', selectHandler);
});

function searchTrees(query) {
	$('#collectionsLocal').jstree('search', query);
	$('#collectionsRemote').jstree('search', query);
}

function selectHandler(event, data) {
	var node = data.rslt.obj;
	var mdata = node.data();
	if (mdata.collectionId) {
		fetchCollection(mdata.collectionId, node);
	} else if (mdata.manuscriptId) {
		fetchManuscript(mdata.manuscriptId, node);
	} else if (mdata.bodyId) {
		$('#toolContent iframe').attr('src', 'loading.htm');
		setTimeout(function() {
			var iframeWidth = $('#toolContent iframe').width();
			var ratio = mdata.width / iframeWidth;
			$('#toolContent iframe').attr('src', mdata.bodyId);
			$('#toolContent iframe').attr('height', mdata.height / ratio);
		}, 150);
	}
}

function fetchCollection(id, node) {
	var qry = $.rdf(opts);
	fetchTriples('xml/Manifest.xml', qry, function(query, uri) {
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
		addManifestsToNode(node, manifests);
	});
}

function fetchManuscript(id, node) {
	var qry = $.rdf(opts);
	fetchTriples('xml/ImageAnnotations.xml', qry, function(qry, uri) {
		var annotations = buildAllAnnos(qry);
		addAnnotationsToNode(node, annotations);
	});
}

function addManifestsToNode(node, manifests) {
	var parentTree = node.parents('.jstree');
	for (var i = 0; i < manifests.length; i++) {
		var m = manifests[i];
		parentTree.jstree('create_node', node, 'last', {
			data: m.title,
			metadata: {
				manuscriptId: m.id
			}
		});
	}
	parentTree.jstree('open_node', node);
}

function addAnnotationsToNode(node, annotations) {
	var parentTree = node.parents('.jstree');
	for (var i = 0; i < annotations.length; i++) {
		var a = annotations[i];
		parentTree.jstree('create_node', node, 'last', {
			data: a.targets[0].title,
			metadata: {
				bodyId: a.body.id,
				width: a.body.width,
				height: a.body.height
			}
		});
	}
	parentTree.jstree('open_node', node);
}