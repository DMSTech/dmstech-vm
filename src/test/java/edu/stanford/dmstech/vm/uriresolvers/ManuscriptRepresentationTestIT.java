package edu.stanford.dmstech.vm.uriresolvers;

import static com.jayway.restassured.RestAssured.expect;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasXPath;

import org.junit.Test;

public class ManuscriptRepresentationTestIT {


	
	@Test
	public void testGetManifestAsXML() {
		expect().
	    	statusCode(200).
	    	body(
	    			hasXPath("/RDF")	    			
	    		).
	    when().
	    with().
	    get("/dms/sc/Stanford/kq131cs7229.xml");
	}

	@Test
	public void testGetManifestAsTurtle() {
		expect().
	    	statusCode(200).
	    	body(
	    			containsString("/dms/sc/Stanford/kq131cs7229")	    			
	    		).
	    when().
	    with().
	    get("/dms/sc/Stanford/kq131cs7229.ttl");
	}
	
	@Test
	public void testGetManifestAsHTML() {
		expect().
	    	statusCode(200).
	    	body(
	    			containsString("/dms/sc/Stanford/kq131cs7229")	    			
	    		).
	    when().
	    with().
	    get("/dms/sc/Stanford/kq131cs7229.html");
	}
	
	

}
