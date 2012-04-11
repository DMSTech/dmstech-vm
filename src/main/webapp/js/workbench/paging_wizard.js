function PagingWizard(config) {
	this.config = config;
	
	this.id = this.config.id;
	
	this.pageSize = this.config.pageSize == null ? 20 : this.config.pageSize;
	
	this.idCount = 0;
	
	this.currentStep = 0;
	
	$(this.id).html(''+
	'<div id="wizParent">'+
		'<div id="wizNav">'+
			'<span id="wizPrev" class="ui-state-default ui-corner-all"><span class="ui-icon ui-icon-triangle-1-w"></span></span>'+
		'</div>'+
		'<div id="wizSteps">'+
			'<div id="wizCollections"><h2>Collections</h2><ul></ul></div>'+
			'<div id="wizManifests"><h2>Manifests</h2><ul></ul></div>'+
			'<div id="wizImages"><h2>Images</h2><ul></ul></div>'+
		'</div>'+
		'<div id="wizPager">'+
			'<span id="pagerPrev" class="ui-state-default ui-corner-all"><span class="ui-icon ui-icon-triangle-1-w"></span></span>'+
			'Page <input type="text" id="pagerCurrent" /> of <span id="pagerTotal"></span>'+
			'<span id="pagerNext" class="ui-state-default ui-corner-all"><span class="ui-icon ui-icon-triangle-1-e"></span></span>'+
			'<div class="clear"></div>'+
		'</div>'+
	'</div>'+
	'<div id="wizLoading" class="ui-corner-all ui-widget-content"></div>');
	
	this.steps = [new WizardStep({
		wiz: this,
		id: '#wizCollections',
		pagerFunction: this.collectionsPager
	}),new WizardStep({
		wiz: this,
		id: '#wizManifests',
		pagerFunction: this.manifestsPager
	}),new WizardStep({
		wiz: this,
		id: '#wizImages',
		pagerFunction: this.imagesPager
	})];
	
	$('#wizLoading').offset($(this.id).offset());
	
	$('#wizPrev').hover(function() {
		$(this).toggleClass('ui-state-hover');
	}).click($.proxy(function() {
		if (this.currentStep > 0) {
			this.showStep(this.currentStep - 1);
		}
	}, this));
	$('#pagerPrev').hover(function() {
		$(this).toggleClass('ui-state-hover');
	}).click($.proxy(function() {
		this.getPage('prev');
	}, this));
	$('#pagerNext').hover(function() {
		$(this).toggleClass('ui-state-hover');
	}).click($.proxy(function() {
		this.getPage('next');
	}, this));
	
	$('#pagerCurrent').keyup($.proxy(function(event) {
		if (event.which == 13) {
			var page = $('#pagerCurrent').val();
			if (page.match(/\D/) == null) {
				this.getPage(parseInt(page));
			}
		}
	}, this));
	
	$('#wizManifests').hide();
	$('#wizImages').hide();
	$('#wizPager').hide();
	
	this.initCollections();
}

PagingWizard.prototype.loading = function(isLoading) {
	if (isLoading) {
		$('#wizLoading')
			.outerHeight($(this.id).outerHeight())
			.outerWidth($(this.id).outerWidth())
			.show();
	} else {
		$('#wizLoading').hide();
	}
};

PagingWizard.prototype.getPage = function(opt) {
	var currLev = this.steps[this.currentStep];
	
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

	$('#pagerCurrent').val(currLev.currentPage+1);
};

PagingWizard.prototype.showStep = function(index) {
	this.steps[this.currentStep].hide();
	this.currentStep = index;
	this.steps[this.currentStep].show();
};

