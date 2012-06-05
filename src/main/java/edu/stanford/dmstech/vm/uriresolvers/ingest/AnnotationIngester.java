package edu.stanford.dmstech.vm.uriresolvers.ingest;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.ws.rs.core.Response;

import org.apache.commons.transaction.file.FileResourceManager;
import org.apache.commons.transaction.util.Jdk14Logger;
import org.apache.commons.transaction.util.LoggerFacade;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.DCTypes;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;

import edu.stanford.dmstech.vm.Config;
import edu.stanford.dmstech.vm.DMSTechRDFConstants;
import edu.stanford.dmstech.vm.RDFUtils;
import edu.stanford.dmstech.vm.indexing.SharedCanvasSOLRIndexer;
import edu.stanford.dmstech.vm.indexing.SharedCanvasTDBManager;

public class AnnotationIngester {

	public void test() {
		// http://www.shared-canvas.org/ns/ContentAnnotation
		String sampleAnnotation = "<rdf:RDF\n" + 
				"    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n" + 
				"    xmlns:cnt=\"http://www.w3.org/2008/content#\"\n" + 
				"    xmlns:sc=\"http://www.shared-canvas.org/ns/\"\n" + 
				"    xmlns:ore=\"http://www.openarchives.org/ore/terms/\"\n" + 
				"    xmlns:oac=\"http://www.openannotation.org/ns/\" > \n" + 
				"  <rdf:Description rdf:about=\"urn:uuid:7679f48e-e2b0-42c3-b5b1-7ebfc704379f\">\n" + 
				"    <rdf:type rdf:resource=\"http://www.shared-canvas.org/ns/ContentAnnotation\"/>\n" + 
				"    <oac:hasTarget rdf:resource=\"http://dms-data.stanford.edu/Parker/nr257ff7994/canvas-324#xyhw=0,0,139,610\"/>\n" + 
				"    <oac:hasBody rdf:resource=\"urn:uuid:d6105f4e-cd2e-4a9c-b5c9-2c8a196e5057\"/>\n" + 
				"  </rdf:Description>\n" + 
				"  <rdf:Description rdf:about=\"urn:uuid:d6105f4e-cd2e-4a9c-b5c9-2c8a196e5057\">\n" + 				
				"  		 <rdf:type rdf:resource=\"http://www.w3.org/2008/content#ContentAsText\"/>" + 
				"        <cnt:rest rdf:parseType=\"Literal\">" + 
							"grauius, ut non solum patiaris harum rerum indigentiam, sed" + 
				"        </cnt:rest>\n" + 
				"        <cnt:characterEncoding>utf-8</cnt:characterEncoding>\n" + 
				"  </rdf:Description>\n" + 
				"</rdf:RDF>";
		
		String sampleAnnotationOld= "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
				"<rdf:RDF\n" + 
				"   xmlns:dc=\"http://purl.org/dc/elements/1.1/\"\n" + 
				"   xmlns:exif=\"http://www.w3.org/2003/12/exif/ns#\"\n" + 
				"   xmlns:oac=\"http://www.openannotation.org/ns/\"\n" + 
				"   xmlns:ore=\"http://www.openarchives.org/ore/terms/\"\n" + 
				"   xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n" +
				"   xmlns:cnt=\"http://www.w3.org/2008/content#\"" +
				"   xmlns:sc=\"http://www.shared-canvas.org/ns/\"" + 
				">\n" + 
				"   <oac:Body rdf:about=\"URN:uuid:751183c0-d182-11df-bd3b-0800200c9a66\">" + 
				"        <rdf:type rdf:resource=\"http://www.w3.org/2008/content#ContentAsText\"/>" + 
				"        <cnt:rest rdf:parseType=\"Literal\">" + 
				"            A bunch of text.\n" + 
				"        </cnt:rest>\n" + 
				"        <cnt:characterEncoding>utf-8</cnt:characterEncoding>\n" + 
				"    </oac:Body>\n" + 
				"  <sc:TextAnnotation rdf:about=\"URN:woohoo\">\n" + 
				"    <oac:hasBody rdf:resource=\"URN:uuid:751183c0-d182-11df-bd3b-0800200c9a66\"/>\n" + 
				"    <oac:hasTarget rdf:resource=\"http://localhost:8080/Stanford/kq131cs7229/canvas-1\"/>\n" + 
				"    <rdf:type rdf:resource=\"http://www.openannotation.org/ns/Annotation\"/>\n" + 
				"    <rdf:type rdf:resource=\"http://www.shared-canvas.org/ns/TextAnnotation\"/>\n" + 
				"  </sc:TextAnnotation>\n" + 
				"  <sc:Canvas rdf:about=\"http://localhost:8080/Stanford/kq131cs7229/canvas-1\">\n" + 
				"    <dc:title>f. 8r</dc:title>\n" + 
				"    <exif:width>4019</exif:width>\n" + 
				"    <exif:height>5575</exif:height>\n" +    
				"  </sc:Canvas>\n" + 
				"  \n" + 
				"</rdf:RDF>";
		try {
			InputStream inputStream = new ByteArrayInputStream(sampleAnnotation.getBytes("UTF-8"));
			saveAnnotations(inputStream);
			} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
	}
	
	
	private final String W3CDTF_NOW = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")).format(new Date());
	Logger logger = Logger.getLogger(AnnotationIngester.class.getName());
	LoggerFacade loggerFacade = null;
	//private static final String LOG_FILE_NAME = "log.txt";
	static private FileHandler textFileHandler;	
	static private SimpleFormatter textFormatter;
	String resultURI = null;
	StringWriter rdfForAnnotations = new StringWriter();
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
	HashMap<String, List<String>> canvasTextToIndex = new HashMap<String, List<String>>();
	ArrayList<String> filesToAddToTDB = new ArrayList<String>();
	
