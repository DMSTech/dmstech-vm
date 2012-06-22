$(document).ready(function() {
	var currentSearchTool = 'solr';
	
	var host = window.location.host;
	var path = window.location.pathname.match(/^.*\//)[0];
	var solrUrl = null;
	$.ajax({
		url: 'http://'+host+path+'sc/lookup/solr',
		type: 'GET',
		success: function(data, status, xhr) {
			solrUrl = data;
		},
		error: function() {
			alert('There was an error getting the solr url.');
		}
	});
	
	var logicSelect = '<select name="logic">'+
	'<option value="">Or</option>'+
	'<option value="+">And</option>'+
	'<option value="-">Not</option>'+
	'</select>';
	
	var fieldSelect = '<select name="field">'+
	'<option value="text">Anywhere</option>'+
	'<option value="mantitle">Manuscript Title</option>'+
	'<option value="canvastitle">Canvas Title</option>'+
	'<option value="annotationtext">Annotation Text</option>'+
	'<option value="institution">Institution</option>'+
	'<option value="settlement">Settlement</option>'+
	'<option value="region">Region</option>'+
	'<option value="country">Country</option>'+
	'<option value="repository">Repository</option>'+
	'<option value="collection">Collection</option>'+
	'<option value="uri">URI</option>'+
	'<option value="altid">Alternate ID</option>'+
	'<option value="idno">Idno</option>'+
	'</select>';
	
	function buildRow(firstRow) {
		var rowString = '<div class="searchRow"';
		if (firstRow) {
			rowString += ' id="firstRow">Search';
		} else {
			rowString += '>'+logicSelect;
		}
		rowString += fieldSelect;
		rowString += 'for';
		rowString += '<input type="text" name="query" />';
		if (firstRow) {
			rowString += '<button title="Add a row">+</button>';
		} else {
			rowString += '<button title="Remove a row">&#8211;</button>';
		}
		rowString += '</div>';
		
		$('#search').append(rowString);
		
		if (firstRow) {
			$('#search > div:last button').button().click(function(event) {
				buildRow();
			});
		} else {
			$('#search > div:last button').button().click(function(event) {
				$(event.target).parent().remove();
			});
		}
		
		$('#search > div:last input').on('keyup', function(event) {
			if (event.which == 13) {
				if (currentSearchTool == 'solr') {
					doSolrSearch();
				} else {
					doSparqlSearch();
				}
			}
		});
	}
	
	function init() {
		$(window).resize(doResize);
		
		$('#searchSelector').buttonset();
		$('#searchSelector input').click(function(event) {
			if ($(event.target).attr('id').match('solr')) {
				$('#solrSearch').show();
				$('#sparqlSearch').hide();
				currentSearchTool = 'solr';
			} else {
				$('#solrSearch').hide();
				$('#sparqlSearch').show();
				currentSearchTool = 'sparql';
			}
		});
		$('#searchSelector input:first').click();
		$('#sparqlSearch textarea').val('SELECT ?x ?mname WHERE {?x <http://purl.org/dc/elements/1.1/identifier> ?mname}');
		
		buildRow(true);
		
		$('#searchButton').button().click(function() {
			if (currentSearchTool == 'solr') {
				doSolrSearch();
			} else {
				doSparqlSearch();
			}
		});
		$('#clearButton').button().click(function() {
			if (currentSearchTool == 'solr') {
				$('#search').empty();
				buildRow(true);
			} else {
				$('#sparqlQuery').val('');
			}
		});
	}
	
	function doSolrSearch() {
		var queryString = '';
		
		$('#search > div').each(function(index, item) {
			var logic = $('select[name="logic"]', item).val();
			if (index == 0) logic = '';
			var field = $('select[name="field"]', item).val();
			var query = $('input[name="query"]', item).val();
			if (query != '') {
				query = query.replace(/\:/g, '\\:');
				if (query.match(/\"/) == null && query.match(/\s/) != null) {
					var words = query.split(/\s/);
					for (var i = 0; i < words.length; i++) {
						queryString += logic+field+':'+words[i]+' ';
					}
				} else {
					queryString += logic+field+':'+query+' ';
				}
			}
		});
		
		if (queryString == '') queryString = '*:*'; // return everything
		
		$.ajax({
			url: solrUrl+'/select',
			data: {
				q: queryString,
				wt: 'json'
			},
			dataType: 'json',
			success: function(data, status, xhr) {
				$('#results').empty();
				
				var docs = data.response.docs;
				$('#results').html('<ul></ul>');
				var ul = $('#results ul');
				if (docs.length == 0) {
					ul.append('<li>No results.</li>');
				} else {
					for (var i = 0; i < docs.length; i++) {
						var d = docs[i];
						var type = d.resulttype;
						var title = 'Manuscript: ' + d.mantitle || 'No title available';
						if (type == 'canvas') {
							title += '<br/>Page: ';
							title += d.canvastitle || 'No title available';
						}
						var digitalLocation = '';
						var physicalLocation = '';
						var locationKeys = ['repository','collection','institution','settlement','region','country'];
						for (var j = 0; j < 2; j++) {
							var l = d[locationKeys[j]];
							if (l != undefined && l != '') {
								digitalLocation += l +', ';
							}
						}
						digitalLocation = digitalLocation.slice(0, digitalLocation.length-2);
						for (var j = 2; j < locationKeys.length; j++) {
							var l = d[locationKeys[j]];
							if (l != undefined && l != '') {
								physicalLocation += l +', ';
							}
						}
						physicalLocation = physicalLocation.slice(0, physicalLocation.length-2);
						
						ul.append(''+
						'<li>'+
							'<div class="title">'+title+'</div>'+
							'<div class="type"><span class="label">Type:</span> '+type+'</div>'+
							'<div class="digitalLocation"><span class="label">Digital Location:</span> '+digitalLocation+'</div>'+
							'<div class="physicalLocation"><span class="label">Physical Location:</span> '+physicalLocation+'</div>'+
							'<div class="idno"><span class="label">Idno:</span> '+d.idno+'</div>'+
							'<div class="altid"><span class="label">Alt. ID:</span> '+d.altid+'</div>'+
						'</li>');
						
						$('li:last', ul).data({
							type: type,
							uri: d.uri,
							manuri: d.manuri
						});
					}
				}
				
				$('li', ul).click(function(event) {
					var li;
					if ($(event.target).is('li')) {
						li = $(event.target);
					} else {
						li = $(event.target).parents('li').first();
					}
					
					var data = li.data();
					var type = data.type;
					var uri = data.uri;
					var manuri = data.manuri;
					if (manuri.match(/\.xml$/) == null) manuri += '.xml';

					var url = 'workbench.jsp?manifest='+encodeURIComponent(manuri);
					if (type == 'canvas') {
						url += '&canvas='+uri;
					}
					
					window.location = url;
				});
			}
		});
	}
	
	function doSparqlSearch() {
		var queryString = $('#sparqlQuery').val();
		$.ajax({
			url: 'http://'+host+path+'sc/sparql',
			data: {
				q: queryString
			},
			dataType: 'xml',
			success: function(data, status, xhr) {
				$('#results').empty();
				var resultString = '<ul>';
				var results = $('result', data);
				if (results.length == 0) {
					resultString += '<li>No results.</li>';
				} else {
					results.each(function(index, item) {
						resultString += '<li>';
						$('binding', item).each(function(index2, item2) {
							resultString += '<div><span class="label">'+$(item2).attr('name')+': </span>'+$(item2).children().text()+'</div>';
						});
						resultString += '</li>';
					});
				}
				resultString += '</ul>';
				$('#results').html(resultString);
			},
			error: function(xhr, status, error) {
				var text = xhr.responseText;
				var msg = text.match(/<pre>.*(?=\n|\r)/);
				if (msg != null) {
					msg = msg[0].replace('<pre>', '');
				}
				$('#results').empty();
				var resultString = '<ul><li class="error">'+msg+'</li></ul>';
				$('#results').html(resultString);
			}
		});
	}
	
	function doResize() {
		var height = $(window).height() - 84;
		$('#results').height(height);
	}
	
	init();
	doResize();
});