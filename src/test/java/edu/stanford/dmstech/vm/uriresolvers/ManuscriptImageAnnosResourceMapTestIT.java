package edu.stanford.dmstech.vm.uriresolvers;

import static com.jayway.restassured.RestAssured.expect;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasXPath;

import org.junit.Test;

public class ManuscriptImageAnnosResourceMapTestIT {


	
	@Test
	public void testGetAsXML() {
		expect().
	    	statusCode(200).
	    	body(
	    			hasXPath("/RDF")	    			
	    		).
	    when().
	    with().
	    get("/dms/sc/Stanford/kq131cs7229/ImageAnnotations.xml");
	}

	@Test
	public void testGetAsTurtle() {
		expect().
	    	statusCode(200).
	    	body(
	    			containsString("/dms/sc/Stanford/kq131cs7229")	    			
	    		).
	    when().
	    with().
	    get("/dms/sc/Stanford/kq131cs7229/ImageAnnotations.ttl");
	}
	
	@Test
	public void testGetAsHTML() {
		expect().
	    	statusCode(200).
	    	body(
	    			containsString("/dms/sc/Stanford/kq131cs7229")	    			
	    		).
	    when().
	    with().
	    get("/dms/sc/Stanford/kq131cs7229/ImageAnnotations.html");
	}
	
	

}
