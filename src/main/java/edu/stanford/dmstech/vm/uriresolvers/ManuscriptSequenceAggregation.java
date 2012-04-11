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

	@GET
	@Path("/{collectiondId}/{manuscriptId}/sequence/{sequenceId: (.*\\.html$|.*\\.xml$|.*\\.ttl$)}") 
	@Produces("application/rdf+xml")
	public Response getSequenceResourceMapAsXML(
			@PathParam("collectionId") final String collectionId,
			@PathParam("manuscriptId") final String manuscriptId,
			@PathParam("sequenceId") final String sequenceId
			) throws Exception {   
		String sequenceIdWithoutExtension = sequenceId.substring(0, sequenceId.lastIndexOf("."));
		return SharedCanvasUtil.getSerializedRDFFromHomeDir(Config.collectionSubDir + "/" + collectionId + "/" + manuscriptId + "/" + sequenceIdWithoutExtension, sequenceId);
		
	}
		
	
	

	
		
}

