package edu.stanford.dmstech.vm.indexing;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;

import edu.stanford.dmstech.vm.Config;
import edu.stanford.dmstech.vm.DMSTechRDFConstants;
import edu.stanford.dmstech.vm.RDFUtils;
import edu.stanford.dmstech.vm.manuscriptgeneration.SharedCanvasGenerator;

public class SharedCanvasSOLRIndexer {

	// two allowed values for the resulttype solr field
	public static final String RESULT_TYPE_CANVAS = "canvas";
	public static final String RESULT_TYPE_MANUSCRIPT = "manuscript";
	// SOLR FIELD NAMES
	// 'uri' is the identifier for the solr record
	public static final String URI_FIELD = "uri";
	public static final String MANNAME_FIELD = "manname";
	public static final String ALTID_FIELD = "altid";
	public static final String IDNO_FIELD = "idno";
	public static final String COLLECTION_FIELD = "collection";
	public static final String REPOSITORY_FIELD = "repository";
	public static final String INSTITUTION_FIELD = "institution";
	public static final String SETTLEMENT_FIELD = "settlement";
	public static final String REGION_FIELD = "region";
	public static final String COUNTRY_FIELD = "country";
	public static final String MANTITLE_FIELD = "mantitle";
	public static final String CANVAS_TITLE_FIELD = "canvastitle";
	public static final String RESULT_TYPE_FIELD = "resulttype";
	public static final String ANNOTATION_TEXT_FIELD = "annotationtext";

	DMSTechRDFConstants sharedCanvasConstants = DMSTechRDFConstants.getInstance();

	 static Logger logger = Logger.getLogger(SharedCanvasSOLRIndexer.class);

