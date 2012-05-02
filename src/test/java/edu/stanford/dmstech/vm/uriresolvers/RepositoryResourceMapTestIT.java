package edu.stanford.dmstech.vm.uriresolvers;

import static com.jayway.restassured.RestAssured.expect;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasXPath;

import org.junit.Test;

public class RepositoryResourceMapTestIT {


	
	@Test
	public void testGetManifestAsXML() {
		expect().
	    	statusCode(200).
	    	body(
	    			hasXPath("/RDF")	    			
	    		).
	    when().
	    with().
	    get("/dms/sc/Repository.xml");
	}

	@Test
	public void testGetManifestAsTurtle() {
		expect().
	    	statusCode(200).
	    	body(
	    			containsString("http://localhost:8080/dms/sc/Stanford")	    			
	    		).
	    when().
	    with().
	    get("/dms/sc/Repository.ttl");
	}
	
	@Test
	public void testGetManifestAsHTML() {
		expect().
	    	statusCode(200).
	    	body(
	    			containsString("http://localhost:8080/dms/sc/Stanford")	    			
	    		).
	    when().
	    with().
	    get("/dms/sc/Repository.html");
	}
	
	

}
