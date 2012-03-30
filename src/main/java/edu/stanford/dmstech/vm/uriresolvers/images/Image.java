package edu.stanford.dmstech.vm.uriresolvers.images;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import javax.activation.MimetypesFileTypeMap;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Path;

import edu.stanford.dmstech.vm.Config;
import edu.stanford.dmstech.vm.uriresolvers.AggregationRedirect;

import com.sun.jersey.api.NotFoundException;


	@Path("/{collectionId}/{manuscriptId}/images/{filename}")
	public class Image {

		@GET
		@Produces("image/jp2")
		public Response redirectToResourceMap(
				@PathParam("collectionId") String collectionId,
				@PathParam("manuscriptId") String manuscriptId,
				@PathParam("filename") String filename) {
			
		    File f = new File(Config.getAbsolutePathToManuscriptDir(collectionId, manuscriptId), filename);

		    if (!f.exists()) {
		        throw new WebApplicationException(404);
		    }

		    String mt = new MimetypesFileTypeMap().getContentType(f);
		    return Response.ok(f, mt).build();
		}

	
				
}


