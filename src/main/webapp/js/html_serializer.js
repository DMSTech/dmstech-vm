$(document).ready(function() {
	var host = window.location.host;
	var path = window.location.pathname.match(/^.*\//)[0];
	
	var url = getParameterByName('uri');
	
	var xmlString = '';
	var turtleString = '';
	
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
	
	$('button').button().click(function() {
		if ($(this).text() == 'Turtle') {
			$('#turtleDialog pre').html(turtleString);
			$('#turtleDialog').dialog('open');
		} else {
			$('#rdfDialog pre').html(xmlString);
			$('#rdfDialog').dialog('open');
		}
	});
	
	$('#types h1').text(url);
	
	var ajaxUrl = url;
	if (ajaxUrl.match(host) == null) {
		ajaxUrl = 'http://'+host+path+'proxy.jsp?url='+ajaxUrl;
	}
	$.ajax({
		url: ajaxUrl,
		headers: { 
	        Accept : "application/rdf+xml; charset=utf-8",
	        "Content-Type": "application/rdf+xml; charset=utf-8"
	    },
		success: function(data, status, xhr) {
			var desc = $(data).find('rdf\\:Description, Description').filter('[rdf\\:about="'+url+'"]');
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
						'<a href="'+resource+'">'+resource+'</a>'+
						'<div class="extraLinks">'+
							'Link formats: '+
							'<a href="'+resource+'.html">HTML</a> | '+
							'<a href="'+resource+'.xml">RDF/XML</a> | '+
							'<a href="'+resource+'.ttl">Turtle</a>'+
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
			$(data).find('rdf\\:Description, Description').not('[rdf\\:about="'+url+'"]').each(function(index, el) {
				var rdf = $(el).attr('rdf:about');
				if (rdf != undefined && rdf != '' && rdf.match(/\.xml$/) == null) {
					rdfString += '<li><a href="http://'+host+path+'html_serializer.jsp?uri='+rdf+'">'+rdf+'</a></li>';
				}
			});
			rdfString += '</ul>';
			$('#rdfs').append(rdfString);
			
			xmlString = xmlToString(data);
			xmlString = xmlString.replace(/</g, '&lt;').replace(/>/g, '&gt;');
		}
	});
	
	$.ajax({
		url: ajaxUrl,
		headers: { 
	        Accept : "text/turtle; charset=utf-8",
	        "Content-Type": "text/turtle; charset=utf-8"
	    },
		success: function(data, status, xhr) {
			turtleString = data.replace(/</g, '&lt;').replace(/>/g, '&gt;');
		}
	});
});