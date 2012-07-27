package edu.stanford.dmstech.vm.uriresolvers;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import edu.stanford.dmstech.vm.SharedCanvasUtil;

@Path("{collectionId}/{manuscriptId}/{canvasId: (.*\\.html$|.*\\.xml$|.*\\.ttl$)}")
public class CanvasRepresentation {
 
	@Context 
	UriInfo uriInfo;
	
	@GET
	@Produces("application/rdf+xml")
	public Response getCanvasRepresentationAsXML(
			@PathParam("collectionId") final String collectionId,
			@PathParam("manuscriptId") final String manuscriptId,
			@PathParam("canvasId") final String canvasId
			) throws Exception {   
		String originalRequest = uriInfo.getAbsolutePath().toASCIIString();
		
		if (originalRequest.toLowerCase().endsWith(".html")) return SharedCanvasUtil.redirectToHTMLPage(originalRequest);
		
		
		String canvasURI = originalRequest.substring(0, originalRequest.lastIndexOf("."));
		String fileExtension = canvasId.substring(canvasId.lastIndexOf(".") + 1);
		return SharedCanvasUtil.getSerializedCanvasRDF(canvasURI, fileExtension);
		
	}
	



	
}