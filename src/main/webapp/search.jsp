<%@ include file="head.jsp" %>
<link href="css/search.css" type="text/css" rel="stylesheet"/>
<link href="js/jquery/jquery.collapsiblepanel.css" type="text/css" rel="stylesheet"/>
<script type="text/javascript" src="js/search.js"></script>
</head>
<body>
<div id="content">
	<div id="sidebar" class="ui-corner-all ui-widget-header">
		<h2>Search</h2>
		<div id="search">
		</div>
		<button id="searchButton">Search</button>
		<button id="clearButton">Clear</button>
	</div>
	<div id="main" class="ui-corner-all ui-widget-header">
		<h2>Results</h2>
		<div id="results" class="ui-corner-all ui-widget-content"></div>
	</div>
</div>
</body>
</html>