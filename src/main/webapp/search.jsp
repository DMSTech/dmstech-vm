<%@ include file="head.jsp" %>
<link href="css/search.css" type="text/css" rel="stylesheet"/>
<script type="text/javascript" src="js/search.js"></script>
</head>
<body>
<%@ include file="header.jsp" %>
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
		</div>
	</div>
	<div id="main" class="ui-corner-all ui-widget-header">
		<h2>Results</h2>
		<div id="results" class="ui-corner-all ui-widget-content"></div>
	</div>
</div>
</body>
</html>