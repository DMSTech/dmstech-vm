package edu.stanford.dmstech.vm.uriresolvers;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.endsWith;

import org.junit.Test;

public class ManuscriptManifestAggregationTest {

	@Test
	public void testManifestRedirectToXML() {
		 given().
		 	redirects().follow(false).
         	header("Accept", "application/rdf+xml").
         expect().
         	statusCode(303).
         	header("Location", endsWith("/dms/sc/Stanford/kq131cs7229/Manifest.xml")).
         when().
         get("/dms/sc/Stanford/kq131cs7229/Manifest");
	}
	
	@Test
	public void testManifestRedirectToTurtle() {
		 given().
		 	redirects().follow(false).
         	header("Accept", "text/turtle;charset=utf-8").
         expect().
         	statusCode(303).
         	header("Location", endsWith("/dms/sc/Stanford/kq131cs7229/Manifest.ttl")).
         when().
         get("/dms/sc/Stanford/kq131cs7229/Manifest");
	}
	
	@Test
	public void testManifestRedirectToHTML() {
		 given().
		 	redirects().follow(false).
         	header("Accept", "text/html;charset=utf-8").
         expect().
         	statusCode(303).
         	header("Location", endsWith("/dms/sc/Stanford/kq131cs7229/Manifest.html")).
         when().
         get("/dms/sc/Stanford/kq131cs7229/Manifest");
	}
	

	
	

}
