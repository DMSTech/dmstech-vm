package edu.stanford.dmstech.vm.uriresolvers.images;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.LinkedHashMap;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
