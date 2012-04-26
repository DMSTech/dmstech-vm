package edu.stanford.dmstech.vm.uriresolvers.search;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.endsWith;

import org.junit.Test;

public class ReindexRequestTest {

	
	
	@Test
	public void reindexSolrAndTripleStore() {
		 given().	      
	       formParam("solr", "true").
	       formParam("triple", "true").
         expect().
         	statusCode(200).
         when().
         post("/dms/sc/reindex");
	}

	@Test
	public void reindexSolr() {
		 given().	      
	       formParam("solr", "true").
	       formParam("triple", "false").
         expect().
         	statusCode(200).
         when().
         post("/dms/sc/reindex");
	}
	
	@Test
	public void reindexTripleStore() {
		 given().	      
	       formParam("solr", "false").
	       formParam("triple", "true").
         expect().
         	statusCode(200).
         when().
         post("/dms/sc/reindex");
	}
	

}
