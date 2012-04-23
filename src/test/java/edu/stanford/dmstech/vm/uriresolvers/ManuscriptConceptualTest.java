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
         	header("Location", endsWith("/vm/sc/Stanford/kq131cs7229.xml")).
         when().
         get("/vm/sc/Stanford/kq131cs7229");
	}
	
	@Test
	public void testRedirectToTurtle() {
		 given().
		 	redirects().follow(false).
         	header("Accept", "text/turtle;charset=utf-8").
         expect().
         	statusCode(303).
         	header("Location", endsWith("/vm/sc/Stanford/kq131cs7229.ttl")).
         when().
         get("/vm/sc/Stanford/kq131cs7229");
	}
	
	@Test
	public void testRedirectToHTML() {
		 given().
		 	redirects().follow(false).
         	header("Accept", "text/html;charset=utf-8").
         expect().
         	statusCode(303).
         	header("Location", endsWith("/vm/sc/Stanford/kq131cs7229.html")).
         when().
         get("/vm/sc/Stanford/kq131cs7229");
	}
	
	@Test
	public void testSubmitNewManuscript() {
		 given().
	       param("manname", "The name of our test manuscript.").
	       param("mantitle", "The title of our test manuscript.").
	       param("collection", "The name of the manuscript's collection.").
	       param("idno", "The identifier for the manuscript.").
	       param("altid", "An alternate identifier for the manuscript.").
	       param("repository", "The repository holding our manuscript.").
	       param("institution", "The institution holding our manuscript.").
	       param("settlement", "Some settlement.").
	       param("region", "Some region.").
	       param("country", "Some country.").
	       param("parseFileNames", "false").
		 	redirects().follow(false).
         	header("Accept", "text/html;charset=utf-8").
         expect().
         	statusCode(201).
         	header("Location", endsWith("/vm/sc/ingest/test1")).
         when().
         put("/vm/sc/ingest/test1");
	}

	
	

}
