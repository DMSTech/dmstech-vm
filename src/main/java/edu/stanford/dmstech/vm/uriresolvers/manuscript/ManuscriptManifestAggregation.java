package edu.stanford.dmstech.vm.uriresolvers.manuscript;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import edu.stanford.dmstech.vm.uriresolvers.AggregationRedirect;

@Path("/{collectiondId}/{manuscriptId}/Manifest")
public class ManuscriptManifestAggregation {

	@GET
	public AggregationRedirect redirectToResourceMap(@Context UriInfo uriInfo) {
		return new AggregationRedirect(uriInfo);
	}
}

