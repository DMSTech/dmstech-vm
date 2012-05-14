package edu.stanford.dmstech.vm.uriresolvers.ingest;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

import edu.stanford.dmstech.vm.Config;
import edu.stanford.dmstech.vm.DMSTechRDFConstants;
import edu.stanford.dmstech.vm.RDFUtils;
import edu.stanford.dmstech.vm.SharedCanvasUtil;


@Path("/{collectionId}/{manuscriptId}/sequences")
public class SequenceSubmission {

	@Context 
	UriInfo uriInfo;
	DMSTechRDFConstants rdfConstants = DMSTechRDFConstants.getInstance();
	
	@POST
	public Response saveNewSequence(
			@PathParam("collectionId") final String collectionId,
			@PathParam("manuscriptId") final String manuscriptId,
			@QueryParam("makedefault") final boolean makeDefault,
			InputStream inputStream) throws Exception  {
		String sequenceUUID = UUID.randomUUID().toString();
		String originalRequest = uriInfo.getAbsolutePath().toASCIIString();
		String newSequenceURI = UriBuilder.fromUri(originalRequest).path(sequenceUUID).build().toString();
		String newOptimizedSequenceURI = UriBuilder.fromUri(originalRequest).path("optimized/" + sequenceUUID).build().toString();
		String manuscriptManifestURI = originalRequest.substring(0, originalRequest.lastIndexOf("/")) + "/Manifest";
		Model model = RDFUtils.loadModelFromInputStream(inputStream, "RDF/XML");
		String fileToSave = new File(Config.getAbsolutePathToManuscriptsSequenceDir(collectionId, manuscriptId), sequenceUUID + ".nt").getAbsolutePath();
		RDFUtils.serializeModelToFile(model, fileToSave, "N-TRIPLE");
		
		String absolutePathToManuscriptManifestFile = Config.getAbsolutePathToManuscriptManifestFile(collectionId, manuscriptId);
		Model manifestModel = RDFUtils.loadModelInAbsoluteDir(absolutePathToManuscriptManifestFile, "N-TRIPLE");
	//	System.out.println(RDFUtils.serializeModelToString(manifestModel, "N-TRIPLE"));
		boolean doesSequenceChoiceExist = manifestModel.contains(null, RDF.type, rdfConstants.scSequenceChoiceClass);
		if (doesSequenceChoiceExist) {
			Resource sequenceChoice = manifestModel.listSubjectsWithProperty(RDF.type, rdfConstants.scSequenceChoiceClass).nextResource();
			if (makeDefault) {
				Resource oldDefaultSequence = sequenceChoice.listProperties(rdfConstants.scOptionDefault).nextStatement().getObject().asResource();
				sequenceChoice.removeAll(rdfConstants.scOptionDefault);
				sequenceChoice.addProperty(rdfConstants.scOptionSequence, oldDefaultSequence);
				sequenceChoice.addProperty(rdfConstants.scOptionDefault, newSequenceURI);
			} else {
				sequenceChoice.addProperty(rdfConstants.scOptionSequence, newSequenceURI);
			} 
		} else {
			//remove ore:aggregates <sequenceURI>:
			Resource manifestResource = manifestModel.getResource(manuscriptManifestURI);
			Resource oldSequence = null;
			StmtIterator aggregatesStatements = manifestModel.listStatements(manifestResource, rdfConstants.oreAggregates, (RDFNode)null );
			while (aggregatesStatements.hasNext()) {
				Statement nextStatement = aggregatesStatements.nextStatement();
				if (nextStatement.getObject().asResource().hasProperty(RDF.type, rdfConstants.scSequenceClass)) {
					oldSequence = nextStatement.getObject().asResource();
					manifestModel.remove(nextStatement);
					// should only be one
					break;
				}
			}
			
			Resource newSequenceChoice = manifestModel.createResource().addProperty(RDF.type, rdfConstants.scSequenceChoiceClass);
			
			if (makeDefault) {
				newSequenceChoice
						.addProperty(rdfConstants.scOptionDefault, newSequenceURI)
						.addProperty(rdfConstants.scOptionSequence, oldSequence);			
			} else {
				newSequenceChoice
						.addProperty(rdfConstants.scOptionDefault, oldSequence)
						.addProperty(rdfConstants.scOptionSequence, newSequenceURI);			
			}
			
			manifestResource.addProperty(rdfConstants.oreAggregates, newSequenceChoice);
			
		}
		String W3CDTF_NOW = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")).format(new Date());
	//	System.out.println("just before adding the agregation: \n" + RDFUtils.serializeModelToString(manifestModel, "N-TRIPLE"));
		
		
		SharedCanvasUtil.createSequenceAggregationAndRMWithoutList(manifestModel, newOptimizedSequenceURI, newSequenceURI, W3CDTF_NOW);
		
	//	System.out.println("after adding the agregation: \n" + RDFUtils.serializeModelToString(manifestModel, "N-TRIPLE"));
		
		RDFUtils.serializeModelToFile(manifestModel, absolutePathToManuscriptManifestFile, "N-TRIPLE");
		
		return Response.created(new URI(newSequenceURI)).build();
	}	

}
