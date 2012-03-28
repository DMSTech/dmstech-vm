package edu.stanford.dmstech.vm.indexing;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import org.junit.internal.matchers.Each;

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

import edu.stanford.dmstech.vm.Config;
import edu.stanford.dmstech.vm.uriresolvers.search.AnnotationQuery;

public class SharedCanvasTBDIndexer {

	public void reindexAllLocalData() {
		probably want this to be callable from a URI
		
		loop over default collection dir, recursively opening directories and indexing any .nt files in jena
		loop over packaged collection dir, recursively opening directories and indexing any .nt file in jena
		for each of the two above, also check to see if any of the files are Manifest files and if so,
		open a model for them, get the metdata and indexing in solr.
		
		loop over the submitted_annotations dir, indexing all .nt files in jena
			also open each in a model and get the uuid for the text body, then get the text body from the 
			annotation_body_texts directory and index it in solr record with fields:
					CollectionId, ManuscriptId, canvasId, annotationId, annotationText, collectionName, collectionLocation, etc.?
		
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
		loadFileIntoTBDModel(Config.getAbsolutePathToMainTBDDir(), rdfSourcePath);
	}
	
	public void loadFileIntoTBDModel(String tbdDatasetPath, String rdfFilePath) {
		   Model tdb = loadTBDDataset(tbdDatasetPath);
		    FileManager.get().readModel( tdb, rdfFilePath);
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
