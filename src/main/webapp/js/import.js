$(document).ready(function() {
	var host = window.location.host;
	$.ajax({
		url: 'http://'+host+'/ingest/ingestdirectory',
		type: 'GET',
		success: function(data, status, xhr) {
			$('#directoryPath').html(data);
		},
		error: function() {
			$('#directoryPath').html('[DIRECTORY]');
		}
	});
	
	$('#subdir').keyup(function() {
		$('#subdir').removeClass('ui-state-error');
	});
	
	$('button:eq(0)').button().click(function() {
		var info = {};
		
		if ($('#subdir').val().match(/[^\w]/) != null) {
			$('#subdir').addClass('ui-state-error');
			alert('The subdirectory name may only contain letters and numbers.');
			return;
		}
		
		$('#importer input').each(function(index, el) {
			info[$(this).attr('name')] = $(this).val();
		});
		
		var collectionId = 'ingested';
		var manuscriptId = $('#subdir').val();
		
		$.ajax({
			url: 'http://'+host+'/'+collectionId+'/'+manuscriptId,
			type: 'PUT',
			data: info,
			success: function(data, status, xhr) {
				window.location = 'import_ordering.jsp';
			},
			error: function() {
				window.location = 'import_ordering.jsp';
			}
		});
	});
	$('button:eq(1)').button().click(function() {
		window.location = 'index.jsp';
	});
	
	$('.info').hover(function(event) {
		var offset = $(this).offset();
		$(this).toggleClass('hover ui-corner-all ui-state-highlight');
		$(this).offset(offset);
	}, function(event) {
		$(this).toggleClass('hover ui-corner-all ui-state-highlight');
	});
});