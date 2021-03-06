package edu.stanford.dmstech.vm.uriresolvers.ingest;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import org.junit.Test;

import com.jayway.restassured.response.Response;

public class AnnotationIngestTestIT {

	String sampleAnnotationOLD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
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
	
	String sampleAnnotation = "<rdf:RDF\n" + 
			"    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n" + 
			"    xmlns:cnt=\"http://www.w3.org/2008/content#\"\n" + 
			"    xmlns:sc=\"http://www.shared-canvas.org/ns/\"\n" + 
			"    xmlns:ore=\"http://www.openarchives.org/ore/terms/\"\n" + 
			"    xmlns:oac=\"http://www.openannotation.org/ns/\" > \n" + 
			"  <rdf:Description rdf:about=\"urn:uuid:7679f48e-e2b0-42c3-b5b1-7ebfc704379f\">\n" + 
			"    <rdf:type rdf:resource=\"http://www.shared-canvas.org/ns/ContentAnnotation\"/>\n" + 
			"    <oac:hasTarget rdf:resource=\"http://dms-data.stanford.edu/Parker/nr257ff7994/canvas-324#xyhw=0,0,139,610\"/>\n" + 
			"    <oac:hasBody rdf:resource=\"urn:uuid:d6105f4e-cd2e-4a9c-b5c9-2c8a196e5057\"/>\n" + 
			"  </rdf:Description>\n" + 
			"  <rdf:Description rdf:about=\"urn:uuid:d6105f4e-cd2e-4a9c-b5c9-2c8a196e5057\">\n" + 				
			"  		 <rdf:type rdf:resource=\"http://www.w3.org/2008/content#ContentAsText\"/>" + 
			"        <cnt:rest rdf:parseType=\"Literal\">" + 
						"grauius, ut non solum patiaris harum rerum indigentiam, sed" + 
			"        </cnt:rest>\n" + 
			"        <cnt:characterEncoding>utf-8</cnt:characterEncoding>\n" + 
			"  </rdf:Description>\n" + 
			"</rdf:RDF>";
	
	String replacementAnnotationForFormat = "<rdf:RDF\n" + 
			"    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n" + 
			"    xmlns:cnt=\"http://www.w3.org/2008/content#\"\n" + 
			"    xmlns:sc=\"http://www.shared-canvas.org/ns/\"\n" + 
			"    xmlns:ore=\"http://www.openarchives.org/ore/terms/\"\n" + 
			"    xmlns:oac=\"http://www.openannotation.org/ns/\" > \n" + 
			"  <rdf:Description rdf:about=\"%s\">\n" + 
			"    <rdf:type rdf:resource=\"http://www.shared-canvas.org/ns/ContentAnnotation\"/>\n" + 
			"    <oac:hasTarget rdf:resource=\"http://dms-data.stanford.edu/Parker/nr257ff7994/canvas-324#xyhw=0,0,139,610\"/>\n" + 
			"    <oac:hasBody rdf:resource=\"urn:uuid:d6105f4e-cd2e-4a9c-b5c3-2c8a196e5057\"/>\n" + 
			"  </rdf:Description>\n" + 
			"  <rdf:Description rdf:about=\"urn:uuid:d6105f4e-cd2e-4a9c-b5c3-2c8a196e5057\">\n" + 				
			"  		 <rdf:type rdf:resource=\"http://www.w3.org/2008/content#ContentAsText\"/>" + 
			"        <cnt:rest rdf:parseType=\"Literal\">" + 
						"grauius, ut non solum patiaris NEW NEW NEW harum rerum indigentiam, sed" + 
			"        </cnt:rest>\n" + 
			"        <cnt:characterEncoding>utf-8</cnt:characterEncoding>\n" + 
			"  </rdf:Description>\n" + 
			"</rdf:RDF>";
	
