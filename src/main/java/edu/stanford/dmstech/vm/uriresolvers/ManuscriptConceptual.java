package edu.stanford.dmstech.vm.uriresolvers;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import edu.stanford.dmstech.vm.manuscriptgeneration.SharedCanvasGenerator;


@Path("/{collectionId}/{manuscriptId}")
public class ManuscriptConceptual {

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
	
	@javax.ws.rs.PUT
	public Response generateSharedCanvas(
			@PathParam("manuscriptId") final String manuscriptIdForIngest,
			@PathParam("collectionId") final String collectionIdForIngest,
			@FormParam("manname") final String manuscriptName,
			@FormParam("mantitle") final String manuscriptTitle,
			@FormParam("collection") final String collectionId,
			@FormParam("idno") final String manuscriptIdno,
			@FormParam("altid") final String alternateId,
			@FormParam("repository") final String repositoryName,
			@FormParam("institution") final String institutionName,
			@FormParam("settlement") final String settlementName,
			@FormParam("region") final String regionName,
			@FormParam("country") final String countryName,						
			@FormParam("parseFileNames") final boolean parseTitlesAndPageNums
			) throws Exception {
	
		SharedCanvasGenerator sharedCanvasGenerator = new SharedCanvasGenerator();
			
		String manuscriptManifestURI = sharedCanvasGenerator.generateSharedCanvasInDefaultDir(
				manuscriptName,
				manuscriptTitle,
				collectionId,
				manuscriptIdno,
				alternateId,
				repositoryName,
				institutionName,
				settlementName,
				regionName,
				countryName,
				manuscriptIdForIngest,
				collectionIdForIngest,
				parseTitlesAndPageNums
				);
		
		return Response.created(new URI(manuscriptManifestURI)).build();

}


		
	
	

	
		
}

