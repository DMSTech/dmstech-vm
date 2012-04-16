function Orderer(config) {
	
	this.id = config.id;
	
	$(document.body).append(''+
	'<div id="titleDialog"><input type="hidden" id="pageId" /><label for="pageTitle">Title</label><input type="text" id="pageTitle" /><button>Ok</button></div>'+
	'<div id="pageView"></div>');
	
	$('#titleDialog').dialog({
		draggable: false,
		resizable: false,
		modal: false,
		title: '',
		autoOpen: false,
		height: 40,
		width: 290,
		dialogClass: 'titleDialog'
	});
	
	$('#pageView').dialog({
		draggable: true,
		resizable: true,
		modal: false,
		title: 'Image Viewer',
		autoOpen: false,
		width: 530,
		position: 'top'
	});
	
	$('#pageTitle').keyup($.proxy(function(event) {
		if (event.which == 13) this.saveTitle();
	}, this));
	
	$('#titleDialog button').button({
		icons: {primary: 'ui-icon-check'}
	}).click($.proxy(function() {
		this.saveTitle();
	}, this));
}

Orderer.prototype.processSequence = function(event, data) {
	var count = 0;
	for (var i = 0; i < data.length; i++) {
		var a = data[i];
		var thumbUrl = 'img/manu_thumb.png';
		if (a.body.id.indexOf('stacks') != -1) {
			thumbUrl = a.body.id+'?w=100&h=140';
		}
		$('#orderer').append('<div id="page_'+count+'" class="page ui-corner-all"><div class="cell"><img src="'+thumbUrl+'" /></div></div>');
		$('#page_'+count)
		.data('uri', a.id)
		.data('title', a.targets[0].title)
		.data('bodyId', a.body.id)
		.data('width', a.body.width)
		.data('height', a.body.height);
		count++;
	}
	
	$.proxy(this.addPageEvents(), this);
};

Orderer.prototype.addPageEvents = function() {
	var that = this;
	$('#orderer .page').hover(function() {
		$(this).addClass('pageHover');
	}, function() {
		$(this).removeClass('pageHover');
	}).click(function() {
		$(this).siblings().removeClass('pageSelected');
		$(this).addClass('pageSelected');
		that.showTitleDialog(this);
	}).dblclick(function() {
		var bodyId = $(this).data('bodyId');
		var height = $(this).data('height');
		var width = $(this).data('width');
		var ratio = height / width;
		var dialogHeight = 500 * ratio + 50;
		$('#pageView').html('<img src="'+bodyId+'?w=500" />');
		$('#pageView').dialog('open').dialog('option', 'height', dialogHeight);
	});
};

Orderer.prototype.showTitleDialog = function(pageEl) {
	var offset = $(pageEl).offset();
	$('#titleDialog').dialog('open').dialog('option', 'position', [offset.left, offset.top+$(pageEl).outerHeight()-1]);
	$('#pageId').val($(pageEl)[0].id);
	$('#pageTitle').val($(pageEl).data('title')).focus();
};

Orderer.prototype._handleDocClick = function(event) {
	if ($(event.target).hasClass('.pageSelected') || $(event.target).parents('.pageSelected').length > 0 ||
		$(event.target).hasClass('titleDialog') || $(event.target).parents('.titleDialog').length > 0) {
		
	} else {
		$('#orderer .page').removeClass('pageSelected');
		$('#titleDialog').dialog('close');
	}
};

Orderer.prototype.saveTitle = function() {
	var id = $('#pageId').val();
	var title = $('#pageTitle').val();
	$('#'+id).data('title', title);
	$('#titleDialog').dialog('close');
};

