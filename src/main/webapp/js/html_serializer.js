$(document).ready(function() {
	var host = window.location.host;
	var path = window.location.pathname.match(/^.*\//)[0];
	
	var url = getParameterByName('uri');
	
	$('#types h1').text(url);
	
	var xmlUrl = url;
	if (xmlUrl.match(host) == null) {
		xmlUrl = 'http://'+host+path+'proxy.jsp?url='+xmlUrl;
	}
	if (xmlUrl.match(/\.xml$/) == null) {
		xmlUrl += '.xml';
	}
	$.ajax({
		url: xmlUrl,
		accepts: 'application/rdf+xml',
		success: function(data, status, xhr) {
			var desc = $(data).find('rdf\\:Description, Description').filter('[rdf\\:about="'+url+'"]');
			var typesString = '';
			desc.find('rdf\\:type, type').each(function(index, el) {
				if (index == 0) {
					typesString += 'This resource is defined as a <b>';
				} else {
					typesString += '</b><br/>and a <b>';
				}
				typesString += $(el).attr('rdf:resource');
			});
			$('#types p').html(typesString);
			
			var assocString = '<ul>';
			var propsString = '<ul>';
			desc.find('*').not('rdf\\:type, type').each(function(index, el) {
				var name = el.nodeName;
				var localName = el.localName || el.baseName; // IE uses baseName
				name = name.replace(localName, '<b>'+localName+'</b>');
				var text = $(el).text();
				var resource = $(el).attr('rdf:resource');
				if (text != '') {
					propsString += '<li><span class="label">'+name+':</span>'+text+'</li>';
				} else if (resource != '') {
					assocString += '<li><span class="label">'+name+'</span>'+resource+'</li>';
				}
			});
			assocString += '</ul>';
			propsString += '</ul>';
			$('#assocations').append(assocString);
			$('#assocations').append(propsString);
			
			var rdfString = '<ul>';
			$(data).find('rdf\\:Description, Description').not('[rdf\\:about="'+url+'"]').each(function(index, el) {
				var rdf = $(el).attr('rdf:about');
				rdfString += '<li><a href="http://'+host+path+'html_serializer.jsp?uri='+rdf+'">'+rdf+'</a></li>';
			});
			rdfString += '</ul>';
			$('#rdfs').append(rdfString);
			
			var xmlString = xmlToString(data);
			xmlString = xmlString.replace(/</g, '&lt;').replace(/>/g, '&gt;');
			$('#rdfCode div.section').html('<pre>'+xmlString+'</pre>');
		}
	});
	
	var ttlUrl = url;
	if (ttlUrl.match(host) == null) {
		ttlUrl = 'http://'+host+path+'proxy.jsp?url='+ttlUrl;
	}
	if (ttlUrl.match(/\.ttl$/) == null) {
		ttlUrl += '.ttl';
	}
	$.ajax({
		url: ttlUrl,
		accepts: 'text/turtle',
		success: function(data, status, xhr) {
			var ttlString = data.replace(/</g, '&lt;').replace(/>/g, '&gt;');
			$('#turtleCode div.section').html('<pre>'+ttlString+'</pre>');
		}
	});
});