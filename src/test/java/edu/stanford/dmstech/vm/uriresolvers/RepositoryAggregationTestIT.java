package edu.stanford.dmstech.vm.uriresolvers;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.endsWith;

import org.junit.Test;

public class RepositoryAggregationTestIT {

	@Test
	public void testManifestRedirectToXML() {
		 given().
		 	redirects().follow(false).
         	header("Accept", "application/rdf+xml").
         expect().
         	statusCode(303).
         	header("Location", endsWith("/dms/sc/Repository.xml")).
         when().
         get("/dms/sc/Repository");
	}
	
	@Test
	public void testManifestRedirectToTurtle() {
		 given().
		 	redirects().follow(false).
         	header("Accept", "text/turtle;charset=utf-8").
         expect().
         	statusCode(303).
         	header("Location", endsWith("/dms/sc/Repository.ttl")).
         when().
         get("/dms/sc/Repository");
	}
	
	@Test
	public void testManifestRedirectToHTML() {
		 given().
		 	redirects().follow(false).
         	header("Accept", "text/html;charset=utf-8").
         expect().
         	statusCode(303).
         	header("Location", endsWith("/dms/sc/Repository.html")).
         when().
         get("/dms/sc/Repository");
	}
	

	
	

}
