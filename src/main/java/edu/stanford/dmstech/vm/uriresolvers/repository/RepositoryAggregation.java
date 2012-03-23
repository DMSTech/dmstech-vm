package edu.stanford.dmstech.vm.uriresolvers.repository;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import edu.stanford.dmstech.vm.uriresolvers.AggregationRedirect;

@Path("/Repository")
public class RepositoryAggregation {

	@GET
	public AggregationRedirect redirectToResourceMap(@Context UriInfo uriInfo) {
		return new AggregationRedirect(uriInfo);
	}
}

	
		