	String sampleAnnotationsOLD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
			"<rdf:RDF\n" + 
			"   xmlns:dc=\"http://purl.org/dc/elements/1.1/\"\n" + 
			"   xmlns:exif=\"http://www.w3.org/2003/12/exif/ns#\"\n" + 
			"   xmlns:oac=\"http://www.openannotation.org/ns/\"\n" + 
			"   xmlns:ore=\"http://www.openarchives.org/ore/terms/\"\n" + 
			"   xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n" +
			"   xmlns:cnt=\"http://www.w3.org/2008/content#\"" +
			"   xmlns:sc=\"http://www.shared-canvas.org/ns/\"" + 
			">\n" + 
			"   <oac:Body rdf:about=\"URN:aTextBodyTestURN\">" + 
			"        <rdf:type rdf:resource=\"http://www.w3.org/2008/content#ContentAsText\"/>" + 
			"        <cnt:rest rdf:parseType=\"Literal\">" + 
			"            A bunch of text testing multiple annotations.\n" + 
			"        </cnt:rest>\n" + 
			"        <cnt:characterEncoding>utf-8</cnt:characterEncoding>\n" + 
			"    </oac:Body>\n" + 
			"  <sc:TextAnnotation rdf:about=\"URN:aTextAnnoTestURN\">\n" + 
			"    <oac:hasBody rdf:resource=\"URN:aTextBodyTestURN\"/>\n" + 
			"    <oac:hasTarget rdf:resource=\"http://sul-dms-dev.stanford.edu/dms/sc/Stanford/kq131cs7229/canvas-2\"/>\n" + 
			"    <rdf:type rdf:resource=\"http://www.openannotation.org/ns/Annotation\"/>\n" + 
			"    <rdf:type rdf:resource=\"http://www.shared-canvas.org/ns/TextAnnotation\"/>\n" + 
			"  </sc:TextAnnotation>\n" + 
			"  <sc:Canvas rdf:about=\"http://sul-dms-dev.stanford.edu/dms/sc/Stanford/kq131cs7229/canvas-2\">\n" + 
			"    <dc:title>f. 8r</dc:title>\n" + 
			"    <exif:width>4019</exif:width>\n" + 
			"    <exif:height>5575</exif:height>\n" +    
			"  </sc:Canvas>" +
			"  <oac:Body rdf:about=\"URN:aTextBodyTestURN2\">" + 
			"        <rdf:type rdf:resource=\"http://www.w3.org/2008/content#ContentAsText\"/>" + 
			"        <cnt:rest rdf:parseType=\"Literal\">" + 
			"            A test annotation of canvas-2 in the Stanford Collection.\n" + 
			"        </cnt:rest>\n" + 
			"        <cnt:characterEncoding>utf-8</cnt:characterEncoding>\n" + 
			"    </oac:Body>\n" + 
			"  <sc:TextAnnotation rdf:about=\"URN:aTextAnnoTestURN2\">\n" + 
			"    <oac:hasBody rdf:resource=\"URN:aTextBodyTestURN2\"/>\n" + 
			"    <oac:hasTarget rdf:resource=\"http://sul-dms-dev.stanford.edu/dms/sc/Stanford/kq131cs7229/canvas-2\"/>\n" + 
			"    <rdf:type rdf:resource=\"http://www.openannotation.org/ns/Annotation\"/>\n" + 
			"    <rdf:type rdf:resource=\"http://www.shared-canvas.org/ns/TextAnnotation\"/>\n" + 
			"  </sc:TextAnnotation>\n" + 
			"  <sc:Canvas rdf:about=\"http://sul-dms-dev.stanford.edu/dms/sc/Stanford/kq131cs7229/canvas-2\">\n" + 
			"    <dc:title>f. 8r</dc:title>\n" + 
			"    <exif:width>4019</exif:width>\n" + 
			"    <exif:height>5575</exif:height>\n" +    
			"  </sc:Canvas>" +
			"  <oac:Body rdf:about=\"URN:aTextBodyTestURN3\">" + 
			"        <rdf:type rdf:resource=\"http://www.w3.org/2008/content#ContentAsText\"/>" + 
			"        <cnt:rest rdf:parseType=\"Literal\">" + 
			"            A test annotation of canvas-3 in the Stanford Collection.\n" + 
			"        </cnt:rest>\n" + 
			"        <cnt:characterEncoding>utf-8</cnt:characterEncoding>\n" + 
			"    </oac:Body>\n" + 
			"  <sc:TextAnnotation rdf:about=\"URN:aTextAnnoTestURN3\">\n" + 
			"    <oac:hasBody rdf:resource=\"URN:aTextBodyTestURN3\"/>\n" + 
			"    <oac:hasTarget rdf:resource=\"http://sul-dms-dev.stanford.edu/dms/sc/Stanford/kq131cs7229/canvas-3\"/>\n" + 
			"    <rdf:type rdf:resource=\"http://www.openannotation.org/ns/Annotation\"/>\n" + 
			"    <rdf:type rdf:resource=\"http://www.shared-canvas.org/ns/TextAnnotation\"/>\n" + 
			"  </sc:TextAnnotation>\n" + 
			"  <sc:Canvas rdf:about=\"http://sul-dms-dev.stanford.edu/dms/sc/Stanford/kq131cs7229/canvas-3\">\n" + 
			"    <dc:title>f. 8r</dc:title>\n" + 
			"    <exif:width>4019</exif:width>\n" + 
			"    <exif:height>5575</exif:height>\n" +    
			"  </sc:Canvas>" +
			"</rdf:RDF>";
	
