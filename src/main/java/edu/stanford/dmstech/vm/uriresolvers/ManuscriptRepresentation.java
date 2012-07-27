package edu.stanford.dmstech.vm.uriresolvers;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import edu.stanford.dmstech.vm.Config;
import edu.stanford.dmstech.vm.SharedCanvasUtil;


@Path("/{collectionId}/{manuscriptFile: (.*\\.html$|.*\\.xml$|.*\\.ttl$)}")
public class ManuscriptRepresentation {
	
	@Context 
	UriInfo uriInfo;
	
	@GET
	public Response getRepresentation(
			@PathParam("collectionId") final String collectionId,
			@PathParam("manuscriptFile") final String manuscriptFile
			) throws Exception {
		String originalRequest = uriInfo.getAbsolutePath().toASCIIString();
		if (originalRequest.toLowerCase().endsWith(".html")) return SharedCanvasUtil.redirectToHTMLPage(originalRequest);
		
		// TODO need to build manuscript RDF here, probably like the canvas.
		// for now just return the manifest
		String manuscriptId = manuscriptFile.substring(0, manuscriptFile.lastIndexOf("."));
		String requestedExtension = manuscriptFile.substring(manuscriptFile.lastIndexOf("."));
		return SharedCanvasUtil.getSerializedRDFFromHomeDir(Config.collectionSubDir + "/" + collectionId + "/" + manuscriptId + "/rdf/" + Config.manifestFileName, Config.manifestFileName  + requestedExtension);
			
	}
	
		
}

