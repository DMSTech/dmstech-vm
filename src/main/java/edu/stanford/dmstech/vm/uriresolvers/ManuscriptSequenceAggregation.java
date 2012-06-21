package edu.stanford.dmstech.vm.uriresolvers;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;

import com.hp.hpl.jena.rdf.model.Model;

import edu.stanford.dmstech.vm.Config;
import edu.stanford.dmstech.vm.RDFUtils;
import edu.stanford.dmstech.vm.SharedCanvasUtil;



@Path("/{collectionId}/{manuscriptId}/sequences/{sequenceId: (?!.*\\.html$|.*\\.xml$|.*\\.ttl$).*}")
public class ManuscriptSequenceAggregation {	
	
	@Context 
	UriInfo uriInfo;
	
	@GET 
	@Produces("application/rdf+xml")
	public Response redirectSequenceReqToXMLResourceMap() throws URISyntaxException {
		String originalRequest = uriInfo.getAbsolutePath().toASCIIString();
		return Response.seeOther(new URI(originalRequest + ".xml")).build();
	} 
	
	@GET 
	@Produces("text/turtle;charset=utf-8")
	public Response redirectSequenceReqToTurtleResourceMap() throws URISyntaxException {
		String originalRequest = uriInfo.getAbsolutePath().toASCIIString();
		return Response.seeOther(new URI(originalRequest + ".ttl")).build();
	}

	@GET 
	@Produces("text/html;charset=utf-8")
	public Response redirectSequenceReqToHTMLResourceMap() throws URISyntaxException {
		String originalRequest = uriInfo.getAbsolutePath().toASCIIString();
		return Response.seeOther(new URI(originalRequest + ".html")).build();
	}
	
	@PUT
	public Response replaceSequence(
			@PathParam("collectionId") final String collectionId,
			@PathParam("manuscriptId") final String manuscriptId,
			@PathParam("sequenceId") final String sequenceId,
			@FormParam("makedefault") final boolean makeDefault,
			@FormParam("sequence") final String newSequence) throws Exception {

        Model model = RDFUtils.loadModelFromString(newSequence, "N-TRIPLE");
        
        String fileToSave = new File(Config.getAbsolutePathToManuscriptsSequenceDir(collectionId, manuscriptId), sequenceId + ".nt").getAbsolutePath();
		RDFUtils.serializeModelToFile(model, fileToSave, "N-TRIPLE");

		boolean success = SharedCanvasUtil.notifyTPENAboutIngest();
		// TODO:  may want to do something here if false
		
		return Response.ok().build();
	}
	

	
		
}

