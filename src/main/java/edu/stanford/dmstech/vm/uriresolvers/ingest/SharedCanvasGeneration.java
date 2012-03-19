package edu.stanford.dmstech.vm.uriresolvers.ingest;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.sun.xml.txw2.Document;

import edu.stanford.dmstech.vm.Config;
import edu.stanford.dmstech.vm.manuscriptgeneration.SharedCanvasGenerator;
import edu.stanford.dmstech.vm.manuscriptgeneration.SharedCanvasModel;

/**	the images have to have been placed in a directory that's web accessible.  and writable.  
the directory (DATA_DIR) will be listed in the config file.
the base url resolving to the directory will be also in the config.
this directory will probably just be somewhere in the apache default web dir.
a new set of images will be put in a subdirectory:
	DATA_DIR/collections/collectionIdMatchingForm/manuscriptNameMatchingForm/allImagesHERE
	we'll then generate the jp2s in this directory, and the three RMs.	
the titles for each canvas will come from either the filenames,
if they are labelled in the exact format:  title_pageNum, or after
generation using the thumbnail editing process, which 
will submit a json ordered list of imageURI:theCanvasTitle.*/
public class SharedCanvasGeneration {

	@POST
	public Response generateSharedCanvas(
			@FormParam("collectionId") final String collectionId,
			@FormParam("mansucriptId") final String manuscriptId,
			@FormParam("parseFileNames") final boolean parseTitlesAndPageNums
			) throws Exception {
	
		String baseURI = Config.getBaseURIForIds() + collectionId + "/" + manuscriptId + "/";
		String manuscriptDirectoryPath = Config.getBaseDirForCollections() + collectionId + "/" + manuscriptId + "/";
		SharedCanvasGenerator sharedCanvasGenerator = new SharedCanvasGenerator();
		boolean success = sharedCanvasGenerator.generateSharedCanvas(manuscriptDirectoryPath, baseURI, parseTitlesAndPageNums);
		
		return Response.ok().build();
		
		
	
}
}
