$(document).ready(function() {
	var host = window.location.host;
	var path = window.location.pathname.match(/^.*\//)[0];
	$.ajax({
		url: 'http://'+host+path+'sc/lookup/ingestdirectory',
		type: 'GET',
		success: function(data, status, xhr) {
			$('#directoryPath').html(data);
		},
		error: function() {
			alert('There was an error getting the ingest directory.');
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
			url: 'http://'+host+'/dms/sc/'+collectionId+'/'+manuscriptId,
			type: 'PUT',
			data: info,
			success: function(data, status, xhr) {
				console.log(data, status);
//				window.location = 'import_ordering.jsp';
			},
			error: function(xhr, status, msg) {
				console.log(status, msg);
//				window.location = 'import_ordering.jsp';
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