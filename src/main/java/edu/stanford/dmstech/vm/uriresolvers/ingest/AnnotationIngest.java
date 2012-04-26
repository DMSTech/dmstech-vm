package edu.stanford.dmstech.vm.uriresolvers.ingest;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/annotations/ingest")
public class AnnotationIngest {
	
	@POST
	public Response saveAnnotations(
			InputStream inputStream) throws IOException {	
			return (new AnnotationIngester().saveAnnotations(inputStream));
		
	}
	
	
}
