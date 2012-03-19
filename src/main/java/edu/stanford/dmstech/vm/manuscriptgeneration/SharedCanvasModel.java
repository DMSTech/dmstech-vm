package edu.stanford.dmstech.vm.manuscriptgeneration;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

import org.apache.commons.transaction.util.Jdk14Logger;
import org.apache.commons.transaction.util.LoggerFacade;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;

import edu.stanford.dmstech.vm.Config;
import edu.stanford.dmstech.vm.DMSTechRDFConstants;
import edu.stanford.dmstech.vm.uriresolvers.canvas.CanvasTextAnnoResourceMap;

public class SharedCanvasModel {

	private final String W3CDTF_NOW = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")).format(new Date());
	Logger logger = Logger.getLogger(CanvasTextAnnoResourceMap.class.getName());
	LoggerFacade loggerFacade = null;
	
	DMSTechRDFConstants rdfConstants = null;
	
	// one model for each of the files we'll serialize
	Model sequenceResourceMap;
	Model imageAnnosResourceMap;
	Model sharedCanvasManifestResourceMap;
	
	Resource sequenceAggregation;
	Resource imageAnnoAggregation;
	
	/*
	 * We store the new canvases in an array list for convenience,
	 * which we convert to a Jena RDFList as needed.
	 */
	private HashMap<Integer, Resource> imageSequence;
	/*
	 * We store the new canvases in an array list for convenience,
	 * which we convert to a Jena RDFList as needed.
	 */
	private HashMap<Integer, Resource> annotationSequence;
	
	//String collectionId;
	//String manuscriptId;
	String baseURI;
	/**
	 * pageCount tracks the pages as we add them, assuming they should be ordered
	 * in the order they are added.  The alternative is that the incoming
	 * page numbers are passed in which case we don't use pageCount.
	 */
	private int pageCount = 0;
	
	/**
	 * 
	 */
	private SharedCanvasModel() {
		
		loggerFacade = new Jdk14Logger(logger);
		rdfConstants = DMSTechRDFConstants.getInstance();

	}
	
	public static SharedCanvasModel createNewSharedCanvasModel(String baseURI){
		return (new SharedCanvasModel()).initialize(baseURI);
		
	}
	
	public static SharedCanvasModel loadSharedCanvasModelFromRMs(Model sequenceRM, Model annoRM, Model manifestRM) {
		// set the local RMs, then pull the image annos into anno ArrayList and canvases into sequence ArrayList
		return (new SharedCanvasModel()).
				setSequenceResourceMap(sequenceRM).
				setImageAnnosResourceMap(annoRM).
				setSharedCanvasManifestResourceMap(manifestRM);
	}
	
	private SharedCanvasModel setSharedCanvasManifestResourceMap(Model manifestRM) {
		this.setSharedCanvasManifestResourceMap(manifestRM);
		return this;
	}
	
	private SharedCanvasModel setImageAnnosResourceMap(Model annoRM) {
		this.imageAnnosResourceMap = annoRM;
		return this;
	}

	private SharedCanvasModel setSequenceResourceMap(Model sequenceRM) {
		this.sequenceResourceMap = sequenceRM;
		return this;
	}

	private SharedCanvasModel initialize(String baseURI) {
		
		this.baseURI = baseURI;
		
		imageSequence = new HashMap<Integer, Resource>();
		annotationSequence = new HashMap<Integer, Resource>();
		
		sequenceResourceMap = ModelFactory.createDefaultModel();
		imageAnnosResourceMap = ModelFactory.createDefaultModel();
		sharedCanvasManifestResourceMap = ModelFactory.createDefaultModel();
		
		sequenceAggregation = sequenceResourceMap.createResource(getSequenceAggregationURI())
				.addProperty(RDF.type, rdfConstants.oreAggregationClass)		
				.addProperty(RDF.type, rdfConstants.sequenceClass)
				.addProperty(RDF.type, RDF.List);
		    	  // add the first and rest later, as well as each 'aggregates'  
		// add the resource map for the transaction to the model
		sequenceResourceMap.createResource(getSequenceRMURI())
				.addProperty(RDF.type, rdfConstants.oreResourceMapClass)
				.addProperty(rdfConstants.oreDescribes, sequenceAggregation)
				.addProperty(DCTerms.created, sequenceResourceMap.createTypedLiteral(W3CDTF_NOW, rdfConstants.DCTERMS_NAMESPACE + "W3CDTF"))
				.addProperty(DC.format, "application/rdf+xml");						
				// .addProperty(DCTerms.creator, ?? )		
		
		
		imageAnnoAggregation = imageAnnosResourceMap.createResource(getImageAnnosAggregationURI())
				.addProperty(RDF.type, rdfConstants.oreAggregationClass)		
				.addProperty(RDF.type, rdfConstants.imageCollectionClass)
				.addProperty(RDF.type, RDF.List);
		    	  // add the first and rest later, as well as each 'aggregates'  
		// e.g.,
		// <rdf:first rdf:resource="http://dms-data.stanford.edu/Stanford/kq131cs7229/imageanno/image-32"/>
		//    <rdf:rest rdf:nodeID="TvhHadil3"/>
		
		// add the resource map for the transaction to the model
		imageAnnosResourceMap.createResource(getImageAnnosRMURI())
				.addProperty(RDF.type, rdfConstants.oreResourceMapClass)
				.addProperty(rdfConstants.oreDescribes, imageAnnoAggregation)
				.addProperty(DCTerms.created, sequenceResourceMap.createTypedLiteral(W3CDTF_NOW, rdfConstants.DCTERMS_NAMESPACE + "W3CDTF"))
				.addProperty(DC.format, "application/rdf+xml");	
		return this;
	}
	

