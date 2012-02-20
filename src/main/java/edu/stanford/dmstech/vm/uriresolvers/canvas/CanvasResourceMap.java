package edu.stanford.dmstech.vm.uriresolvers.canvas;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.transaction.file.FileResourceManager;
import org.apache.commons.transaction.util.Jdk14Logger;
import org.apache.commons.transaction.util.LoggerFacade;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.sun.jersey.api.NotFoundException;

import edu.stanford.dmstech.vm.Config;
import edu.stanford.dmstech.vm.DMSTechRDFConstants;
import edu.stanford.dmstech.vm.uriresolvers.TextAnnotationUtils;

@Path("/manuscript/{manuscriptId}/textannotations")
public class CanvasResourceMap {
 
	private final String W3CDTF_NOW = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")).format(new Date());
	Logger logger = Logger.getLogger(CanvasResourceMap.class.getName());
	
	@GET 
	@Produces("application/rdf+xml")
	public Response redirectReqToXMLResourceMap(@Context UriInfo uriInfo) throws URISyntaxException {
		String originalRequest = uriInfo.getAbsolutePath().toASCIIString();
		String resourceMapFileName = "/textAnnoResourceMap.xml";
		return Response.seeOther(new URI(originalRequest + resourceMapFileName)).build();
	} 
	
	@GET
	@Produces("text/turtle;charset=utf-8")
	public Response redirectReqToTurtleResourceMap(@Context UriInfo uriInfo) throws URISyntaxException {
		String originalRequest = uriInfo.getAbsolutePath().toASCIIString();
		String resourceMapFileName = "/textAnnoResourceMap.ttl";
		return Response.seeOther(new URI(originalRequest + resourceMapFileName)).build();
	}

	
	/* Do I want this included in the  manifest:
	 * 
	 * <dms:Canvas rdf:about="http://dmss.stanford.edu/Parker/524/manifest/17V">
   <exif:height>9700</exif:height>
   <dc:title>f. 17 V</dc:title>
   <exif:width>8000</exif:width>
   <dms:acceptsAnnotations rdf:id="http://dmss.stanford.edu/Parker/524/manifest/17V/annotation"/>
   <dms:pleasePostAnnotationsTo rdf:id="http://someOtherServer/annotationService"/>
 </dms:Canvas>
 
	 */
	@GET
	@Path("textAnnoResourceMap.xml") 
	@Produces("application/rdf+xml")
	public File getResourceMapAsXML(@PathParam("manuscriptId") final String manuscriptId) throws URISyntaxException {
    	
		final File file = new File(Config.homeDir, "manuscripts/" + manuscriptId + "/rdf/" + Config.getTextAnnotationFileName());
		if (!file.exists()) {
			throw new NotFoundException("File, " + file.getAbsolutePath() + ", is not found");
		}
		return file;
	}
	
	@GET
	@Path("textAnnoResourceMap.ttl")  
	@Produces("text/turtle;charset=utf-8")
	public String getResourceMapAsTurtle(@PathParam("manuscriptId") final String manuscriptId) throws URISyntaxException {
		Model textAnnotationsModel = loadTextAnnotationsModel(manuscriptId);
		StringWriter stringWriter = new StringWriter();
		textAnnotationsModel.write(stringWriter, "TURTLE");
		return stringWriter.toString();
		
	}

	
	@POST
	public Response saveAnnotations(@PathParam("manuscriptId") final String manuscriptId,
			InputStream inputStream) {
	
		String resultURI = null;
		HashMap<String, String> bodyTextsToWriteToFile = new HashMap<String, String>();
		DMSTechRDFConstants rdfConstants = DMSTechRDFConstants.getInstance();
		
		try {
			
			Model existingTextAnnosRMModel = loadTextAnnotationsModel(manuscriptId);
			Model incomingTextAnnotationsModel = ModelFactory.createDefaultModel();
			Model transactionResourceMapModel = ModelFactory.createDefaultModel();
			Model oldTextAnnotationsRMModel = loadOldTextAnnosModel(manuscriptId);
			
			
			incomingTextAnnotationsModel.read(inputStream, null);

			String transactionUUID = UUID.randomUUID().toString();
			String resourceMapURI = getTransactionResourceMapURI(manuscriptId, transactionUUID);
			String aggregationURI = getTransactionAggregationURI(manuscriptId, transactionUUID);
			// add the aggregation for the transaction	  
			Resource transactionsAggregationResource = transactionResourceMapModel.createResource(aggregationURI)
						.addProperty(RDF.type, rdfConstants.oreAggregationClass)	
			  			.addProperty(RDF.type, rdfConstants.transactionClass)
			  			.addProperty(rdfConstants.httpMethod, rdfConstants.httpPostResource);
			  // might want to also say that it is a list, or a Bag?
			
			
			// add the resource map for the transaction
			Resource transactionResourceMapResource = transactionResourceMapModel.createResource(resourceMapURI)
						.addProperty(RDF.type, rdfConstants.transactionClass)
						.addProperty(rdfConstants.oreDescribes, transactionsAggregationResource)
						.addProperty(DCTerms.created, transactionResourceMapModel.createTypedLiteral(W3CDTF_NOW, rdfConstants.DCTERMS_NAMESPACE + "W3CDTF"))
						.addProperty(DC.format, "application/rdf+xml");						
						// .addProperty(DCTerms.creator, annotationAuthor )
			

			ResIterator resourceIter = incomingTextAnnotationsModel.listResourcesWithProperty(RDF.type, rdfConstants.textAnnotationClass);	
			int incomingAnnoCount = 0;
			Resource newTextAnnotationResource = null;
			
			while (resourceIter.hasNext()) {
				incomingAnnoCount++;
				Resource incomingTextAnno = resourceIter.nextResource();
				TextAnnotationUtils textAnnoUtils = new TextAnnotationUtils();
				newTextAnnotationResource = textAnnoUtils.mintURIForAnnoAndBody(
						manuscriptId,
						bodyTextsToWriteToFile, rdfConstants,
						existingTextAnnosRMModel, incomingTextAnnotationsModel,
						oldTextAnnotationsRMModel,
						transactionsAggregationResource,
						incomingTextAnno);				
			}
			

		    
		    // QUESTION:  should I assert the 'replaces' and 'sameAs' properties in the transactions resource map?
			
			
		    
		    writeFilesAsFileTransaction(transactionResourceMapModel, manuscriptId, transactionUUID, existingTextAnnosRMModel, oldTextAnnotationsRMModel, bodyTextsToWriteToFile);
			
		    // return uri for annotation if only one posted.  otherwise return uri for resource map
			resultURI = incomingAnnoCount == 1?newTextAnnotationResource.getURI():resourceMapURI;
			
		} catch (IOException e) {
			e.printStackTrace();
			return Response.serverError().entity("There appears to have been an error:  " + e.getMessage()).build();
		} finally {
			if (inputStream != null)
				try {
					inputStream.close();
				} catch (IOException e) {

				}	
		}

		return Response.created(URI.create(resultURI)).build();
	}

	