	private void configureLogger() {
			try {
				textFileHandler = new FileHandler(Config.homeDir.getAbsolutePath() + "/logs/annotationIngest.txt");
			} catch (SecurityException e) {
				e.printStackTrace();
				System.out.println("Couldn't open the log.txt file for logging in the directory to which the environment variable, " + Config.HOME_DIR_ENV_VAR + ", points.");
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Couldn't open the log.txt file for logging in the directory to which the environment variable, " + Config.HOME_DIR_ENV_VAR + ", points.");
			}
			logger.setLevel(Level.INFO);
			textFormatter = new SimpleFormatter();
			textFileHandler.setFormatter(textFormatter);
			logger.addHandler(textFileHandler);
			loggerFacade = new Jdk14Logger(logger);
			
	}
	
	public Response saveAnnotations(InputStream inputStream) throws IOException {
		
		try {			
			
			configureLogger();
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
			  			.addProperty(RDF.type, rdfConstants.scTransactionClass)
			  			.addProperty(rdfConstants.httpMethod, rdfConstants.httpPostResource);			
			// add the resource map for the transaction
			transactionResourceMapModel.createResource(transactionResourceMapURI)
						.addProperty(RDF.type, rdfConstants.scTransactionClass)
						.addProperty(rdfConstants.oreDescribes, transactionsAggregationResource)
						.addProperty(DCTerms.created, transactionResourceMapModel.createTypedLiteral(W3CDTF_NOW, DMSTechRDFConstants.DCTERMS_NAMESPACE + "W3CDTF"))
						.addProperty(DC.format, "application/rdf+xml");						
						// .addProperty(DCTerms.creator, annotationAuthor )
	       	        
			ResIterator annotationsIterator = incomingAnnotationsModel.listResourcesWithProperty(RDF.type, rdfConstants.scTextAnnotationClass);
		//	RDFUtils.serializeModelToSystemOut(incomingAnnotationsModel);
			while (annotationsIterator.hasNext()) {
				Resource annotationRes = annotationsIterator.next();
				writeAnnotationToFile(annotationRes);
				incomingAnnoCount++;
			}

		// finish off by writing the transaction file
		 OutputStream foutForTransactionResourceMap = frm.writeResource(txId, getTransactionResourceMapRelativeFileLocation(), true);
	        transactionResourceMapModel.write(foutForTransactionResourceMap, "N-TRIPLE");
	        filesToAddToTDB.add(getTransactionResourceMapRelativeFileLocation());
	        // commit all our new files
		 frm.commitTransaction(txId);
		 
		 // index the new rdf files in the  main dataset
		 (new SharedCanvasTDBManager()).indexFileListInHomeDirIntoMainDataset(filesToAddToTDB);
		 // index the text for the  new annotations in solr
		 (new SharedCanvasSOLRIndexer()).indexCanvasText(canvasTextToIndex);
		 
		}  catch (Exception e) {
			e.printStackTrace();
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
		return Response.created(URI.create(resultURI)).entity(rdfForAnnotations.toString()).build();
	}

	

	private void writeAnnotationToFile(Resource incomingAnno) throws IOException {
		String uuid = UUID.randomUUID().toString();
	
		    try {
		    	Model newModelToWrite = ModelFactory.createDefaultModel();
		    	newModelToWrite.setNsPrefixes(DMSTechRDFConstants.getInstance().getInitializingModel());
				
		    	String newAnnoURI = incomingAnno.getURI();
		    	
		    	if (newAnnoURI.startsWith("urn:")) {	
		    		newAnnoURI = getNewTextAnnotationURI(uuid);
		    	}
			
		    	Resource targetCanvas = incomingAnno.listProperties(rdfConstants.oacHasTargetProperty).nextStatement().getObject().asResource();
		    	Resource oldBody = incomingAnno.listProperties(rdfConstants.oacHasBodyProperty).nextStatement().getObject().asResource();
				
		    	
		    	
		    	
		    	newAnnotationResource = newModelToWrite.createResource( newAnnoURI, rdfConstants.oacTextAnnotationType)
											.addProperty(RDF.type, rdfConstants.oacAnnotationType)
											.addProperty(rdfConstants.oacHasTargetProperty, targetCanvas);
				String bodyText = null;
				if (oldBody.hasProperty(RDF.type, rdfConstants.cntAsTxtType)) {
					Statement bodyTextStmt = oldBody.getProperty(rdfConstants.cntRestProperty);
					if (bodyTextStmt != null) bodyText = bodyTextStmt.getString();				
					//should this next body type actually be a TextBody?
					Resource newBody = newModelToWrite.createResource( getNewTextAnnoBodyURI(uuid), rdfConstants.oacBodyType)
											.addProperty(OWL.sameAs, oldBody)
											.addProperty(RDF.type, DCTypes.Text);
					newAnnotationResource.addProperty(rdfConstants.oacHasBodyProperty, newBody); 
					String textBodyRelativeFileLocation = getTextBodyRelativeFileLocation(uuid);
					OutputStream foutForAnnoBodyText = frm.writeResource(txId, textBodyRelativeFileLocation, true);
		        	foutForAnnoBodyText.write(bodyText.getBytes());
				} else {
					newModelToWrite.add(newAnnotationResource, rdfConstants.oacHasBodyProperty, oldBody);
					bodyText = getTextFromURI(oldBody.getURI());
				}
				
				addTextToSolrIndexQueue(bodyText, targetCanvas.getURI());
				
				if (! newAnnotationResource.equals(incomingAnno)) {
					newModelToWrite.add(newAnnotationResource, OWL.sameAs, incomingAnno);				
					newModelToWrite.add(incomingAnno, OWL.sameAs, newAnnotationResource);					
				}
				transactionsAggregationResource.addProperty(rdfConstants.oreAggregates, newAnnotationResource);			
			
		    				       				
			 String submittedAnnosRelativeFileLocation = getSubmittedAnnosRelativeFileLocation(uuid);
			 filesToAddToTDB.add(submittedAnnosRelativeFileLocation);
			 OutputStream foutForTextAnnos = frm.writeResource(txId, submittedAnnosRelativeFileLocation, true);
		     newModelToWrite.write(foutForTextAnnos, "N-TRIPLE");
		     newModelToWrite.write(rdfForAnnotations, "RDF/XML");
		     
		    	
		        
		    }   catch (Exception e) {
		    	e.printStackTrace();
		    	throw new IOException("Couldn't save new annotation. Caused by: " + e.getMessage());
		    }
		    
	}
	
	private void addTextToSolrIndexQueue(String bodyText, String canvasURI) {
	  if (canvasTextToIndex.containsKey(canvasURI)) {
		  canvasTextToIndex.get(canvasURI).add(bodyText);
	  } else {
		  List<String> canvasList = new ArrayList<String>();
		  canvasList.add(bodyText);
		  canvasTextToIndex.put(canvasURI, canvasList);
	  }
	
	}


	private String getTextFromURI(String uri) throws IOException {
	     return (String) new URL(uri).getContent();
	}

	// URIs
	private String getNewTextAnnotationURI(String uuid) {
		return Config.getBaseURIForIds() + "/submitted_annotations/" + uuid;
	}
	private String getNewTextAnnoBodyURI(String uuid) {
		return Config.getBaseURIForIds() + "/annotation_body_texts/" + uuid;	
	}	
	private String getTransactionAggregationURI() {
		return Config.getBaseURIForIds() + "/transactions/" + transactionUUID;
	}
	private String getTransactionResourceMapURI() {
		return Config.getBaseURIForDocuments() + "/transactions/" + transactionUUID + ".xml";
	}
	
	
	// FILE PATHS
	private String getSubmittedAnnosRelativeFileLocation(String uuid) {
		return "/submitted_annotations/" + uuid + ".nt";
	}
	private String getTextBodyRelativeFileLocation(String uuid) {
		return "/annotation_body_texts/" + uuid + ".txt";
	}
	private String getTransactionResourceMapRelativeFileLocation() {
		return "/transactions/" + transactionUUID + ".nt";
	}
	
	
	
	


	
	
	
}
