package edu.stanford.dmstech.vm.uriresolvers;

import static com.jayway.restassured.RestAssured.expect;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasXPath;

import org.junit.Test;

public class ManuscriptZoneAnnosResourceMapTestIT {
	
	@Test
	public void testGetAsXML() {
		expect().
	    	statusCode(200).
	    	body(
	    			hasXPath("/RDF")	    			
	    		).
	    when().
	    with().
	    get("/dms/sc/Stanford/kq131cs7229/ZoneAnnotations.xml");
	}

	@Test
	public void testGetAsTurtle() {
		expect().
	    	statusCode(200).
	    	body(
	    			containsString("http://localhost:8080/dms/sc/Stanford/kq131cs7229")	    			
	    		).
	    when().
	    with().
	    get("/dms/sc/Stanford/kq131cs7229/ZoneAnnotations.ttl");
	}
	
	@Test
	public void testGetAsHTML() {
		expect().
	    	statusCode(200).
	    	body(
	    			containsString("http://localhost:8080/dms/sc/Stanford/kq131cs7229")	    			
	    		).
	    when().
	    with().
	    get("/dms/sc/Stanford/kq131cs7229/ZoneAnnotations.html");
	}
	
}
