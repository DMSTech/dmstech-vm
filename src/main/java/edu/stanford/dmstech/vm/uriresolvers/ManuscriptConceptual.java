package edu.stanford.dmstech.vm.uriresolvers;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import javax.ws.rs.Consumes;

import com.sun.jersey.multipart.FormDataParam;

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
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response generateSharedCanvas(
			@PathParam("manuscriptId") final String manuscriptIdForIngest,
			@FormDataParam("collectionId") final String collectionIdForIngest,
			@FormDataParam("manname") final String manuscriptName,
			@FormDataParam("mantitle") final String manuscriptTitle,
			@FormDataParam("collection") final String collectionId,
			@FormDataParam("idno") final String manuscriptIdno,
			@FormDataParam("altid") final String alternateId,
			@FormDataParam("repository") final String repositoryName,
			@FormDataParam("institution") final String institutionName,
			@FormDataParam("settlement") final String settlementName,
			@FormDataParam("region") final String regionName,
			@FormDataParam("country") final String countryName,						
			@FormDataParam("parseFileNames") final boolean parseTitlesAndPageNums,
			@FormDataParam("file") InputStream uploadedInputStream
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
				parseTitlesAndPageNums,
				uploadedInputStream
				);
		 		
		return Response.created(new URI(manuscriptManifestURI)).entity(manuscriptManifestURI).build();
		
}


		
	
	

	
		
}

