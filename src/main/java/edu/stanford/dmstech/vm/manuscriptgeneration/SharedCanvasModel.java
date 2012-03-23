package edu.stanford.dmstech.vm.manuscriptgeneration;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.apache.commons.transaction.util.Jdk14Logger;
import org.apache.commons.transaction.util.LoggerFacade;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFList;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;

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
	
	//Resource sequenceAggregation;
	//Resource imageAnnoAggregation;
	
	/*
	 * We store the new canvases in an array list for convenience,
	 * which we convert to a Jena RDFList as needed.
	 */
	private List<RDFNode> imageSequence;
	
	/*
	 * We store the new canvases in an array list for convenience,
	 * which we convert to a Jena RDFList as needed.
	 */
	private List<RDFNode> annotationSequence;
	
	//String collectionId;
	//String manuscriptId;
	String baseURI;
	
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
	
	public static SharedCanvasModel loadSharedCanvasModelFromRMs(Model sequenceRM, Model annoRM, Model manifestRM, String baseURI) throws Exception {
		// set the local RMs, then pull the image annos into anno ArrayList and canvases into sequence ArrayList
		SharedCanvasModel sharedCanvasModel = new SharedCanvasModel();
		sharedCanvasModel.initializeFromExistingModels(sequenceRM, annoRM, manifestRM, baseURI);		
		return sharedCanvasModel;
	}
	
	private void initializeFromExistingModels(Model sequenceRM, Model annoRM, Model manifestRM, String baseURI) throws Exception {
		this.baseURI = baseURI;
 		setSequenceResourceMap(sequenceRM);
		setImageAnnosResourceMap(annoRM);
		setSharedCanvasManifestResourceMap(manifestRM);
		
		annotationSequence = extractSingleRDFListFromModel(annoRM);
		imageSequence = extractSingleRDFListFromModel(sequenceRM);

	}
	private List<RDFNode> extractSingleRDFListFromModel(Model model) throws Exception {
		List<RDFNode> theList = null;
		ResIterator resIter = model.listSubjectsWithProperty(RDF.type, rdfConstants.sequenceClass);
		if (resIter.hasNext()){
			// from http://tech.groups.yahoo.com/group/jena-dev/message/6769
			//RDFList l = (RDFList) x.getPropertyValue( p ).as( RDFList.class );
			theList = ((RDFList) resIter.next().as(RDFList.class)).asJavaList();			
		} else {
			throw new Exception("Missing rdf lists in either annotation or normal sequence file.");
		}
		return theList;
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
		
		imageSequence = new ArrayList<RDFNode>();
		annotationSequence = new ArrayList<RDFNode>();
		
		sequenceResourceMap = ModelFactory.createDefaultModel();
		imageAnnosResourceMap = ModelFactory.createDefaultModel();
		sharedCanvasManifestResourceMap = ModelFactory.createDefaultModel();
		
		//create the manifest
		createManifestInModel(sharedCanvasManifestResourceMap);
		return this;
	}

	/**
	 * The imageURI comes in predetermined.
	 * @param imageId
	 */
	public void addImageToSharedCanvas(String imageURI, String pageTitle, String width, String height) {
		
		if (pageTitle == null) pageTitle = "";
		
		String canvasURI = getNewCanvasURI();
		
		// populate image annotation resource map and aggregation
		Resource imageBody = createImageBodyInModel(imageAnnosResourceMap, imageURI, width, height);
		Resource canvasTargetInAnnoRM = createCanvasInModel(imageAnnosResourceMap, width, height, pageTitle, canvasURI);
		Resource annotation = createAnnotationInModel(imageAnnosResourceMap, canvasURI, width, height, imageBody, canvasTargetInAnnoRM);
		annotationSequence.add(annotation);
		//imageAnnoAggregation.addProperty(rdfConstants.oreAggregates, annotation);
		
		// populate the image sequence aggregation and resource map
		Resource canvasTargetInSeqRM = createCanvasInModel(sequenceResourceMap, width, height, pageTitle, canvasURI);
		//sequenceAggregation.addProperty(rdfConstants.oreAggregates, canvasTargetInSeqRM);
		imageSequence.add(canvasTargetInSeqRM);
				
	}
	
	/** We rebuild the aggregation and resourcemap everytime model is requested
	 * in case something new has been added.
	 * @return
	 */
	public Model getImageAnnoResourceMap() {
		deleteAllAggregationsAndRMFromModel(imageAnnosResourceMap);
		createAnnoAggregationAndRMInModel(imageAnnosResourceMap);
		return imageAnnosResourceMap;
	}
	public void serializeImageAnnoResourceMapToFile(String pathToFile, String format){
		serializeModelToFile(getImageAnnoResourceMap(), pathToFile, format);
	}


	/** We rebuild the aggregation and resourcemap everytime the  model is requested
	 * in case something new has been added.
	 * @return
	 */
	public Model getNormalSequenceResourceMap() {
		deleteAllAggregationsAndRMFromModel(sequenceResourceMap);
		createSequenceAggregationAndRMInModel(sequenceResourceMap);
		return sequenceResourceMap;
	}	
	public void serializeNormalSequenceResourceMapToFile(String pathToFile, String format){
		serializeModelToFile(getNormalSequenceResourceMap(), pathToFile, format);
	}
	
	public Model getManifestResourceMap() {
		return sharedCanvasManifestResourceMap;
	}
	public void serializeManifestResourceMapToFile(String pathToFile, String format){
		serializeModelToFile(getManifestResourceMap(), pathToFile, format);
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
	
	private void deleteAllAggregationsAndRMFromModel(Model model) {
		ResIterator resourceMapResIter = model.listSubjectsWithProperty(RDF.type, rdfConstants.oreResourceMapClass);
		while (resourceMapResIter.hasNext()){
			Resource resourceMapResource = resourceMapResIter.nextResource();	
			// remove all Statements for which our old resource map was the subject
			model.remove(model.listStatements(resourceMapResource, null, (RDFNode) null));
		} 
		ResIterator resIter = model.listSubjectsWithProperty(RDF.type, rdfConstants.oreAggregationClass);
		while (resIter.hasNext()){
			Resource aggregationResource = resIter.nextResource();	
			// remove all Statements for which our old aggregation was the subject
			model.remove(model.listStatements(aggregationResource, null, (RDFNode) null));
		} 		
		// finally remove all old RDFList entries
		model.remove(model.listStatements(null, RDF.first, (RDFNode) null));
		model.remove(model.listStatements(null, RDF.rest, (RDFNode) null));
		model.remove(model.listStatements(null, RDF.type, RDF.List));
		
	}


	private Resource createAggregationAndRMWithoutList(Model model, String aggregationURI) {
		Resource aggregation = model.createResource(aggregationURI)
				.addProperty(RDF.type, rdfConstants.oreAggregationClass)
				.addProperty(RDF.type, RDF.List);
		
		model.createResource(aggregationURI + ".xml")
		.addProperty(RDF.type, rdfConstants.oreResourceMapClass)
		.addProperty(rdfConstants.oreDescribes, aggregation)
		.addProperty(DCTerms.created, model.createTypedLiteral(W3CDTF_NOW, DMSTechRDFConstants.DCTERMS_NAMESPACE + "W3CDTF"))
		.addProperty(DC.format, "application/rdf+xml");						
		// .addProperty(DCTerms.creator, ?? )	
		return aggregation;
	}
	
	
	
	private Resource createAggregationAndRMFromList(List<RDFNode> resourceList, Model model, String aggregationURI) {
		// assumes we're starting with a fresh model with no prior RDFList for the resources
		
		
		Resource aggregation = model.createList(resourceList.iterator())
				.addProperty(RDF.type, rdfConstants.oreAggregationClass);
		com.hp.hpl.jena.util.ResourceUtils.renameResource(aggregation, aggregationURI);		
		for (RDFNode resource : resourceList) {
			aggregation.addProperty(rdfConstants.oreAggregates, resource);
		}
		model.createResource(aggregationURI + ".xml")
		.addProperty(RDF.type, rdfConstants.oreResourceMapClass)
		.addProperty(rdfConstants.oreDescribes, aggregation)
		.addProperty(DCTerms.created, model.createTypedLiteral(W3CDTF_NOW, DMSTechRDFConstants.DCTERMS_NAMESPACE + "W3CDTF"))
		.addProperty(DC.format, "application/rdf+xml");						
		// .addProperty(DCTerms.creator, ?? )	
		return aggregation;
		
	}
	
	private Resource createManifestInModel(Model model) {
		
	//	hmmmm, this will also add the rdflist, which I don't think i want here.
		Resource sequenceAggregation = createSequenceAggregationAndRMWithoutList(sharedCanvasManifestResourceMap);
		Resource imageAnnoAggregation = createAnnoAggregationAndRMInModelWithoutList(sharedCanvasManifestResourceMap);		
		
		Resource manifestAggregation = model.createResource(getManifestAggregationURI())
				.addProperty(RDF.type, rdfConstants.oreAggregationClass)		
				.addProperty(RDF.type, rdfConstants.manifestClass)
				.addProperty(rdfConstants.oreAggregates, imageAnnoAggregation)
				.addProperty(rdfConstants.oreAggregates, sequenceAggregation);
		    	  // add the first and rest later, as well as each 'aggregates'  
		// add the resource map for the transaction to the model
		model.createResource(getManifestRMURI())
				.addProperty(RDF.type, rdfConstants.oreResourceMapClass)
				.addProperty(rdfConstants.oreDescribes, manifestAggregation)
				.addProperty(DCTerms.created, model.createTypedLiteral(W3CDTF_NOW, rdfConstants.DCTERMS_NAMESPACE + "W3CDTF"))
				.addProperty(DC.format, "application/rdf+xml");						
				// .addProperty(DCTerms.creator, ?? )	
		return manifestAggregation;
	}

	private Resource createSequenceAggregationAndRMInModel(Model model) {		
		return createAggregationAndRMFromList(imageSequence, model, getSequenceAggregationURI())
				.addProperty(RDF.type, rdfConstants.sequenceClass);		
	}

	private Resource createAnnoAggregationAndRMInModel(Model model) {		
		return createAggregationAndRMFromList(annotationSequence, model, getImageAnnosAggregationURI())
				.addProperty(RDF.type, rdfConstants.imageAnnotationListClass);
		
	}

	private Resource createSequenceAggregationAndRMWithoutList(Model model) {		
		return createAggregationAndRMWithoutList(model, getSequenceAggregationURI())
				.addProperty(RDF.type, rdfConstants.sequenceClass);		
	}

	private Resource createAnnoAggregationAndRMInModelWithoutList(Model model) {		
		return createAggregationAndRMWithoutList(model, getImageAnnosAggregationURI())
				.addProperty(RDF.type, rdfConstants.imageAnnotationListClass);
		
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

    
    private String getManifestAggregationURI() {
		return baseURI + "/Manifest";
	}
	private String getManifestRMURI() {
		return getManifestAggregationURI() + ".xml";
	}
	
	private String getSequenceAggregationURI() {
		return baseURI + "/NormalSequence";
	}
	
	private String getImageAnnosAggregationURI() {
		return baseURI + "/ImageAnnotations";		
	}
	
	public String getNewCanvasURI() {
		return baseURI + UUID.randomUUID();
	}
	public String getAnnotationURI(String canvasURI) {
		return canvasURI + "/" + UUID.randomUUID();
	}
	
}