	private void writeFilesAsFileTransaction(Model transactionResourceMapModel, String manuscriptId, String transactionUuid, Model textAnnotationsModel, Model oldTextAnnotationsRMModel, HashMap<String, String> bodyTexts) throws IOException {
		
		//still need to write the oldannos file here.

		
		LoggerFacade loggerFacade = new Jdk14Logger(logger);
		FileResourceManager frm = new FileResourceManager(Config.homeDir.getAbsolutePath(), Config.homeDir.getAbsolutePath() + "/temp", false, loggerFacade);

		Object txId = null;
		    try {
		        frm.start();
		        txId = frm.generatedUniqueTxId();
		        frm.startTransaction(txId);
		        
		        OutputStream foutForResourceMap = frm.writeResource(txId, getTransactionResourceMapRelativeFileLocation(manuscriptId, transactionUuid), true);
		        transactionResourceMapModel.write(foutForResourceMap, "RDF/XML");
		        
		        OutputStream foutForTextAnnos = frm.writeResource(txId, getTextAnnosRelativeFileLocation(manuscriptId), true);
		        textAnnotationsModel.write(foutForTextAnnos, "RDF/XML");
		      		        
		        OutputStream foutForOldTextAnnos = frm.writeResource(txId, getOldTextAnnosRelativeFileLocation(manuscriptId), true);
		        textAnnotationsModel.write(foutForOldTextAnnos, "RDF/XML");
		      	
		        Iterator<Entry<String, String>> bodyTextIter = bodyTexts.entrySet().iterator();
		        while (bodyTextIter.hasNext()) {
		        	Map.Entry<String, String> uuidAndText = (Entry<String, String>) bodyTextIter.next();
		        	OutputStream foutForAnnoBodyText = frm.writeResource(txId, getTextBodyRelativeFileLocation(manuscriptId, uuidAndText.getKey()), true);
		        	foutForAnnoBodyText.write(uuidAndText.getValue().getBytes());
		        }
		        
		        
		        frm.commitTransaction(txId);

		    }

		    catch (Exception e) {
		        throw new IOException("Couldn't save new annotations.  Rolled back all.  Error: " + e.getMessage());
		    }
		
	}

	
	/*private Model writeTransactionResourceMapFile(Model transactionResourceMapModel, String manuscriptId, String uuid) throws Exception {		
		//model.write(System.out, "TURTLE");
		FileOutputStream fout = null;
		try {
			fout = new FileOutputStream(getTransactionResourceMapRelativeFileLocation(manuscriptId, uuid));
			transactionResourceMapModel.write(fout, "RDF/XML");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new Exception("Couldn't write RDF to file sytem.");
		}				  
		return transactionResourceMapModel;
	}

	private Model writeTextAnnotationsFile(Model model, String manuscriptId) throws Exception {		
		//model.write(System.out, "TURTLE");
		FileOutputStream fout = null;
		try {
			fout = new FileOutputStream(getTextAnnosRelativeFileLocation(manuscriptId));
			model.write(fout, "RDF/XML");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new Exception("Couldn't write RDF to file sytem.");
		}				  
		return model;
	} */
	
