package edu.stanford.dmstech.vm.uriresolvers;

import java.io.File;
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
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

import edu.stanford.dmstech.vm.Config;
import edu.stanford.dmstech.vm.DMSTechRDFConstants;
import edu.stanford.dmstech.vm.RDFUtils;
import edu.stanford.dmstech.vm.indexing.SharedCanvasSOLRIndexer;
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
	public Response replaceAnnotation(@Context UriInfo uriInfo,
				InputStream inputStream) throws IOException {	
		
		String annotationURI = uriInfo.getAbsolutePath().toASCIIString();
		SharedCanvasTDBManager tdbManager = new SharedCanvasTDBManager();
		Model tdb = tdbManager.loadMainTDBDataset();
		Resource annotation = tdb.createResource(annotationURI);
		// 1.  REMOVE STATEMENTS FOR OLD ANNOTATION FROM TRIPLESTORE
		tdb.remove(annotation.listProperties());
		
		// 2.  DELETE THE OLD RDF FILE IN THE FILESYSTEM
		File annoFile = new File(Config.getAbsolutePathToTextAnnoFile(annotationURI));
		if (annoFile.exists()) annoFile.delete();
		
		// 3.  SAVE THE NEW ANNOTATION
		Response response = new AnnotationIngester().saveAnnotations(inputStream);
		
		// 4.  REINDEX THE SOLR DOCUMENTS FOR ANY AFFECTED CANVASES
		StmtIterator canvasIter = tdb.listStatements(annotation, DMSTechRDFConstants.getInstance().oacHasTargetProperty, (RDFNode) null);
		while (canvasIter.hasNext()) {
			Resource canvas = canvasIter.next().getObject().asResource();
		//	if (! annotation.hasProperty(RDF.type, DMSTechRDFConstants.getInstance().oacTextAnnotationType)) continue;
			SharedCanvasSOLRIndexer solrIndexer = new SharedCanvasSOLRIndexer();
			solrIndexer.updateTextAnnosForCanvas(canvas, tdb);
		}
		return response;

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