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
import edu.stanford.dmstech.vm.RDFUtils;
import edu.stanford.dmstech.vm.SharedCanvasUtil;
import edu.stanford.dmstech.vm.uriresolvers.canvas.CanvasTextAnnoResourceMap;

public class SharedCanvas {

	public final String W3CDTF_NOW = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")).format(new Date());
	Logger logger = Logger.getLogger(CanvasTextAnnoResourceMap.class.getName());
	LoggerFacade loggerFacade = null;
	
	DMSTechRDFConstants rdfConstants = null;
	
	// one model for each of the files we'll serialize
	Model sequenceResourceMapModel;
	Model imageAnnosResourceMapModel;
	Model sharedCanvasManifestResourceMapModel;

	Resource manifestAggregation;
	/*
	 * We store the new canvases in an array list for convenience,
	 * which we convert to a Jena RDFList as needed.
	 */
	private List<RDFNode> canvasSequenceJavaList;
	
	/*
	 * We store the new canvases in an array list for convenience,
	 * which we convert to a Jena RDFList as needed.
	 */
	private List<RDFNode> annotationSequenceJavaList;
	
	String baseURI;
	
	/**
	 * 
	 */
	private SharedCanvas() {
		
		loggerFacade = new Jdk14Logger(logger);
		rdfConstants = DMSTechRDFConstants.getInstance();

	}
	
	public static SharedCanvas createNewSharedCanvasModel(
			String baseURI,
			String manuscriptName,
			String manuscriptTitle, 
			String collectionId, 
			String manuscriptId, 
			String alternateId, 
			String repositoryName, 
			String institutionName, 
			String settlementName, 
			String regionName, 
			String countryName
			){
		return (new SharedCanvas()).initializeNewModel(
				baseURI,
				manuscriptName,
				manuscriptTitle,
				collectionId,
				manuscriptId,
				alternateId,
				repositoryName,
				institutionName,
				settlementName,
				regionName,
				countryName
				);
		
	}
	
	public static SharedCanvas loadSharedCanvasModelFromRMs(Model sequenceRM, Model annoRM, Model manifestRM, String baseURI) throws Exception {
		// set the local RMs, then pull the image annos into anno ArrayList and canvases into sequence ArrayList
		SharedCanvas sharedCanvasModel = new SharedCanvas();
		sharedCanvasModel.initializeFromExistingModels(sequenceRM, annoRM, manifestRM, baseURI);		
		return sharedCanvasModel;
	}
	
	private void initializeFromExistingModels(Model sequenceRM, Model annoRM, Model manifestRM, String baseURI) throws Exception {
		this.baseURI = baseURI;
 		setSequenceResourceMap(sequenceRM);
		setImageAnnosResourceMap(annoRM);
		setSharedCanvasManifestResourceMap(manifestRM);
		
		annotationSequenceJavaList = extractSingleRDFListFromModel(annoRM);
		canvasSequenceJavaList = extractSingleRDFListFromModel(sequenceRM);

	}
	private List<RDFNode> extractSingleRDFListFromModel(Model model) throws Exception {
		List<RDFNode> theList = null;
		ResIterator resIter = model.listSubjectsWithProperty(RDF.type, rdfConstants.scSequenceClass);
		if (resIter.hasNext()){
			// from http://tech.groups.yahoo.com/group/jena-dev/message/6769
			//RDFList l = (RDFList) x.getPropertyValue( p ).as( RDFList.class );
			theList = ((RDFList) resIter.next().as(RDFList.class)).asJavaList();			
		} else {
			throw new Exception("Missing rdf lists in either annotation or normal sequence file.");
		}
		return theList;
	}
	
	private SharedCanvas setSharedCanvasManifestResourceMap(Model manifestRM) {
		this.setSharedCanvasManifestResourceMap(manifestRM);
		return this;
	}
	
	private SharedCanvas setImageAnnosResourceMap(Model annoRM) {
		this.imageAnnosResourceMapModel = annoRM;
		return this;
	}

