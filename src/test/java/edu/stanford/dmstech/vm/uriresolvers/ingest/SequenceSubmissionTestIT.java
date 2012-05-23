package edu.stanford.dmstech.vm.uriresolvers.ingest;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import org.junit.Test;

public class SequenceSubmissionTestIT {

	String sampleAnnotation = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
			"<rdf:RDF\n" + 
			"   xmlns:dc=\"http://purl.org/dc/elements/1.1/\"\n" + 
			"   xmlns:exif=\"http://www.w3.org/2003/12/exif/ns#\"\n" + 
			"   xmlns:oac=\"http://www.openannotation.org/ns/\"\n" + 
			"   xmlns:ore=\"http://www.openarchives.org/ore/terms/\"\n" + 
			"   xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n" +
			"   xmlns:cnt=\"http://www.w3.org/2008/content#\"" +
			"   xmlns:sc=\"http://www.shared-canvas.org/ns/\"" + 
			">\n" + 
			"   <oac:Body rdf:about=\"URN:uuid:751183c0-d182-11df-bd3b-0800200c9a66\">" + 
			"        <rdf:type rdf:resource=\"http://www.w3.org/2008/content#ContentAsText\"/>" + 
			"        <cnt:rest rdf:parseType=\"Literal\">" + 
			"            A bunch of text testing a text annotation of a canvas.\n" + 
			"        </cnt:rest>\n" + 
			"        <cnt:characterEncoding>utf-8</cnt:characterEncoding>\n" + 
			"    </oac:Body>\n" + 
			"  <sc:TextAnnotation rdf:about=\"URN:woohoo\">\n" + 
			"    <oac:hasBody rdf:resource=\"URN:uuid:751183c0-d182-11df-bd3b-0800200c9a66\"/>\n" + 
			"    <oac:hasTarget rdf:resource=\"http://sul-dms-dev.stanford.edu/dms/sc/Stanford/kq131cs7229/canvas-1\"/>\n" + 
			"    <rdf:type rdf:resource=\"http://www.openannotation.org/ns/Annotation\"/>\n" + 
			"    <rdf:type rdf:resource=\"http://www.shared-canvas.org/ns/TextAnnotation\"/>\n" + 
			"  </sc:TextAnnotation>\n" + 
			"  <sc:Canvas rdf:about=\"http://sul-dms-dev.stanford.edu/dms/sc/Stanford/kq131cs7229/canvas-1\">\n" + 
			"    <dc:title>f. 8r</dc:title>\n" + 
			"    <exif:width>4019</exif:width>\n" + 
			"    <exif:height>5575</exif:height>\n" +    
			"  </sc:Canvas>" +
			"</rdf:RDF>";
	
	
	@Test
	public void testSubmitNewSequence() {
		 given().
		 	queryParam("makedefault", "true").
		 	body(sampleAnnotation).
	       
         expect().
         	statusCode(201).
         	header("Location", containsString("/dms/sc/Stanford/kq131cs7229/sequences/")).
         when().
         post("/dms/sc/Stanford/kq131cs7229/sequences");
	}

	
	

}