PagingWizard.prototype.initCollections = function() {
	this.loading(true);
	$.ajax({
		url: 'http://'+window.location.host+proxyString,
		data: {
			url: 'http://dms-data.stanford.edu/Repository.xml'
		},
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
				$.ajax({
					url: 'proxy.jsp',
					data: {
						url: uris[i],
					},
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
							this.steps[0].init(localCollections);
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
	$('#wizCollections ul').empty();
	
	for (var i = 0; i < collections.length; i++) {
		var c = collections[i];
		$('#wizCollections ul').append('<li id="col_'+this.idCount+'">'+c.data+'</li>');
		$('#col_'+this.idCount).data('uris', c.metadata.uris);
		this.idCount++;
	}
	
	$('#wizCollections li').click($.proxy(function(event) {
		this.steps[1].init(($(event.target).data('uris')));
	}, this));
	
	this.showStep(0);
};

PagingWizard.prototype.manifestsPager = function(pageNum, uris) {
	$('#wizManifests ul').empty();
	
	function buildPage(liString, cache) {
		$('#wizManifests ul').append(liString);
		
		$('#wizManifests li').each($.proxy(function(index, el) {
			var data = cache[index];
			$(el).data('iaUri', data.iaUri);
			$(el).data('nsUri', data.nsUri);
			$(el).click($.proxy(function(event) {
				$.proxy(this.fetchSequence($(event.target).data('iaUri')), this);
			}, this));
		}, this));
	}
	
	var cache = $('#wizManifests').data('page'+pageNum);
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
					
					cache.push({title: title, nsUri: nsUri, iaUri: iaUri});
					
					liString += '<li id="man_'+this.idCount+'">'+title+'</li>';
					this.idCount++;
					
					if (cache.length == uris.length) {
						this.loading(false);
						
						buildPage.apply(this, [liString, cache]);
						
						this.showStep(1);
						
						$('#wizManifests').data('page'+pageNum, cache);
					}
					
				}, this)
			});
		}
	}
};

PagingWizard.prototype.fetchSequence = function(iaUri) {
	this.loading(true);
	
	var qry = $.rdf(opts);
	function parseAnnotations(qry, uri) {
		var annotations = buildAllAnnos(qry);
		eventManager.trigger('tree.sequenceSelected', [annotations]);
		this.loading(false);
		this.steps[2].init(annotations);
	}
	fetchTriples('http://'+window.location.host+proxyString+'?url='+iaUri, qry, $.proxy(parseAnnotations, this));
};

PagingWizard.prototype.imagesPager = function(pageNum, annotations) {
	$('#wizImages ul').empty();
	
	function buildPage(liString, cache) {
		$('#wizImages ul').append(liString);
		
		$('#wizImages li').each($.proxy(function(index, el) {
			var data = cache[index];
			$(el).data('metadata', {title: data.title, bodyId: data.bodyId, width: data.width, height: data.height});
			$(el).click($.proxy(function(event) {
				eventManager.trigger('tree.imageSelected', $(event.target).data('metadata'));
			}, this));
		}, this));
	}
	
	var cache = $('#wizImages').data('page'+pageNum);
	var liString = '';
	if (cache != null) {
		for (var i = 0; i < cache.length; i++) {
			var data = cache[i];
			liString += '<li id="img_'+this.idCount+'">'+data.title+'</li>';
			this.idCount++;
		}
		buildPage.apply(this, [liString, cache]);
	} else {
		cache = [];
		for (var i = 0; i < annotations.length; i++) {
			var a = annotations[i];
			cache.push({title: a.targets[0].title, bodyId: a.body.id, width: a.body.width, height: a.body.height});
			
			liString += '<li id="img_'+this.idCount+'">'+a.targets[0].title+'</li>';
			this.idCount++;
		}
		buildPage.apply(this, [liString, cache]);
		this.showStep(2);
		$('#wizImages').data('page'+pageNum, cache);
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

WizardStep.prototype.init = function(items) {
	var totalPages = Math.ceil(items.length / this.wiz.pageSize);
	
	$(this.id).removeData();
	
	this.cache = items;
	this.currentPage = 0;
	this.totalPages = totalPages;
	
	if (items.length > this.wiz.pageSize) {
		$('#wizPager').show();
		$('#pagerCurrent').val(1);
		$('#pagerTotal').html(totalPages);
		var curritems = items.slice(0, this.wiz.pageSize);
		this.pagerFunction.apply(this.wiz, [0, curritems]);
	} else {
		$('#wizPager').hide();
		this.pagerFunction.apply(this.wiz, [0, items]);
	}
};

WizardStep.prototype.show = function() {
	$(this.id).show();
	if (this.cache.length > this.wiz.pageSize) {
		$('#pagerCurrent').val(this.currentPage+1);
		$('#pagerTotal').html(this.totalPages);
		$('#wizPager').show();
	} else {
		$('#wizPager').hide();
	}
};

WizardStep.prototype.hide = function() {
	$(this.id).hide();
};