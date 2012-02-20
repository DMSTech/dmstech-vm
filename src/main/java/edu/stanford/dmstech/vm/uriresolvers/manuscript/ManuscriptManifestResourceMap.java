package edu.stanford.dmstech.vm.uriresolvers.manuscript;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Path;

import edu.stanford.dmstech.vm.Config;

import com.sun.jersey.api.NotFoundException;

@Path("/manuscript/{manuscriptId}/manifest/")
public class ManuscriptManifestResourceMap {

		@GET
		@Produces("application/rdf+xml")
		public Response redirectManifestReqToXMLResourceMap(@Context UriInfo uriInfo) throws URISyntaxException {
			String originalRequest = uriInfo.getAbsolutePath().toASCIIString();
			String resourceMapFileName = "/manifestResourceMap.xml";
			return Response.seeOther(new URI(originalRequest + resourceMapFileName)).build();
		} 
		
		@GET 
		@Produces("text/turtle;charset=utf-8")
		public Response redirectManifestReqToTurtleResourceMap(@Context UriInfo uriInfo) throws URISyntaxException {
			String originalRequest = uriInfo.getAbsolutePath().toASCIIString();
			String resourceMapFileName = "/manifestResourceMap.ttl";
			return Response.seeOther(new URI(originalRequest + resourceMapFileName)).build();
		} 
		
		@GET
		@Path("manifestResourceMap.xml")  
		public File getManifestResourceMapAsXML(@PathParam("manuscriptId") final String manuscriptId, @PathParam("ext") final String fileExtension) throws URISyntaxException {
			final File file = new File(Config.homeDir,  manuscriptId + "/rdf/" + Config.getNormalSequenceFileName());
			if (!file.exists()) {
				throw new NotFoundException("File, " + file.getAbsolutePath() + ", is not found");
			}
			return file;
		}
		
		@GET
		@Path("manifestResourceMap.ttl")  
		public String getManifestResourceMapAsTurtle(@PathParam("manuscriptId") final String manuscriptId, @PathParam("ext") final String fileExtension) throws URISyntaxException {
			// either want to keep turtle in its own prebuilt file, or generate turtle on the fly from the rdf-xml
			return "todo:  fix this method";
		}
		

		}


