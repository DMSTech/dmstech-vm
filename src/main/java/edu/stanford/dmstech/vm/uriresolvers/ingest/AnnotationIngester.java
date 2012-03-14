package edu.stanford.dmstech.vm.uriresolvers.ingest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Logger;

import javax.ws.rs.core.Response;

import org.apache.commons.transaction.file.FileResourceManager;
import org.apache.commons.transaction.util.Jdk14Logger;
import org.apache.commons.transaction.util.LoggerFacade;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.DCTypes;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;

import edu.stanford.dmstech.vm.Config;
import edu.stanford.dmstech.vm.DMSTechRDFConstants;
import edu.stanford.dmstech.vm.uriresolvers.canvas.CanvasTextAnnoResourceMap;

public class AnnotationIngester {

	
	private final String W3CDTF_NOW = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")).format(new Date());
	Logger logger = Logger.getLogger(CanvasTextAnnoResourceMap.class.getName());
	LoggerFacade loggerFacade = null;
	String resultURI = null;
	DMSTechRDFConstants rdfConstants = null;
	Model incomingAnnotationsModel = null;
	String transactionUUID = null;
	Model transactionResourceMapModel = null;
	String transactionAggregationURI = null;
	String transactionResourceMapURI = null;
	FileResourceManager frm = null;
	Object txId  = null;
	int incomingAnnoCount = 0;
	Resource transactionsAggregationResource;
	Resource newAnnotationResource = null;
	
	public Response saveAnnotations(InputStream inputStream) throws IOException {
		
		try {			
			loggerFacade = new Jdk14Logger(logger);
			rdfConstants = DMSTechRDFConstants.getInstance();
			incomingAnnotationsModel = ModelFactory.createDefaultModel();
			
			incomingAnnotationsModel.read(inputStream, null);
					
			frm = new FileResourceManager(Config.homeDir.getAbsolutePath(), Config.homeDir.getAbsolutePath() + "/temp", false, loggerFacade);
			frm.start();
			txId  = frm.generatedUniqueTxId();
			frm.startTransaction(txId);
        
			transactionResourceMapModel = ModelFactory.createDefaultModel();
			transactionUUID = UUID.randomUUID().toString();
			// these will have to be stored in one spot for the whole system.
			transactionResourceMapURI = getTransactionResourceMapURI();
			transactionAggregationURI = getTransactionAggregationURI();
			// add the aggregation for the transaction	  
			transactionsAggregationResource = transactionResourceMapModel.createResource(transactionAggregationURI)
						.addProperty(RDF.type, rdfConstants.oreAggregationClass)	
			  			.addProperty(RDF.type, rdfConstants.transactionClass)
			  			.addProperty(rdfConstants.httpMethod, rdfConstants.httpPostResource);			
			// add the resource map for the transaction
			transactionResourceMapModel.createResource(transactionResourceMapURI)
						.addProperty(RDF.type, rdfConstants.transactionClass)
						.addProperty(rdfConstants.oreDescribes, transactionsAggregationResource)
						.addProperty(DCTerms.created, transactionResourceMapModel.createTypedLiteral(W3CDTF_NOW, rdfConstants.DCTERMS_NAMESPACE + "W3CDTF"))
						.addProperty(DC.format, "application/rdf+xml");						
						// .addProperty(DCTerms.creator, annotationAuthor )
	       	        
			ResIterator annotationsIterator = incomingAnnotationsModel.listResourcesWithProperty(RDF.type, rdfConstants.oacAnnotationType);
			while (annotationsIterator.hasNext()) {
				Resource annotationRes = annotationsIterator.next();
				writeAnnotationToFile(annotationRes);
				incomingAnnoCount++;
			}

		// finish off by writing the transaction file
		 OutputStream foutForTransactionResourceMap = frm.writeResource(txId, getTransactionResourceMapRelativeFileLocation(), true);
	        transactionResourceMapModel.write(foutForTransactionResourceMap, "RDF/XML");
	        // commit all our new files
		 frm.commitTransaction(txId);
		}  catch (Exception e) {
		        throw new IOException("Couldn't save new annotations.  Rolled back all.  Caused by: " + e.getMessage());
		    }
		finally {
			if (inputStream != null)
				try {
					inputStream.close();
				} catch (IOException e) {

				}	
		}
		 // return uri for annotation if only one posted.  otherwise return uri for resource map		
		resultURI = incomingAnnoCount == 1?newAnnotationResource.getURI():transactionResourceMapURI;		
		return Response.created(URI.create(resultURI)).build();
	}

	

	

