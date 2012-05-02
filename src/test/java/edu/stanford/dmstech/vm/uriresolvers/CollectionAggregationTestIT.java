package edu.stanford.dmstech.vm.uriresolvers;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.endsWith;

import org.junit.Test;

public class CollectionAggregationTestIT {

	@Test
	public void testManifestRedirectToXML() {
		 given().
		 	redirects().follow(false).
         	header("Accept", "application/rdf+xml").
         expect().
         	statusCode(303).
         	header("Location", endsWith("/dms/sc/Stanford/Collection.xml")).
         when().
         get("/dms/sc/Stanford/Collection");
	}
	
	@Test
	public void testManifestRedirectToTurtle() {
		 given().
		 	redirects().follow(false).
         	header("Accept", "text/turtle;charset=utf-8").
         expect().
         	statusCode(303).
         	header("Location", endsWith("/dms/sc/Stanford/Collection.ttl")).
         when().
         get("/dms/sc/Stanford/Collection");
	}
	
	@Test
	public void testManifestRedirectToHTML() {
		 given().
		 	redirects().follow(false).
         	header("Accept", "text/html;charset=utf-8").
         expect().
         	statusCode(303).
         	header("Location", endsWith("/dms/sc/Stanford/Collection.html")).
         when().
         get("/dms/sc/Stanford/Collection");
	}
	

	
	

}
