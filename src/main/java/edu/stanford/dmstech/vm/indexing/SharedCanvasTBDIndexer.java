package edu.stanford.dmstech.vm.indexing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.shared.Lock;
import com.hp.hpl.jena.sparql.core.Quad;
import com.hp.hpl.jena.tdb.StoreConnection;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.tdb.TDBLoader;
import com.hp.hpl.jena.tdb.base.file.Location;
import com.hp.hpl.jena.tdb.store.DatasetGraphTDB;
import com.hp.hpl.jena.tdb.transaction.DatasetGraphTxn;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;

import edu.stanford.dmstech.vm.Config;
import edu.stanford.dmstech.vm.DMSTechRDFConstants;
import edu.stanford.dmstech.vm.manuscriptgeneration.nTripleSourceFileFilter;
import gov.lanl.adore.djatoka.util.IOUtils;

public class SharedCanvasTBDIndexer {

	public void reindexAllLocalData() throws SolrServerException, IOException {
		 Model tdb = loadTBDDataset(Config.getAbsolutePathToMainTBDDir());		    
		recursivelyIndexAllNtripleFilesIn(Config.baseDirForCollections, tdb);
		reindexAllLocalDataInSolr();
	}
	
	public void recursivelyIndexAllNtripleFilesIn(String directoryPath, Model tdb) {
		FileFilter nTripleSourceFileFilter = new nTripleSourceFileFilter();
		ArrayList<File> files = IOUtils.getFileList(directoryPath, nTripleSourceFileFilter, false);
		for (File f : files) {
			if (f.isDirectory()) recursivelyIndexAllNtripleFilesIn(f.getAbsolutePath(), tdb);
			FileManager.get().readModel( tdb, f.getAbsolutePath());		
		}
	}

	private void addObjectsStringValueToDoc(Resource subject, Property predicate, SolrInputDocument doc, String fieldName) {
		doc.addField(fieldName, subject.getPropertyResourceValue(predicate).asLiteral().getLexicalForm());
	}
	
