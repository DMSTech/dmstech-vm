package edu.stanford.dmstech.vm.uriresolvers;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import edu.stanford.dmstech.vm.Config;
import edu.stanford.dmstech.vm.SharedCanvasUtil;


@Path("/{collectiondId}/{manuscriptId}/sequence/{sequenceId: (.*\\.html$|.*\\.xml$|.*\\.ttl$)}") 
public class ManuscriptSequenceResourceMap {	
	
	@GET
	@Produces("application/rdf+xml")
	public Response getSequenceResourceMapAsXML(
			@PathParam("collectionId") final String collectionId,
			@PathParam("manuscriptId") final String manuscriptId,
			@PathParam("sequenceId") final String sequenceId
			) throws Exception {   
		String sequenceIdWithoutExtension = sequenceId.substring(0, sequenceId.lastIndexOf("."));
		return SharedCanvasUtil.getSerializedRDFFromHomeDir(Config.collectionSubDir + "/" + collectionId + "/" + manuscriptId + "/rdf/" + sequenceIdWithoutExtension, sequenceId);
		
	}
		
}

