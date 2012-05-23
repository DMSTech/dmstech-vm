<%@ include file="head.jsp" %>
<link href="css/admin.css" type="text/css" rel="stylesheet"/>
<script type="text/javascript" src="js/admin.js"></script>
</head>
<body>
<%@ include file="header.jsp" %>
<div id="content">
	<div id="reindex" class="ui-corner-all ui-widget-header">
		<h2>Reindex Search</h2>
		<div class="section ui-corner-all ui-widget-content">
			<div><input type="checkbox" name="solr" id="reindexSolr"/><label for="reindexSolr">Reindex SOLR</label></div>
			<div><input type="checkbox" name="sparql" id="reindexSparql"/><label for="reindexSparql">Reindex SPARQL</label></div>
			<div class="buttonsParent">
				<button>Reindex</button>
			</div>
		</div>
	</div>
	<div id="data" class="ui-corner-all ui-widget-header">
		<h2>Manage Local Data</h2>
		<div class="section ui-corner-all ui-widget-content">
			<div><input type="checkbox" name="ingested" id="data1"/><label for="data1">Delete Ingested Collections</label></div>
			<div><input type="checkbox" name="logs" id="data2"/><label for="data2">Delete Logs</label></div>
			<div><input type="checkbox" name="solr" id="data3"/><label for="data3">Delete SOLR Index</label></div>
			<div><input type="checkbox" name="triple" id="data4"/><label for="data4">Delete Triplestore</label></div>
			<div><input type="checkbox" name="annos" id="data5"/><label for="data5">Delete Submitted Annoations</label></div>
			<div><input type="checkbox" name="transactions" id="data6"/><label for="data6">Delete Recorded Transactions</label></div>
			<div class="buttonsParent">
				<button>Delete Selected Data</button>
				<hr/>
				<button>Reset Local Data to Original State</button>
			</div>
		</div>
	</div>
	<div id="resetDialog">
		<p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 50px 0;"></span>Are you sure?<br/>This will delete all new data you've entered including: ingested collections, annotations, logs, and recorded transactions.</p>
	</div>
</div>
</body>
</html>