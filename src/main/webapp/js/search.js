$(document).ready(function() {
	var host = window.location.host;
	
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
				doSearch();
			}
		});
	}
	
	function init() {
		$(window).resize(doResize);
		
		buildRow(true);
		
		$('#searchButton').button().click(function() {
			doSearch();
		});
		$('#clearButton').button().click(function() {
			$('#search').empty();
			buildRow(true);
		});
	}
	
	function doSearch() {
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
			url: 'http://'+host+'/solr/select/',
			data: {
				q: queryString,
				wt: 'json'
			},
			dataType: 'json',
			success: function(data, status, xhr) {
				$('#results').empty();
				
				var docs = data.response.docs;
				var resultString = '<ul>';
				if (docs.length == 0) {
					resultString += '<li>No results.</li>';
				}
				for (var i = 0; i < docs.length; i++) {
					var d = docs[i];
					var type = d.resulttype;
					var title = '';
					if (type == 'canvas') {
						title = d.canvastitle;
					} else {
						title = d.mantitle;
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
					
					resultString += '<li>'+
						'<div class="title">'+title+'</div>'+
						'<div class="digitalLocation"><span class="label">Digital Location:</span> '+digitalLocation+'</div>'+
						'<div class="physicalLocation"><span class="label">Physical Location:</span> '+physicalLocation+'</div>'+
						'<div class="idno"><span class="label">Idno:</span> '+d.idno+'</div>'+
						'<div class="altid"><span class="label">Alt. ID:</span> '+d.altid+'</div>'+
						'<input type="hidden" name="uri" value="'+d.uri+'" />'+
					'</li>';
				}
				resultString += '</ul>';
				
				$('#results').html(resultString);
			}
		});
	}
	
	function doResize() {
		var height = $(window).height() - 60;
		$('#results').height(height);
	}
	
	init();
	doResize();
});