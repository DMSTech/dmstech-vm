package edu.stanford.dmstech.vm;

import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;

import edu.stanford.dmstech.vm.indexing.SharedCanvasTDBManager;


public class SharedCanvasUtil {

	private static DMSTechRDFConstants rdfConstants = DMSTechRDFConstants.getInstance();
	
	public static Response getSerializedRDFFromHomeDir(String relativePath, String requestedFile) throws Exception{		
		Model model = RDFUtils.loadModelInHomeDir(relativePath, "N-TRIPLE");
		return getSerializedRDFForModel(model, requestedFile);
	}		

	public static Response getSerializedRDFFromDir(String absolutePath, String requestedFile) throws Exception{		
		Model model = RDFUtils.loadModelInAbsoluteDir(absolutePath, "N-TRIPLE");
		return getSerializedRDFForModel(model, requestedFile);
		
	}		

	private static Response getSerializedRDFForModel(Model model, String requestedFile) throws TransformerConfigurationException, TransformerFactoryConfigurationError, TransformerException {
		model.setNsPrefixes(rdfConstants.getInitializingModel());
		StringWriter stringWriter = new StringWriter();
		String mediaType = null;
		String result = null;
		if (requestedFile.toLowerCase().endsWith(".xml")) {
			model.write(stringWriter, "RDF/XML");
			result = stringWriter.toString();	
			mediaType = "application/rdf+xml";
		} else if (requestedFile.toLowerCase().endsWith(".ttl")) {
			model.write(stringWriter, "TURTLE");
			result = stringWriter.toString();	
			mediaType = "text/turtle;charset=utf-8";
		} else {
			model.write(stringWriter, "RDF/XML");
			result = RDFUtils.serializeRDFToHTML(stringWriter.toString());
			mediaType = MediaType.TEXT_HTML;
		}

		return Response.ok(result, mediaType).build();
	}
	
	public static Response getSerializedCanvasRDF(String canvasURI, String fileExtension) throws TransformerConfigurationException, TransformerFactoryConfigurationError, TransformerException {
		
		SharedCanvasTDBManager tdbManager = new SharedCanvasTDBManager();
		Model tdb = tdbManager.loadMainTDBDataset();
		 
		Model canvasModel = ModelFactory.createDefaultModel();
		canvasModel.setNsPrefixes(rdfConstants.getInitializingModel());
		
		canvasModel.createResource(canvasURI)
				  .addProperty(rdfConstants.scHasTextAnnotations, canvasURI  + "/TextAnnotations" )
				  .addProperty(rdfConstants.scHasImageAnnotations, canvasURI  + "/ImageAnnotations" )
				  .addProperty(rdfConstants.scHasZoneAnnotations, canvasURI  + "/ZoneAnnotations" );
		 
		Resource canvasResource = tdb.createResource(canvasURI);
		  StmtIterator canvasStmtIter = tdb.listStatements(canvasResource, null, (RDFNode) null);
		  canvasModel.add(canvasStmtIter);	  
		  return buildResponseFromModel(canvasModel, fileExtension);
	}		
	
	public static String buildStringFromModel(Model model, String fileExtension) throws TransformerConfigurationException, TransformerFactoryConfigurationError, TransformerException {
		StringWriter stringWriter = new StringWriter();
		
		String result = null;
		if (fileExtension.equals(".xml")) {
			model.write(stringWriter, "RDF/XML");
			result = stringWriter.toString();	
			
		} else if (fileExtension.equals(".ttl")) {
			model.write(stringWriter, "TURTLE");
			result = stringWriter.toString();	
			
		} else {
			model.write(stringWriter, "RDF/XML");
			result = RDFUtils.serializeRDFToHTML(stringWriter.toString());
			
		}

		return result;
	}

	
	public static Response buildResponseFromModel(Model model, String fileExtension) throws TransformerConfigurationException, TransformerFactoryConfigurationError, TransformerException {
		StringWriter stringWriter = new StringWriter();
		String mediaType = null;
		String result = null;
		if (fileExtension.equals(".xml")) {
			model.write(stringWriter, "RDF/XML");
			result = stringWriter.toString();	
			mediaType = "application/rdf+xml";
		} else if (fileExtension.equals(".ttl")) {
			model.write(stringWriter, "TURTLE");
			result = stringWriter.toString();	
			mediaType = "text/turtle;charset=utf-8";
		} else {
			model.write(stringWriter, "RDF/XML");
			result = RDFUtils.serializeRDFToHTML(stringWriter.toString());
			mediaType = MediaType.TEXT_HTML;
		}

		return Response.ok(result, mediaType).build();
	}

