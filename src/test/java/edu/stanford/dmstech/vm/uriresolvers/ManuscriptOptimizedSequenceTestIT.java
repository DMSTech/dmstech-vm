package edu.stanford.dmstech.vm.uriresolvers;

import static com.jayway.restassured.RestAssured.expect;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasXPath;

import org.junit.Test;

public class ManuscriptOptimizedSequenceTestIT {


	@Test
	public void testGet() {
		expect().
	    	statusCode(200).
	    	body(
	    			containsString("/dms/sc/Stanford/kq131cs7229/canvas-")  			
	    		).
	    when().
	    with().
	    get("/dms/sc/Stanford/kq131cs7229/sequences/optimized/OriginalSequence.json");
	}


	

}
