package edu.stanford.dmstech.vm.uriresolvers.ingest;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

public class AnnotationIngest {
	
	@POST
	public Response saveAnnotations(
			@PathParam("collectionId") final String collectionId,
			@PathParam("manuscriptId") final String manuscriptId,
			@PathParam("canvasId") final String canvasId,
			InputStream inputStream) throws IOException {
	
			return (new AnnotationIngester().saveAnnotations(inputStream));
		
	}
	
	
}
