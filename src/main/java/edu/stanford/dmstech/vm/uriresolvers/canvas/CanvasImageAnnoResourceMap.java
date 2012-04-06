package edu.stanford.dmstech.vm.uriresolvers.canvas;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.log4j.BasicConfigurator;

import edu.stanford.dmstech.vm.Config;
import edu.stanford.dmstech.vm.DMSTechRDFConstants;
import edu.stanford.dmstech.vm.RDFUtils;
import edu.stanford.dmstech.vm.SharedCanvasUtil;


public class CanvasImageAnnoResourceMap {
 	
	@Context 
	UriInfo uriInfo;
	
	
	@GET 
	@Path("{collectionId}/{manuscriptId}/{canvasId}/ImageAnnotations.xml")
	@Produces("application/rdf+xml")
	public String redirectReqToXMLResourceMap() throws URISyntaxException, IOException {
		return buildMap("RDF/XML");
			} 
	
	@GET
	@Path("{collectionId}/{manuscriptId}/{canvasId}/ImageAnnotations.ttl")
	@Produces("text/turtle;charset=utf-8")
	public String redirectReqToTurtleResourceMap() throws URISyntaxException, IOException {
		return buildMap("TURTLE");
	}

	@GET
	@Path("{collectionId}/{manuscriptId}/{canvasId}/ImageAnnotations.html")
	@Produces("text/html;charset=utf-8")
	public String redirectReqToHTMLResourceMap() throws URISyntaxException, TransformerConfigurationException, TransformerFactoryConfigurationError, TransformerException, IOException {		
		String rdfAsXML = buildMap("RDF/XML");
		return RDFUtils.serializeRDFToHTML(rdfAsXML);	
	}
	
	private String buildMap(String format) throws IOException {
		
		String originalRequest = uriInfo.getAbsolutePath().toASCIIString();	
		return SharedCanvasUtil.buildResourceMapForAnnotations(format, originalRequest, "ImageAnnotation",  DMSTechRDFConstants.getInstance().scImageAnnotationListClass);
		
	}
	
	public static void main(String args[]) {
		try {
			BasicConfigurator.configure();
			Config config = new Config();
			config.initializeThisConfig();

			
			System.out.println(SharedCanvasUtil.buildResourceMapForAnnotations("TURTLE", "http://locahost:8080/vm/myColl/myMan/myCan/resourceMap.xml", "ImageAnnotation",  DMSTechRDFConstants.getInstance().scImageAnnotationListClass));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}



