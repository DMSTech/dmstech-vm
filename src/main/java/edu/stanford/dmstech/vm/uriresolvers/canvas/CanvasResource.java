package edu.stanford.dmstech.vm.uriresolvers.canvas;

import java.io.File;
import java.io.StringWriter;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.xml.transform.TransformerFactoryConfigurationError;

import com.hp.hpl.jena.rdf.model.Model;

import edu.stanford.dmstech.vm.Config;
import edu.stanford.dmstech.vm.uriresolvers.AnnotationUtils;
import edu.stanford.dmstech.vm.uriresolvers.RDFUtils;
import edu.stanford.dmstech.vm.uriresolvers.textannotations.TextAnnotationResource;

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