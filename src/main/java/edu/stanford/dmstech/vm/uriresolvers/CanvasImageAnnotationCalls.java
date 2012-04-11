package edu.stanford.dmstech.vm.uriresolvers;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import com.hp.hpl.jena.rdf.model.Resource;

import edu.stanford.dmstech.vm.DMSTechRDFConstants;
import edu.stanford.dmstech.vm.RDFUtils;
import edu.stanford.dmstech.vm.SharedCanvasUtil;

@Path("{collectionId}/{manuscriptId}/{canvasId}/{imageAnnoReq: ImageAnnotations\\.")
public class CanvasImageAnnotationCalls {
 
	@Context 
	UriInfo uriInfo;
	
	@GET 
	@Path("ImageAnnotations") 
	@Produces("application/rdf+xml")
	public Response redirectImageAnnosReqToXMLResourceMap() throws URISyntaxException {
		String originalRequest = uriInfo.getAbsolutePath().toASCIIString();
		return Response.seeOther(new URI(originalRequest + ".xml")).build();
	} 
	
	@GET 
	@Path("ImageAnnotations") 
	@Produces("text/turtle;charset=utf-8")
	public Response redirectImageAnnosReqToTurtleResourceMap() throws URISyntaxException {
		String originalRequest = uriInfo.getAbsolutePath().toASCIIString();
		return Response.seeOther(new URI(originalRequest + ".ttl")).build();
	}

	@GET 
	@Path("ImageAnnotations") 
	@Produces("text/html;charset=utf-8")
	public Response redirectImageAnnosReqToHTMLResourceMap() throws URISyntaxException {
		String originalRequest = uriInfo.getAbsolutePath().toASCIIString();
		return Response.seeOther(new URI(originalRequest + ".html")).build();
	}
	@GET 
	@Path("ImageAnnotations.xml")
	@Produces("application/rdf+xml")
	public String getImageAnnosMLResourceMap() throws URISyntaxException, IOException {
		return buildMap("RDF/XML", "ImageAnnotation", DMSTechRDFConstants.getInstance().scImageAnnotationListClass);
			} 
		
	@GET
	@Path("ImageAnnotations.ttl")
	@Produces("text/turtle;charset=utf-8")
	public String getImageAnnosTurtleResourceMap() throws URISyntaxException, IOException {
		return buildMap("TURTLE", "ImageAnnotation", DMSTechRDFConstants.getInstance().scImageAnnotationListClass);
	}

	@GET
	@Path("ImageAnnotations.html")
	@Produces("text/html;charset=utf-8")
	public String getImageAnnosHTMLResourceMap() throws URISyntaxException, TransformerConfigurationException, TransformerFactoryConfigurationError, TransformerException, IOException {		
		String rdfAsXML = buildMap("RDF/XML", "ImageAnnotation", DMSTechRDFConstants.getInstance().scImageAnnotationListClass);
		return RDFUtils.serializeRDFToHTML(rdfAsXML);	
	}
	
	
	
	private String buildMap(String format, String annotationType, Resource annotationListClass) throws IOException {		
		String originalRequest = uriInfo.getAbsolutePath().toASCIIString();	
		return SharedCanvasUtil.buildResourceMapForAnnotations(format, originalRequest, annotationType,  annotationListClass);
		
	}

	
	
	
	
}