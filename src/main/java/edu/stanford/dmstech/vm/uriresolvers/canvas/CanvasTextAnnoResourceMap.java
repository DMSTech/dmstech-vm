package edu.stanford.dmstech.vm.uriresolvers.canvas;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.apache.log4j.BasicConfigurator;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import edu.stanford.dmstech.vm.Config;
import edu.stanford.dmstech.vm.DMSTechRDFConstants;
import edu.stanford.dmstech.vm.RDFUtils;
import edu.stanford.dmstech.vm.SharedCanvasUtil;
import edu.stanford.dmstech.vm.tdb.SharedCanvasTDBManager;
import edu.stanford.dmstech.vm.uriresolvers.ResourceMapSerialization;

@Path("{collectionId}/{manuscriptId}/{canvasId}/TextAnnotations.xml")
public class CanvasTextAnnoResourceMap {
 
	@Context 
	UriInfo uriInfo;
	
	
	@GET 
	@Produces("application/rdf+xml")
	public String redirectReqToXMLResourceMap() throws URISyntaxException, IOException {
		return buildMap("RDF/XML");
	} 
	
	@GET
	@Produces("text/turtle;charset=utf-8")
	public String redirectReqToTurtleResourceMap() throws URISyntaxException, IOException {
		return buildMap("TURTLE");
	}

	@GET
	@Produces("text/html;charset=utf-8")
	public String redirectReqToHTMLResourceMap() throws URISyntaxException, TransformerConfigurationException, TransformerFactoryConfigurationError, TransformerException, IOException {		
		String rdfAsXML = buildMap("RDF/XML");
		return RDFUtils.serializeRDFToHTML(rdfAsXML);	
	}
	
private String buildMap(String format) throws IOException {
		
		String originalRequest = uriInfo.getAbsolutePath().toASCIIString();	
		return SharedCanvasUtil.buildResourceMapForAnnotations(format, originalRequest, "TextAnnotation",  DMSTechRDFConstants.getInstance().scTextAnnotationListClass);
		
	}

public static void main(String args[]) {
	try {
		BasicConfigurator.configure();
		Config config = new Config();
		config.initializeThisConfig();

		
		System.out.println(SharedCanvasUtil.buildResourceMapForAnnotations("TURTLE", "http://locahost:8080/vm/myColl/myMan/myCan/resourceMap.xml", "TextAnnotation",  DMSTechRDFConstants.getInstance().scTextAnnotationListClass));
	} catch (IOException e) {
		e.printStackTrace();
	}
}

	

	}



