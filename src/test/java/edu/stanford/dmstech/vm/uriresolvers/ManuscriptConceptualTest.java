package edu.stanford.dmstech.vm.uriresolvers;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.endsWith;

import org.junit.Test;

public class ManuscriptConceptualTest {

	@Test
	public void testRedirectToXML() {
		 given().
		 	redirects().follow(false).
         	header("Accept", "application/rdf+xml").
         expect().
         	statusCode(303).
         	header("Location", endsWith("/dms/sc/Stanford/kq131cs7229.xml")).
         when().
         get("/dms/sc/Stanford/kq131cs7229");
	}
	
	@Test
	public void testRedirectToTurtle() {
		 given().
		 	redirects().follow(false).
         	header("Accept", "text/turtle;charset=utf-8").
         expect().
         	statusCode(303).
         	header("Location", endsWith("/dms/sc/Stanford/kq131cs7229.ttl")).
         when().
         get("/dms/sc/Stanford/kq131cs7229");
	}
	
	@Test
	public void testRedirectToHTML() {
		 given().
		 	redirects().follow(false).
         	header("Accept", "text/html;charset=utf-8").
         expect().
         	statusCode(303).
         	header("Location", endsWith("/dms/sc/Stanford/kq131cs7229.html")).
         when().
         get("/dms/sc/Stanford/kq131cs7229");
	}
	
	@Test
	public void testSubmitNewManuscript() {
		 given().
	       formParam("manname", "The name of our test manuscript.").
	       formParam("mantitle", "The title of our test manuscript.").
	       formParam("collection", "The name of the manuscript's collection.").
	       formParam("idno", "The identifier for the manuscript.").
	       formParam("altid", "An alternate identifier for the manuscript.").
	       formParam("repository", "The repository holding our manuscript.").
	       formParam("institution", "The institution holding our manuscript.").
	       formParam("settlement", "Some settlement.").
	       formParam("region", "Some region.").
	       formParam("country", "Some country.").
	       formParam("parseFileNames", "false").
		 	redirects().follow(false).
         	header("Accept", "text/html;charset=utf-8").
         expect().
         	statusCode(201).
         	header("Location", endsWith("/dms/sc/ingested/test1")).
         when().
         put("/dms/sc/ingested/test1");
	}

	
	

}
