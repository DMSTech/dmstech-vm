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
		var zip = $('#zipupload')[0];
		var collectionId = 'ingested';
		var manuscriptId = $('#subdir').val();
		
		if (manuscriptId == '') {
			$('#subdir').addClass('ui-state-error');
			alert('You must enter a subdirectory name.');
			return;
		} else if (manuscriptId.match(/[^\w]/) != null) {
			$('#subdir').addClass('ui-state-error');
			alert('The subdirectory name may only contain letters and numbers.');
			return;
		}
		
		var formData = new FormData();
		$('#importer input[type!="file"]').each(function(index, el) {
			formData.append($(this).attr('name'), $(this).val());
		});
		
		if (zip.files.length > 0) {
			formData.append('file', zip.files[0]);
		}
		
		// need to use this, since jquery ajax doesn't support formdata object
		var req = new XMLHttpRequest();
		req.addEventListener('load', function(e) {
			if (req.status == '201') {
				window.location = 'import_ordering.jsp?uri='+request.responseText;
			} else {
				alert('An error has occurred: '+req.statusText);
			}
		}, false);
		req.addEventListener('error', function(e) {
			alert('An error has occurred: '+req.statusText);
		}, false);
		
		req.open('PUT', 'http://'+host+path+'sc/'+collectionId+'/'+manuscriptId);
		req.send(formData);
		
//		$.ajax({
//			url: 'http://'+host+path+'sc/'+collectionId+'/'+manuscriptId,
//			type: 'PUT',
//			data: info,
//			success: function(data, status, xhr) {
//			},
//			error: function(xhr, status, msg) {
//			}
//		});
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