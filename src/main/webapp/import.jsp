<%@ include file="head.jsp" %>
<link href="css/import.css" type="text/css" rel="stylesheet"/>
<script type="text/javascript" src="js/import.js"></script>
</head>
<body>
<div id="content">
	<div id="importer" class="ui-corner-all ui-widget-header">
		<h2>Shared Canvas Generation</h2>
		<div class="section ui-corner-all ui-widget-content">
			<div class="info"><span class="icon"></span><p>JP2 images are generated for each image.</p></div>
			<h3>Manuscript Directory and Images</h3>
			<p class="description">Please create a subdirectory in the following directory on your machine:  <span id="directoryPath" class="technical"></span></p>
			<p class="description">The name you give the subdirectory will be used to identify your manuscript in the local system.  It can be anything, e.g. <span class="technical">2323432</span> but should not contain characters other than numbers and letters.  No spaces are allowed in the directory or file names.</p>
			<p class="description">Place the images you wish to ingest into the newly created subdirectory. Valid image file types are: <span class="technical">tif</span>, <span class="technical">tiff</span>, <span class="technical">jpg</span>, <span class="technical">jpeg</span>, <span class="technical">jp2</span>, <span class="technical">png</span>, <span class="technical">gif</span>, <span class="technical">bmp</span>.</p>
			<label for="subdir">Subdirectory Name:</label><input type="text" id="subdir" name="subdir"/>
			<p class="description">If you wish you may order your pages and provide page titles by naming the image files this way: <span class="technical">pageNumber_pageTitle</span>.  For example, <span class="technical">1_firstPage.tif</span> will be set as page 1 with title 'firstPage'.  No spaces may be included in the file name.</p>
			<p class="description">After ingest you may reorder the pages and provide different titles.</p>
			<p class="description">You do not need to name your files this way, or check the box, and may still reorder and name your pages after ingest.</p>
			<label for="parseFileNames">Parse File Name:</label><input type="checkbox" id="parseFileNames" name="parseFileNames"/>
		</div>
		<div class="section ui-corner-all ui-widget-content">
			<div class="info"><span class="icon"></span><p>For search/display.</p></div>
			<h3>Name your Collection</h3>
			<label for="coll1">Country:</label><input type="text" id="coll1" name="country"/>
			<label for="coll2">Region:</label><input type="text" id="coll2" name="region"/>
			<label for="coll3">Settlement:</label><input type="text" id="coll3" name="settlement"/>
			<label for="coll4">Institution:</label><input type="text" id="coll4" name="institution"/>
			<label for="coll5">Repository:</label><input type="text" id="coll5" name="repository"/>
			<label for="coll6">Collection:</label><input type="text" id="coll6" name="collection"/>
		</div>
		<div class="section ui-corner-all ui-widget-content">
			<div class="info"><span class="icon"></span><p>For search/display.</p></div>
			<h3>Identify your Manuscript</h3>
			<label for="man1">Shelfmark or Id No:</label><input type="text" id="man1" name="idno"/>
			<label for="man2">Former or Alternate Identifier(s):</label><input type="text" id="man2" name="altid"/>
			<label for="man3">Manuscript Name:</label><input type="text" id="man3" name="manname"/>
			<div class="info" style="margin-right: 175px;"><span class="icon"></span><p>Include principal authors or works.</p></div>
			<label for="man4">Manuscript Descriptive Title:</label><input type="text" id="man4" name="mantitle"/>
		</div>
		<div class="info" style="margin-right: 10px;"><span class="icon"></span><p>Generates SharedCanvas RDF for use by viewing, transcription, and annotation tools; and for cross repository sharing.</p></div>
		<div id="buttons">
			<button>Generate</button><button>Cancel</button>
		</div>
	</div>
</div>
</body>
</html>