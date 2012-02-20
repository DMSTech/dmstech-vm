<%@ include file="head.jsp" %>
<script type="text/javascript" src="js/jstree/jquery.jstree.js"></script>
<script type="text/javascript" src="js/event_manager.js"></script>
<script type="text/javascript" src="js/workbench/tree.js"></script>
<script type="text/javascript" src="js/workbench/viewer.js"></script>
<script type="text/javascript" src="js/workbench/viewer_zpr.js"></script>
<script type="text/javascript" src="js/workbench/workbench.js"></script>
<script type="text/javascript" src="js/zpr/zpr_djatoka.js"></script>
<script type="text/javascript" src="js/oac_js/jquery.rdfquery.rdfa-1.0.js"></script>
<script type="text/javascript" src="js/oac_js/oac_utils.js"></script>
<script type="text/javascript" src="js/oac_js/oac_rdf.js"></script>
<script type="text/javascript" src="js/oac_js/oac_rdfjson.js"></script>
<link href="css/workbench.css" type="text/css" rel="stylesheet"/>
<link href="css/zpr.css" type="text/css" rel="stylesheet"/>
</head>
<body>
<div id="content">
	<div id="sidebar" class="ui-corner-all ui-widget-header">
		<h2>Collections Browser</h2>
		<div id="search">
			<input type="text" name="search" />
			<button>Search</button>
		</div>
		<h3>Local</h3>
		<div id="collectionsLocal" class="ui-corner-all ui-widget-content"></div>
		<h3>Remote</h3>
		<div id="collectionsRemote" class="ui-corner-all ui-widget-content"></div>
	</div>
	<div id="main" class="ui-corner-all ui-widget-header">
		<div style="text-align: center;">
			<h2>Page Viewer</h2>
			<div id="tools"></div>
		</div>
		<div id="toolContent" class="ui-corner-all ui-widget-content"></div>
	</div>
</div>
</body>
</html>