	public void reindexAllLocalDataInSolr() throws SolrServerException, IOException {
		DMSTechRDFConstants sharedCanvasConstants = DMSTechRDFConstants.getInstance();
		SolrServer server = new CommonsHttpSolrServer("http://localhost:8080/solr-sharedcanvas/");
		server.deleteByQuery( "*:*" );  //delete everything in solr
		
		Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
		Model tdb = loadMainTDBDataset();
		
		/*
		 * 1.  get all manuscript resources
		 */
		 int count = 0;
		ResIterator manuscriptIter = tdb.listResourcesWithProperty(RDF.type, sharedCanvasConstants.manifestClass);
		while (manuscriptIter.hasNext()) {
			 	
			Resource manuscriptRes = manuscriptIter.next();	
			/*
			 * 2.  get and index the  metadata from each manuscript resource
			 */
			SolrInputDocument manuscriptSolrDoc = new SolrInputDocument();
			manuscriptSolrDoc.addField("id", manuscriptRes.getURI());
			addObjectsStringValueToDoc(manuscriptRes, DC.title, manuscriptSolrDoc, "mantitle");
			addObjectsStringValueToDoc(manuscriptRes, sharedCanvasConstants.teiCountryProperty, manuscriptSolrDoc, "country");
			addObjectsStringValueToDoc(manuscriptRes, sharedCanvasConstants.teiRegionProperty, manuscriptSolrDoc, "region");
			addObjectsStringValueToDoc(manuscriptRes, sharedCanvasConstants.teiSettlementProperty, manuscriptSolrDoc, "settlement");
			addObjectsStringValueToDoc(manuscriptRes, sharedCanvasConstants.teiInstitutionProperty, manuscriptSolrDoc, "institution");
			addObjectsStringValueToDoc(manuscriptRes, sharedCanvasConstants.teiRepositoryProperty, manuscriptSolrDoc, "repository");
			addObjectsStringValueToDoc(manuscriptRes, sharedCanvasConstants.teiCollectionProperty, manuscriptSolrDoc, "collection");
			addObjectsStringValueToDoc(manuscriptRes, sharedCanvasConstants.teiIdnoProperty, manuscriptSolrDoc, "idno");
			addObjectsStringValueToDoc(manuscriptRes, sharedCanvasConstants.teiAltIdentifierProperty, manuscriptSolrDoc, "altid");
			addObjectsStringValueToDoc(manuscriptRes, sharedCanvasConstants.teiMsNameProperty, manuscriptSolrDoc, "manname");
			docs.add( manuscriptSolrDoc );
			/*
			 * 3.  get the canvas list from the manuscript
			 */
			 
			Resource sequenceAggregation = null;
			StmtIterator aggregatesIter = manuscriptRes.listProperties(sharedCanvasConstants.oreAggregates);
			while (aggregatesIter.hasNext()) { 
				Resource object = aggregatesIter.next().getObject().asResource();
				if (object.hasProperty(RDF.type, sharedCanvasConstants.sequenceClass)) {
					sequenceAggregation = object;
					break;
				}
			}
			
			/*
			 * 4.  loop over the canvas list
			 */
			StmtIterator canvasIter = sequenceAggregation.listProperties(sharedCanvasConstants.oreAggregates);
			while (canvasIter.hasNext()) {
				Resource canvas = canvasIter.next().getObject().asResource();
				if (! canvas.hasProperty(RDF.type, sharedCanvasConstants.dmsCanvasClass)) continue;
				SolrInputDocument canvasSolrDoc = new SolrInputDocument(); 
				/* 
				  * 5.  for each canvas, get all TextAnnotations that target the canvas.
				  */
				StmtIterator annotationIter = tdb.listStatements(null, sharedCanvasConstants.oacHasTargetProperty, (RDFNode) null);
				while (annotationIter.hasNext()) {
					Resource annotation = annotationIter.next().getSubject();
					if (! annotation.hasProperty(RDF.type, sharedCanvasConstants.oacTextAnnotationType)) continue;
					/*
					  *  6.  for each TextAnnotation, get the Body.
					  */
					Resource annotationBody = annotation.getPropertyResourceValue(sharedCanvasConstants.oacHasBodyProperty);
					 /* 
					  * 7.  if inline body, then index the text immediately, otherwise get the text from the body's URI
					 */
					String bodyText = null;
					if (annotationBody.hasProperty(RDF.type, sharedCanvasConstants.cntAsTxtType)) {
						bodyText = annotationBody.getProperty(sharedCanvasConstants.cntRestProperty).getString();				
					} else {
						bodyText = getTextFromURI(annotationBody.getURI());
					}	
					// annotation_text is a multi value field so we can add as many 'instances' of the field as we like
					canvasSolrDoc.addField("annotation_text", bodyText);
				}
				docs.add(canvasSolrDoc);
			}										
			
			count++;

		}

		server.add( docs );
		server.commit();

		System.out.println(count + " docs indexed.");
	}
	
	
	
	private String getTextFromURI(String uri) throws IOException {
	     return (String) new URL(uri).getContent();
	}

	/**
	 * 
	 */
	public void loadDatasetInTransaction() {
		FileManager fm = FileManager.get();
	
      //  fm.addLocatorClassLoader(ExampleTDB_03.class.getClassLoader());
        InputStream in = fm.open("/Users/jameschartrand/STANFORD_KVM_HOME/manuscripts/Stanford/rdf/data.nt");

        Location location = new Location ("/Users/jameschartrand/STANFORD_KVM_HOME/manuscripts/Stanford/tdb");
        StoreConnection sConn = StoreConnection.make(location);

        DatasetGraphTxn dsgTx = null;
        try {
            dsgTx = sConn.begin(ReadWrite.WRITE);
            TDBLoader.load(dsgTx, in, true);
        } catch (Exception e) {
        	e.printStackTrace();
        	System.out.println("yes exception");
            if ( dsgTx != null ) dsgTx.abort();
        } finally {
            if ( dsgTx != null ) dsgTx.commit();
        }       


        try {
            dsgTx = sConn.begin(ReadWrite.READ);
            Iterator<Quad> iter = dsgTx.find();
            while ( iter.hasNext() ) {
                Quad quad = iter.next();
                System.out.println(quad);
            }
        } catch (Exception e) {
            if ( dsgTx != null ) dsgTx.abort();
        } finally {
            if ( dsgTx != null ) dsgTx.commit();
        }       

	}
	
