package edu.stanford.dmstech.vm.uriresolvers.canvas;

import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import edu.stanford.dmstech.vm.uriresolvers.AggregationRedirect;

public class CanvasZoneAnnoAggregation {
 
	@Path("/{collectionId}/{manuscriptId}/{canvasId}/zoneannos")
	public AggregationRedirect redirectToResourceMap(@Context UriInfo uriInfo) {
		return new AggregationRedirect(uriInfo);
	}

	
}


