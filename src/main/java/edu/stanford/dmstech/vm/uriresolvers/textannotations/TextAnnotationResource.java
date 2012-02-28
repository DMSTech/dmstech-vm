package edu.stanford.dmstech.vm.uriresolvers.textannotations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Path;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

import org.apache.commons.transaction.file.FileResourceManager;
import org.apache.commons.transaction.util.Jdk14Logger;
import org.apache.commons.transaction.util.Log4jLogger;
import org.apache.commons.transaction.util.LoggerFacade;
import org.codehaus.jackson.jaxrs.Annotations;


import edu.stanford.dmstech.vm.Config;
import edu.stanford.dmstech.vm.uriresolvers.AnnotationUtils;
import edu.stanford.dmstech.vm.uriresolvers.TextAnnotationUtils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.sun.jersey.api.NotFoundException;
import com.sun.research.ws.wadl.Doc;
import com.sun.research.ws.wadl.Resources;

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
				return AnnotationUtils.serialzeRDFToHTML(statements);
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