<%@ include file="head.jsp" %>
<link href="css/html_serializer.css" type="text/css" rel="stylesheet"/>
<script type="text/javascript" src="js/html_serializer.js"></script>
</head>
<body>
<div id="info">
	<div id="types" class="box">
		<h1></h1>
		<div class="buttons">
			<button>RDF/XML</buton> <button>Turtle</button>
		</div>
		<h2 style="margin-top: 15px;">Resource Definitions</h2>
	</div>
	<div id="associations" class="box">
		<h2>Other Associations for this Resource</h2>
	</div>
	<div id="rdfs" class="box">
		<h2>Other Resource Descriptions Returned when Deferencing this Resource</h2>
	</div>
</div>
<div id="rdfDialog">
	<pre></pre>
</div>
<div id="turtleDialog">
	<pre></pre>
</div>
</body>
</html>