	public static Resource addResourceMapAndAggregationToModel(Model model, String aggregationURI, Resource dmsAggregationType, String dateCreated) {
		
		Resource aggregation = model.createResource(aggregationURI)
				.addProperty(RDF.type, rdfConstants.oreAggregationClass)
				.addProperty(RDF.type, RDF.List)
				.addProperty(RDF.type, dmsAggregationType);
		
		model.createResource(aggregationURI + ".xml")
		.addProperty(RDF.type, rdfConstants.oreResourceMapClass)
		.addProperty(rdfConstants.oreDescribes, aggregation)
		.addProperty(DCTerms.created, model.createTypedLiteral(dateCreated, DMSTechRDFConstants.DCTERMS_NAMESPACE + "W3CDTF"))
		.addProperty(DC.format, "application/rdf+xml");	
		
		return aggregation;
	}
	
	public static String queryTest() {
		
		String result = null;
			
		String queryString = "PREFIX oac: <http://www.openannotation.org/ns/> " +
				"PREFIX sc: <http://www.shared-canvas.org/ns/> " +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				"PREFIX ore: <http://www.openarchives.org/ore/terms/> " +
				"SELECT  ?anno " +
				"	WHERE {" +
				   "" +
				   "<http://localhost:8080/ingested/myTestManu/NormalSequence> ore:aggregates ?canvas ." +				
				"	 ?anno oac:hasTarget ?canvas ." +
				"	 ?anno rdf:type sc:" + "ImageAnnotation"  +
				"	}";

		Model model;
		try {
			model = buildAnnotationResourceMap("http://localhost:8080/ingested/myTestManu/NormalSequence.xml",
					"TextAnnotation", rdfConstants.scImageAnnotationListClass, queryString);
			result =  buildStringFromModel(model, "xml");
			
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		
		
		return result;
		
	
	}
	
	public static Response buildResourceMapForManuscriptAnnotations(String originalRequest, String annotationType, Resource annotationListClass) throws TransformerConfigurationException, TransformerFactoryConfigurationError, TransformerException {
		String aggregationURI = originalRequest.substring(0, originalRequest.lastIndexOf("."));
		String fileExtension = originalRequest.substring(originalRequest.lastIndexOf(".") + 1);
		
		
		String queryString = "PREFIX oac: <http://www.openannotation.org/ns/> " +
				"PREFIX sc: <http://www.shared-canvas.org/ns/> " +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				"PREFIX ore: <http://www.openarchives.org/ore/terms/> " +
				"SELECT ?anno " +
				"	WHERE {" +
				   "<" +aggregationURI + "> ore:aggregates ?canvas ." +				
				"	 ?anno oac:hasTarget ?canvas ." +
				"	 ?anno rdf:type sc:" + annotationType  +
				"	}";
		
		try {
			Model model = buildAnnotationResourceMap(originalRequest,
					annotationType, annotationListClass, queryString);
			
			return buildResponseFromModel(model, fileExtension);
		} catch (Exception e) {
			e.printStackTrace();
			return Response.serverError().build();
		}
	
		
	}
	
	

	
	public static Response buildResourceMapForCanvasAnnotations(String originalRequest, String annotationType, Resource annotationListClass){
		String canvasURI = originalRequest.substring(0, originalRequest.lastIndexOf("/") );
		String fileExtension = originalRequest.substring(originalRequest.lastIndexOf(".") + 1);
		
		String queryString = "PREFIX oac: <http://www.openannotation.org/ns/> " +
				"PREFIX sc: <http://www.shared-canvas.org/ns/> " +
				"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
				"SELECT ?anno " +
				"	WHERE {" +
				"	 ?anno oac:hasTarget <" + canvasURI + "> ." +
				"	 ?anno rdf:type sc:" + annotationType  +
				"	}";
		
		try {
			Model model = buildAnnotationResourceMap(originalRequest,
					annotationType, annotationListClass, queryString);
			return buildResponseFromModel(model, fileExtension);
		} catch (Exception e) {
			e.printStackTrace();
			return Response.serverError().build();
		}
	
	}



	private static Model buildAnnotationResourceMap(
			String originalRequest, String annotationType,
			Resource annotationListClass, String queryString)
			throws IOException, TransformerConfigurationException,
			TransformerFactoryConfigurationError, TransformerException {
		String W3CDTF_NOW = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")).format(new Date());
		
		
		String aggregationURI = originalRequest.substring(0, originalRequest.lastIndexOf("."));
		
		Model model = ModelFactory.createDefaultModel();
		model.setNsPrefixes(rdfConstants.getInitializingModel());
		
		Resource aggregation = SharedCanvasUtil.addResourceMapAndAggregationToModel(model, aggregationURI, annotationListClass, W3CDTF_NOW);
		SharedCanvasTDBManager tdbManager = new SharedCanvasTDBManager();
		Model tdb = tdbManager.loadMainTDBDataset();	

		   Query query = QueryFactory.create(queryString);
		   QueryExecution qe = QueryExecutionFactory.create(query, tdb);
		   ResultSet results = qe.execSelect();
	
		Resource currentRDFListNode = aggregation;
		
		while (results.hasNext()) {
			Resource annotation = results.nextSolution().getResource("anno");
			model.createResource(annotation);
			// add all statements for which the annotation is the subject.
			// i.e., everything 'about' the annotation
			StmtIterator annoStmtIter = tdb.listStatements(annotation, null, (RDFNode) null);
			model.add(annoStmtIter);
			StmtIterator targets = annotation.listProperties(rdfConstants.oacHasTargetProperty);
			StmtIterator bodies = annotation.listProperties(rdfConstants.oacHasBodyProperty);
			// add all statements for which the annotation target is the subject.
			// i.e., everything 'about' the target
			while (targets.hasNext()) {
				Resource target = targets.nextStatement().getObject().asResource();
				StmtIterator targetStmts = tdb.listStatements(target, null, (RDFNode) null);
				model.add(targetStmts);
			}
			// add all statements for which the annotation body is the subject.
			// i.e, everything 'about' the body
			while (bodies.hasNext()) {
				Resource body = bodies.nextStatement().getObject().asResource();
				StmtIterator bodyStmts = tdb.listStatements(body, null, (RDFNode) null);
				model.add(bodyStmts);
				if (annotationType.equals(rdfConstants.oacTextAnnotationType) && ! body.hasProperty(RDF.type, rdfConstants.cntAsTxtType)) {
					// If the body isn't inline then get it and put it inline as an
					// anonymous node sameAs'd to the original URI
					model.createResource(AnonId.create())
							.addProperty(RDF.type, rdfConstants.cntAsTxtType)
							.addLiteral(rdfConstants.cntRestProperty, RDFUtils.getTextFromURI(body.getURI()))
							.addLiteral(rdfConstants.cntCharEncProperty, "utf-8")
							.addProperty(OWL.sameAs, body);					
				}					
			}
			aggregation.addProperty(rdfConstants.oreAggregates, annotation);		
			currentRDFListNode.addProperty(RDF.first, annotation);
	
		if ( results.hasNext()) {	
			Resource newListNode = model.createResource(AnonId.create());
			currentRDFListNode.addProperty(RDF.rest, newListNode);
			currentRDFListNode = newListNode;
			
		} else {
			currentRDFListNode.addProperty(RDF.rest, RDF.nil);
		}				
		}
		qe.close();
		
		return model;
	}
}
