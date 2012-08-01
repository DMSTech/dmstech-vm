<%@ include file="head.jsp" %>
<link href="css/html_serializer.css" type="text/css" rel="stylesheet"/>
<script type="text/javascript" src="js/event_manager.js"></script>
<script type="text/javascript" src="js/html_serializer.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	var h = new HTMLSerializer({id:'#html_serializer'});
	h.activate();
	var uri = getParameterByName('uri');
	h.loadUri(uri);
});
</script>
</head>
<body>
<div id="html_serializer"></div>
</body>
</html>