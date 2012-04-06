package edu.stanford.dmstech.vm.uriresolvers.collection;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import edu.stanford.dmstech.vm.uriresolvers.AggregationRedirect;

@Path("{CollectionId}/Collection")
public class CollectionAggregation {

	@Context 
	UriInfo uriInfo;
		

	@GET 
	@Produces("application/rdf+xml")
	public Response redirectReqToXMLResourceMap() throws URISyntaxException {
		String originalRequest = uriInfo.getAbsolutePath().toASCIIString();
		return Response.seeOther(new URI(originalRequest + ".xml")).build();
	} 
	
	@GET
	@Produces("text/turtle;charset=utf-8")
	public Response redirectReqToTurtleResourceMap() throws URISyntaxException {
		String originalRequest = uriInfo.getAbsolutePath().toASCIIString();
		return Response.seeOther(new URI(originalRequest + ".ttl")).build();
	}

	@GET
	@Produces("text/html;charset=utf-8")
	public Response redirectReqToHTMLResourceMap() throws URISyntaxException {
		String originalRequest = uriInfo.getAbsolutePath().toASCIIString();
		return Response.seeOther(new URI(originalRequest + ".html")).build();
	}

}


