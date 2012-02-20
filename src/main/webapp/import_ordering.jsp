<%@ include file="head.jsp" %>
<link href="css/import_ordering.css" type="text/css" rel="stylesheet"/>
<script type="text/javascript" src="js/import_ordering.js"></script>
</head>
<body>
<div id="content">
	<div id="sidebar" class="ui-corner-all ui-widget-header">
		<h2>Metadata</h2>
		<div id="metadata" class="section ui-corner-all ui-widget-content">
			<input type="hidden" name="id" value="" />
			<label for="m1">Title:</label><input type="text" id="m1" name="title"/>
		</div>
		<div class="buttons">
			<button>Apply</button>
		</div>
	</div>
	<div id="main" class="ui-corner-all ui-widget-header">
		<h2>Page Ordering</h2>
		<div id="ordering" class="section ui-corner-all ui-widget-content"></div>
		<div class="clear"></div>
		<div class="buttons">
			<button>Confirm</button>
		</div>
	</div>
</div>
</body>
</html>