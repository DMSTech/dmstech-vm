package edu.stanford.dmstech.vm.uriresolvers;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import edu.stanford.dmstech.vm.Config;
import edu.stanford.dmstech.vm.SharedCanvasUtil;


@Path("/{collectionId}/{manuscriptId: (.*\\.html$|.*\\.xml$|.*\\.ttl$)}")
public class ManuscriptRepresentation {
	
	@GET
	public Response getRepresentation(
			@PathParam("collectionId") final String collectionId,
			@PathParam("manuscriptId") final String manuscriptId
			) throws Exception {
		// TODO need to build manuscript RDF here, probably like the canvas.
		// for now just return the manifest
		return SharedCanvasUtil.getSerializedRDFFromHomeDir(Config.collectionSubDir + "/" + collectionId + "/" + manuscriptId + "/" + Config.manifestFileName, Config.manifestFileName  + ".xml");
			
	}
	
		
}

