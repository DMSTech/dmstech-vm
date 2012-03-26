package edu.stanford.dmstech.vm.uriresolvers.search;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

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
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.sparql.core.Quad;
import com.hp.hpl.jena.tdb.StoreConnection;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.tdb.TDBLoader;
import com.hp.hpl.jena.tdb.base.file.Location;
import com.hp.hpl.jena.tdb.store.DatasetGraphTDB;
import com.hp.hpl.jena.tdb.transaction.DatasetGraphTransaction;
import com.hp.hpl.jena.tdb.transaction.DatasetGraphTxn;
import com.hp.hpl.jena.util.FileManager;

public class AnnotationQuery {

	/**
	 * 
	 */
	public void anotherDatasetTest() {
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
	public  void datasetTest() {
        FileManager fm = FileManager.get();
     //   fm.addLocatorClassLoader(ExampleTDB_01.class.getClassLoader());
       // InputStream in = fm.open("data/data.nt");
        InputStream in = fm.open("/Users/jameschartrand/STANFORD_KVM_HOME/manuscripts/testImages/rdf/ImageAnnotations.xml");

        Location location = new Location ("/Users/jameschartrand/STANFORD_KVM_HOME/manuscripts/Stanford/tdb");
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
	 String tdbDir = "/Users/jameschartrand/STANFORD_KVM_HOME/manuscripts/Stanford/tdb/";
	   Dataset dataset = TDBFactory.createDataset(tdbDir);
	  
	   Model tdb = dataset.getDefaultModel();
	  // String source = "/Users/jameschartrand/STANFORD_KVM_HOME/testImages/rdf/ImageAnnotations.nt";
	 //  String source = "/Users/jameschartrand/STANFORD_KVM_HOME/manuscripts/Stanford/rdf/data.nt";
	 //  FileManager.get().readModel( tdb, source);
	   String queryString = "PREFIX foaf: <http://xmlns.com/foaf/0.1/> SELECT * WHERE { ?x foaf:knows ?y }";
	  StmtIterator iter = tdb.listStatements();
	  System.out.print("should see the statements here");
	  while (iter.hasNext()) {
		    Statement stmt      = iter.nextStatement();  // get next statement
		    Resource  subject   = stmt.getSubject();     // get the subject
		    Property  predicate = stmt.getPredicate();   // get the predicate
		    RDFNode   object    = stmt.getObject();      // get the object

		    System.out.print(subject.toString());
		    System.out.print(" " + predicate.toString() + " ");
		    
		    if (object instanceof Resource) {
		       System.out.print(object.toString());
		    } else {
		        // object is a literal
		        System.out.print(" \"" + object.toString() + "\"");
		    }

		    System.out.println(" .");
		} 
	  
	   Query query = QueryFactory.create(queryString);

	   QueryExecution qe = QueryExecutionFactory.create(query, tdb);
	   ResultSet results = qe.execSelect();

	   ResultSetFormatter.out(System.out, results, query);

	   qe.close();
}
	public static void main(String[] args) throws IOException {
		
		(new AnnotationQuery()).tdbTest();
		  
		 }
	
}
