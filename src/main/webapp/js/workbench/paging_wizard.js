function PagingWizard(config) {
	this.config = config;
	
	this.type = this.config.type;
	
	this.initialUrl = this.config.url;
	this.id = this.config.id;
	this.pageSize = this.config.pageSize == null ? 20 : this.config.pageSize;
	this.initManifest = this.config.manifest;
	
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
	if (url.match('localhost') == null) {
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
				var url = uris[i];
				if (url.match('localhost') == null) {
					url = 'http://'+this.host+this.path+'proxy.jsp?url='+url;
				}
				$.ajax({
					url: url,
					success: $.proxy(function(data, status, xhr) {
						var repository = $.rdf.databank();
						repository.load(data);
						setPrefixesForDatabank(data, repository);
						
						var title = $.rdf({databank: repository}).where('?repository dc:title ?title').get(0).title.value;
						
						repositories.push({
							data: title,
							metadata: {
								type: 'repository',
								uri: url
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
					}
				});
			}
		}, this)
	});
};

PagingWizard.prototype.repositoriesPager = function(pageNum, repositories) {	
	$(this.id+' .wizRepositories ul').empty();
	
	for (var i = 0; i < repositories.length; i++) {
		var r = repositories[i];
		$(this.id+' .wizRepositories ul').append('<li>'+r.data+'</li>');
		$(this.id+' .wizRepositories ul li:last').data('uri', r.metadata.uri);
		this.idCount++;
	}
	
	$(this.id+' .wizRepositories li').click($.proxy(function(event) {
		this.initCollections($(event.target).data('uri'));
	}, this));
	
	this.showStep(0);
};