	private void writeAnnotationToFile(Resource incomingAnno) throws IOException {
		String uuid = UUID.randomUUID().toString();
	
		    try {
		    	Model newModelToWrite = ModelFactory.createDefaultModel();
		    	
		    	if (incomingAnno.getNameSpace().toLowerCase().equals("urn")) {	
			
				newAnnotationResource = newModelToWrite.createResource( getNewTextAnnotationURI(uuid), rdfConstants.oacTextAnnotationType);
				newModelToWrite.add(newAnnotationResource, RDF.type, rdfConstants.oacAnnotationType);
				newModelToWrite.add(newAnnotationResource, rdfConstants.oacHasTargetProperty, incomingAnno.listProperties(rdfConstants.oacHasTargetProperty).nextStatement().getObject());
				Resource oldBody = incomingAnno.listProperties(rdfConstants.oacHasBodyProperty).nextStatement().getObject().asResource();
				if (oldBody.hasProperty(RDF.type, rdfConstants.cntAsTxtType)) {
					String bodyText = oldBody.getProperty(rdfConstants.cntRestProperty).getString();				
					//should this next body type actually be a TextBody?
					Resource newBody = newModelToWrite.createResource( getNewTextAnnoBodyURI(uuid), rdfConstants.oacBodyType);
					// I'm not sure if this next sameAs is necessary, or in the right place.
					newModelToWrite.add(newBody, OWL.sameAs, oldBody);
					newModelToWrite.add(newAnnotationResource, rdfConstants.oacHasTargetProperty, newBody);
					newModelToWrite.add(newBody, RDF.type, DCTypes.Text);
					OutputStream foutForAnnoBodyText = frm.writeResource(txId, getTextBodyRelativeFileLocation(uuid), true);
		        	foutForAnnoBodyText.write(bodyText.getBytes());
				} else {
					newModelToWrite.add(newAnnotationResource, rdfConstants.oacHasBodyProperty, oldBody);
				}
				newModelToWrite.add(newAnnotationResource, OWL.sameAs, incomingAnno);
				newModelToWrite.add(incomingAnno, OWL.sameAs, newAnnotationResource);	
				
			
				transactionsAggregationResource.addProperty(rdfConstants.oreAggregates, newAnnotationResource);			
			} else {
				// possibly notify the user that they submitted an annotation that doesn't need a URI, and so they should submit it instead for ingest
			}
			 OutputStream foutForTextAnnos = frm.writeResource(txId, getSubmittedAnnosRelativeFileLocation(uuid), true);
		        newModelToWrite.write(foutForTextAnnos, "RDF/XML");
		        
		    }   catch (Exception e) {
		    	throw new IOException("Couldn't save new annotation. Caused by: " + e.getMessage());
		    }
		    
	}
		    
	// uri and file location for new annotations
	private String getNewTextAnnotationURI(String uuid) {
		return Config.getBaseURIForIds() + "submitted_annotations/" + uuid;
	}
	private String getSubmittedAnnosRelativeFileLocation(String uuid) {
		return "/submitted_annotations/" + uuid;
	}
	
	// uri and file location for textual annotation bodies
	private String getNewTextAnnoBodyURI(String uuid) {
		return Config.getBaseURIForIds() + "annotation_body_texts/" + uuid;	
	}	
	private String getTextBodyRelativeFileLocation(String uuid) {
		return "/annotation_body_texts/" + uuid + ".txt";
	}
	
	// file locations and uri for transactions
	private String getTransactionResourceMapRelativeFileLocation() {
		return "/transactions/" + transactionUUID + ".xml";
	}
	private String getTransactionAggregationURI() {
		return Config.getBaseURIForIds() + "transactions/" + transactionUUID;
	}
	private String getTransactionResourceMapURI() {
		return Config.getBaseURIForDocs() + "transactions/" + transactionUUID + ".xml";
	}
	
	


	
	
	
}
