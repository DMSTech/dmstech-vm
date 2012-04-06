package edu.stanford.dmstech.vm.uriresolvers.images;

import java.io.File;
import java.io.StringWriter;
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
import edu.stanford.dmstech.vm.RDFUtils;
import edu.stanford.dmstech.vm.SharedCanvasUtil;

import com.hp.hpl.jena.rdf.model.Model;
import com.sun.jersey.api.NotFoundException;

@Path("/{collectionId}/{manuscriptId}/")
public class NormalSequenceResource {

	@GET
	@Path("/NormalSequence.xml") 
	@Produces("application/rdf+xml")
	public String getResourceMapAsXML(
			@PathParam("collectionId") final String collectionId,
			@PathParam("manuscriptId") final String manuscriptId
			) throws Exception {   
		return SharedCanvasUtil.getSerializedRDFFromHomeDir(collectionId + "/" + manuscriptId + "/" + Config.normalSequenceFileName, "RDF/XML");
		
	}
		
	@GET
	@Path("/NormalSequence.ttl")  
	@Produces("text/turtle;charset=utf-8")
	public String getResourceMapAsTurtle(
			@PathParam("collectionId") final String collectionId,
			@PathParam("manuscriptId") final String manuscriptId
			) throws Exception {
		return SharedCanvasUtil.getSerializedRDFFromHomeDir(collectionId + "/" + manuscriptId + "/" + Config.normalSequenceFileName, "TURTLE");
			
	}

	@GET
	@Path("/ManifNormalSequenceest.html")  
	@Produces("text/html;charset=utf-8")
	public String getResourceMapAsHTML(
			@PathParam("collectionId") final String collectionId,
			@PathParam("manuscriptId") final String manuscriptId
			) throws Exception {
		return RDFUtils.serializeRDFToHTML(SharedCanvasUtil.getSerializedRDFFromHomeDir(collectionId + "/" + manuscriptId + "/" + Config.normalSequenceFileName, "RDF/XML"));		
	}
		

				
}


