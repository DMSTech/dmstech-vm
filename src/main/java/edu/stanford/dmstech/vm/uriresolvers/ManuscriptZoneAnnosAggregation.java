package edu.stanford.dmstech.vm.uriresolvers;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;


@Path("/{collectiondId}/{manuscriptId}/ZoneAnnotations")
public class ManuscriptZoneAnnosAggregation {

	@Context 
	UriInfo uriInfo;

	
	@GET 
	@Produces("application/rdf+xml")
	public Response redirectZoneAnnosReqToXMLResourceMap() throws URISyntaxException {
		String originalRequest = uriInfo.getAbsolutePath().toASCIIString();
		return Response.seeOther(new URI(originalRequest + ".xml")).build();
	} 
	
	@GET 
	@Produces("text/turtle;charset=utf-8")
	public Response redirectZoneAnnosReqToTurtleResourceMap() throws URISyntaxException {
		String originalRequest = uriInfo.getAbsolutePath().toASCIIString();
		return Response.seeOther(new URI(originalRequest + ".ttl")).build();
	}

	@GET 
	@Produces("text/html;charset=utf-8")
	public Response redirectZoneAnnosReqToHTMLResourceMap() throws URISyntaxException {
		String originalRequest = uriInfo.getAbsolutePath().toASCIIString();
		return Response.seeOther(new URI(originalRequest + ".html")).build();
	}


	
		
}

