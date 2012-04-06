package edu.stanford.dmstech.vm.uriresolvers.canvas;

import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import edu.stanford.dmstech.vm.uriresolvers.AggregationRedirect;

public class CanvasTextAnnoAggregation {
 
	@Path("/{collectionId}/{manuscriptId}/{canvasId}/TextAnnotations")
	public AggregationRedirect redirectToResourceMap(@Context UriInfo uriInfo) {
		return new AggregationRedirect(uriInfo);
	}

	
}



