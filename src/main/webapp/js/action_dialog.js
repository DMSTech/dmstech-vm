function ActionDialog() {
	$(document).ready(function() {
		$(document.body).append(''+
		'<div id="actionDialog">'+
			'<div id="statusParent"><span id="statusIcon"></span><span id="status"></span></div>'+
		'</div>');
		
		$('#actionDialog').dialog({
			autoOpen: false,
			modal: true,
			resizable: false,
			closeOnEscape: false,
			title: 'Status',
			width: 150,
			height: 125,
			buttons: {
				'Close': function() {
					$(this).dialog('close');
				}
			}
		});
		
		$('#actionDialog').prev().children('a').hide(); // hide close button
	});
}

ActionDialog.prototype.doAction = function(action, config) {
	$('#actionDialog').dialog('open');
	$('#actionDialog').next().find('button').button('disable');
	$('#statusIcon').removeClass();
	$('#statusIcon').addClass('loading');
	$('#status').text(action);
	$.ajax({
		url: config.url,
		type: config.type || 'POST',
		data: config.data,
		success: function(data, status, xhr) {
			$('#actionDialog').next().find('button').button('enable');
			$('#statusIcon').removeClass('loading');
			$('#statusIcon').addClass('ui-icon ui-icon-check');
			$('#status').text('Success!');
			if (config.callback) {
				config.callback.call(this, true);
			}
		},
		error: function(xhr, status, error) {
			$('#actionDialog').next().find('button').button('enable');
			$('#statusIcon').removeClass('loading');
			$('#statusIcon').addClass('ui-icon ui-icon-notice');
			$('#status').text('Error: '+status);
			if (config.callback) {
				config.callback.call(this, false);
			}
		}
	});
};

var actionDialog = new ActionDialog();