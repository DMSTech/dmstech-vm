package edu.stanford.dmstech.vm.uriresolvers;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.hp.hpl.jena.rdf.model.Model;

import edu.stanford.dmstech.vm.Config;
import edu.stanford.dmstech.vm.RDFUtils;



@Path("/{collectiondId}/{manuscriptId}/sequence/{sequenceId: (?!.*\\.html$|.*\\.xml$|.*\\.ttl$).*}")
public class ManuscriptSequenceAggregation {	
	
	@Context 
	UriInfo uriInfo;
	
	@GET 
	@Produces("application/rdf+xml")
	public Response redirectSequenceReqToXMLResourceMap() throws URISyntaxException {
		String originalRequest = uriInfo.getAbsolutePath().toASCIIString();
		return Response.seeOther(new URI(originalRequest + ".xml")).build();
	} 
	
	@GET 
	@Produces("text/turtle;charset=utf-8")
	public Response redirectSequenceReqToTurtleResourceMap() throws URISyntaxException {
		String originalRequest = uriInfo.getAbsolutePath().toASCIIString();
		return Response.seeOther(new URI(originalRequest + ".ttl")).build();
	}

	@GET 
	@Produces("text/html;charset=utf-8")
	public Response redirectSequenceReqToHTMLResourceMap() throws URISyntaxException {
		String originalRequest = uriInfo.getAbsolutePath().toASCIIString();
		return Response.seeOther(new URI(originalRequest + ".html")).build();
	}
	
	@PUT
	public Response replaceSequence(
			@PathParam("collectionId") final String collectionId,
			@PathParam("manuscriptId") final String manuscriptId,
			@PathParam("sequenceId") final String sequenceId,
			InputStream inputStream) throws Exception {
		Model model = RDFUtils.loadModelFromInputStream(inputStream, "RDF/XML");
		String fileToSave = Config.collectionSubDir + "/" + collectionId + "/" + manuscriptId  + "/sequences/" + sequenceId + ".nt"; 
		RDFUtils.serializeModelToHomeDir(model, fileToSave, "N-TRIPLE");
		return Response.ok().build();
	}
	

	
		
}

