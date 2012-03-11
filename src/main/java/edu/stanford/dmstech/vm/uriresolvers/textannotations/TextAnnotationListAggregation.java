package edu.stanford.dmstech.vm.uriresolvers.textannotations;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.transaction.file.FileResourceManager;
import org.apache.commons.transaction.util.Jdk14Logger;
import org.apache.commons.transaction.util.LoggerFacade;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.FileManager;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.sun.jersey.api.NotFoundException;

import edu.stanford.dmstech.vm.Config;
import edu.stanford.dmstech.vm.DMSTechRDFConstants;

@Path("/{manuscriptId}/{canvasId}/textannotations")
public class TextAnnotationListAggregation {
 
	private final String W3CDTF_NOW = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")).format(new Date());
	Logger logger = Logger.getLogger(TextAnnotationListAggregation.class.getName());
	
	@GET 
	@Produces("application/rdf+xml")
	public Response redirectReqToXMLResourceMap(@Context UriInfo uriInfo) throws URISyntaxException {
		String originalRequest = uriInfo.getAbsolutePath().toASCIIString();
		String resourceMapFileName = "/textAnnoResourceMap.xml";
		return Response.seeOther(new URI(originalRequest + resourceMapFileName)).build();
	} 
	
	@GET
	@Produces("text/turtle;charset=utf-8")
	public Response redirectReqToTurtleResourceMap(@Context UriInfo uriInfo) throws URISyntaxException {
		String originalRequest = uriInfo.getAbsolutePath().toASCIIString();
		String resourceMapFileName = "/textAnnoResourceMap.ttl";
		return Response.seeOther(new URI(originalRequest + resourceMapFileName)).build();
	}

	
	/* Do I want this included in the  manifest:
	 * 
	 * <dms:Canvas rdf:about="http://dmss.stanford.edu/Parker/524/manifest/17V">
   <exif:height>9700</exif:height>
   <dc:title>f. 17 V</dc:title>
   <exif:width>8000</exif:width>
   <dms:acceptsAnnotations rdf:id="http://dmss.stanford.edu/Parker/524/manifest/17V/annotation"/>
   <dms:pleasePostAnnotationsTo rdf:id="http://someOtherServer/annotationService"/>
 </dms:Canvas>
 
	 */

	
}



