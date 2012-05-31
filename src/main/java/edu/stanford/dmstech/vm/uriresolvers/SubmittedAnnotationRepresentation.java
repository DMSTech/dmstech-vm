package edu.stanford.dmstech.vm.uriresolvers;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
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

import edu.stanford.dmstech.vm.DMSTechRDFConstants;
import edu.stanford.dmstech.vm.RDFUtils;
import edu.stanford.dmstech.vm.indexing.SharedCanvasTDBManager;
import edu.stanford.dmstech.vm.uriresolvers.ingest.AnnotationIngester;

@Path("/submitted_annotations/{annoId}")
public class SubmittedAnnotationRepresentation {

	@GET
	@Produces("application/rdf+xml")
	public String getResourceAsXML(
			@Context UriInfo uriInfo
			) throws URISyntaxException {
		return getAllStatementsForAnnotation(uriInfo, "RDF/XML");
	}
	
	@GET 
	@Produces("text/turtle;charset=utf-8")
	public String getResourceStmtsAsTurtle(
			@Context UriInfo uriInfo 
			) throws URISyntaxException {		
		return getAllStatementsForAnnotation(uriInfo, "TURTLE");
	}

	@GET 
	@Produces("text/html;charset=utf-8")
	public String getResourceStmtsAsHTML(
			@Context UriInfo uriInfo
			) throws URISyntaxException, TransformerConfigurationException, TransformerFactoryConfigurationError, TransformerException {		
		String statements = getAllStatementsForAnnotation(uriInfo, "RDF/XML");
				return RDFUtils.serializeRDFToHTML(statements);
	}
	
	@PUT
	public Response replaceAnnotation(
				InputStream inputStream) throws IOException {	
		
		//delete old rdf
		
		//a) from tbd as below - delete old 
		//b) from file system  - delete the single file
		//c) from solr - delete old solr record for canvas?  or will the indexing step
		// below just replace the value for the given annotation???
		
		
		return (new AnnotationIngester().saveAnnotations(inputStream));
			
		
	}
	
	private String getAllStatementsForAnnotation(UriInfo uriInfo, String serializeAs) {
		StringWriter stringWriter = new StringWriter();		
		String annotationURI = uriInfo.getAbsolutePath().toASCIIString();
		
		SharedCanvasTDBManager tdbManager = new SharedCanvasTDBManager();
		Model tdb = tdbManager.loadMainTDBDataset();	
		
		Model model = ModelFactory.createDefaultModel();
		model.setNsPrefixes(DMSTechRDFConstants.getInstance().getInitializingModel());
		
		model.add(tdb.createResource(annotationURI).listProperties()).write(stringWriter, serializeAs);		
		return stringWriter.toString();
	}

	
	
	
}