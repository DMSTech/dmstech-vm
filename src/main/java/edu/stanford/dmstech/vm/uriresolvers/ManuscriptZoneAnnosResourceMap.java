package edu.stanford.dmstech.vm.uriresolvers;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import edu.stanford.dmstech.vm.Config;
import edu.stanford.dmstech.vm.DMSTechRDFConstants;
import edu.stanford.dmstech.vm.SharedCanvasUtil;


@Path("/{collectionId}/{manuscriptId}/{fileName: ZoneAnnotations\\.(?i)(xml|ttl|html)}")
public class ManuscriptZoneAnnosResourceMap {
	
	@GET
	public Response getRepresentation(
			@PathParam("collectionId") final String collectionId,
			@PathParam("manuscriptId") final String manuscriptId,
			@Context UriInfo uriInfo
			) throws Exception {
		String originalRequest = uriInfo.getAbsolutePath().toASCIIString();
		if (originalRequest.toLowerCase().endsWith(".html")) return SharedCanvasUtil.redirectToHTMLPage(originalRequest);
		
		return SharedCanvasUtil.buildResourceMapForManuscriptAnnotations(originalRequest, "ZoneAnnotation",  DMSTechRDFConstants.getInstance().scZoneAnnotationListClass);
			
	}
	
		
}