PagingWizard.prototype.initCollections = function(url) {
	this.loading(true);
	if (url.match('localhost') == null) {
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
			.where('?collection ore:isDescribedBy ?uri')
			.each(function(index, match) {
				uris.push(match.uri.value.toString());
			});
			
			var count = 0;
			for (var i = 0; i < uris.length; i++) {
				var url = uris[i];
				if (url.match('localhost') == null) {
					url = 'http://'+this.host+this.path+'proxy.jsp?url='+url;
				}
				$.ajax({
					url: url,
					success: $.proxy(function(data, status, xhr) {
						var collection = $.rdf.databank();
						collection.load(data);
						setPrefixesForDatabank(data, collection);
						
						var title = $.rdf({databank: collection}).where('?collection dc:title ?title').get(0).title.value;
						var manifests = [];
						$.rdf({databank: collection}).where('?manifest ore:isDescribedBy ?uri').each(function(index, match) {
							manifests.push(match.uri.value.toString());
						});
						
						localCollections.push({
							data: title,
							metadata: {
								type: 'collection',
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
		$(this.id+' .wizCollections ul li:last').data('uris', c.metadata.uris);
		this.idCount++;
	}
	
	$(this.id+' .wizCollections li').click($.proxy(function(event) {
		this.steps[2].init($(event.target).data('uris'));
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
				$(el).data('iaUri', data.iaUri);
				$(el).data('nsUri', data.nsUri);
				$(el).click($.proxy(function(event) {
					$.proxy(this.fetchSequence($(event.target).data()), this);
				}, this));
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
			var url = uris[i];
			if (url.match('localhost') == null) {
				url = 'http://'+this.host+this.path+'proxy.jsp?url='+url;
			}
			$.ajax({
				url: url,
				success: $.proxy(function(data, status, xhr) {
					try {
						var manifest = $.rdf.databank();
						manifest.load(data);
						setPrefixesForDatabank(data, manifest);
						
						var title = $.rdf({databank: manifest}).where('?manifest dc:title ?title').get(0).title.value;
						
						var entry = $.rdf({databank: manifest})
						.where('?ns rdf:type ?type')
						.filter('type', /ns\/Sequence/)
						.where('?ns ore:isDescribedBy ?uri').get(0);
						var ns = entry.ns.value.toString();
	//					var nsUri = entry.uri.value.toString();
						
						var iaUri = $.rdf({databank: manifest})
						.where('?ns rdf:type ?type')
						.filter('type', /ns\/ImageAnnotationList/)
						.where('?ns ore:isDescribedBy ?uri').get(0).uri.value.toString();
						
						cache.push({title: title, nsUri: ns, iaUri: iaUri});
						
						liString += '<li id="man_'+this.idCount+'">'+title+'</li>';
						this.idCount++;
						
						if (cache.length == uris.length) {
							this.loading(false);
							
							buildPage.apply(this, [liString, cache]);
							
							this.showStep(2);
							
							$(this.id+' .wizManifests').data('page'+pageNum, cache);
						}
					} catch (e) {
						cache.push(null);
						liString += '<li id="man_'+this.idCount+'">(Error loading this manifest)</li>';
						this.idCount++;
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
	var iaUri = data.iaUri;
	var nsUri = data.nsUri;
	
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
	if (iaUri.match('localhost') == null) {
		iaUri = 'http://'+this.host+this.path+'proxy.jsp?url='+iaUri;
		$.ajax({
			url: iaUri,
			success: function(data, status, xhr, url) {
				var annos = [];
				var id = url.split('?url=').pop();
				var rdf = $('rdf\\:Description[rdf\\:about="'+id+'"]', data);
				var oreDescribes = $('ore\\:describes', rdf);
				var listId = oreDescribes.attr('rdf:resource');
				var list;
				if (listId != null) {
					list = $('rdf\\:Description[rdf\\:about="'+listId+'"]', data);
				} else {
					listId = oreDescribes.attr('rdf:nodeID');
					list = $('rdf\\:Description[rdf\\:nodeID="'+listId+'"]', data);
				}
				var firstId = $('rdf\\:first', list).attr('rdf:resource');
				var restId = $('rdf\\:rest', list).attr('rdf:nodeID');
				
				function getJsonForAnno(anno) {
					var targetId = anno.children('oac\\:hasTarget').attr('rdf:resource');
					var bodyId = anno.children('oac\\:hasBody').attr('rdf:resource');
					var target = $('rdf\\:Description[rdf\\:about="'+targetId+'"]', data);
					var body = $('rdf\\:Description[rdf\\:about="'+bodyId+'"]', data);
					return {
						canvasURI: targetId,
						canvasTitle: target.children('dc\\:title').text(),
						imageURI: bodyId,
						width: body.children('exif\\:width').text(),
						height: body.children('exif\\:height').text()
					};
				}
				
				var firstAnno = $('rdf\\:Description[rdf\\:about="'+firstId+'"]', data);
				annos.push(getJsonForAnno(firstAnno));
				
				function getOrder(nodeId, count) {
					var node = $('rdf\\:Description[rdf\\:nodeID="'+nodeId+'"]', data);
					var firstId = $('rdf\\:first', node).attr('rdf:resource');
					var firstAnno = $('rdf\\:Description[rdf\\:about="'+firstId+'"]', data);
					annos.push(getJsonForAnno(firstAnno));
					var restId = $('rdf\\:rest', node).attr('rdf:nodeID');
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
						this.steps[3].init(annos);
						eventManager.trigger('sequenceSelected', [annos]);
					}
				}
				getOrder.apply(this, [restId, 0]);
			}.createDelegate(this, [iaUri], true)
		});
	} else {
		var optUrl = nsUri.replace(/(\/\w*$)/, '/optimized$1.json');
		$.ajax({
			url: optUrl,
			success: $.proxy(function(data, status, xhr) {
				this.loading(false);
				this.steps[3].init(data, nsUri);
				eventManager.trigger('sequenceSelected', [data, nsUri]);
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
				eventManager.trigger('imageSelected', $(event.target).data('metadata'));
			}, this));
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