package edu.stanford.dmstech.vm.uriresolvers;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import edu.stanford.dmstech.vm.Config;
import edu.stanford.dmstech.vm.RDFUtils;
import edu.stanford.dmstech.vm.SharedCanvasUtil;
import edu.stanford.dmstech.vm.manuscriptgeneration.SharedCanvasGenerator;


@Path("/{collectiondId}/{manuscriptId}")
public class SequenceSubmission {

	@Context 
	UriInfo uriInfo;
	
	@POST
	@Path("/{collectionId}/{manuscriptId}/sequence")
	@Produces("application/rdf+xml")
	public Response saveNewSequence(InputStream inputStream)  {
		URI uriForNewSequence = null;
		// TODO:  save new sequence file with a uuid, return the URI.
		//so basically just generate a new uuid and save the new sequence with this as the 
		//filename.  may have to convert to n-triple
		return Response.created(uriForNewSequence).build();
	}	

}
