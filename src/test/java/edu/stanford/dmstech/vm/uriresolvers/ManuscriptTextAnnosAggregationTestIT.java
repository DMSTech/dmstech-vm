package edu.stanford.dmstech.vm.uriresolvers;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.endsWith;

import org.junit.Test;

public class ManuscriptTextAnnosAggregationTestIT {

	@Test
	public void testRedirectToXML() {
		 given().
		 	redirects().follow(false).
         	header("Accept", "application/rdf+xml").
         expect().
         	statusCode(303).
         	header("Location", endsWith("/dms/sc/Stanford/kq131cs7229/TextAnnotations.xml")).
         when().
         get("/dms/sc/Stanford/kq131cs7229/TextAnnotations");
	}
	
	@Test
	public void testRedirectToTurtle() {
		 given().
		 	redirects().follow(false).
         	header("Accept", "text/turtle;charset=utf-8").
         expect().
         	statusCode(303).
         	header("Location", endsWith("/dms/sc/Stanford/kq131cs7229/TextAnnotations.ttl")).
         when().
         get("/dms/sc/Stanford/kq131cs7229/TextAnnotations");
	}
	
	@Test
	public void testRedirectToHTML() {
		 given().
		 	redirects().follow(false).
         	header("Accept", "text/html;charset=utf-8").
         expect().
         	statusCode(303).
         	header("Location", endsWith("/dms/sc/Stanford/kq131cs7229/TextAnnotations.html")).
         when().
         get("/dms/sc/Stanford/kq131cs7229/TextAnnotations");
	}
	

	
	

}
