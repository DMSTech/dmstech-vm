package edu.stanford.dmstech.vm.uriresolvers;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import com.hp.hpl.jena.rdf.model.Resource;

import edu.stanford.dmstech.vm.Config;
import edu.stanford.dmstech.vm.DMSTechRDFConstants;
import edu.stanford.dmstech.vm.RDFUtils;
import edu.stanford.dmstech.vm.SharedCanvasUtil;

@Path("{collectionId}/{manuscriptId}/{canvasId: (.*\\\\.html$|.*\\\\.xml$|.*\\\\.ttl$)}")
public class CanvasRepresentation {
 
	@Context 
	UriInfo uriInfo;
	
	@GET
	@Produces("application/rdf+xml")
	public Response getSequenceResourceMapAsXML(
			@PathParam("collectionId") final String collectionId,
			@PathParam("manuscriptId") final String manuscriptId,
			@PathParam("canvasId") final String sequenceId
			) throws Exception {   
		String sequenceIdWithoutExtension = sequenceId.substring(0, sequenceId.lastIndexOf("."));
		return SharedCanvasUtil.getSerializedRDFFromHomeDir(Config.collectionSubDir + "/" + collectionId + "/" + manuscriptId + "/" + sequenceIdWithoutExtension, sequenceId);
		
	}
	
	/*need a new canvasConceptual class.
	
	here i need to create another method in SharedCanvasUtil  that is like the getSerializedFromhomeDir method
	but also like the getserialiedcnavasrdf.  so combines them into a new method.
	*/
	@GET
	@Produces("application/rdf+xml")
	public String getCanvasAsXML() throws Exception {	
		String canvasURI = uriInfo.getAbsolutePath().toASCIIString();		
		return SharedCanvasUtil.getSerializedCanvasRDF(canvasURI, "RDF/XML");
		}
	
	@GET 
	@Produces("text/turtle;charset=utf-8")
	public String getCanvasAsTurtle() throws Exception {		
		String canvasURI = uriInfo.getAbsolutePath().toASCIIString();		
		return SharedCanvasUtil.getSerializedCanvasRDF(canvasURI, "TURTLE");	
	}

	@GET 
	@Produces("text/html;charset=utf-8")
	public String getCanvasAsHTML() throws TransformerFactoryConfigurationError, Exception {	
		String canvasURI = uriInfo.getAbsolutePath().toASCIIString();
		String rdfAsXML = SharedCanvasUtil.getSerializedCanvasRDF(canvasURI, "RDF/XML");
		return RDFUtils.serializeRDFToHTML(rdfAsXML);	
	}

	
}