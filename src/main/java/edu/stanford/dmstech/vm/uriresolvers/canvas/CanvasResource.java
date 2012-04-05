package edu.stanford.dmstech.vm.uriresolvers.canvas;

import java.io.File;
import java.io.StringWriter;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.xml.transform.TransformerFactoryConfigurationError;

import com.hp.hpl.jena.rdf.model.Model;

import edu.stanford.dmstech.vm.Config;
import edu.stanford.dmstech.vm.RDFUtils;
import edu.stanford.dmstech.vm.SharedCanvasUtil;
import edu.stanford.dmstech.vm.uriresolvers.textannotations.TextAnnotationResource;

@Path("/{collectionId}/{manuscriptId}/{canvasId}")
public class CanvasResource {
 
	@Context 
	UriInfo uriInfo;
	
Logger logger = Logger.getLogger(TextAnnotationResource.class.getName());
	
/* Do we want 'post to' included in the  canvas:
 * 
 * <dms:Canvas rdf:about="http://dmss.stanford.edu/Parker/524/manifest/17V">
<dms:acceptsAnnotations rdf:id="http://dmss.stanford.edu/Parker/524/manifest/17V/annotation"/>
<dms:pleasePostAnnotationsTo rdf:id="http://someOtherServer/annotationService"/>
</dms:Canvas>

 */


//do I really want subdirectory in the manuscript collection for each canvas?  might be better just to generate this thing dyanamically.
		
	@GET
	@Produces("application/rdf+xml")
	public String getResourceAsXML(
			) throws Exception {
	
		String canvasURI = uriInfo.getAbsolutePath().toASCIIString();		
		return SharedCanvasUtil.getSerializedCanvasRDF(canvasURI, "RDF/XML");

		}
	
	
	@GET 
	@Produces("text/turtle;charset=utf-8")
	public String getResourceStmtsAsTurtle() throws Exception {		
		String canvasURI = uriInfo.getAbsolutePath().toASCIIString();		
		return SharedCanvasUtil.getSerializedCanvasRDF(canvasURI, "TURTLE");
	
	}

	@GET 
	@Produces("text/html;charset=utf-8")
	public String getResourceStmtsAsHTML() throws TransformerFactoryConfigurationError, Exception {	
		String canvasURI = uriInfo.getAbsolutePath().toASCIIString();
		String rdfAsXML = SharedCanvasUtil.getSerializedCanvasRDF(canvasURI, "RDF/XML");
		return RDFUtils.serializeRDFToHTML(rdfAsXML);	
	}
	


	
	
	
	
}