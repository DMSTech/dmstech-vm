package edu.stanford.dmstech.vm.uriresolvers.images;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONException;

@Path("/{collectionId}/{manuscriptId}/Sequence")
public class SequenceGenerator {
	
	
	@POST
	@Produces("application/rdf+xml")
	public Response saveNewSequence(InputStream inputStream)  {
		URI uriForNewSequence = null;
		return Response.created(uriForNewSequence).build();
	}
	
	public static void main(String args[]) throws IOException, JSONException
	{
	//	InputStream is = new ByteArrayInputStream(testData.getBytes("UTF-8"));
;
	}
		
}
