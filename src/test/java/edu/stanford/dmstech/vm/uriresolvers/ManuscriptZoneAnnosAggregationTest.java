package edu.stanford.dmstech.vm.uriresolvers;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.endsWith;

import org.junit.Test;

public class ManuscriptZoneAnnosAggregationTest {

	@Test
	public void testRedirectToXML() {
		 given().
		 	redirects().follow(false).
         	header("Accept", "application/rdf+xml").
         expect().
         	statusCode(303).
         	header("Location", endsWith("/vm/sc/Stanford/kq131cs7229/ZoneAnnotations.xml")).
         when().
         get("/vm/sc/Stanford/kq131cs7229/ZoneAnnotations");
	}
	
	@Test
	public void testRedirectToTurtle() {
		 given().
		 	redirects().follow(false).
         	header("Accept", "text/turtle;charset=utf-8").
         expect().
         	statusCode(303).
         	header("Location", endsWith("/vm/sc/Stanford/kq131cs7229/ZoneAnnotations.ttl")).
         when().
         get("/vm/sc/Stanford/kq131cs7229/ZoneAnnotations");
	}
	
	@Test
	public void testRedirectToHTML() {
		 given().
		 	redirects().follow(false).
         	header("Accept", "text/html;charset=utf-8").
         expect().
         	statusCode(303).
         	header("Location", endsWith("/vm/sc/Stanford/kq131cs7229/ZoneAnnotations.html")).
         when().
         get("/vm/sc/Stanford/kq131cs7229/ZoneAnnotations");
	}
	

	
	

}