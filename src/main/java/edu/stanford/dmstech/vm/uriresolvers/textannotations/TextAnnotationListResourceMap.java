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

@Path("/{collectionId}/{manuscriptId}/")
public class TextAnnotationListResourceMap {
 
	@GET
	@Path("/TextAnnotations.xml") 
	@Produces("application/rdf+xml")
	public File getResourceMapAsXML(
			@PathParam("collectionId") final String collectionId,
			@PathParam("manuscriptId") final String manuscriptId
			) throws URISyntaxException {   	
		return RDFUtils.getFileInHomeDir(collectionId + "/" + manuscriptId + "/" + Config.textAnnotationFileName);
	}
		
	@GET
	@Path("/TextAnnotations.ttl")  
	@Produces("text/turtle;charset=utf-8")
	public String getResourceMapAsTurtle(
			@PathParam("collectionId") final String collectionId,
			@PathParam("manuscriptId") final String manuscriptId
			) throws Exception {
		Model textAnnotationsModel = RDFUtils.loadModelInHomeDir(collectionId + "/" + manuscriptId + "/" + Config.textAnnotationFileName);
		StringWriter stringWriter = new StringWriter();
		textAnnotationsModel.write(stringWriter, "TURTLE");
		return stringWriter.toString();		
	}

	@GET
	@Path("/TextAnnotations.html")  
	@Produces("text/turtle;charset=utf-8")
	public String getResourceMapAsHTML(
			@PathParam("collectionId") final String collectionId,
			@PathParam("manuscriptId") final String manuscriptId
			) throws Exception {
		return RDFUtils.serializeRDFToHTML(RDFUtils.getFileInHomeDir(collectionId + "/" + manuscriptId + "/" + Config.textAnnotationFileName));		
	}
		

}



