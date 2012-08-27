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
	<div id="title">
		<h1>Prototype Resource Interoperability<br/>for Manuscript Environments and Repositories</h1>
	</div>
	<div id="buttons" class="ui-corner-all ui-widget-content">
		<div>
			<button>Browse Manuscripts</button>
			<button>Import a Manuscript</button>
			<button>Search Manuscripts</button>
			<button>Local Repository Admin</button>
		</div>
	</div>
	<div id="info">
		<p>The DMSTech Kickstart Package provides a demonstration of the technologies and tools that enable shared resources between image repositories and tools.
		This prototype is being developed as part of the project '<span style="font-style: italic">Defining a Modular and Interoperating Environment for Collections of Digitized Medieval Manuscripts, Tools, and Users</span>' (DMSTech)
		funded by <a href="http://www.mellon.org/" target="_blank">The Andrew W. Mellon Foundation</a>.</p>
		<p>This prototype was developed by <a href="http://openskysolutions.ca" target="_blank">Open Sky Solutions</a>, with input from the following collaborators:</p>
		<ul>
			<li><a href="http://bob.drew.edu/mappaemundi" target="_blank">DM</a>, Drew University</li>
			<li><a href="http://www.lanl.gov/" target="_blank">Los Alamos National Laboratory</a></li>
			<li><a href="http://romandelarose.org/" target="_blank">Roman de la Rose Digital Library</a>, Johns Hopkins University</li>
			<li><a href="http://digital-editor.blogspot.com/" target="_blank">T-PEN</a>, Saint Louis University</li>
			<li><a href="http://www-sul.stanford.edu/" target="_blank">Stanford University Libraries</a></li>
			<li><a href="http://www.textandbytes.com/" target="_blank">text & bytes</a>, Switzerland</li>
			<li>Participating Members of the Digital Manuscript Technical Council</li>
		</ul>
		<p>Learn more:</p>
		<ul>
			<li>Project: <a href="http://www.stanford.edu/group/dmstech/" target="_blank">http://www.stanford.edu/group/dmstech/</a></li>
			<li>Data Model: <a href="http://www.shared-canvas.org" target="_blank">http://www.shared-canvas.org</a></li>
			<li>Image API: <a href="http://lib.stanford.edu/iiif" target="_blank">http://lib.stanford.edu/iiif</a></li>
		</ul>
		<p style="font-size: 14px; font-weight: bold;"><a href="http://dms-data.stanford.edu/" target="_blank">Prototype documentation</a></p>
	</div>
	<div id="logos">
		<img src="img/logos.png" />
	</div>
</div>
</body>
</html>