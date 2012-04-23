package edu.stanford.dmstech.vm.uriresolvers;

import static com.jayway.restassured.RestAssured.expect;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasXPath;

import org.junit.Test;

public class RepositoryResourceMapTest {


	
	@Test
	public void testGetManifestAsXML() {
		expect().
	    	statusCode(200).
	    	body(
	    			hasXPath("/RDF")	    			
	    		).
	    when().
	    with().
	    get("/vm/sc/Repository.xml");
	}

	@Test
	public void testGetManifestAsTurtle() {
		expect().
	    	statusCode(200).
	    	body(
	    			containsString("http://localhost:8080/vm/sc/Stanford")	    			
	    		).
	    when().
	    with().
	    get("/vm/sc/Repository.ttl");
	}
	
	@Test
	public void testGetManifestAsHTML() {
		expect().
	    	statusCode(200).
	    	body(
	    			containsString("http://localhost:8080/vm/sc/Stanford")	    			
	    		).
	    when().
	    with().
	    get("/vm/sc/Repository.html");
	}
	
	

}
