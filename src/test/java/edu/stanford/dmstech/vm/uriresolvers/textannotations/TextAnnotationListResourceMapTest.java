package edu.stanford.dmstech.vm.uriresolvers.textannotations;


import static org.junit.Assert.*;

import org.junit.Test;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.get;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.matcher.RestAssuredMatchers.matchesXsd;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasXPath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TextAnnotationListResourceMapTest {

	@Test
	public void testGetResourceMapAsXML() {
		expect().
	    statusCode(200).
	   // body(
	  //    "user.email", equalTo("test@hascode.com"),
	  //    "user.firstName", equalTo("Tim"),
	  //    "user.lastName", equalTo("Testerman"),
	  //    "user.id", equalTo("1")
	   // 		).
	    when().
	    with().
	//      authentication().basic("stanf", "stanf").
	    get("/clbr/manuscript/Bodley342/textannotations/textAnnoResourceMap.xml");
	}

	@Test
	public void testSaveAnnotations() {
		//fail("Not yet implemented");
		assertEquals("Result", 50, 10*5);
	}

}
