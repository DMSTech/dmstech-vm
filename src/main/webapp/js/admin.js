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
	
	function deleteSelected() {
		$('#statusDialog').dialog('open');
		$('#statusIcon').removeClass();
		$('#statusIcon').addClass('loading');
		$('#status').text('Deleting');
		var params = {};
		$('#data input').each(function(index, item) {
			if ($(item).prop('checked')) {
				params[$(item).attr('name')] = true;
			}
		});
		actionDialog.doAction('Deleting', {
			url:'http://'+host+path+'sc/reset',
			data: params
		});
	}
	
	function resetData() {
		$('#statusDialog').dialog('open');
		$('#statusIcon').addClass('loading');
		$('#status').text('Resetting');
		actionDialog.doAction('Resetting', {
			url: 'http://'+host+path+'sc/reset',
			data: {
				all: true
			}
		});
	}
	
	function init() {
		$('#resetDialog').dialog({
			autoOpen: false,
			modal: true,
			resizable: false,
			closeOnEscape: false,
			title: 'Confirm Reset',
			width: 250,
			height: 180,
			buttons: {
				'Reset Data': function() {
					$(this).dialog('close');
					resetData();
				},
				'Cancel': function() {
					$(this).dialog('close');
				}
			}
		});
		
		$('#reindex button').button().click(reindex);
		
		$('#data button:eq(0)').button().click(deleteSelected);
		$('#data button:eq(1)').button().click(function() {
			$('#resetDialog').dialog('open');
		});
	}
	
	init();
});