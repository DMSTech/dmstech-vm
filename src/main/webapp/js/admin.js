$(document).ready(function() {
	var host = window.location.host;
	var path = window.location.pathname.match(/^.*\//)[0];
	
	function reindex() {
		var solr = $('#reindex input[name=solr]').prop('checked');
		var sparql = $('#reindex input[name=sparql]').prop('checked');
		actionDialog.doAction('Indexing', {
			url: 'http://'+host+path+'sc/reindex',
			data: {
				solr: solr, sparql: sparql
			}
		});
	}
	
	function resetData() {
		$('#statusDialog').dialog('open');
		$('#statusIcon').removeClass();
		$('#statusIcon').addClass('loading');
		$('#status').text('Resetting');
		actionDialog.doAction('Resetting', {
			url: 'http://'+host+path+'sc/reset',
			data: {
				all: true
			}
		});
	}
	
	function deleteTpen() {
		$('#statusDialog').dialog('open');
		$('#statusIcon').removeClass();
		$('#statusIcon').addClass('loading');
		$('#status').text('Deleting');
		actionDialog.doAction('Deleting', {
			url: 'http://'+host+'/TPEN/delete'
		});
	}
	
	function init() {
		$('#reindex button').button().click(reindex);
		
		$('#data button').button().click(function() {
			actionDialog.confirm({
				title: 'Confirm Reset',
				query: "Are you sure?<br/>This will delete all new data you've entered including: ingested collections, annotations, logs, and recorded transactions.",
				accept: 'Reset Data',
				callback: function(doReset) {
					if (doReset) resetData();
				}
			});
		});
	}
	
	init();
});