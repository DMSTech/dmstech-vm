package edu.stanford.dmstech.vm.uriresolvers.canvas;

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
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.commons.transaction.file.FileResourceManager;
import org.apache.commons.transaction.util.Jdk14Logger;
import org.apache.commons.transaction.util.Log4jLogger;
import org.apache.commons.transaction.util.LoggerFacade;
import org.codehaus.jackson.jaxrs.Annotations;


import edu.stanford.dmstech.vm.Config;
import edu.stanford.dmstech.vm.uriresolvers.AnnotationUtils;
import edu.stanford.dmstech.vm.uriresolvers.RDFUtils;
import edu.stanford.dmstech.vm.uriresolvers.TextAnnotationUtils;
import edu.stanford.dmstech.vm.uriresolvers.textannotations.TextAnnotationResource;

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

@Path("/{collectionId}/{manuscriptId}/{canvasId}")
public class CanvasResource {
 
	
	
Logger logger = Logger.getLogger(TextAnnotationResource.class.getName());
	
/* Do I want 'post to' included in the  canvas:
 * 
 * <dms:Canvas rdf:about="http://dmss.stanford.edu/Parker/524/manifest/17V">
<dms:acceptsAnnotations rdf:id="http://dmss.stanford.edu/Parker/524/manifest/17V/annotation"/>
<dms:pleasePostAnnotationsTo rdf:id="http://someOtherServer/annotationService"/>
</dms:Canvas>

 */

	@GET
	@Produces("application/rdf+xml")
	public File getResourceAsXML(
			@PathParam("collectionId") final String collectionId,
			@PathParam("manuscriptId") final String manuscriptId,
			@PathParam("canvasId") final String canvasId
			) throws Exception {
		return RDFUtils.getFileInHomeDir(getPathToModelInHomeDir(collectionId, manuscriptId, canvasId));
	}
	
	@GET 
	@Produces("text/turtle;charset=utf-8")
	public String getResourceStmtsAsTurtle(
			@PathParam("collectionId") final String collectionId,
			@PathParam("manuscriptId") final String manuscriptId,
			@PathParam("canvasId") final String canvasId
			) throws Exception {		
		Model textAnnotationsModel = RDFUtils.loadModelInHomeDir(getPathToModelInHomeDir(collectionId, manuscriptId, canvasId));
		StringWriter stringWriter = new StringWriter();
		textAnnotationsModel.write(stringWriter, "TURTLE");
		return stringWriter.toString();	
	}

	@GET 
	@Produces("text/html;charset=utf-8")
	public String getResourceStmtsAsHTML(
			@PathParam("collectionId") final String collectionId,
			@PathParam("manuscriptId") final String manuscriptId,
			@PathParam("canvasId") final String canvasId
			) throws TransformerFactoryConfigurationError, Exception {		
		return AnnotationUtils.serializeRDFToHTML(RDFUtils.getFileInHomeDir(getPathToModelInHomeDir(collectionId, manuscriptId, canvasId)));	
	}
	
	
	private String getPathToModelInHomeDir(String collectionId, String manuscriptId, String canvasId) throws Exception {
		
		return "collections/" + collectionId + "/manuscripts/" + manuscriptId + "/canvases/" + canvasId + "/" + Config.getCanvasFileName();
		
	}

	
	
	
	
}