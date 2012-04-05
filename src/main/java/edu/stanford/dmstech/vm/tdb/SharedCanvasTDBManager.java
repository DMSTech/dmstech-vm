package edu.stanford.dmstech.vm.tdb;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.solr.client.solrj.SolrServerException;

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
import edu.stanford.dmstech.vm.manuscriptgeneration.nTripleSourceFileFilter;
import gov.lanl.adore.djatoka.util.IOUtils;

public class SharedCanvasTDBManager {

	public void reindexAllLocalRDFData() throws IOException {
		
		 Model tdb = loadTDBDataset(Config.getAbsolutePathToMainTBDDir());	
		 tdb.removeAll();
		recursivelyIndexAllNtripleFilesIn(Config.getAbsolutePathToCollectionsDir(), tdb);
		recursivelyIndexAllNtripleFilesIn(Config.getAbsolutePathToTextAnnosDir(), tdb);
		recursivelyIndexAllNtripleFilesIn(Config.getAbsolutePathToTransactionsDir(), tdb);	
	}
	

	
	public void recursivelyIndexAllNtripleFilesIn(String directoryPath, Model tdb) {
		FileFilter nTripleSourceFileFilter = new nTripleSourceFileFilter();
		ArrayList<File> files = IOUtils.getFileList(directoryPath, nTripleSourceFileFilter, false);
		for (File f : files) {
			if (f.isDirectory()) recursivelyIndexAllNtripleFilesIn(f.getAbsolutePath(), tdb);
			FileManager.get().readModel( tdb, f.getAbsolutePath());		
		}
	}

	public void indexFileListInMainDataset(List<String> fileList) {
		Model mainTBDDataset = loadMainTDBDataset();
		for (String filePath : fileList) {			
			FileManager.get().readModel( mainTBDDataset, filePath);
		}
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
	
	Model tdbModel = loadTDBDataset(tbdDir);
	
//   String queryString = "PREFIX sc: <http://www.shared-canvas.org/ns/> SELECT * WHERE { ?x sc:hasTarget ?y }";
//	String queryString = "PREFIX oac: <http://www.openannotation.org/ns/> SELECT * WHERE { ?x oac:hasTarget ?y }";
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
	   Model tdb = loadTDBDataset(tbdDatasetPath);
	    FileManager.get().readModel( tdb, rdfFilePath);
}

public Model loadMainTDBDataset() {
	return loadTDBDataset(Config.getAbsolutePathToMainTBDDir());
}

private Model loadTDBDataset(String tbdDatasetPath) {
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
	

	//BasicConfigurator.configure();
	Config config = new Config();
	config.initializeThisConfig();
		
		SharedCanvasTDBManager sharedCanvasTDBManager = new SharedCanvasTDBManager();
		
		sharedCanvasTDBManager.reindexAllLocalRDFData();
		sharedCanvasTDBManager.tdbTest();
		
	
	  
	 }

}