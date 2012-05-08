package edu.stanford.dmstech.vm.uriresolvers;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;
import org.json.JSONException;

import edu.stanford.dmstech.vm.Config;
import edu.stanford.dmstech.vm.indexing.SharedCanvasSOLRIndexer;
import edu.stanford.dmstech.vm.indexing.SharedCanvasTDBManager;

public class GeneralTests {

	/*
	 * Also want to manually delete any generated shared canvas files.  could do 
	 * that here, but too dangerous.
	 */
	private void test() throws JSONException, Exception  {
		//BasicConfigurator.configure();
		Config config = new Config();
		config.initializeThisConfig();
		new SharedCanvasTDBManager().reindexAllLocalRDFData();
		new SharedCanvasSOLRIndexer().reindexAllLocalDataInSolr();
		
		OptimizedManuscriptPageList pageListGenerator = new OptimizedManuscriptPageList();
		String collectionId = "Stanford";
		String manuscriptId = "kq131cs7229";
		String sequenceId = "OriginalSequence";
		String sequenceURI = "http://localhost:8080/dms/sc/Stanford/kq131cs7229/sequences/OriginalSequence";
		
		final long startTime = System.nanoTime();
		final long endTime;
		String modelResult;
		
		try {
			modelResult = pageListGenerator.resultFromModel(collectionId, manuscriptId, sequenceId);
		} finally {
		  endTime = System.nanoTime();
		}
		final long duration = endTime - startTime;
		System.out.println("From model took: " + (double)duration / 1000000000.0 + " seconds.");
		
		final long startTime2 = System.nanoTime();
		final long endTime2;
		String sparqlResult;
		try {
			sparqlResult = pageListGenerator.resultFromSPARQL(sequenceURI);
		} finally {
		  endTime2 = System.nanoTime();
		}
		final long duration2 = endTime2 - startTime2;
		System.out.println("From sparql took: " + (double)duration2 / 1000000000.0 + " seconds.");
		
		System.out.println("MODEL RESULT:  ");
		System.out.println(modelResult);
		
		System.out.println("\n\n\nSPARQL RESULT:  ");
		System.out.println(sparqlResult);
		
		//new AnnotationIngester().test();
	//	new SharedCanvasGenerator().ingestTestManu();
	//	new SharedCanvasTDBManager().emptyMainTDBIndex();
	// SharedCanvasTDBManager().tdbListAllStmtsTest();
	//	System.out.println("Should be empty above this.");
	//	boolean reindexSolr = true;
	//	boolean reindexTripleStore = true;
	//	new ReindexRequest().getSparqlResult(reindexSolr, reindexTripleStore);
		
	//	new SharedCanvasTDBManager().tdbListAllStmtsTest();
	//	String result = SharedCanvasUtil.queryTest();
	//	System.out.println(result);
		
//		Model model;
//		try {
//			model = RDFUtils.loadModelInAbsoluteDir("/Users/jameschartrand/SHARED_CANVAS_HOME/collections/Stanford/kq131cs7229/rdf/sequences/OriginalSequence.xml", "RDF/XML");
//			RDFUtils.serializeModelToFile(model, "/Users/jameschartrand/SHARED_CANVAS_HOME/collections/Stanford/kq131cs7229/rdf/sequences/OriginalSequence.nt", "N-TRIPLE");
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		}
	
	public static void main(String[] args) throws IOException, SolrServerException {
		
		GeneralTests tester = new GeneralTests();
		
		try {
			tester.test();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		}
}
