package edu.stanford.dmstech.vm.uriresolvers;

import java.io.File;

import javax.activation.MimetypesFileTypeMap;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import edu.stanford.dmstech.vm.Config;


	@Path("/{collectionId}/{manuscriptId}/images/{filename}")
	public class ImageResource {

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


