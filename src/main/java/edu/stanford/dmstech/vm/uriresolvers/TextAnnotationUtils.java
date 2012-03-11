package edu.stanford.dmstech.vm.uriresolvers;

import java.util.HashMap;
import java.util.UUID;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.DCTypes;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;

import edu.stanford.dmstech.vm.Config;
import edu.stanford.dmstech.vm.DMSTechRDFConstants;

public class TextAnnotationUtils {

	
	public Resource mintURIsForAnnoAndBody(
			final String manuscriptId,
			final String canvasId,
			HashMap<String, String> bodyTextsToWriteToFile,
			DMSTechRDFConstants rdfConstants, 
			Model existingTextAnnosRMModel,
			Model incomingTextAnnotationsModel,
			Model oldTextAnnotationsRMModel,
			Resource transactionsAggregationResource,
			Resource incomingTextAnno) {
		Resource newTextAnnotationResource = null;
		if (incomingTextAnno.getNameSpace().toLowerCase().equals("urn")) {	
			// SHOULD WE ALSO HAVE AN 'ELSE' SO WE CAN STILL CHECK FOR annotation text bodies inline? 
			// and more generally, to add the incoming annotation, even if it doesn't have a urn, but a full uri.  but that might then
			// suggest checking for the existence of the URI.  I think we can assume anything coming into here (to the POST) does not
			// have a URI:  that is exactly why it is being POSTED.  We have a separate 'publish' endpoint
			// for submitting annontations with prexisting uris.  We could nevertheless include a check here for non-urn ids and 
			// notify the user as needed.
			newTextAnnotationResource = existingTextAnnosRMModel.createResource( getNewTextAnnotationURI(manuscriptId), rdfConstants.oacTextAnnotationType);
			existingTextAnnosRMModel.add(newTextAnnotationResource, RDF.type, rdfConstants.oacAnnotationType);
			existingTextAnnosRMModel.add(newTextAnnotationResource, rdfConstants.oacHasTargetProperty, incomingTextAnno.listProperties(rdfConstants.oacHasTargetProperty).nextStatement().getObject());
			Resource oldBody = incomingTextAnno.listProperties(rdfConstants.oacHasBodyProperty).nextStatement().getObject().asResource();
			if (oldBody.hasProperty(RDF.type, rdfConstants.cntAsTxtType)) {
				String bodyText = oldBody.getProperty(rdfConstants.cntRestProperty).getString();				
				//should this next body type actually be a TextBody?
				Resource newBody = existingTextAnnosRMModel.createResource( getNewTextAnnoBodyURI(manuscriptId, bodyTextsToWriteToFile, bodyText), rdfConstants.oacBodyType); 
				// I'm not sure if this next sameAs is necessary, or in the right place.
				existingTextAnnosRMModel.add(newBody, OWL.sameAs, oldBody);
				existingTextAnnosRMModel.add(newTextAnnotationResource, rdfConstants.oacHasTargetProperty, newBody);
				existingTextAnnosRMModel.add(newBody, RDF.type, DCTypes.Text);
			} else {
				existingTextAnnosRMModel.add(newTextAnnotationResource, rdfConstants.oacHasBodyProperty, oldBody);
			}
			existingTextAnnosRMModel.add(newTextAnnotationResource, OWL.sameAs, incomingTextAnno);
			// add the new annotation to the annotation list aggregation
			existingTextAnnosRMModel.add(existingTextAnnosRMModel.listResourcesWithProperty(RDF.type, rdfConstants.oreAggregationClass).nextResource(), rdfConstants.oreAggregates, newTextAnnotationResource);
			
			incomingTextAnnotationsModel.add(incomingTextAnno, OWL.sameAs, newTextAnnotationResource);					
			oldTextAnnotationsRMModel.add(incomingTextAnnotationsModel);
			// add the old annotation to the aggregation in the list of old annotations
			oldTextAnnotationsRMModel.add(oldTextAnnotationsRMModel.listResourcesWithProperty(RDF.type, rdfConstants.oreAggregationClass).nextResource(), rdfConstants.oreAggregates, incomingTextAnno);
		
			transactionsAggregationResource.addProperty(rdfConstants.oreAggregates, newTextAnnotationResource);			
		} else {
			
		}
		return newTextAnnotationResource;
	}
	
	private String getNewTextAnnotationURI(final String manuscriptId) {
		return Config.getBaseURIForIds() + "manuscript" + "/" + manuscriptId + "/textannotations/" + UUID.randomUUID();
	}

	private String getNewTextAnnoBodyURI(final String manuscriptId, HashMap<String, String> bodyTextsToWriteToFiles, String bodyText) {
		String uuid = UUID.randomUUID().toString();
		bodyTextsToWriteToFiles.put(uuid, bodyText);
		return Config.getBaseURIForIds() + "manuscript" + "/" + manuscriptId + "/textannotations/bodytexts/" + uuid;	
	}

	
}
