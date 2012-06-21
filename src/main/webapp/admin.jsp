<%@ include file="head.jsp" %>
<link href="css/admin.css" type="text/css" rel="stylesheet"/>
<script type="text/javascript" src="js/admin.js"></script>
</head>
<body>
<%@ include file="header.jsp" %>
<div id="content">
	<div id="reindex" class="ui-corner-all ui-widget-header adminBox">
		<h2>Reindex Search</h2>
		<div class="section ui-corner-all ui-widget-content">
			<div><input type="checkbox" name="solr" id="reindexSolr"/><label for="reindexSolr">Reindex SOLR</label></div>
			<div><input type="checkbox" name="sparql" id="reindexSparql"/><label for="reindexSparql">Reindex SPARQL</label></div>
			<div class="buttonsParent">
				<button>Reindex</button>
			</div>
		</div>
	</div>
	<div id="data" class="ui-corner-all ui-widget-header adminBox">
		<h2>Manage Local Data</h2>
		<div class="section ui-corner-all ui-widget-content">
			<div class="buttonsParent">
				<button>Reset Local Data to Original State</button>
			</div>
		</div>
	</div>
</div>
</body>
</html>