package edu.stanford.dmstech.vm.uriresolvers.textannotations;

import java.io.File;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.hp.hpl.jena.rdf.model.Model;
import com.sun.jersey.api.NotFoundException;

import edu.stanford.dmstech.vm.Config;
import edu.stanford.dmstech.vm.RDFUtils;
import edu.stanford.dmstech.vm.SharedCanvasUtil;

@Path("/{collectionId}/{manuscriptId}/")
public class TextAnnotationListResourceMap {
 

	@GET
	@Path("/TextAnnotations.xml") 
	@Produces("application/rdf+xml")
	public String getResourceMapAsXML(
			@PathParam("collectionId") final String collectionId,
			@PathParam("manuscriptId") final String manuscriptId
			) throws Exception {   
		return SharedCanvasUtil.getSerializedRDFFromHomeDir(collectionId + "/" + manuscriptId + "/" + Config.textAnnotationFileName, "RDF/XML");
		
	}
		
	@GET
	@Path("/TextAnnotations.ttl")  
	@Produces("text/turtle;charset=utf-8")
	public String getResourceMapAsTurtle(
			@PathParam("collectionId") final String collectionId,
			@PathParam("manuscriptId") final String manuscriptId
			) throws Exception {
		return SharedCanvasUtil.getSerializedRDFFromHomeDir(collectionId + "/" + manuscriptId + "/" + Config.textAnnotationFileName, "TURTLE");
			
	}

	@GET
	@Path("/TextAnnotations.html")  
	@Produces("text/html;charset=utf-8")
	public String getResourceMapAsHTML(
			@PathParam("collectionId") final String collectionId,
			@PathParam("manuscriptId") final String manuscriptId
			) throws Exception {
		return RDFUtils.serializeRDFToHTML(SharedCanvasUtil.getSerializedRDFFromHomeDir(collectionId + "/" + manuscriptId + "/" + Config.textAnnotationFileName, "RDF/XML"));		
	}

}



