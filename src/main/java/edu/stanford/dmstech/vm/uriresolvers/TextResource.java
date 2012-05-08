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
import groovyx.net.http.ContentType;


	@Path("/annotation_body_texts/{text_id}")
	public class TextResource {

		@GET
		@Produces("text/plain")
		public Response redirectToResourceMap(
				@PathParam("text_id") String textId) {
			
		    File f = new File(Config.getAbsolutePathToTextAnnosBodiesDir(), textId + ".txt");

		    if (!f.exists()) {
		        throw new WebApplicationException(404);
		    }

		    String mt = new MimetypesFileTypeMap().getContentType(f);
		    return Response.ok(f, mt).build();
		}

	
				
}