	private SharedCanvas setSequenceResourceMap(Model sequenceRM) {
		this.sequenceResourceMapModel = sequenceRM;
		return this;
	}

	private SharedCanvas initializeNewModel(
			String baseURI,
			String manuscriptName,
			String manuscriptTitle, 
			String collectionId, 
			String manuscriptId, 
			String alternateId, 
			String repositoryName, 
			String institutionName, 
			String settlementName, 
			String regionName, 
			String countryName) {
		
		this.baseURI = baseURI;
		
		canvasSequenceJavaList = new ArrayList<RDFNode>();
		annotationSequenceJavaList = new ArrayList<RDFNode>();

		sequenceResourceMapModel = ModelFactory.createDefaultModel();
		imageAnnosResourceMapModel = ModelFactory.createDefaultModel();
		sharedCanvasManifestResourceMapModel = ModelFactory.createDefaultModel();
		
		sequenceResourceMapModel.setNsPrefixes(rdfConstants.getInitializingModel());
		imageAnnosResourceMapModel.setNsPrefixes(rdfConstants.getInitializingModel());
		sharedCanvasManifestResourceMapModel.setNsPrefixes(rdfConstants.getInitializingModel());
		//create the manifest
		createManifestInModel(
				sharedCanvasManifestResourceMapModel,
				manuscriptName,
				manuscriptTitle,
				collectionId,
				manuscriptId,
				alternateId,
				repositoryName,
				institutionName,
				settlementName,
				regionName,
				countryName);
		return this;
	}

	/**
	 * The imageURI comes in predetermined.
	 * @param imageId
	 */
	public void addImageToSharedCanvas(String imageFileName, String pageTitle, String width, String height) {
		
		if (pageTitle == null) pageTitle = "";
		
		String canvasURI = getNewCanvasURI();
		String imageURI = getImageURI(imageFileName);
		String annotationURI = getNewAnnotationURI(canvasURI);
		
		// populate image annotation resource map and aggregation
		Resource imageBody = createImageBodyInModel(imageAnnosResourceMapModel, imageURI, width, height);
		Resource canvasTargetInAnnoRM = createCanvasInModel(imageAnnosResourceMapModel, width, height, pageTitle, canvasURI);
		Resource annotation = createAnnotationInModel(imageAnnosResourceMapModel, annotationURI, width, height, imageBody, canvasTargetInAnnoRM);
		annotationSequenceJavaList.add(annotation);
		
		Resource canvasTargetInSeqRM = createCanvasInModel(sequenceResourceMapModel, width, height, pageTitle, canvasURI);
		canvasSequenceJavaList.add(canvasTargetInSeqRM);
				
	}
	
	/** We rebuild the aggregation and resourcemap everytime model is requested
	 * in case something new has been added.
	 * @return
	 */
	public Model getImageAnnoResourceMap() {
		deleteAllAggregationsAndRMFromModel(imageAnnosResourceMapModel);
		createAnnoAggregationAndRMInModel(imageAnnosResourceMapModel);
		return imageAnnosResourceMapModel;
	}
	

	public void serializeImageAnnoResourceMapToFile(String pathToFile, String format){
		RDFUtils.serializeModelToFile(getImageAnnoResourceMap(), pathToFile, format);
	}


	/** We rebuild the aggregation and resourcemap everytime the  model is requested
	 * in case something new has been added.
	 * @return
	 */
	public Model getNormalSequenceResourceMap() {
		deleteAllAggregationsAndRMFromModel(sequenceResourceMapModel);
		createSequenceAggregationAndRMInModel(sequenceResourceMapModel);
		return sequenceResourceMapModel;
	}	
	public void serializeNormalSequenceResourceMapToFile(String pathToFile, String format){
		RDFUtils.serializeModelToFile(getNormalSequenceResourceMap(), pathToFile, format);
	}
	
	public Model getManifestResourceMap() {
		return sharedCanvasManifestResourceMapModel;
	}
	public void serializeManifestResourceMapToFile(String pathToFile, String format){
		RDFUtils.serializeModelToFile(getManifestResourceMap(), pathToFile, format);
	}
	
