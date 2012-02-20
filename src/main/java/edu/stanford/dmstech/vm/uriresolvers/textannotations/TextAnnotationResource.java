package edu.stanford.dmstech.vm.uriresolvers.textannotations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

import org.apache.commons.transaction.file.FileResourceManager;
import org.apache.commons.transaction.util.Jdk14Logger;
import org.apache.commons.transaction.util.Log4jLogger;
import org.apache.commons.transaction.util.LoggerFacade;
import org.codehaus.jackson.jaxrs.Annotations;


import edu.stanford.dmstech.vm.Config;
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

@Path("/collection/{collectionId}/manuscript/{manuscriptId}/canvas/{canvasId}/textannotation/{textAnnoId}")
public class TextAnnotationResource {
 
	private final String W3CDTF_NOW = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")).format(new Date());
	Logger logger = Logger.getLogger(TextAnnotationResource.class.getName());
	
	


	@GET
	@Produces("application/rdf+xml")
	public String getResourceAsXML(@Context UriInfo uriInfo, @PathParam("textAnnoId") final String manuscriptId) throws URISyntaxException {
		return getAllStatementsForAnnotation(uriInfo, manuscriptId, "RDF/XML");
	}
	
	@GET 
	@Produces("text/turtle;charset=utf-8")
	public String getResourceStmtsAsTurtle(@Context UriInfo uriInfo, @PathParam("manuscriptId") final String manuscriptId) throws URISyntaxException {		
		return getAllStatementsForAnnotation(uriInfo, manuscriptId, "TURTLE");
	}

	private String getAllStatementsForAnnotation(UriInfo uriInfo, String manuscriptId, String serializeAs) {
		StringWriter stringWriter = new StringWriter();
		
		String originalRequest = uriInfo.getAbsolutePath().toASCIIString();
		String resourceURI = originalRequest.substring(0, originalRequest.indexOf(".ttl"));
		Model textAnnotationsModel = loadTextAnnotationsModel(manuscriptId);
		ModelFactory.createDefaultModel().add(textAnnotationsModel.createResource(resourceURI).listProperties()).write(stringWriter, serializeAs);;	
		
		return stringWriter.toString();
	}
	

	@PUT
	@Path("textannotation/{annotationId}")
	public Response updateAnnotation(
			@Context UriInfo uriInfo,
			@PathParam("manuscriptId") final String manuscriptId, 
			@PathParam("annotationId") final String annotationId, 
			InputStream inputStream) {
		
		String originalRequest = uriInfo.getAbsolutePath().toASCIIString();
		String resourceURI = originalRequest.substring(0, originalRequest.indexOf(".ttl"));
		Model textAnnotationsModel = loadTextAnnotationsModel(manuscriptId);
		Resource oldAnnotationResource = textAnnotationsModel.createResource(resourceURI);
				
		// have to still add this: <newAnnoId 'supercedes' oladAnnoURI>   ??
		TextAnnotationUtils textAnnoUtils = new TextAnnotationUtils();
		newTextAnnotationResource = textAnnoUtils.mintURIForAnnoAndBody
				manuscriptId,
				canvasId,
				bodyTextsToWriteToFile, rdfConstants,
				existingTextAnnosRMModel, incomingTextAnnotationsModel,
				oldTextAnnotationsRMModel,
				transactionsAggregationResource,
				incomingTextAnno);	
		// 2. remove the old anno statements from the existingRM, and put it in the oldAnnotationsRM, and add a 'supercededBy' property
		// 3. in the transactionRM put:  newURI supercedes oldURI.  and who did it I guess.

		// add the resource map for the transaction
		Resource resourceMapResource = transactionResourceMapModel.createResource(resourceMapURI)
					.addProperty(RDF.type, transactionClass)
					.addProperty(oreDescribes, aggregationResource)
					.addProperty(DCTerms.created, transactionResourceMapModel.createTypedLiteral(W3CDTF_NOW, DCTERMS_NAMESPACE + "W3CDTF"))
					.addProperty(DC.format, "application/rdf+xml");						
					// .addProperty(DCTerms.creator, annotationAuthor )
		
		return Response.created(URI.create(newURI)).build();
		
		
		
	}
	
	@DELETE
	@Path("textannotation/{annotationId}") 
	public Response deleteAnnotation(
			@PathParam("manuscriptId") final String manuscriptId, 
			@PathParam("annotationId") final String annotationId) {
		// load the model for the current annotations
		// remove statements for the deleted annotation from the model, except maybe put in a statement like hadDeletedAnnotation
		// put the removed statements in the oldAnnotations file.
	}
	
	private Model loadTextAnnotationsModel(String manuscriptId) {
		 Model model = ModelFactory.createDefaultModel();
		 InputStream in = FileManager.get().open(getTextAnnosRelativeFileLocation(manuscriptId));
		 if (in == null) {
		    throw new IllegalArgumentException(
		                                 "File: " + getTextAnnosRelativeFileLocation(manuscriptId) + " not found");
		 }	
		 model.read(in, null);
		 return model;	
	}
	
	// I have subdirectores for images, rdf, transactions
	// but the URIs are a little different because I need URIs for the individual things inside the files, 
	// like the textannotations, imageannotations, canvases
			
	private String getTextAnnosRelativeFileLocation(String manuscriptId, String canvasId) {
		return "manuscripts/" + manuscriptId + "/canvas/" + canvasId + "/rdf/" + Config.getTextAnnotationFileName();
	}
	
	
}