package edu.stanford.dmstech.vm.uriresolvers.imageannotations;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Path;

import edu.stanford.dmstech.vm.Config;

import com.sun.jersey.api.NotFoundException;

@Path("/manuscript/{manuscriptId}/imageannotations")
public class ImageAnnotationListAggregation {

	@GET 
	@Produces("application/rdf+xml")
	public Response redirectReqToXMLResourceMap(@Context UriInfo uriInfo) throws URISyntaxException {
		String originalRequest = uriInfo.getAbsolutePath().toASCIIString();
		String resourceMapFileName = "/imgAnnoResourceMap.xml";
		return Response.seeOther(new URI(originalRequest + resourceMapFileName)).build();
	} 
	
	@GET
	@Produces("text/turtle;charset=utf-8")
	public Response redirectReqToTurtleResourceMap(@Context UriInfo uriInfo) throws URISyntaxException {
		String originalRequest = uriInfo.getAbsolutePath().toASCIIString();
		String resourceMapFileName = "/imgAnnoResourceMap.ttl";
		return Response.seeOther(new URI(originalRequest + resourceMapFileName)).build();
	}

	@GET
	@Path("imgAnnoResourceMap.xml") 
	@Produces("application/rdf+xml")
	public File getResourceMapAsXML(@PathParam("manuscriptId") final String manuscriptId) throws URISyntaxException {
    	
		final File file = new File(Config.homeDir,  manuscriptId + "/rdf/" + Config.getImageAnnotationFileName());
		if (!file.exists()) {
			throw new NotFoundException("File, " + file.getAbsolutePath() + ", is not found");
		}
		return file;
	}
	
	@GET
	@Path("imgAnnoResourceMap.ttl")  
	@Produces("text/turtle;charset=utf-8")
	public String getResourceMapAsTurtle(@PathParam("manuscriptId") final String manuscriptId) throws URISyntaxException {
		// either want to keep turtle in its own prebuilt file, or generate turtle on the fly from the rdf-xml
		return "todo:  fix next this method" + manuscriptId;
	}
	


	
				
}


