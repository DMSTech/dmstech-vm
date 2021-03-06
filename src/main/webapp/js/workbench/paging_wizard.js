function PagingWizard(config) {
	this.config = config;
	
	this.type = this.config.type;
	
	this.initialUrl = this.config.url;
	this.id = this.config.id;
	this.pageSize = this.config.pageSize == null ? 20 : this.config.pageSize;
	this.initManifest = this.config.manifest;
	this.initCanvas = this.config.canvas;
	
	this.idCount = 0;
	
	this.currentStep = 0;
	
	this.host = window.location.host;
	this.path = window.location.pathname.match(/^.*\//)[0];
	
	$(this.id).html(''+
	'<div class="wizParent">'+
		'<div class="wizNav">'+
			'<span class="wizPrev" class="ui-state-default ui-corner-all"><span class="ui-icon ui-icon-triangle-1-w"></span></span>'+
		'</div>'+
		'<div class="wizSteps">'+
			'<div class="wizRepositories"><h2>Repositories</h2><ul></ul></div>'+
			'<div class="wizCollections"><h2>Collections</h2><ul></ul></div>'+
			'<div class="wizManifests"><h2>Manifests</h2><ul></ul></div>'+
			'<div class="wizImages"><h2>Images</h2><ul></ul></div>'+
		'</div>'+
		'<div class="wizPager">'+
			'<span class="pagerPrev" class="ui-state-default ui-corner-all"><span class="ui-icon ui-icon-triangle-1-w"></span></span>'+
			'Page <input type="text" class="pagerCurrent" /> of <span class="pagerTotal"></span>'+
			'<span class="pagerNext" class="ui-state-default ui-corner-all"><span class="ui-icon ui-icon-triangle-1-e"></span></span>'+
			'<div class="clear"></div>'+
		'</div>'+
	'</div>'+
	'<div class="wizLoading" class="ui-corner-all ui-widget-content"></div>');
	
	this.steps = [new WizardStep({
		wiz: this,
		id: 'wizRepositories',
		pagerFunction: this.repositoriesPager
	}),new WizardStep({
		wiz: this,
		id: 'wizCollections',
		pagerFunction: this.collectionsPager
	}),new WizardStep({
		wiz: this,
		id: 'wizManifests',
		pagerFunction: this.manifestsPager
	}),new WizardStep({
		wiz: this,
		id: 'wizImages',
		pagerFunction: this.imagesPager
	})];
	
	$(this.id+' .wizPrev').hover(function() {
		$(this).toggleClass('ui-state-hover');
	}).click($.proxy(function() {
		if (this.type == 'remote' && this.currentStep > 0 || this.type == 'local' && this.currentStep > 1) {
			this.showStep(this.currentStep - 1);
		}
	}, this));
	$(this.id+' .pagerPrev').hover(function() {
		$(this).toggleClass('ui-state-hover');
	}).click($.proxy(function() {
		this.getPage('prev');
	}, this));
	$(this.id+' .pagerNext').hover(function() {
		$(this).toggleClass('ui-state-hover');
	}).click($.proxy(function() {
		this.getPage('next');
	}, this));
	
	$(this.id+' .pagerCurrent').keyup($.proxy(function(event) {
		if (event.which == 13) {
			var page = $(this.id+' .pagerCurrent').val();
			if (page.match(/\D/) == null) {
				this.getPage(parseInt(page));
			}
		}
	}, this));
	
	$(this.id+' .wizManifests').hide();
	$(this.id+' .wizImages').hide();
	$(this.id+' .wizPager').hide();
	
	if (this.type == 'remote') {
		$(this.id+' .wizCollections').hide();
		this.initRepositories(this.initialUrl);
	} else {
		$(this.id+' .wizRepositories').hide();
		this.initCollections(this.initialUrl);
	}
}

PagingWizard.prototype.show = function() {
	$(this.id).show();
	var offset = $(this.id).offset();
	$(this.id+' .wizLoading').css({top: offset.top, left: offset.left});
};

PagingWizard.prototype.hide = function() {
	$(this.id).hide();
};

PagingWizard.prototype.loading = function(isLoading) {
	if (isLoading) {
		$(this.id+' .wizLoading')
			.outerHeight($(this.id).outerHeight())
			.outerWidth($(this.id).outerWidth())
			.show();
	} else {
		$(this.id+' .wizLoading').hide();
	}
};

PagingWizard.prototype.getPage = function(opt) {
	var currLev = this.getCurrentStep();
	
	if (opt == 'next') {
		currLev.currentPage++;
		if (currLev.currentPage >= currLev.totalPages) currLev.currentPage = currLev.totalPages - 1;
	} else if (opt == 'prev') {
		currLev.currentPage--;
		if (currLev.currentPage < 0) currLev.currentPage = 0;
	} else {
		if (opt > 0 && opt <= currLev.totalPages) {
			currLev.currentPage = opt-1;
		}
	}
	
	var beginSlice = currLev.currentPage * this.pageSize;
	var currItems = currLev.cache.slice(beginSlice, beginSlice + this.pageSize);
	
	currLev.pagerFunction.apply(this, [currLev.currentPage, currItems]);

	$(this.id+' .pagerCurrent').val(currLev.currentPage+1);
};

PagingWizard.prototype.showStep = function(index) {
	this.steps[this.currentStep].hide();
	this.currentStep = index;
	this.steps[this.currentStep].show();
};

PagingWizard.prototype.getCurrentStep = function() {
	return this.steps[this.currentStep];
};

PagingWizard.prototype.initRepositories = function(url) {
	this.loading(true);
	var parentUrl = url;
	if (url.match(this.host) == null) {
		url = 'http://'+this.host+this.path+'proxy.jsp?url='+url;
	}
	$.ajax({
		url: url,
		success: $.proxy(function(data, status, xhr) {
			var repository = $.rdf.databank();
			repository.load(data);
			setPrefixesForDatabank(data, repository);
			
			var uris = [];
			var repositories = [];
			
			$.rdf({databank: repository})
			.where('?repository ore:aggregates ?uri')
			.each(function(index, match) {
				uris.push(match.uri.value.toString()+'.xml');
			});
			
			var count = 0;
			for (var i = 0; i < uris.length; i++) {
				var uri = uris[i];
				var url = uri;
				if (url.match(this.host) == null) {
					url = 'http://'+this.host+this.path+'proxy.jsp?url='+url;
				}
				$.ajax({
					url: url,
					headers: { 
				        Accept : "application/rdf+xml; charset=utf-8",
				        "Content-Type": "application/rdf+xml; charset=utf-8"
				    },
					success: $.proxy(function(data, status, xhr) {
						var repository = $.rdf.databank();
						repository.load(data);
						setPrefixesForDatabank(data, repository);
						
						var title = $.rdf({databank: repository}).where('?repository dc:title ?title').get(0).title.value;
						
						repositories.push({
							data: title,
							metadata: {
								parent: parentUrl,
								uri: uri
							}
						});
						
						count++;
						if (count == uris.length) {
							this.loading(false);
							this.steps[0].init(repositories);
						}
					}, this),
					error: function() {
						count++;
						if (count == uris.length) {
							this.loading(false);
							this.steps[0].init(repositories);
						}
					}
				});
			}
			if (uris.length == 0) {
				this.loading(false);
				this.steps[0].init([{
					data: 'No repositories exist.',
					metadata: {}
				}]);
			}
		}, this)
	});
};

PagingWizard.prototype.repositoriesPager = function(pageNum, repositories) {	
	$(this.id+' .wizRepositories ul').empty();
	
	for (var i = 0; i < repositories.length; i++) {
		var r = repositories[i];
		$(this.id+' .wizRepositories ul').append('<li>'+r.data+'</li>');
		$(this.id+' .wizRepositories ul li:last').data('uri', r.metadata.uri).data('parent', r.metadata.parent);
		this.idCount++;
	}
	
	$(this.id+' .wizRepositories li').click($.proxy(function(event) {
		var data = $(event.target).data();
		if (data != null) {
			this.initCollections(data.uri);
			eventManager.trigger('repositorySelected', data);
		}
	}, this));
	
	this.showStep(0);
};

PagingWizard.prototype.initCollections = function(url) {
	this.loading(true);
	if (url.match(this.host) == null) {
		url = 'http://'+this.host+this.path+'proxy.jsp?url='+url;
	}
	$.ajax({
		url: url,
		success: $.proxy(function(data, status, xhr) {
			var repository = $.rdf.databank();
			repository.load(data);
			setPrefixesForDatabank(data, repository);
			
			var uris = [];
			var localCollections = [];
			
			$.rdf({databank: repository})
			.where('?collection ore:aggregates ?uri')
			.each(function(index, match) {
				uris.push(match.uri.value.toString()+'.xml');
			});
			
			var count = 0;
			for (var i = 0; i < uris.length; i++) {
				var uri = uris[i];
				var url = uri; 
				if (url.match(this.host) == null) {
					url = 'http://'+this.host+this.path+'proxy.jsp?url='+url;
				}
				$.ajax({
					url: url,
					headers: { 
				        Accept : "application/rdf+xml; charset=utf-8",
				        "Content-Type": "application/rdf+xml; charset=utf-8"
				    },
					success: $.proxy(function(data, status, xhr) {
						var collection = $.rdf.databank();
						collection.load(data);
						setPrefixesForDatabank(data, collection);
						
						var title = $.rdf({databank: collection}).where('?collection dc:title ?title').get(0).title.value;
						var manifests = [];
						$.rdf({databank: collection})
						.where('?manifest ore:aggregates ?uri')
						.each(function(index, match) {
							manifests.push(match.uri.value.toString()+'.xml');
						});
						
						localCollections.push({
							data: title,
							metadata: {
								parent: uri,
								uris: manifests
							}
						});
						
						count++;
						if (count == uris.length) {
							this.loading(false);
							this.steps[1].init(localCollections);
							if (this.initManifest != null) {
								this.loadManifestURI(this.initManifest);
							}
						}
					}, this),
					error: function() {
						count++;
						if (count == uris.length) {
							this.loading(false);
							this.steps[1].init(localCollections);
							if (this.initManifest != null) {
								this.loadManifestURI(this.initManifest);
							}
						}
					}
				});
			}
		}, this)
	});
};

PagingWizard.prototype.collectionsPager = function(pageNum, collections) {	
	$(this.id+' .wizCollections ul').empty();
	
	for (var i = 0; i < collections.length; i++) {
		var c = collections[i];
		$(this.id+' .wizCollections ul').append('<li>'+c.data+'</li>');
		$(this.id+' .wizCollections ul li:last').data('uris', c.metadata.uris).data('parent', c.metadata.parent);
		this.idCount++;
	}
	
	$(this.id+' .wizCollections li').click($.proxy(function(event) {
		var data = $(event.target).data();
		if (data.uris.length > 0) {
			this.steps[2].init(data.uris);
			eventManager.trigger('collectionSelected', data);
		} else {
			alert('No manuscripts available for this collection.');
		}
	}, this));
	
	this.showStep(1);
};

PagingWizard.prototype.loadManifestURI = function(uri) {
	this.steps[2].init([uri]);
};

PagingWizard.prototype.manifestsPager = function(pageNum, uris) {
	$(this.id+' .wizManifests ul').empty();
	
	function buildPage(liString, cache) {
		$(this.id+' .wizManifests ul').append(liString);
		
		$(this.id+' .wizManifests li').each($.proxy(function(index, el) {
			var data = cache[index];
			if (data != null) {
				$(el).data(data);
				$(el).click($.proxy(function(event) {
					$.proxy(this.fetchSequence($(event.target).data()), this);
				}, this));
				if (this.initManifest != null) {
					this.initManifest = null;
					$(el).click();
				}
			}
		}, this));
	}
	
	var cache = $(this.id+' .wizManifests').data('page'+pageNum);
	var liString = '';
	
	if (cache != null) {
		for (var i = 0; i < cache.length; i++) {
			var data = cache[i];
			liString += '<li id="man_'+this.idCount+'">'+data.title+'</li>';
			this.idCount++;
		}
		buildPage.apply(this, [liString, cache]);
	} else {
		this.loading(true);
		cache = [];
		for (var i = 0; i < uris.length; i++) {
			var uri = uris[i];
			var url = uri;
			if (url.match(this.host) == null) {
				url = 'http://'+this.host+this.path+'proxy.jsp?url='+url;
			}
			$.ajax({
				url: url,
				headers: { 
			        Accept : "application/rdf+xml; charset=utf-8",
			        "Content-Type": "application/rdf+xml; charset=utf-8"
			    },
				success: $.proxy(function(data, status, xhr) {
					try {
						var manifest = $.rdf.databank();
						manifest.load(data);
						setPrefixesForDatabank(data, manifest);
						
						var info = {parent: uri};
						info.title = $.rdf({databank: manifest}).where('?manifest dc:title ?title').get(0).title.value; 

						if (manifest.prefix().sc != null) {
							var entry = $.rdf({databank: manifest})
								.where('?ns rdf:type ?type')
								.filter('type', /ns\/Sequence/)
								.where('?ns sc:hasOptimizedSerialization ?opt')
								.where('?ns ore:isDescribedBy ?uri')
								.where('?agg sc:newSequenceEndpoint ?newSeq').get(0);
							if (entry.opt != null) {
								info.optUri = entry.opt.value.toString();
							}
							if (entry.newSeq != null) {
								info.newSeqUri = entry.newSeq.value.toString();
							}
							info.nsUri = entry.ns.value.toString();
						} else {
							var entry = $.rdf({databank: manifest})
								.where('?ns rdf:type ?type')
								.filter('type', /ns\/Sequence/)
								.where('?ns ore:isDescribedBy ?uri').get(0);
							info.nsUri = entry.ns.value.toString();
						}
						
						info.iaUri = $.rdf({databank: manifest})
							.where('?ns rdf:type ?type')
							.filter('type', /ns\/ImageAnnotationList/)
							.where('?ns ore:isDescribedBy ?uri').get(0).uri.value.toString();
						
						cache.push(info);
						
						liString += '<li id="man_'+this.idCount+'">'+info.title+'</li>';
						this.idCount++;
						
						if (cache.length == uris.length) {
							this.loading(false);
							buildPage.apply(this, [liString, cache]);
							this.showStep(2);
							$(this.id+' .wizManifests').data('page'+pageNum, cache);
						}
					} catch (e) {
						alert(e);
						cache.push(null);
						liString += '<li id="man_'+this.idCount+'">(Error loading this manifest)</li>';
						this.idCount++;
						if (cache.length == uris.length) {
							this.loading(false);
							buildPage.apply(this, [liString, cache]);
							this.showStep(2);
							$(this.id+' .wizManifests').data('page'+pageNum, cache);
						}
					}
					
				}, this),
				error: $.proxy(function(xhr, status, error) {
					this.loading(false);
					this.showStep(2);
					$(this.id+' .wizManifests ul').append('<li>Error loading collection: '+error+'</li>');
				}, this)
			});
		}
	}
};

PagingWizard.prototype.fetchSequence = function(data) {
	this.loading(true);
	
	/* ordering code using rdfquery
	var annotationsOrder = [];
	var rest = '';
	$.rdf({databank: qry.databank})
	.where('?annos rdf:type ?type')
	.filter('type', /terms\/Aggregation/)
	.where('?annos rdf:first ?first')
	.where('?annos rdf:rest ?rest')
	.each(function(index, match) {
		rest = match.rest.value.toString();
		annotationsOrder.push(match.first.value.toString());
	});
	
	function orderAnnotations(db, annotationsOrder, rest) {
		var newRest;
		$.rdf({databank: qry.databank})
		.where(rest+' rdf:first ?first')
		.where(rest+' rdf:rest ?rest')
		.each(function(index, match) {
			annotationsOrder.push(match.first.value.toString());
			newRest = match.rest.value.toString();
		});
		if (newRest.match('22-rdf-syntax-ns#nil') == null) {
			orderAnnotations(db, annotationsOrder, newRest);
		}
	}
	orderAnnotations(qry.databank, annotationsOrder, rest);
	*/
	if (data.optUri == null) {
		if (data.iaUri.match(this.host) == null) {
			data.iaUri = 'http://'+this.host+this.path+'proxy.jsp?url='+data.iaUri;
		}		
		$.ajax({
			url: data.iaUri,
			success: function(data, status, xhr, uris) {
				var annos = [];
				var id = uris.iaUri.split('?url=').pop();
				
				function getElementByAttribute(context, nodeName, attributeName, attributeValue) {
					var match = null;
					$(context).find('*').filterNode(nodeName).each(function(index, el) {
						var e = $(el);
						if (e.attr(attributeName) == attributeValue) {
							match = e;
							return false;
						}
					});
					return match;
				}
				
				var rdf = getElementByAttribute(data, 'rdf:Description', 'rdf:about', id);
				var oreDescribes = rdf.filterNode('ore:describes');
				var listId = oreDescribes.attr('rdf:resource');
				var list;
				if (listId != null) {
					list = getElementByAttribute(data, 'rdf:Description', 'rdf:about', listId);
				} else {
					listId = oreDescribes.attr('rdf:nodeID');
					list = getElementByAttribute(data, 'rdf:Description', 'rdf:nodeID', listId);
				}
				
				var firstId = list.filterNode('rdf:first').attr('rdf:resource');
				var restId = list.filterNode('rdf:rest').attr('rdf:nodeID');
				
				function getJsonForAnno(anno) {
					var targetId = anno.filterNode('oac:hasTarget').attr('rdf:resource');
					var bodyId = anno.filterNode('oac:hasBody').attr('rdf:resource');
					var target = getElementByAttribute(data, 'rdf:Description', 'rdf:about', targetId);
					var body = getElementByAttribute(data, 'rdf:Description', 'rdf:about', bodyId);
					return {
						canvasURI: targetId,
						canvasTitle: target.filterNode('dc:title').text(),
						imageURI: bodyId,
						width: body.filterNode('exif:width').text(),
						height: body.filterNode('exif:height').text()
					};
				}
				
				var firstAnno = getElementByAttribute(data, 'rdf:Description', 'rdf:about', firstId);
				annos.push(getJsonForAnno(firstAnno));
				
				function getOrder(nodeId, count) {
					var node = getElementByAttribute(data, 'rdf:Description', 'rdf:nodeID', nodeId);
					var firstId = node.filterNode('rdf:first').attr('rdf:resource');
					var firstAnno = getElementByAttribute(data, 'rdf:Description', 'rdf:about', firstId);
					annos.push(getJsonForAnno(firstAnno));
					var restId = node.filterNode('rdf:rest').attr('rdf:nodeID');
					if (restId != undefined) {
						count++;
						if (count > 25) {
							count = 0;
							setTimeout(getOrder.createDelegate(this, [restId, count], 0), 50);
						} else {
							getOrder.apply(this, [restId, count]);
						}
					} else {
						this.loading(false);
						this.steps[3].init(annos, uris);
						eventManager.trigger('sequenceSelected', [annos, uris]);
					}
				}
				getOrder.apply(this, [restId, 0]);
			}.createDelegate(this, [data], true)
		});
	} else {
		var uris = data;
		if (data.optUri.match(this.host) == null) {
			data.optUri = 'http://'+this.host+this.path+'proxy.jsp?url='+data.optUri;
		}
		$.ajax({
			url: data.optUri,
			dataType: 'json',
			success: $.proxy(function(data, status, xhr) {
				this.steps[3].init(data, uris);
				eventManager.trigger('sequenceSelected', [data, uris]);
				if (this.initCanvas == null) {
					this.loading(false);
				} else {
					var i;
					for (i = 0; i < data.length; i++) {
						var canvas = data[i];
						if (canvas.canvasURI == this.initCanvas) {
							break;
						}
					}
					var page = Math.ceil(i / this.pageSize);
					if (page > 1) this.getPage(page);
					
					this.loading(false);
				}
			}, this)
		});
	}
};

PagingWizard.prototype.imagesPager = function(pageNum, annotations) {
	$(this.id+' .wizImages ul').empty();
	
	function buildPage(liString, cache) {
		$(this.id+' .wizImages ul').append(liString);
		
		$(this.id+' .wizImages li').each($.proxy(function(index, el) {
			var data = cache[index];
			$(el).data('metadata', data);
			$(el).click($.proxy(function(event) {
				$(el).siblings().removeClass();
				$(el).addClass('selected');
				eventManager.trigger('imageSelected', $(event.target).data('metadata'));
			}, this));
			if (this.initCanvas != null && this.initCanvas == data.canvasURI) {
				$(el).click();
				this.initCanvas = null;
			}
		}, this));
	}
	
	var cache = $(this.id+' .wizImages').data('page'+pageNum);
	var liString = '';
	if (cache != null) {
		for (var i = 0; i < cache.length; i++) {
			var data = cache[i];
			liString += '<li id="img_'+this.idCount+'">'+data.canvasTitle+'</li>';
			this.idCount++;
		}
		buildPage.apply(this, [liString, cache]);
	} else {
		cache = [];
		for (var i = 0; i < annotations.length; i++) {
			var a = annotations[i];
//			cache.push({title: a.targets[0].title, bodyId: a.body.id, width: a.body.width, height: a.body.height});
			cache.push(a);
			
			liString += '<li id="img_'+this.idCount+'">'+a.canvasTitle+'</li>';
			this.idCount++;
		}
		buildPage.apply(this, [liString, cache]);
		this.showStep(3);
		$(this.id+' .wizImages').data('page'+pageNum, cache);
	}
};

function WizardStep(config) {
	this.wiz = config.wiz;
	this.id = config.id;
	this.cache = null;
	this.currentPage = 0;
	this.totalPages = 1;
	this.pagerFunction = config.pagerFunction;
}

/**
 * Initialize a step in the wizard
 * @param items The items to page through
 * @param [data] Extra data associated with this step (ids, etc.), optional
 */
WizardStep.prototype.init = function(items, data) {
	var totalPages = Math.ceil(items.length / this.wiz.pageSize);
	
	$(this.wiz.id+' .'+this.id).removeData();
	
	this.cache = items;
	this.data = data;
	this.currentPage = 0;
	this.totalPages = totalPages;
	
	if (items.length > this.wiz.pageSize) {
		$(this.wiz.id+' .wizPager').show();
		$(this.wiz.id+' .pagerCurrent').val(1);
		$(this.wiz.id+' .pagerTotal').html(totalPages);
		var curritems = items.slice(0, this.wiz.pageSize);
		this.pagerFunction.apply(this.wiz, [0, curritems]);
	} else {
		$(this.wiz.id+' .wizPager').hide();
		this.pagerFunction.apply(this.wiz, [0, items]);
	}
};

WizardStep.prototype.show = function() {
	$(this.wiz.id+' .'+this.id).show();
	if (this.cache.length > this.wiz.pageSize) {
		$(this.wiz.id+' .pagerCurrent').val(this.currentPage+1);
		$(this.wiz.id+' .pagerTotal').html(this.totalPages);
		$(this.wiz.id+' .wizPager').show();
	} else {
		$(this.wiz.id+' .wizPager').hide();
	}
};

WizardStep.prototype.hide = function() {
	$(this.wiz.id+' .'+this.id).hide();
};

// createDelegate from Extjs
Function.prototype.createDelegate = function(obj, args, appendArgs) {
    var method = this;
    return function() {
        var callArgs = args || arguments;
        if (appendArgs === true) {
            callArgs = Array.prototype.slice.call(arguments, 0);
            callArgs = callArgs.concat(args);
        } else if (typeof appendArgs === 'number') {
            callArgs = Array.prototype.slice.call(arguments, 0); // copy arguments first
            var applyArgs = [appendArgs, 0].concat(args); // create method call params
            Array.prototype.splice.apply(callArgs, applyArgs); // splice them in
        }
        return method.apply(obj || window, callArgs);
    };
};

// cross browser xml node finder
// http://www.steveworkman.com/html5-2/javascript/2011/improving-javascript-xml-node-finding-performance-by-2000/
$.fn.filterNode = function(name) {
	return this.find('*').filter(function() {
		return this.nodeName === name;
	});
};