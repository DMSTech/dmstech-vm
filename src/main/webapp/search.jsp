<%@ include file="head.jsp" %>
<link href="css/search.css" type="text/css" rel="stylesheet"/>
<link href="js/jquery/jquery.collapsiblepanel.css" type="text/css" rel="stylesheet"/>
<script type="text/javascript" src="js/search.js"></script>
</head>
<body>
<div id="content">
	<div id="sidebar" class="ui-corner-all ui-widget-header">
		<h2>Search</h2>
		<div id="searchParent">
			<div id="searchSelector">
				<input type="radio" name="searchRadio" id="solrRadio"/><label for="solrRadio">SOLR</label>
				<input type="radio" name="searchRadio" id="sparqlRadio"/><label for="sparqlRadio">SPARQL</label>
			</div>
			<div id="solrSearch" class="ui-corner-all ui-widget-content searchTool">
				<div id="search">
				</div>
			</div>
			<div id="sparqlSearch" class="ui-corner-all ui-widget-content searchTool">
				<label for="sparqlQuery">SPARQL Query</label>
				<textarea id="sparqlQuery"></textarea>
			</div>
			<button id="searchButton">Search</button>
			<button id="clearButton">Clear</button>
			<button id="showReindexDialog">Reindex</button>
		</div>
	</div>
	<div id="main" class="ui-corner-all ui-widget-header">
		<h2>Results</h2>
		<div id="results" class="ui-corner-all ui-widget-content"></div>
	</div>
	<div id="reindexDialog">
		<div><input type="checkbox" name="solr" id="reindexSolr"/><label for="reindexSolr">Reindex SOLR</label></div>
		<div><input type="checkbox" name="sparql" id="reindexSparql"/><label for="reindexSparql">Reindex SPARQL</label></div>
		<div id="indexing"><span id="status"></span></div>
	</div>
</div>
</body>
</html>