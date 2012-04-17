package edu.stanford.dmstech.vm.uriresolvers;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.UUID;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.hp.hpl.jena.rdf.model.Model;

import edu.stanford.dmstech.vm.Config;
import edu.stanford.dmstech.vm.RDFUtils;


@Path("/{collectiondId}/{manuscriptId}/sequence")
public class SequenceSubmission {

	@Context 
	UriInfo uriInfo;
	
	@POST
	@Path("/{collectionId}/{manuscriptId}/sequence")
	public Response saveNewSequence(
			@PathParam("collectionId") final String collectionId,
			@PathParam("manuscriptId") final String manuscriptId,
			InputStream inputStream) throws Exception  {
		String sequenceUUID = UUID.randomUUID().toString();
		String originalRequest = uriInfo.getAbsolutePath().toASCIIString();
		String newSequenceURI = UriBuilder.fromUri(originalRequest).path(sequenceUUID).build().toString();
		Model model = RDFUtils.loadModelFromInputStream(inputStream, "RDF/XML");
		String fileToSave = new File(Config.getAbsolutePathToManuscriptsSequenceDir(collectionId, manuscriptId), sequenceUUID + ".nt").getAbsolutePath();
		RDFUtils.serializeModelToFile(model, fileToSave, "N-TRIPLE");
		return Response.created(new URI(newSequenceURI)).build();
	}	

	
		
}
