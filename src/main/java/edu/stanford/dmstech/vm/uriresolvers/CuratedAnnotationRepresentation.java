package edu.stanford.dmstech.vm.uriresolvers;

import java.io.StringWriter;
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

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import edu.stanford.dmstech.vm.RDFUtils;
import edu.stanford.dmstech.vm.SharedCanvasUtil;
import edu.stanford.dmstech.vm.indexing.SharedCanvasTDBManager;

@Path("/{collectionId}/{manuscriptId}/{canvasId}/{annoId}")
public class CuratedAnnotationRepresentation {

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
	public Response getResourceStmtsAsHTML(
			@Context UriInfo uriInfo, 
			@PathParam("manuscriptId") final String manuscriptId,
			@PathParam("canvasId") final String canvasId
			) throws URISyntaxException, TransformerConfigurationException, TransformerFactoryConfigurationError, TransformerException {		
		
		String originalRequest = uriInfo.getAbsolutePath().toASCIIString();
		return SharedCanvasUtil.redirectToHTMLPage(originalRequest);
		
	}
	
	private String getAllStatementsForAnnotation(UriInfo uriInfo, String manuscriptId, String canvasId, String serializeAs) {
		StringWriter stringWriter = new StringWriter();		
		String annotationURI = uriInfo.getAbsolutePath().toASCIIString();
		
		SharedCanvasTDBManager tdbManager = new SharedCanvasTDBManager();
		Model tdb = tdbManager.loadMainTDBDataset();	
		
		ModelFactory.createDefaultModel().add(tdb.createResource(annotationURI).listProperties()).write(stringWriter, serializeAs);		
		return stringWriter.toString();
	}

	
			
	
	
	
}