	String sampleAnnotations = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
			 "<rdf:RDF\n" + 
				"    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n" + 
				"    xmlns:cnt=\"http://www.w3.org/2008/content#\"\n" + 
				"    xmlns:sc=\"http://www.shared-canvas.org/ns/\"\n" + 
				"    xmlns:ore=\"http://www.openarchives.org/ore/terms/\"\n" + 
				"    xmlns:oac=\"http://www.openannotation.org/ns/\" > \n" + 
				"  <rdf:Description rdf:about=\"urn:uuid:7679f48e-e2b0-42c3-b5b1-7ebfc704379f\">\n" + 
				"    <rdf:type rdf:resource=\"http://www.shared-canvas.org/ns/ContentAnnotation\"/>\n" + 
				"    <oac:hasTarget rdf:resource=\"http://dms-data.stanford.edu/Parker/nr257ff7994/canvas-324#xyhw=0,0,139,610\"/>\n" + 
				"    <oac:hasBody rdf:resource=\"urn:uuid:d6105f4e-cd2e-4a9c-b5c9-2c8a196e5057\"/>\n" + 
				"  </rdf:Description>\n" + 
				"  <rdf:Description rdf:about=\"urn:uuid:d6105f4e-cd2e-4a9c-b5c9-2c8a196e5057\">\n" + 				
				"  		 <rdf:type rdf:resource=\"http://www.w3.org/2008/content#ContentAsText\"/>" + 
				"        <cnt:rest rdf:parseType=\"Literal\">" + 
							"grauius, ut non solum patiaris harum rerum indigentiam, sed" + 
				"        </cnt:rest>\n" + 
				"        <cnt:characterEncoding>utf-8</cnt:characterEncoding>\n" + 
				"  </rdf:Description>\n" + 
				"<rdf:Description rdf:about=\"urn:uuid:7679f48e-e2b0-42c3-b551-7ebfc704379d\">\n" + 
						"    <rdf:type rdf:resource=\"http://www.shared-canvas.org/ns/ContentAnnotation\"/>\n" + 
						"    <oac:hasTarget rdf:resource=\"http://dms-data.stanford.edu/Parker/nr257ff7994/canvas-324#xyhw=0,0,139,610\"/>\n" + 
						"    <oac:hasBody rdf:resource=\"urn:uuid:d6105f4e-cd2e-4a9c-b5c9-2c8a196e5057\"/>\n" + 
						"  </rdf:Description>\n" + 
						"  <rdf:Description rdf:about=\"urn:uuid:d6105f4e-cd2e-4a9c-b5c9-2c8a196e5057\">\n" + 				
						"  		 <rdf:type rdf:resource=\"http://www.w3.org/2008/content#ContentAsText\"/>" + 
						"        <cnt:rest rdf:parseType=\"Literal\">" + 
									"grauius, ut non solum patiaris harum rerum indigentiam, sed" + 
						"        </cnt:rest>\n" + 
						"        <cnt:characterEncoding>utf-8</cnt:characterEncoding>\n" + 
						"  </rdf:Description>\n" + 
				
				
				"</rdf:RDF>";


	@Test
	public void testSubmitSingleAnnotation() {
		 Response response = given().
	       
		 	body(sampleAnnotation).
	       
         expect().
         	statusCode(201).
         	header("Location", containsString("/dms/sc/submitted_annotations/")).
         	body(containsString("submitted_annotations")).
         when().
         post("/dms/sc/annotations/ingest");
		 
		 String newAnnosURI = response.getHeader("Location");
		 
String newAnnoToUpdateJustCreateAnno = String.format(replacementAnnotationForFormat, newAnnosURI);
		 
		 given().
	      
		 	body(newAnnoToUpdateJustCreateAnno).
	      
	 expect().
	 	statusCode(201).
	 	header("Location", containsString(newAnnosURI)).
	 	body(containsString("sameAs")).
	 when().
	 put(newAnnosURI);
		 
	}

	@Test
	public void testSubmitMultipleAnnotation() {
		 given().
	       
		 	body(sampleAnnotations).
	       
         expect().
         	statusCode(201).
         	header("Location", containsString("/dms/sc/transactions/")).
         	body(containsString("submitted_annotations")).
         when().
         post("/dms/sc/annotations/ingest");
	}
	

	

	
	
}
