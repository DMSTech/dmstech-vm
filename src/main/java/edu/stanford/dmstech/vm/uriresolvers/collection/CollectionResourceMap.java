package edu.stanford.dmstech.vm.uriresolvers.collection;

import java.io.File;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Path;

import edu.stanford.dmstech.vm.Config;
import edu.stanford.dmstech.vm.RDFUtils;
import edu.stanford.dmstech.vm.SharedCanvasUtil;
import edu.stanford.dmstech.vm.uriresolvers.ResourceMapSerialization;

import com.hp.hpl.jena.rdf.model.Model;
import com.sun.jersey.api.NotFoundException;

@Path("/{collectionId}")
public class CollectionResourceMap {

	@GET
	@Path("Collection.xml") 
	@Produces("application/rdf+xml")
	public String getResourceMapAsXML(@PathParam("collectionId") final String collectionId) throws Exception {  
		return SharedCanvasUtil.getSerializedRDFFromHomeDir(collectionId + "/" + Config.collectionFileName, "RDF/XML");		
	}
		
	@GET
	@Path("Collection.ttl")  
	@Produces("text/turtle;charset=utf-8")
	public String getResourceMapAsTurtle(@PathParam("collectionId") final String collectionId) throws Exception {
		return SharedCanvasUtil.getSerializedRDFFromHomeDir(collectionId + "/" + Config.collectionFileName, "TURTLE");				
	}

	@GET
	@Path("Collection.html")  
	@Produces("text/html;charset=utf-8")
	public String getResourceMapAsHTML(@PathParam("collectionId") final String collectionId) throws Exception {		
		return RDFUtils.serializeRDFToHTML(SharedCanvasUtil.getSerializedRDFFromHomeDir(collectionId + "/" + Config.collectionFileName, "RDF/XML"));		
	}

}


