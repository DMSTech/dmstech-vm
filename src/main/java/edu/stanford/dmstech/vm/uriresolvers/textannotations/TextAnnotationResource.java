package edu.stanford.dmstech.vm.uriresolvers.textannotations;

import java.io.InputStream;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;

import edu.stanford.dmstech.vm.Config;
import edu.stanford.dmstech.vm.uriresolvers.AnnotationUtils;

@Path("/{collectionId}/{manuscriptId}/{canvasId}/{textAnnoId}")
public class TextAnnotationResource {
 
	
	
	Logger logger = Logger.getLogger(TextAnnotationResource.class.getName());
	

	@GET
	@Produces("application/rdf+xml")
	public String getResourceAsXML(
			@Context UriInfo uriInfo, 
			@PathParam("manuscriptId") final String manuscriptId,
			@PathParam("canvasId") final String canvasId
			) throws URISyntaxException {
		return getAllStatementsForAnnotation(uriInfo, manuscriptId, canvasId, "RDF/XML");
	}
	
	@GET 
	@Produces("text/turtle;charset=utf-8")
	public String getResourceStmtsAsTurtle(
			@Context UriInfo uriInfo, 
			@PathParam("manuscriptId") final String manuscriptId,
			@PathParam("canvasId") final String canvasId
			) throws URISyntaxException {		
		return getAllStatementsForAnnotation(uriInfo, manuscriptId, canvasId, "TURTLE");
	}

	@GET 
	@Produces("text/html;charset=utf-8")
	public String getResourceStmtsAsHTML(
			@Context UriInfo uriInfo, 
			@PathParam("manuscriptId") final String manuscriptId,
			@PathParam("canvasId") final String canvasId
			) throws URISyntaxException {		
		String statements = getAllStatementsForAnnotation(uriInfo, manuscriptId, canvasId, "RDF/XML");
				return AnnotationUtils.serializeRDFToHTML(statements);
	}
	
	private String getAllStatementsForAnnotation(UriInfo uriInfo, String manuscriptId, String canvasId, String serializeAs) {
		StringWriter stringWriter = new StringWriter();		
		String annotationURI = uriInfo.getAbsolutePath().toASCIIString();
		Model textAnnotationsModel = loadTextAnnotationsModel(manuscriptId, canvasId);
		ModelFactory.createDefaultModel().add(textAnnotationsModel.createResource(annotationURI).listProperties()).write(stringWriter, serializeAs);		
		return stringWriter.toString();
	}

	private Model loadTextAnnotationsModel(String manuscriptId, String canvasId) {
		 Model model = ModelFactory.createDefaultModel();
		 InputStream in = FileManager.get().open(getTextAnnosRelativeFileLocation(manuscriptId, canvasId));
		 if (in == null) {
		    throw new IllegalArgumentException(
		                                 "File: " + getTextAnnosRelativeFileLocation(manuscriptId, canvasId) + " not found");
		 }	
		 model.read(in, null);
		 return model;	
	}

			
	private String getTextAnnosRelativeFileLocation(String manuscriptId, String canvasId) {
		return "manuscripts/" + manuscriptId + "/canvas/" + canvasId + "/rdf/" + Config.getTextAnnotationFileName();
	}
	
	
	
}