	private Model loadTextAnnotationsModel(String manuscriptId) {
		
		DMSTechRDFConstants rdfConstants = DMSTechRDFConstants.getInstance();
		
		 Model model = ModelFactory.createDefaultModel();
		 InputStream in = FileManager.get().open(getTextAnnosRelativeFileLocation(manuscriptId));
		 if (in != null) {
			 model.read(in, null);
		 } else {
			 // create the model manually	  		 
				Resource transactionsAggregationResource = model.createResource(getTextAnnotationsAggregationURI(manuscriptId))
							.addProperty(RDF.type, rdfConstants.oreAggregationClass);
				  // might want to also say that it is a list, or a Bag?

				Resource transactionResourceMapResource = model.createResource(getTextAnnotationsResourceMapURI(manuscriptId))
							.addProperty(RDF.type, rdfConstants.transactionClass)
							.addProperty(rdfConstants.oreDescribes, transactionsAggregationResource)
							.addProperty(DCTerms.created, model.createTypedLiteral(W3CDTF_NOW, rdfConstants.DCTERMS_NAMESPACE + "W3CDTF"))
							.addProperty(DC.format, "application/rdf+xml");			
		 }
		 return model;	
	}
	
	private Model loadOldTextAnnosModel(String manuscriptId) {

		DMSTechRDFConstants rdfConstants = DMSTechRDFConstants.getInstance();
		
		 Model model = ModelFactory.createDefaultModel();
		 InputStream in = FileManager.get().open(getOldTextAnnosRelativeFileLocation(manuscriptId));
		 if (in == null) {
			 // create the model manually	  		 
				Resource transactionsAggregationResource = model.createResource(getOldTextAnnotationsAggregationURI(manuscriptId))
							.addProperty(RDF.type, rdfConstants.oreAggregationClass);
				  // might want to also say that it is a list, or a Bag?

				Resource transactionResourceMapResource = model.createResource(getOldTextAnnotationsResourceMapURI(manuscriptId))
							.addProperty(RDF.type, rdfConstants.transactionClass)
							.addProperty(rdfConstants.oreDescribes, transactionsAggregationResource)
							.addProperty(DCTerms.created, model.createTypedLiteral(W3CDTF_NOW, rdfConstants.DCTERMS_NAMESPACE + "W3CDTF"))
							.addProperty(DC.format, "application/rdf+xml");		
		 }	
		 model.read(in, null);
		 return model;	
	}
	
	
	// I have subdirectores for images, rdf, transactions
	// but the URIs are a little different because I need URIs for the individual things inside the files, 
	// like the textannotations, imageannotations, canvases
			
	private String getTextAnnosRelativeFileLocation(String manuscriptId) {
		return "manuscripts/" + manuscriptId + "/rdf/" + Config.getTextAnnotationFileName();
	}
	
	private String getOldTextAnnosRelativeFileLocation(String manuscriptId) {
		return "manuscripts/" + manuscriptId + "/rdf/" + Config.getFileNameForOldTextAnnos();
	}
	
	private String getTransactionResourceMapRelativeFileLocation(String manuscriptId, String uuid) {
		return "manuscripts/" + manuscriptId + "/rdf/transactions/" + uuid + ".xml";
	}
	
	private String getTextBodyRelativeFileLocation(String manuscriptId, String uuid) {
		return "manuscripts/" + manuscriptId + "/bodytexts/" + uuid + ".txt";
	}
	

	
	private String getTransactionAggregationURI(final String manuscriptId, String newUUID) {
		return Config.getBaseURIForIds() + "manuscript" + "/" + manuscriptId + "/transactions/" + newUUID;
	}

	private String getTransactionResourceMapURI(final String manuscriptId, String newUUID) {
		return Config.getBaseURIForDocs() + "manuscript" + "/" + manuscriptId + "/transactions/" + newUUID + ".xml";
	}
	
	private String getTextAnnotationsResourceMapURI(final String manuscriptId) {
		return Config.getBaseURIForDocs() + "manuscript" + "/" + manuscriptId + "/textannotations/textAnnoResourceMap.xml";
	}
	
	private String getTextAnnotationsAggregationURI(final String manuscriptId) {
		return Config.getBaseURIForIds() + "manuscript" + "/" + manuscriptId + "/textannotations/";
	}
	
	private String getOldTextAnnotationsResourceMapURI(final String manuscriptId) {
		return Config.getBaseURIForDocs() + "manuscript" + "/" + manuscriptId + "/oldtextannotations/oldtextAnnoResourceMap.xml";
	}
	
	private String getOldTextAnnotationsAggregationURI(final String manuscriptId) {
		return Config.getBaseURIForIds() + "manuscript" + "/" + manuscriptId + "/oldtextannotations/";
	}
	
}



