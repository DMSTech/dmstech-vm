package edu.stanford.dmstech.vm;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class DMSTechRDFConstants {

	private static DMSTechRDFConstants instance = null;

	static Model initializingModel = null;

	public static final String ORE_NAMESPACE = "http://www.openarchives.org/ore/terms/";
	public static final String SHARED_CANVAS_NAMESPACE = "http://www.shared-canvas.org/ns/";
	public static final String DCTERMS_NAMESPACE = "http://purl.org/dc/terms/";
	public static final String HTTP_NAMESPACE = "http://www.w3.org/2011/http#";
	public static final String CNT_NAMESPACE = "http://www.w3.org/2008/content#";
	public static final String OAC_NAMESPACE = "http://www.openannotation.org/ns/";
	public static final String EXIF_NAMESPACE = "http://www.w3.org/2003/12/exif/ns#";
	public static final String TEI_NAMESPACE = "http://www.tei-c.org/ns/1.0/";

	
	
	// shared canvas types
	public Resource scTextAnnotationClass;
	public Resource scZoneAnnotationClass;
	public Resource scImageAnnotationClass;
	public Resource scTransactionClass;
	public Resource scSequenceClass;
	public Resource scSequenceChoiceClass;
	public Resource scImageAnnotationListClass;
	public Resource scTextAnnotationListClass;
	public Resource scZoneAnnotationListClass;	
	public Resource scManifestClass;
	public Resource scImageClass;
	public Resource scImageBodyClass;
	public Resource scCanvasClass;
	public Resource scRepositoryClass;
	public Resource scCollectionClass;
	
	//shared canvas properties
	public Property scHasTextAnnotations;
	public Property scHasImageAnnotations;
	public Property scHasZoneAnnotations;
	public Property scOptionSequence;
	public Property scOptionDefault;	
	public Property scNewSequenceEnpoint;
	public Property scHasOptimizedSerialization;	
	

	// exif properties
	public Property exifHeight;
	public Property exifWidth;
	
	// http properties and types
	public Property httpMethod;
	public Resource httpPostResource;

	// cnt properties and types
	public Property cntRestProperty;
	public Property cntCharEncProperty;
	public Resource cntAsTxtType;

	// ORE properties and types
	public Property oreDescribes;
	public Property oreAggregates;
	public Resource oreResourceMapClass;
	public Resource oreAggregationClass;

	// OAC properties and types
	public Property oacHasBodyProperty;
	public Property oacHasTargetProperty;
	public Resource oacAnnotationType;
	public Resource oacTextAnnotationType;
	public Resource oacBodyType;
	public Resource oacTargetType;

	// TEI properties
	public Property teiCountryProperty;
	public Property teiRegionProperty;
	public Property teiSettlementProperty;
	public Property teiInstitutionProperty;
	public Property teiRepositoryProperty;
	public Property teiCollectionProperty;
	public Property teiIdnoProperty;
	public Property teiAltIdentifierProperty;
	public Property teiMsNameProperty;

	
	
	private DMSTechRDFConstants() {
		super();
	}

	public static DMSTechRDFConstants getInstance() {
		if (instance == null) {
			instance = new DMSTechRDFConstants();
			instance.initializeModel();
		}

		return instance;
	}

	public Model getInitializingModel() {
		if (initializingModel == null) {
			

		}
		return initializingModel;
	}
	
	private void initializeModel() {
		initializingModel = ModelFactory.createDefaultModel();

		initializingModel.setNsPrefix("dcterms", DCTERMS_NAMESPACE);
		initializingModel.setNsPrefix("ore", ORE_NAMESPACE);
		initializingModel.setNsPrefix("http", HTTP_NAMESPACE);
		initializingModel.setNsPrefix("cnt", CNT_NAMESPACE);
		initializingModel.setNsPrefix("oac", OAC_NAMESPACE);
		initializingModel.setNsPrefix("exif", EXIF_NAMESPACE);
		initializingModel.setNsPrefix("sc", SHARED_CANVAS_NAMESPACE);
		initializingModel.setNsPrefix("tei", TEI_NAMESPACE);

		// shared canvas types
		
		scTextAnnotationClass = initializingModel
				.createResource(SHARED_CANVAS_NAMESPACE + "TextAnnotation");
		scTransactionClass = initializingModel
				.createResource(SHARED_CANVAS_NAMESPACE + "Transaction");
		scSequenceClass = initializingModel
				.createResource(SHARED_CANVAS_NAMESPACE + "Sequence");
		scSequenceChoiceClass = initializingModel
				.createResource(SHARED_CANVAS_NAMESPACE + "SequenceChoice");		
		scImageAnnotationListClass = initializingModel
				.createResource(SHARED_CANVAS_NAMESPACE + "ImageAnnotationList");
		scTextAnnotationListClass = initializingModel
				.createResource(SHARED_CANVAS_NAMESPACE + "TextAnnotationList");
		scZoneAnnotationListClass = initializingModel
				.createResource(SHARED_CANVAS_NAMESPACE + "ZoneAnnotationList");
		scManifestClass = initializingModel
				.createResource(SHARED_CANVAS_NAMESPACE + "Manifest");
		scRepositoryClass = initializingModel
				.createResource(SHARED_CANVAS_NAMESPACE + "Repository");
		scCollectionClass = initializingModel
				.createResource(SHARED_CANVAS_NAMESPACE + "Collection");
		scImageAnnotationClass= initializingModel
				.createResource(SHARED_CANVAS_NAMESPACE + "ImageAnnotation");
		scZoneAnnotationClass = initializingModel
				.createResource(SHARED_CANVAS_NAMESPACE + "ZoneAnnotation");
		scImageClass = initializingModel
				.createResource(SHARED_CANVAS_NAMESPACE + "Image");
		scImageBodyClass = initializingModel
				.createResource(SHARED_CANVAS_NAMESPACE + "ImageBody");
		scCanvasClass = initializingModel
				.createResource(SHARED_CANVAS_NAMESPACE + "Canvas");
		scHasOptimizedSerialization = initializingModel
				.createProperty(SHARED_CANVAS_NAMESPACE + "hasOptimizedSerialization");
		
		// shared canvas properties
		
		scHasTextAnnotations = initializingModel
				.createProperty(SHARED_CANVAS_NAMESPACE, "hasTextAnnotations");
		scOptionSequence = initializingModel
				.createProperty(SHARED_CANVAS_NAMESPACE, "optionSequence");	
		scOptionDefault = initializingModel
				.createProperty(SHARED_CANVAS_NAMESPACE + "optionDefault");
		scHasImageAnnotations = initializingModel
				.createProperty(SHARED_CANVAS_NAMESPACE, "hasImageAnnotations");
		scHasZoneAnnotations = initializingModel
				.createProperty(SHARED_CANVAS_NAMESPACE, "hasZoneAnnotations");
		scNewSequenceEnpoint = initializingModel
				.createProperty(SHARED_CANVAS_NAMESPACE + "newSequenceEndpoint");
		
		
		
		
		// exif properties
		exifHeight = initializingModel.createProperty(EXIF_NAMESPACE,
				"height");
		exifWidth = initializingModel.createProperty(EXIF_NAMESPACE,
				"width");
		
		// http properties and types
		httpMethod = initializingModel.createProperty(HTTP_NAMESPACE,
				"mthd");
		httpPostResource = initializingModel.createResource(HTTP_NAMESPACE
				+ "POST");

		cntRestProperty = initializingModel.createProperty(CNT_NAMESPACE,
				"rest");
		cntCharEncProperty = initializingModel.createProperty(
				CNT_NAMESPACE, "characterEncoding");
		cntAsTxtType = initializingModel.createResource(CNT_NAMESPACE
				+ "ContentAsText");

		// ORE properties and types
		oreDescribes = initializingModel.createProperty(ORE_NAMESPACE,
				"describes");
		oreAggregates = initializingModel.createProperty(ORE_NAMESPACE,
				"aggregates");
		oreResourceMapClass = initializingModel
				.createResource(ORE_NAMESPACE + "ResourceMap");
		oreAggregationClass = initializingModel
				.createResource(ORE_NAMESPACE + "Aggregation");

		// OAC properties and types
		oacHasBodyProperty = initializingModel.createProperty(
				OAC_NAMESPACE, "hasBody");
		oacHasTargetProperty = initializingModel.createProperty(
				OAC_NAMESPACE, "hasTarget");
		oacAnnotationType = initializingModel.createResource(OAC_NAMESPACE
				+ "Annotation");
		oacTextAnnotationType = initializingModel
				.createResource(OAC_NAMESPACE + "TextAnnotation");
		oacBodyType = initializingModel.createResource(OAC_NAMESPACE
				+ "Body");
		oacTargetType = initializingModel.createResource(OAC_NAMESPACE
				+ "Target");
		
		// TEI properties
		teiCountryProperty = initializingModel.createProperty(
				TEI_NAMESPACE, "country");
		teiRegionProperty = initializingModel.createProperty(
				TEI_NAMESPACE, "region");
		teiSettlementProperty = initializingModel.createProperty(
				TEI_NAMESPACE, "settlement");
		teiInstitutionProperty = initializingModel.createProperty(
				TEI_NAMESPACE, "institution");
		teiRepositoryProperty = initializingModel.createProperty(
				TEI_NAMESPACE, "repository");
		teiCollectionProperty = initializingModel.createProperty(
				TEI_NAMESPACE, "collection");
		teiIdnoProperty = initializingModel.createProperty(
				TEI_NAMESPACE, "idno");
		teiAltIdentifierProperty = initializingModel.createProperty(
				TEI_NAMESPACE, "altIdentifier");
		teiMsNameProperty = initializingModel.createProperty(
				TEI_NAMESPACE, "msName");
		
	}
}
