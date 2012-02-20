<%@ include file="head.jsp" %>
<link href="css/import.css" type="text/css" rel="stylesheet"/>
<script type="text/javascript" src="js/import.js"></script>
</head>
<body>
<div id="content">
	<div id="importer" class="ui-corner-all ui-widget-header">
		<h2>Shared Canvas Generation</h2>
		<div class="section ui-corner-all ui-widget-content">
			<h3>Please choose the directory containing your manuscript images &mdash; they may be JPGs, TIFFs, or PNGs</h3>
			<div class="info"><span class="icon"></span><p>JP2 images are generated for each image.</p></div>
			<label for="imageDir">Image Directory:</label><input type="text" id="imageDir" name="imageDir"/>
		</div>
		<div class="section ui-corner-all ui-widget-content">
			<h3>Name your Collection</h3>
			<div class="info"><span class="icon"></span><p>For search/display.</p></div>
			<label for="coll1">Country:</label><input type="text" id="coll1" name="country"/>
			<label for="coll2">Settlement:</label><input type="text" id="coll2" name="settlement"/>
			<label for="coll3">Institution:</label><input type="text" id="coll3" name="institution"/>
			<label for="coll4">Repository:</label><input type="text" id="coll4" name="repository"/>
			<label for="coll5">Collection:</label><input type="text" id="coll5" name="collection"/>
		</div>
		<div class="section ui-corner-all ui-widget-content">
			<h3>Identify your Manuscript</h3>
			<div class="info"><span class="icon"></span><p>For search/display.</p></div>
			<label for="man1">IDNO:</label><input type="text" id="man1" name="idno"/>
			<label for="man2">Alt ID:</label><input type="text" id="man2" name="altid"/>
			<label for="man3">Title:</label><input type="text" id="man3" name="title"/>
		</div>
		<div class="info" style="margin-right: 10px;"><span class="icon"></span><p>Generates SharedCanvas RDF for use by viewing, transcription, and annotation tools; and for cross repository sharing.</p></div>
		<div id="buttons">
			<button>Generate</button><button>Cancel</button>
		</div>
	</div>
</div>
</body>
</html>