<%@ include file="head.jsp" %>
<link href="css/index.css" type="text/css" rel="stylesheet"/>
<script type="text/javascript">
	$(document).ready(function() {
		$('button:eq(0)').button().click(function() {
			window.location = 'workbench.jsp';
		});
		$('button:eq(1)').button().click(function() {
			window.location = 'import.jsp';
		});
		$('button:eq(2)').button().click(function() {
			window.location = 'search.jsp';
		});
		$('button:eq(3)').button().click(function() {
			window.location = 'admin.jsp';
		});
	});
</script>
</head>
<body>
<div id="content">
	<div id="start" class="ui-corner-all ui-widget-content">
		<h1>DMSTech</h1>
		<h2>Manuscripts Workbench</h2>
		<div>
			<button>Browse Manuscripts</button>
			<button>Import a Manuscript</button>
			<button>Search Manuscripts</button>
			<button>Local Repository Admin</button>
		</div>
	</div>
</div>
</body>
</html>