	public  void loadIntoDatasetUsingLock(String datasetPath, String sourcePath ) {
        FileManager fm = FileManager.get();
     //   fm.addLocatorClassLoader(ExampleTDB_01.class.getClassLoader());
       // InputStream in = fm.open("data/data.nt");
        InputStream in = fm.open(sourcePath);

        Location location = new Location (datasetPath);
        DatasetGraphTDB dsg = (DatasetGraphTDB)TDBFactory.createDatasetGraph(location);

        TDBLoader.load(dsg, in, false);
        
        Lock lock = dsg.getLock();
        lock.enterCriticalSection(Lock.READ);
        try {
            Iterator<Quad> iter = dsg.find();
            while ( iter.hasNext() ) {
                Quad quad = iter.next();
                System.out.println(quad);
            }
        } finally {
            lock.leaveCriticalSection();
        }

        dsg.close();
    }

	public void tdbTest() {
		String tbdDir = "/Users/jameschartrand/STANFORD_KVM_HOME/repository_tbd_index/";
	//	String source = "/Users/jameschartrand/STANFORD_KVM_HOME/testImages/rdf/ImageAnnotations.nt";
		  
	//	loadFileIntoTBDModel(tbdDir, source);
		
		Model tdbModel = loadTBDDataset(tbdDir);
		
	//   String queryString = "PREFIX sc: <http://www.shared-canvas.org/ns/> SELECT * WHERE { ?x sc:hasTarget ?y }";
		String queryString = "PREFIX oac: <http://www.openannotation.org/ns/> SELECT * WHERE { ?x oac:hasTarget ?y }";
	 //  printQueryResultToConsole(queryString, tdbModel);
	 
	    listAllStatementsInModelToConsole(tdbModel);	   
}
	
	private void listAllStatementsInModelToConsole(Model model) {
		 StmtIterator iter = model.listStatements();
		  printStatementsToConsole(iter); 
	}
	
	public void loadFileIntoMainRepoTBDDataset( String rdfSourcePath) {
		FileManager.get().readModel( loadMainTDBDataset(), rdfSourcePath);
	}
	
	public void loadFileIntoTBDModel(String tbdDatasetPath, String rdfFilePath) {
		   Model tdb = loadTBDDataset(tbdDatasetPath);
		    FileManager.get().readModel( tdb, rdfFilePath);
	}
	
	private Model loadMainTDBDataset() {
		return loadTBDDataset(Config.getAbsolutePathToMainTBDDir());
	}
	
	private Model loadTBDDataset(String tbdDatasetPath) {
		Dataset dataset = TDBFactory.createDataset(tbdDatasetPath);
		  Model tdb = dataset.getDefaultModel();
		return tdb;
	}
	
	private void printQueryResultToConsole(String queryString, Model model) {
			Query query = QueryFactory.create(queryString);

		   QueryExecution qe = QueryExecutionFactory.create(query, model);
		   ResultSet results = qe.execSelect();

		   ResultSetFormatter.out(System.out, results, query);

		   qe.close();
	}
	
	private void printStatementsToConsole(StmtIterator iter) {
		while (iter.hasNext()) {
			    Statement stmt      = iter.nextStatement();  
			    Resource  subject   = stmt.getSubject();     
			    Property  predicate = stmt.getPredicate();   
			    RDFNode   object    = stmt.getObject();      

			    System.out.print(subject.toString());
			    System.out.print(" " + predicate.toString() + " ");
			    
			    if (object instanceof Resource) {
			       System.out.print(object.toString());
			    } else {
			        // literal
			        System.out.print(" \"" + object.toString() + "\"");
			    }

			    System.out.println(" .");
			}
	}
	public static void main(String[] args) throws IOException {
		
		(new SharedCanvasTBDIndexer()).tdbTest();
		  
		 }
}
