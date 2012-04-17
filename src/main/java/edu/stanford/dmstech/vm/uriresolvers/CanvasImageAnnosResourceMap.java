package edu.stanford.dmstech.vm.uriresolvers;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import edu.stanford.dmstech.vm.DMSTechRDFConstants;
import edu.stanford.dmstech.vm.SharedCanvasUtil;

@Path("/{collectionId}/{manuscriptId}/{canvasId}/{fileName: ImageAnnotations\\.(?i)(xml|ttl|html)}")
public class CanvasImageAnnosResourceMap {
 
	@Context 
	UriInfo uriInfo;

	@GET
	@Produces("application/rdf+xml")
	public Response getResourceMapAsXML(
			@PathParam("collectionId") final String collectionId,
			@PathParam("manuscriptId") final String manuscriptId,
			@PathParam("canvasId") final String canvasId
			) throws Exception {   
		String originalRequest = uriInfo.getAbsolutePath().toASCIIString();
		
		return SharedCanvasUtil.buildResourceMapForCanvasAnnotations(originalRequest, "ImageAnnotation",  DMSTechRDFConstants.getInstance().scImageAnnotationListClass);
		
	}

	
	
	
	
}