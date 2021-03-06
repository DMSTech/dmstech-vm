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
			title: '',
			width: 250,
			height: 190
		});
		
		$('#actionDialog').prev().children('a').hide(); // hide close button
	});
}

ActionDialog.prototype.confirm = function(config) {
	var title = config.title || 'Confirm';
	var accept = config.accept || 'Yes';
	$('#actionDialog').dialog('option', 'buttons', [{
		text: accept,
		click: function() {
			$(this).dialog('close');
			config.callback.call(this, true);
		}
	},{
		text: 'Cancel',
		click: function() {
			$(this).dialog('close');
			config.callback.call(this, false);
		}
	}]);
	$('#actionDialog').dialog('option', 'title', title);
	$('#actionDialog').dialog('open');
	$('#statusIcon').removeClass();
	$('#statusIcon').addClass('ui-icon ui-icon-alert');
	$('#status').html(config.query);
};

ActionDialog.prototype.doAction = function(action, config) {
	$('#actionDialog').dialog('option', 'buttons', {
		'Close': function() {
			$(this).dialog('close');
		}
	});
	$('#actionDialog').dialog('option', 'title', 'Status');
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