package edu.stanford.dmstech.vm.uriresolvers;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFList;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;

import edu.stanford.dmstech.vm.Config;
import edu.stanford.dmstech.vm.DMSTechRDFConstants;
import edu.stanford.dmstech.vm.RDFUtils;


@Path("/{collectionId}/{manuscriptId}/sequences/optimized/{sequenceFileName: (.*\\.json$)}") 
public class OptimizedManuscriptPageList {	
	
	DMSTechRDFConstants rdfConstants = DMSTechRDFConstants.getInstance();

	@GET
	@Produces("application/rdf+xml")
	public Response getCombinedSequenceAndImages(
			@PathParam("collectionId") final String collectionId,
			@PathParam("manuscriptId") final String manuscriptId,
			@PathParam("sequenceFileName") final String sequenceFileName
			) throws Exception {   
		
		
		String sequenceId = sequenceFileName.substring(0, sequenceFileName.lastIndexOf("."));
		String result = resultFromModel(collectionId, manuscriptId, sequenceId);
		return Response.ok(result, "application/json").build();
		
	}

	public String resultFromModel(final String collectionId,
			final String manuscriptId, String sequenceId)
			throws Exception, JSONException {
		JSONArray jsonResult = new JSONArray();
		String pathToSequenceRDF = Config.getAbsolutePathToManuscriptSequenceSourceFile( collectionId, manuscriptId, sequenceId);
		String pathToAnnoRDF = Config.getAbsolutePathToManuscriptImageAnnoFile( collectionId, manuscriptId);
		Model sequenceModel = RDFUtils.loadModelInAbsoluteDir(pathToSequenceRDF, "N-TRIPLE");
		Model annoModel = RDFUtils.loadModelInAbsoluteDir(pathToAnnoRDF, "N-TRIPLE");
		
		List<RDFNode> orderedCanvasList = extractSingleRDFListFromModel(sequenceModel);
	//	System.out.println("The list from the model has " + orderedCanvasList.size() + " entries.");
		for (RDFNode canvasNode : orderedCanvasList) {
			StmtIterator annosIter = annoModel.listStatements(null, rdfConstants.oacHasTargetProperty, canvasNode);
			if (annosIter.hasNext()) {
				JSONObject canvasObject = new JSONObject();
				String imageURI = annosIter.nextStatement().getSubject().getProperty(rdfConstants.oacHasBodyProperty).getObject().asResource().getURI();
				String canvasURI = canvasNode.asResource().getURI();
				String canvasTitle = canvasNode.asResource().getProperty(DC.title).getObject().asLiteral().getLexicalForm();
				String height = canvasNode.asResource().getProperty(rdfConstants.exifHeight).getObject().asLiteral().getLexicalForm();
				String width = canvasNode.asResource().getProperty(rdfConstants.exifWidth).getObject().asLiteral().getLexicalForm();
				canvasObject.put("imageURI", imageURI);
				canvasObject.put("canvasURI", canvasURI);
				canvasObject.put("canvasTitle", canvasTitle);
				canvasObject.put("height", height);
				canvasObject.put("width", width);
				jsonResult.put(canvasObject);				
			}
		}
		return jsonResult.toString(4);
	}
	
	private List<RDFNode> extractSingleRDFListFromModel(Model model) throws Exception {
		
		List<RDFNode> theList = null;
		ResIterator resIter = model.listSubjectsWithProperty(RDF.type, rdfConstants.scSequenceClass);
		if (resIter.hasNext()){
			theList = ((RDFList) resIter.next().as(RDFList.class)).asJavaList();			
		} else {
			throw new Exception("Missing rdf lists in either annotation or normal sequence file.");
		}
		return theList;
	}
	
	/*public  String resultFromSPARQL(String sequenceURI) throws JSONException {

		JSONArray jsonResult = new JSONArray();
		
		

		
		String queryString = "PREFIX oac: <http://www.openannotation.org/ns/> " +
			"PREFIX ore: <http://www.openarchives.org/ore/terms/> " +
			"PREFIX exif: <http://www.w3.org/2003/12/exif/ns#> " +
			"PREFIX dc: <http://purl.org/dc/elements/1.1/> " +
			"PREFIX sc: <http://www.shared-canvas.org/ns/> " +
			"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " +
			"SELECT DISTINCT ?annotationURI ?canvasURI ?imageURI ?canvasTitle ?height ?width " +
			"	WHERE { " +
			"<" + sequenceURI + "> ore:aggregates ?canvasURI ."  +
			"	 ?anno oac:hasTarget ?canvasURI . " +
			"	 ?anno oac:hasBody ?imageURI . " +
			"	 ?anno rdf:type sc:ImageAnnotation ."  +
			"    ?canvasURI exif:height ?height . " +
			"    ?canvasURI exif:width ?width . " +
			"    ?canvasURI dc:title ?canvasTitle " +			
			"	}";
	
	SharedCanvasTDBManager tdbManager = new SharedCanvasTDBManager();
	Model tdb = tdbManager.loadMainTDBDataset();	

	   Query query = QueryFactory.create(queryString);
	   QueryExecution qe = QueryExecutionFactory.create(query, tdb);
	   ResultSet results = qe.execSelect();
	   
		int count = 0;
	while (results.hasNext()) {
		count++;
		QuerySolution nextSolution = results.nextSolution();		
		JSONObject canvasObject = new JSONObject();
		canvasObject.put("annotationURI", nextSolution.getResource("imageURI").getURI());
		canvasObject.put("imageURI", nextSolution.getResource("imageURI").getURI());
		canvasObject.put("canvasURI", nextSolution.getResource("canvasURI").getURI());
		canvasObject.put("canvasTitle", nextSolution.getLiteral("canvasTitle").getLexicalForm());
		canvasObject.put("height", nextSolution.getLiteral("height").getLexicalForm());
		canvasObject.put("width", nextSolution.getLiteral("width").getLexicalForm());
		jsonResult.put(canvasObject);
	}
	System.out.println("The list from the model has " + count + " entries.");

	qe.close();
	
	return jsonResult.toString(4);
	
}*/

}

