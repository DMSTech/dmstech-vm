function HTMLSerializer(config) {
	this.id = config.id;
	
	this.xmlString = '';
	this.turtleString = '';
	
	$(document.body).append(''+
	'<div id="rdfDialog">'+
		'<pre></pre>'+
	'</div>'+
	'<div id="turtleDialog">'+
		'<pre></pre>'+
	'</div>');
	
	$('#rdfDialog').dialog({
		autoOpen: false,
		modal: false,
		resizable: true,
		closeOnEscape: true,
		title: 'RDF/XML',
		width: 640,
		height: 480
	});
	$('#turtleDialog').dialog({
		autoOpen: false,
		modal: false,
		resizable: true,
		closeOnEscape: true,
		title: 'Turtle',
		width: 640,
		height: 480
	});
}

HTMLSerializer.prototype.loadUri = function(uri) {
	$('#htmlSerializer').show();
	$(this.id+' ul').remove();
	
	var host = window.location.host;
	var path = window.location.pathname.match(/^.*\//)[0];
	
	$('#types h1').text(uri);
	
	var ajaxUrl = uri;
	if (ajaxUrl.match(host) == null) {
		ajaxUrl = 'http://'+host+path+'proxy.jsp?url='+ajaxUrl;
	}
	$.ajax({
		url: ajaxUrl,
		headers: { 
	        Accept : "application/rdf+xml; charset=utf-8",
	        "Content-Type": "application/rdf+xml; charset=utf-8"
	    },
		success: $.proxy(function(data, status, xhr) {
			var desc = $(data).find('rdf\\:Description, Description').filter('[rdf\\:about="'+uri+'"]');
			var typesString = '<ul>';
			desc.find('rdf\\:type, type').each(function(index, el) {
				typesString += '<li>'+$(el).attr('rdf:resource')+'</li>';
			});
			typesString += '</ul>';
			$('#types').append(typesString);
			
			var assocString = '<ul>';
			var propsString = '<ul>';
			desc.find('*').not('rdf\\:type, type').each(function(index, el) {
				var name = el.nodeName;
				var localName = el.localName || el.baseName; // IE uses baseName
				name = name.replace(localName, '<b>'+localName+'</b>');
				var text = $(el).text();
				var resource = $(el).attr('rdf:resource');
				if (text != undefined && text != '') {
					propsString += '<li><div class="label">'+name+'</div><div class="info">'+text+'</div></li>';
				} else if (resource != undefined && resource != '') {
					assocString += '<li>'+
					'<div class="label">'+name+'</div>'+
					'<div class="link">'+
						'<a href="'+resource+'" target="_blank">'+resource+'</a>'+
						'<div class="extraLinks">'+
							'Link formats: '+
							'<a href="'+resource+'.html" target="_blank">HTML</a> | '+
							'<a href="'+resource+'.xml" target="_blank">RDF/XML</a> | '+
							'<a href="'+resource+'.ttl" target="_blank">Turtle</a>'+
						'</div>'+
					'</div>'+
					'</li>';
				}
			});
			assocString += '</ul>';
			propsString += '</ul>';
			$('#associations').append(assocString);
			$('#associations').append(propsString);
			
			$('#associations div.link').hover(function() {
				$(this).toggleClass('hover');
			});
			
			var rdfString = '<ul>';
			$(data).find('rdf\\:Description, Description').not('[rdf\\:about="'+uri+'"]').each(function(index, el) {
				var rdf = $(el).attr('rdf:about');
				if (rdf != undefined && rdf != '' && rdf.match(/\.xml$/) == null) {
					rdfString += '<li><a href="http://'+host+path+'html_serializer.jsp?uri='+rdf+'" target="_blank">'+rdf+'</a></li>';
				}
			});
			rdfString += '</ul>';
			$('#rdfs').append(rdfString);
			
			this.xmlString = xmlToString(data);
			this.xmlString = this.xmlString.replace(/</g, '&lt;').replace(/>/g, '&gt;');
		}, this)
	});
	
	$.ajax({
		url: ajaxUrl,
		headers: { 
	        Accept : "text/turtle; charset=utf-8",
	        "Content-Type": "text/turtle; charset=utf-8"
	    },
		success: $.proxy(function(data, status, xhr) {
			if (data.replace) {
				this.turtleString = data.replace(/</g, '&lt;').replace(/>/g, '&gt;');
			} else {
				this.turtleString = '';
			}
		}, this)
	});
};

HTMLSerializer.prototype.showTurtle = function(ttl) {
	$('#turtleDialog pre').html(ttl);
	$("#turtleDialog").dialog('open');
};

HTMLSerializer.prototype.showRDF = function(rdf) {
	$('#rdfDialog pre').html(rdf);
	$('#rdfDialog').dialog('open');
};

HTMLSerializer.prototype.handleRepository = function(event, data) {
	var uri = data.uri.replace(/\.\w{3}$/, '');
	$.proxy(this.loadUri(uri), this);
};

HTMLSerializer.prototype.handleCollection = function(event, data) {
	var uri = data.parent.replace(/\.\w{3}$/, '');
	$.proxy(this.loadUri(uri), this);
};

HTMLSerializer.prototype.handleSequence = function(event, data, uris) {
	var uri = uris.parent.replace(/\.\w{3}$/, '');
	$.proxy(this.loadUri(uri), this);
};

HTMLSerializer.prototype.handleImage = function(event, data) {
	$.proxy(this.loadUri(data.canvasURI), this);
};

HTMLSerializer.prototype.activate = function() {
	$(this.id).html(''+
	'<div id="htmlSerializer">'+
		'<div id="types" class="box">'+
			'<h1></h1>'+
			'<div class="buttons">'+
				'<button>RDF/XML</buton> <button>Turtle</button>'+
			'</div>'+
			'<h2 style="margin-top: 15px;">Resource Definitions</h2>'+
		'</div>'+
		'<div id="associations" class="box">'+
			'<h2>Other Associations for this Resource</h2>'+
		'</div>'+
		'<div id="rdfs" class="box">'+
			'<h2>Other Resource Descriptions Returned when Deferencing this Resource</h2>'+
		'</div>'+
	'</div>');
	
	$('#htmlSerializer').hide();
	
	$(this.id+' button').button().click($.proxy(function(event) {
		if ($(event.target).text() == 'Turtle') {
			this.showTurtle(this.turtleString);
		} else {
			this.showRDF(this.xmlString);
		}
	}, this));
	
	this.resize($(this.id).height(), $(this.id).width());
	
	eventManager.bind('repositorySelected', $.proxy(this.handleRepository, this));
	eventManager.bind('collectionSelected', $.proxy(this.handleCollection, this));
	eventManager.bind('sequenceSelected', $.proxy(this.handleSequence, this));
	eventManager.bind('imageSelected', $.proxy(this.handleImage, this));
};

HTMLSerializer.prototype.deactivate = function() {
	$(this.id).empty();
	eventManager.unbind('repositorySelected', this.handleRepository);
	eventManager.unbind('collectionSelected', this.handleCollection);
	eventManager.unbind('sequenceSelected', this.handleSequence);
	eventManager.unbind('imageSelected', this.handleImage);
};

HTMLSerializer.prototype.resize = function(height, width) {
	$('#htmlSerializer').height(height-10).width(width-10);
};