<%@ include file="head.jsp" %>
<link href="css/orderer.css" type="text/css" rel="stylesheet"/>
<link href="css/import_ordering.css" type="text/css" rel="stylesheet"/>
<script type="text/javascript" src="js/oac_js/jquery.rdfquery.rdfa-1.0.js"></script>
<script type="text/javascript" src="js/oac_js/oac_utils.js"></script>
<script type="text/javascript" src="js/oac_js/oac_rdf.js"></script>
<script type="text/javascript" src="js/oac_js/oac_rdfjson.js"></script>
<script type="text/javascript" src="js/event_manager.js"></script>
<script type="text/javascript" src="js/workbench/orderer.js"></script>
<script type="text/javascript" src="js/import_ordering.js"></script>
</head>
<body>
<div id="content">
	<div id="main" class="ui-corner-all ui-widget-header">
		<h2>Page Ordering</h2>
		<p>Click a page to edit its title, then click Ok or press Enter to confirm.  Double-click a page to view a larger version. Drag a page to re-order it.</p>
		<div id="orderParent" class="section ui-corner-all ui-widget-content"></div>
	</div>
</div>
</body>
</html>