	/**
	 * The imageURI comes in predetermined.
	 * @param imageId
	 */
	public void addImageToSharedCanvas(String imageURI, String pageTitle, Integer pageNumber, String width, String height) {
		
		if (pageTitle == null) pageTitle = "";
		if (pageNumber == null) pageNumber = pageCount++;
		
		String canvasURI = getNewCanvasURI();
		
		// populate image annotation resource map and aggregation
		Resource imageBody = createImageBodyInModel(imageAnnosResourceMap, imageURI, width, height);
		Resource canvasTargetInAnnoRM = createCanvasInModel(imageAnnosResourceMap, width, height, pageTitle, canvasURI);
		Resource annotation = createAnnotationInModel(imageAnnosResourceMap, canvasURI, width, height, imageBody, canvasTargetInAnnoRM);
		annotationSequence.put(pageNumber, annotation);
		imageAnnoAggregation.addProperty(rdfConstants.oreAggregates, annotation);
		
		// populate the image sequence aggregation and resource map
		Resource canvasTargetInSeqRM = createCanvasInModel(sequenceResourceMap, width, height, pageTitle, canvasURI);
		sequenceAggregation.addProperty(rdfConstants.oreAggregates, canvasTargetInSeqRM);
		imageSequence.put(pageNumber, canvasTargetInSeqRM);
	}
	
	public Model getImageAnnoResourceMap() {
		return imageAnnosResourceMap;
	}
	public void serializeImageAnnoResourceMapToFile(String pathToFile, String format){
		serializeModelToFile(imageAnnosResourceMap, pathToFile, format);
	}

	public Model getNormalSequenceResourceMap() {
		return sequenceResourceMap;
	}
	public void serializeNormalSequenceResourceMapToFile(String pathToFile, String format){
		serializeModelToFile(sequenceResourceMap, pathToFile, format);
	}
	
	public Model getManifestResourceMap() {
		return sharedCanvasManifestResourceMap;
	}
	public void serializeManifestResourceMapToFile(String pathToFile, String format){
		serializeModelToFile(sharedCanvasManifestResourceMap, pathToFile, format);
	}
	
	private void serializeModelToFile(Model model, String pathToFile, String format) {
		//		model.write(System.out, "TURTLE");
		FileOutputStream fout = null;
		try {
			fout = new FileOutputStream(pathToFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
			model.write(fout, format);
	}


	public Resource createCanvasInModel(Model model, String width, String height, String title, String canvasURI) {		
		return model.createResource(canvasURI)
				.addProperty(DC.title, title)
				.addProperty(rdfConstants.exifWidth, width)
				.addProperty(rdfConstants.exifHeight, height)
				.addProperty(RDF.type, rdfConstants.dmsCanvasClass);	
				 /* <rdf:Description rdf:about="http://dms-data.stanford.edu/Stanford/kq131cs7229/image-24">
				    <dc:title>f. 17r</dc:title>
				    <exif:width>3907</exif:width>
				    <exif:height>5611</exif:height>
				    <rdf:type rdf:resource="http://dms.stanford.edu/ns/Canvas"/>
				  </rdf:Description> */
  }
  
    public Resource createImageBodyInModel(Model model, String imageURI, String width, String height) {
    	return imageAnnosResourceMap.createResource(imageURI)
				.addProperty(rdfConstants.exifWidth, width)
				.addProperty(rdfConstants.exifHeight, height)
				.addProperty(RDF.type, rdfConstants.dmsImageClass)
				.addProperty(RDF.type, rdfConstants.dmsImageBodyClass);
				  /*<rdf:Description rdf:about="http://stacks.stanford.edu/image/kq131cs7229/kq131cs7229_05_0003">
				    <exif:width>4016</exif:width>
				    <exif:height>5888</exif:height>
				    <rdf:type rdf:resource="http://purl.org/dc/dcmitype/Image"/>
				    <rdf:type rdf:resource="http://dms.stanford.edu/ns/ImageBody"/>
				  </rdf:Description>*/
    }
  
    public Resource createAnnotationInModel(Model model, String canvasURI, String width, String height, Resource imageBody, Resource canvasTarget) {
    	return model.createResource(getAnnotationURI(canvasURI))
				.addProperty(rdfConstants.oacHasBodyProperty, imageBody)
				.addProperty(rdfConstants.oacHasTargetProperty, canvasTarget)
				.addProperty(RDF.type, rdfConstants.oacAnnotationType)
				.addProperty(RDF.type, rdfConstants.dmsImageAnnotationClass);
				 /* <rdf:Description rdf:about="http://dms-data.stanford.edu/Stanford/kq131cs7229/imageanno/image-10">
				    <oac:hasBody rdf:resource="http://stacks.stanford.edu/image/kq131cs7229/kq131cs7229_05_0010"/>
				    <oac:hasTarget rdf:resource="http://dms-data.stanford.edu/Stanford/kq131cs7229/image-10"/>
				    <rdf:type rdf:resource="http://www.openannotation.org/ns/Annotation"/>
				    <rdf:type rdf:resource="http://dms.stanford.edu/ns/ImageAnnotation"/>
				  </rdf:Description> */
    }

	private String getSequenceAggregationURI() {
		return baseURI + "/NormalSequence";
	}
	private String getSequenceRMURI() {
		return getSequenceAggregationURI() + ".xml";
	}
	
	private String getImageAnnosAggregationURI() {
		return baseURI + "/ImageAnnotations";		
	}
	private String getImageAnnosRMURI() {
		return getImageAnnosAggregationURI() + ".xml";
	}
	
	public String getNewCanvasURI() {
		return baseURI + UUID.randomUUID();
	}
	public String getAnnotationURI(String canvasURI) {
		return canvasURI + "/" + UUID.randomUUID();
	}
	
}




