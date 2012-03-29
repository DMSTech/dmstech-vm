package edu.stanford.dmstech.vm.uriresolvers.ingest;

import java.net.URI;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import edu.stanford.dmstech.vm.Config;
import edu.stanford.dmstech.vm.manuscriptgeneration.SharedCanvasGenerator;

/**	the images have to have been placed in a directory that's web accessible.  and writable.  
the directory (DATA_DIR) will be listed in the config file.
the base url resolving to the directory will be also in the config.
a new set of images will be put in a subdirectory:
	DATA_DIR/collections/collectionIdMatchingForm/manuscriptNameMatchingForm/allImagesHERE
	we'll then generate the jp2s in this directory, and the three RMs.	
the titles for each canvas will come from either the filenames,
if they are labelled in the exact format:  title_pageNum, or after
generation using the thumbnail editing process, which 
will submit a json ordered list of imageURI:theCanvasTitle.

The incoming parameters to the manuscript generation are:
subdir, country, region, settlement, institution, repository, collection, idno, altid, manname
*/

@Path("/ingest/manuscript/{manuscriptId}")
public class SharedCanvasGeneration {

	@POST
	
	public Response generateSharedCanvas(
			@FormParam("manname") final String manuscriptName,
			@FormParam("mantitle") final String manuscriptTitle,
			@FormParam("collection") final String collectionId,
			@FormParam("idno") final String manuscriptId,
			@FormParam("altid") final String alternateId,
			@FormParam("repository") final String repositoryName,
			@FormParam("institution") final String institutionName,
			@FormParam("settlement") final String settlementName,
			@FormParam("region") final String regionName,
			@FormParam("country") final String countryName,
			@FormParam("subdir") final String manuscriptDirName,						
			@FormParam("parseFileNames") final boolean parseTitlesAndPageNums
			) throws Exception {
	
		SharedCanvasGenerator sharedCanvasGenerator = new SharedCanvasGenerator();
		
		
		String manuscriptManifestURI = sharedCanvasGenerator.generateSharedCanvasInDefaultDir(
				manuscriptName,
				manuscriptTitle,
				collectionId,
				manuscriptId,
				alternateId,
				repositoryName,
				institutionName,
				settlementName,
				regionName,
				countryName,
				manuscriptDirName, 
				parseTitlesAndPageNums
				);
		
		return Response.created(new URI(manuscriptManifestURI)).build();
		
		
	
}
}
