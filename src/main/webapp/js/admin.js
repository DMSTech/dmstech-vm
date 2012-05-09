$(document).ready(function() {
	var host = window.location.host;
	var path = window.location.pathname.match(/^.*\//)[0];
	
	function doAction(action, url, params) {
		$('#statusDialog').dialog('open');
		$('#statusIcon').removeClass();
		$('#statusIcon').addClass('loading');
		$('#status').text(action);
		$.ajax({
			url: url,
			type: 'POST',
			data: params,
			success: function(data, status, xhr) {
				$('#statusIcon').removeClass('loading');
				$('#statusIcon').addClass('ui-icon ui-icon-check');
				$('#status').text('Success!');
			},
			error: function() {
				$('#statusIcon').removeClass('loading');
				$('#statusIcon').addClass('ui-icon ui-icon-notice');
				$('#status').text('Error!');
			}
		});
	}
	
	function reindex() {
		var solr = $('#reindex input[name=solr]').prop('checked');
		var sparql = $('#reindex input[name=sparql]').prop('checked');
		doAction('Indexing', 'http://'+host+path+'sc/reindex', {
			solr: solr, sparql: sparql
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
		doAction('Deleting', 'http://'+host+path+'sc/reset', params);
	}
	
	function resetData() {
		$('#statusDialog').dialog('open');
		$('#statusIcon').addClass('loading');
		$('#status').text('Resetting');
		doAction('Resetting', 'http://'+host+path+'sc/reset', {
			all: true
		});
	}
	
	function init() {
		$('#statusDialog').dialog({
			autoOpen: false,
			modal: true,
			resizable: false,
			closeOnEscape: false,
			title: 'Status',
			width: 150,
			height: 75
		});
		
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