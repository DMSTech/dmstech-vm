package edu.stanford.dmstech.vm;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

public class DMSTechRDFConstants {

	private static DMSTechRDFConstants instance = null;

	static Model initializingModel = null;

	public static final String ORE_NAMESPACE = "http://www.openarchives.org/ore/terms/";
	public static final String STANFORD_NAMESPACE = "http://dms.stanford.edu/ns/";
	public static final String DCTERMS_NAMESPACE = "http://purl.org/dc/terms/";
	public static final String HTTP_NAMESPACE = "http://www.w3.org/2011/http#";
	public static final String CNT_NAMESPACE = "http://www.w3.org/2008/content#";
	public static final String OAC_NAMESPACE = "http://www.openannotation.org/ns/";
	public static final String EXIF_NAMESPACE = "http://www.w3.org/2003/12/exif/ns#";

	// dmstech specific types
	public Resource textAnnotationClass;
	public Resource transactionClass;
	public Resource sequenceClass;
	public Resource imageCollectionClass;
	public Resource dmsImageAnnotationClass;
	public Resource dmsImageClass;
	public Resource dmsImageBodyClass;
	public Resource dmsCanvasClass;

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

	private DMSTechRDFConstants() {
		super();
	}

	public static DMSTechRDFConstants getInstance() {
		if (instance == null) {
			instance = new DMSTechRDFConstants();
		}
		return instance;
	}

	public Model getInitializingModel() {
		if (initializingModel == null) {
			initializingModel = ModelFactory.createDefaultModel();

			initializingModel.setNsPrefix("dcterms", DCTERMS_NAMESPACE);
			initializingModel.setNsPrefix("ore", ORE_NAMESPACE);
			initializingModel.setNsPrefix("http", HTTP_NAMESPACE);
			initializingModel.setNsPrefix("cnt", CNT_NAMESPACE);
			initializingModel.setNsPrefix("oac", OAC_NAMESPACE);

			// dmstech properties and types
			textAnnotationClass = initializingModel
					.createResource("http://dms.stanford.edu/ns/TextAnnotation");
			transactionClass = initializingModel
					.createResource(STANFORD_NAMESPACE + "Transaction");
			sequenceClass = initializingModel
					.createResource(STANFORD_NAMESPACE + "Sequence");
			imageCollectionClass = initializingModel
					.createResource(STANFORD_NAMESPACE + "ImageCollection");
			dmsImageAnnotationClass= initializingModel
					.createResource(STANFORD_NAMESPACE + "ImageAnnotation");
			dmsImageClass = initializingModel
					.createResource(STANFORD_NAMESPACE + "Image");
			dmsImageBodyClass = initializingModel
					.createResource(STANFORD_NAMESPACE + "ImageBody");
			dmsCanvasClass = initializingModel
					.createResource(STANFORD_NAMESPACE + "Canvas");
			
			
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

		}
		return initializingModel;
	}
}