	public Resource getManifestAggregationResource() {
		return manifestAggregation;
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


	
	
	
	private Resource createAggregationAndRMFromList(List<RDFNode> resourceList, Model model, String aggregationURI, Resource dmsAggregationType) {
		// assumes we're starting with a fresh model with no prior RDFList for the resources
		
		
		Resource aggregation = model.createList(resourceList.iterator())
				.addProperty(RDF.type, rdfConstants.oreAggregationClass)
				.addProperty(RDF.type, dmsAggregationType);
		for (RDFNode resource : resourceList) {
			aggregation.addProperty(rdfConstants.oreAggregates, resource);
		}
		model.createResource(aggregationURI + ".xml")
		.addProperty(RDF.type, rdfConstants.oreResourceMapClass)
		.addProperty(rdfConstants.oreDescribes, aggregation)
		.addProperty(DCTerms.created, model.createTypedLiteral(W3CDTF_NOW, DMSTechRDFConstants.DCTERMS_NAMESPACE + "W3CDTF"))
		.addProperty(DC.format, "application/rdf+xml");						
			
		com.hp.hpl.jena.util.ResourceUtils.renameResource(aggregation, aggregationURI);		
		
		return aggregation;
		
	}
	
	private Resource createManifestInModel(
			Model model, 
			String manuscriptName, 
			String manuscriptTitle, 
			String collectionId, 
			String manuscriptId, 
			String alternateId, 
			String repositoryName, 
			String institutionName, 
			String settlementName, 
			String regionName, 
			String countryName
			) {
		
		Resource sequenceAggregation = createSequenceAggregationAndRMWithoutList(sharedCanvasManifestResourceMapModel);
		Resource imageAnnoAggregation = createAnnoAggregationAndRMInModelWithoutList(sharedCanvasManifestResourceMapModel);		
		
		manifestAggregation = model.createResource(getManifestAggregationURI())
				.addProperty(RDF.type, rdfConstants.oreAggregationClass)		
				.addProperty(RDF.type, rdfConstants.scManifestClass)
				.addProperty(rdfConstants.oreAggregates, imageAnnoAggregation)
				.addProperty(rdfConstants.oreAggregates, sequenceAggregation)
				.addProperty(DC.title, manuscriptTitle)	
				.addProperty(rdfConstants.teiMsNameProperty, manuscriptName)	
				.addProperty(rdfConstants.teiCollectionProperty, collectionId)	
				.addProperty(rdfConstants.teiIdnoProperty, manuscriptId)	
				.addProperty(rdfConstants.teiAltIdentifierProperty, alternateId)	
				.addProperty(rdfConstants.teiRepositoryProperty, repositoryName)	
				.addProperty(rdfConstants.teiInstitutionProperty, institutionName)	
				.addProperty(rdfConstants.teiSettlementProperty, settlementName)	
				.addProperty(rdfConstants.teiRegionProperty, regionName)	
				.addProperty(rdfConstants.teiCountryProperty, countryName);;
 
		// add the resource map that describes the aggregation
		model.createResource(getManifestRMURI())
				.addProperty(RDF.type, rdfConstants.oreResourceMapClass)
				.addProperty(rdfConstants.oreDescribes, manifestAggregation)
				.addProperty(DCTerms.created, model.createTypedLiteral(W3CDTF_NOW, rdfConstants.DCTERMS_NAMESPACE + "W3CDTF"))
				.addProperty(DC.format, "application/rdf+xml");
				
				
				// .addProperty(DCTerms.creator, ?? )	
		return manifestAggregation;
	}

	private Resource createSequenceAggregationAndRMInModel(Model model) {		
		return createAggregationAndRMFromList(canvasSequenceJavaList, model, getSequenceAggregationURI(), rdfConstants.scSequenceClass);	
	}

	private Resource createAnnoAggregationAndRMInModel(Model model) {		
		return createAggregationAndRMFromList(annotationSequenceJavaList, model, getImageAnnosAggregationURI(), rdfConstants.scImageAnnotationListClass);
		
	}

	private Resource createSequenceAggregationAndRMWithoutList(Model model) {		
		return SharedCanvasUtil.addResourceMapAndAggregationToModel(model, getSequenceAggregationURI(), rdfConstants.scSequenceClass, W3CDTF_NOW);		
	}

	private Resource createAnnoAggregationAndRMInModelWithoutList(Model model) {		
		return SharedCanvasUtil.addResourceMapAndAggregationToModel(model, getImageAnnosAggregationURI(), rdfConstants.scImageAnnotationListClass, W3CDTF_NOW);
				
		
	}
	
	public Resource createCanvasInModel(Model model, String width, String height, String title, String canvasURI) {		
		return model.createResource(canvasURI)
				.addProperty(DC.title, title)
				.addProperty(rdfConstants.exifWidth, width)
				.addProperty(rdfConstants.exifHeight, height)
				.addProperty(RDF.type, rdfConstants.scCanvasClass);	
				 /* <rdf:Description rdf:about="http://dms-data.stanford.edu/Stanford/kq131cs7229/image-24">
				    <dc:title>f. 17r</dc:title>
				    <exif:width>3907</exif:width>
				    <exif:height>5611</exif:height>
				    <rdf:type rdf:resource="http://dms.stanford.edu/ns/Canvas"/>
				  </rdf:Description> */
  }
  
    public Resource createImageBodyInModel(Model model, String imageURI, String width, String height) {
    	return imageAnnosResourceMapModel.createResource(imageURI)
				.addProperty(rdfConstants.exifWidth, width)
				.addProperty(rdfConstants.exifHeight, height)
				.addProperty(RDF.type, rdfConstants.scImageClass)
				.addProperty(RDF.type, rdfConstants.scImageBodyClass);
				  /*<rdf:Description rdf:about="http://stacks.stanford.edu/image/kq131cs7229/kq131cs7229_05_0003">
				    <exif:width>4016</exif:width>
				    <exif:height>5888</exif:height>
				    <rdf:type rdf:resource="http://purl.org/dc/dcmitype/Image"/>
				    <rdf:type rdf:resource="http://dms.stanford.edu/ns/ImageBody"/>
				  </rdf:Description>*/
    }
  
    public Resource createAnnotationInModel(Model model, String annotationURI, String width, String height, Resource imageBody, Resource canvasTarget) {
    	return model.createResource(annotationURI)
				.addProperty(rdfConstants.oacHasBodyProperty, imageBody)
				.addProperty(rdfConstants.oacHasTargetProperty, canvasTarget)
				.addProperty(RDF.type, rdfConstants.oacAnnotationType)
				.addProperty(RDF.type, rdfConstants.scImageAnnotationClass);
				 /* <rdf:Description rdf:about="http://dms-data.stanford.edu/Stanford/kq131cs7229/imageanno/image-10">
				    <oac:hasBody rdf:resource="http://stacks.stanford.edu/image/kq131cs7229/kq131cs7229_05_0010"/>
				    <oac:hasTarget rdf:resource="http://dms-data.stanford.edu/Stanford/kq131cs7229/image-10"/>
				    <rdf:type rdf:resource="http://www.openannotation.org/ns/Annotation"/>
				    <rdf:type rdf:resource="http://dms.stanford.edu/ns/ImageAnnotation"/>
				  </rdf:Description> */
    }

    
    public String getManifestAggregationURI() {
		return baseURI + "Manifest";
	}
	public String getManifestRMURI() {
		return getManifestAggregationURI() + ".xml";
	}
	
	private String getSequenceAggregationURI() {
		return baseURI + "NormalSequence";
	}
	
	private String getImageAnnosAggregationURI() {
		return baseURI + "ImageAnnotations";		
	}
	
	public String getNewCanvasURI() {
		return baseURI + UUID.randomUUID();
	}
	public String getNewAnnotationURI(String canvasURI) {
		return canvasURI + "/" + UUID.randomUUID();
	}
	public String getImageURI(String imageFileName) {
		return baseURI + "images/" + imageFileName;
	}
	
}




