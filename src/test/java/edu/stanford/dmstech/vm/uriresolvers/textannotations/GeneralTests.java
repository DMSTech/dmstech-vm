package edu.stanford.dmstech.vm.uriresolvers.textannotations;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;

import edu.stanford.dmstech.vm.Config;
import edu.stanford.dmstech.vm.indexing.SharedCanvasSOLRIndexer;
import edu.stanford.dmstech.vm.indexing.SharedCanvasTDBManager;
import edu.stanford.dmstech.vm.manuscriptgeneration.SharedCanvasGenerator;
import edu.stanford.dmstech.vm.uriresolvers.ingest.AnnotationIngester;

public class GeneralTests {

	/*
	 * Also want to manually delete any generated shared canvas files.  could do 
	 * that here, but too dangerous.
	 */
	private void test() throws SolrServerException, IOException {
		//BasicConfigurator.configure();
		Config config = new Config();
		config.initializeThisConfig();
	//	new SharedCanvasGenerator().ingestTestManu();
		new AnnotationIngester().test();
		
//		new SharedCanvasTDBManager().reindexAllLocalRDFData();
//		new SharedCanvasSOLRIndexer().reindexAllLocalDataInSolr();
	}
	
	public static void main(String[] args) throws IOException, SolrServerException {
		
		GeneralTests tester = new GeneralTests();
		tester.test();

		}
}