	public static void main(String args[]) {
		 BasicConfigurator.configure();
	     logger.setLevel(Level.ERROR);
	     Config config = new Config();
	 	config.initializeThisConfig();
		SharedCanvasSOLRIndexer indexer = new SharedCanvasSOLRIndexer();
		try {
			indexer.indexManuscript("http://localhost:8080/ingested/myTestManu/Manifest");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SolrServerException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteAll() throws SolrServerException, IOException {
		SolrServer server = new CommonsHttpSolrServer(Config.solrServer);
		server.deleteByQuery( "*:*" );  //delete everything in solr
		server.commit();
	}
	public void reindexAllLocalDataInSolr() throws SolrServerException, IOException {
		
		SolrServer server = new CommonsHttpSolrServer(Config.solrServer);
		server.deleteByQuery( "*:*" );  //delete everything in solr
		Model tdb = (new SharedCanvasTDBManager()).loadMainTDBDataset();
		List<Resource> manuscripts = tdb.listResourcesWithProperty(RDF.type, sharedCanvasConstants.scManifestClass).toList();		
		indexManuscripts(manuscripts, tdb, server);
	
	}
	
	public void indexManuscript(String manuscriptURI) throws IOException, SolrServerException {

		SolrServer server = new CommonsHttpSolrServer(Config.solrServer);
		Model tdb = (new SharedCanvasTDBManager()).loadMainTDBDataset();
		List<Resource> manuscript = new ArrayList<Resource>();
		Resource manResource = tdb.createResource(manuscriptURI);
		// can't index it if it isn't in the store
		if (tdb.containsResource(manResource)) {
			manuscript.add(manResource);	
			indexManuscripts(manuscript, tdb, server);
		}
		
		
	}
	
	private void indexManuscripts(List<Resource> manuscripts, Model tdb, SolrServer server) throws IOException, SolrServerException {
		
		Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
		/*
		 * 1.  get all manuscript resources
		 */
		 int count = 0;
		
		for (Resource manuscriptRes : manuscripts) {
			 	
			/*
			 * 2.  get and index the  metadata from each manuscript resource
			 */
			SolrInputDocument manuscriptSolrDoc = createSOLRDocForManuscriptResource(tdb, manuscriptRes);
			
			docs.add( manuscriptSolrDoc );
			/*
			 * 3.  get the uri for the canvas aggregation from the manifest
			 */
			 
			Resource sequenceAggregation = null;
			StmtIterator aggregatesIter = manuscriptRes.listProperties(sharedCanvasConstants.oreAggregates);
			while (aggregatesIter.hasNext()) { 
				Resource object = aggregatesIter.next().getObject().asResource();
				if (object.hasProperty(RDF.type, sharedCanvasConstants.scSequenceClass)) {
					sequenceAggregation = object;
					break;
				}
			}
			
			/*
			 * 4.  loop over the canvas list, if there is one
			 */

			if (sequenceAggregation != null) {
				StmtIterator canvasIter = sequenceAggregation.listProperties(sharedCanvasConstants.oreAggregates);
			
			while (canvasIter.hasNext()) {
				Resource canvas = canvasIter.next().getObject().asResource();
				if (! canvas.hasProperty(RDF.type, sharedCanvasConstants.scCanvasClass)) continue;
				SolrInputDocument canvasSolrDoc = new SolrInputDocument();
				canvasSolrDoc.addField(RESULT_TYPE_FIELD, RESULT_TYPE_CANVAS);
				canvasSolrDoc.addField(URI_FIELD, canvas.getURI());
				canvasSolrDoc.addField(CANVAS_TITLE_FIELD, canvas.getPropertyResourceValue(DC.title));
				addManuscriptMetadataToSolrDoc(tdb, manuscriptRes, canvasSolrDoc);
				
				/* 
				  * 5.  for each canvas, get all TextAnnotations that target the canvas.
				  */
				StmtIterator annotationIter = tdb.listStatements(null, sharedCanvasConstants.oacHasTargetProperty, canvas);
				while (annotationIter.hasNext()) {
					Resource annotation = annotationIter.next().getSubject();
					// TODO:  replace this with SPARQL query on TDB to avoid type check
					if (! annotation.hasProperty(RDF.type, sharedCanvasConstants.oacTextAnnotationType)) continue;
						/*
					  *  6.  for each TextAnnotation, get the Body.
					  */
					Resource annotationBody = annotation.getPropertyResourceValue(sharedCanvasConstants.oacHasBodyProperty);
					 /* 
					  * 7.  if inline body, then index the text immediately, otherwise get the text from the body's URI
					 */
					String bodyText = null;
					if (annotationBody.hasProperty(RDF.type, sharedCanvasConstants.cntRestProperty)) {
						bodyText = annotationBody.getProperty(sharedCanvasConstants.cntRestProperty).getString();				
					} else {
						bodyText = RDFUtils.getTextFromURI(annotationBody.getURI());
					}	
					// annotation_text is a multi value field so we can add as many 'instances' of the field as we like
					canvasSolrDoc.addField(ANNOTATION_TEXT_FIELD, bodyText);
				}
				docs.add(canvasSolrDoc);
			}
			}
			
			count++;

		}
		if (! docs.isEmpty()) {
			server.add( docs );
			server.commit();
		}

		System.out.println(count + " docs indexed.");
		
	}


	private SolrInputDocument createSOLRDocForManuscriptResource(Model tdb, Resource manuscriptRes) {
		SolrInputDocument manuscriptSolrDoc = new SolrInputDocument();
		manuscriptSolrDoc.addField(URI_FIELD, manuscriptRes.getURI());
		manuscriptSolrDoc.addField(RESULT_TYPE_FIELD, RESULT_TYPE_MANUSCRIPT);
		addManuscriptMetadataToSolrDoc(tdb, manuscriptRes, manuscriptSolrDoc);
		return manuscriptSolrDoc;
	}

	private void addManuscriptMetadataToSolrDoc(Model tdb, Resource manuscriptRes,
			SolrInputDocument solrDoc) {
		DMSTechRDFConstants sharedCanvasConstants = DMSTechRDFConstants.getInstance();
		
		addObjectsStringValueToDoc(tdb, manuscriptRes, DC.title, solrDoc, MANTITLE_FIELD);
		addObjectsStringValueToDoc(tdb, manuscriptRes, sharedCanvasConstants.teiCountryProperty, solrDoc, COUNTRY_FIELD);
		addObjectsStringValueToDoc(tdb, manuscriptRes, sharedCanvasConstants.teiRegionProperty, solrDoc, REGION_FIELD);
		addObjectsStringValueToDoc(tdb, manuscriptRes, sharedCanvasConstants.teiSettlementProperty, solrDoc, SETTLEMENT_FIELD);
		addObjectsStringValueToDoc(tdb, manuscriptRes, sharedCanvasConstants.teiInstitutionProperty, solrDoc, INSTITUTION_FIELD);
		addObjectsStringValueToDoc(tdb, manuscriptRes, sharedCanvasConstants.teiRepositoryProperty, solrDoc, REPOSITORY_FIELD);
		addObjectsStringValueToDoc(tdb, manuscriptRes, sharedCanvasConstants.teiCollectionProperty, solrDoc, COLLECTION_FIELD);
		addObjectsStringValueToDoc(tdb, manuscriptRes, sharedCanvasConstants.teiIdnoProperty, solrDoc, IDNO_FIELD);
		addObjectsStringValueToDoc(tdb, manuscriptRes, sharedCanvasConstants.teiAltIdentifierProperty, solrDoc, ALTID_FIELD);
		addObjectsStringValueToDoc(tdb, manuscriptRes, sharedCanvasConstants.teiMsNameProperty, solrDoc, MANNAME_FIELD);
	}
	
	private void addObjectsStringValueToDoc(Model tdb, Resource subject, Property predicate, SolrInputDocument doc, String fieldName) {
		
		StmtIterator stmtIter = tdb.listStatements(subject, predicate, (RDFNode) null);
		if (stmtIter.hasNext()) 
			doc.addField(fieldName, stmtIter.nextStatement().getObject().asLiteral().getLexicalForm());
		
	}
	

	public void indexCanvasText(HashMap<String, List<String>> canvasTextToIndex) throws Exception {
			
		try {
			SolrServer server = new CommonsHttpSolrServer(Config.solrServer);	
			Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
			
			for (Map.Entry<String, List<String>> entry: canvasTextToIndex.entrySet()) {
				SolrInputDocument newCanvasSolrDoc = null;
				String canvasURI = entry.getKey();
				List<String> textsToAdd = entry.getValue();
				// get the existing solr doc for the canvas
				SolrQuery query = new SolrQuery();
			    query.setQuery( "uri:" + ClientUtils.escapeQueryChars(canvasURI));
			    SolrDocumentList docResultList = server.query( query ).getResults();
			    if (docs.size() == 1) {
			    	// we only index if we have a matching canvas
			    	SolrDocument oldCanvasSolrDoc = docResultList.get(0);
			    	// copy the old solr doc to a new input doc
			    	newCanvasSolrDoc = org.apache.solr.client.solrj.util.ClientUtils.toSolrInputDocument(oldCanvasSolrDoc);
			  			    
			    	for (String someText : textsToAdd) {
			    		newCanvasSolrDoc.addField(ANNOTATION_TEXT_FIELD, someText);
			    	}
				
			    	// this will replace the old doc in solr
			    	docs.add(newCanvasSolrDoc);
			    }
			}
			if (!docs.isEmpty()) {
				server.add( docs );
				server.commit();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
