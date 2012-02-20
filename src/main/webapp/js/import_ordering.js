$(document).ready(function() {
	$('#sidebar button').button().click(saveMetadata);
	$('#main button').button();
	
	var idCount = 0;
	function generateId() {
		return 'page_'+idCount++;
	}
	
	for (var i = 0; i < 20; i++) {
		$('#ordering').append('<div id="'+generateId()+'" class="page ui-corner-all"><img src="img/manu_thumb.png" /></div>');
		$('#ordering div:last').dblclick(loadMetadata).data('title', 'Title '+i);
	}
	
	function loadMetadata() {
		var data = $(this).data();
		for (var key in data) {
			$('#metadata input[name="'+key+'"]').val(data[key]);
		}
		$('#metadata input[name="id"]').val($(this).attr('id'));
	}
	
	function saveMetadata() {
		var id = $('#metadata input[name="id"]').val();
		if (id != '') {
			var page = $('#'+id);
			$('#metadata input[type!="hidden"]').each(function(index, element) {
				var name = $(this).attr('name');
				var value = $(this).val();
				page.data(name, value);
				$(this).val('');
			});
			$('#metadata input[name="id"]').val('');
		}
	}
	
	$('#ordering').sortable({
		placeholder: "ui-state-highlight",
		forcePlaceholderSize: true
	});
});