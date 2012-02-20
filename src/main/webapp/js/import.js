$(document).ready(function() {
	$('button:eq(0)').button().click(function() {
		window.location = 'import_ordering.jsp';
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