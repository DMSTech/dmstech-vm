package edu.stanford.dmstech.vm.uriresolvers;

import static com.jayway.restassured.RestAssured.expect;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasXPath;

import org.junit.Test;

public class ManuscriptTextAnnosResourceMapTest {


	@Test
	public void testGetAsXML() {
		expect().
	    	statusCode(200).
	    	body(
	    			hasXPath("/RDF")	    			
	    		).
	    when().
	    with().
	    get("/vm/sc/Stanford/kq131cs7229/TextAnnotations.xml");
	}

	@Test
	public void testGetAsTurtle() {
		expect().
	    	statusCode(200).
	    	body(
	    			containsString("http://localhost:8080/vm/sc/Stanford/kq131cs7229")	    			
	    		).
	    when().
	    with().
	    get("/vm/sc/Stanford/kq131cs7229/TextAnnotations.ttl");
	}
	
	@Test
	public void testGetAsHTML() {
		expect().
	    	statusCode(200).
	    	body(
	    			containsString("http://localhost:8080/vm/sc/Stanford/kq131cs7229")	    			
	    		).
	    when().
	    with().
	    get("/vm/sc/Stanford/kq131cs7229/TextAnnotations.html");
	}
	
	

}