Orderer.prototype.submit = function() {
	var collectionId = 'ingested';
	var manuscriptId = 'manuscriptId';
	var sequenceId = 'sequenceId';
	
	var baseUrl = 'http://'+window.location.host+'/'+collectionId+'/'+manuscriptId+'/';
	
	var ordering = [];
	$('#orderer .page').each(function(index, el) {
		var uri = $(this).data('uri');
		var title = $(this).data('title');
		var height = $(this).data('height');
		var width = $(this).data('width');
		ordering.push({uri: uri, title: title, height: height, width: width});
	});
	
	// sequence info
	var nTriples = '';
	nTriples += '<'+baseUrl+'NormalSequence>  <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>  <http://dms-data.stanford.edu/ns/Sequence>\n';
	nTriples += '<'+baseUrl+'NormalSequence>  <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>  <http://www.w3.org/1999/02/22-rdf-syntax-ns#List>\n';
	nTriples += '<'+baseUrl+'NormalSequence>  <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>  <http://www.openarchives.org/ore/terms/Aggregation>\n';
	for (var i = 0; i < ordering.length; i++) {
		nTriples += '<'+baseUrl+'NormalSequence>  <http://www.openarchives.org/ore/terms/aggregates>  <'+ordering[i].uri+'>\n';
	}
	// image sequence
	nTriples += '<'+baseUrl+'NormalSequence>  <http://www.w3.org/1999/02/22-rdf-syntax-ns#first>  <'+ordering[0].uri+'>\n';
	nTriples += '<'+baseUrl+'NormalSequence>  <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest>  _:genid1\n';
	for (var i = 1; i < ordering.length; i++) {
		nTriples += '_:genid'+i+'  <http://www.w3.org/1999/02/22-rdf-syntax-ns#first>  <'+ordering[i].uri+'>\n';
		nTriples += '_:genid'+i+'  <http://www.w3.org/1999/02/22-rdf-syntax-ns#rest>  _:genid'+(i+1)+'\n';
	}
	// image info
	for (var i = 0; i < ordering.length; i++) {
		var o = ordering[i];
		nTriples += '<'+o.uri+'>  <http://www.w3.org/1999/02/22-rdf-syntax-ns#type>  <http://dms.stanford.edu/ns/Canvas>\n';
		nTriples += '<'+o.uri+'>  <http://purl.org/dc/elements/1.1/title>  "'+o.title+'"\n';
		nTriples += '<'+o.uri+'>  <http://www.w3.org/2003/12/exif/ns#width>  "'+o.width+'"^^http://www.w3.org/2001/XMLSchema#int\n';
		nTriples += '<'+o.uri+'>  <http://www.w3.org/2003/12/exif/ns#height>  "'+o.height+'"^^http://www.w3.org/2001/XMLSchema#int\n';
	}
	
	
	var replaceseq = $('#sequenceAction input:checked').attr('id') == 'replaceSequence';
	var makedefault = $('#makeDefault').prop('checked');
	
	var config = {
		url: baseUrl+'sequence',
		data: {sequence: nTriples, replaceseq: replaceseq, makedefault: makedefault},
		type: 'POST',
		success: function(data, status, xhr) {
			
		},
		error: function() {
			
		}
	};
	
	if (replaceseq) {
		config.url += '/'+sequenceId;
		config.type = 'PUT';
	}
	
	$.ajax(config);
};

Orderer.prototype.activate = function() {
	$(this.id).html('<div id="orderer"></div>');
	$('#orderer').sortable({
		placeholder: "ui-state-highlight",
		forcePlaceholderSize: true,
		start: function(event, ui) {
			$('#titleDialog').dialog('close');
		}
	});
	
	$('<div id="orderButtons" class="clear">'+
		'<span>'+
			'<input type="checkbox" name="makedefault" id="makeDefault" /><label for="makeDefault">Make This Sequence the Default</label>'+
		'</span>'+
		'<span id="sequenceAction">'+
			'<input type="radio" name="replaceseq" id="replaceSequence" checked="checked"/><label for="replaceSequence">Replace Existing Sequence</label>'+
			'<input type="radio" name="replaceseq" id="createSequence"/><label for="createSequence">Create New Sequence</label>'+
		'</span>'+
		'<button id="confirmOrder">Confirm</button>'+
	'</div>').insertAfter('#orderer');
	$('#confirmOrder').button().click(this.submit);
	$('#makeDefault').button();
	$('#sequenceAction').buttonset();
	
	this.resize($(this.id).height(), $(this.id).width());
	$(document).bind('click', this._handleDocClick);
	eventManager.bind('tree.sequenceSelected', $.proxy(this.processSequence, this));
};

Orderer.prototype.deactivate = function() {
	$(document).unbind('click', this._handleDocClick);
	eventManager.unbind('tree.sequenceSelected', this.processSequence);
};

Orderer.prototype.resize = function(height, width) {
	$('#orderer').height(height - 60);
};