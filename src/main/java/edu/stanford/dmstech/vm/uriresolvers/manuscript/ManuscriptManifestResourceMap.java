package edu.stanford.dmstech.vm.uriresolvers.manuscript;

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

import com.hp.hpl.jena.rdf.model.Model;
import com.sun.jersey.api.NotFoundException;

@Path("/{collectionId}/{manuscriptId}/")
public class ManuscriptManifestResourceMap {
	
	@GET
	@Path("/Manifest.xml") 
	@Produces("application/rdf+xml")
	public File getResourceMapAsXML(
			@PathParam("collectionId") final String collectionId,
			@PathParam("manuscriptId") final String manuscriptId
			) throws URISyntaxException {   	
		return RDFUtils.getFileInHomeDir(collectionId + "/" + manuscriptId + "/" + Config.manifestFileName);
	}
		
	@GET
	@Path("/Manifest.ttl")  
	@Produces("text/turtle;charset=utf-8")
	public String getResourceMapAsTurtle(
			@PathParam("collectionId") final String collectionId,
			@PathParam("manuscriptId") final String manuscriptId
			) throws Exception {
		Model textAnnotationsModel = RDFUtils.loadModelInHomeDir(collectionId + "/" + manuscriptId + "/" + Config.manifestFileName);
		StringWriter stringWriter = new StringWriter();
		textAnnotationsModel.write(stringWriter, "TURTLE");
		return stringWriter.toString();		
	}

	@GET
	@Path("/Manifest.html")  
	@Produces("text/turtle;charset=utf-8")
	public String getResourceMapAsHTML(
			@PathParam("collectionId") final String collectionId,
			@PathParam("manuscriptId") final String manuscriptId
			) throws Exception {
		return RDFUtils.serializeRDFToHTML(RDFUtils.getFileInHomeDir(collectionId + "/" + manuscriptId + "/" + Config.manifestFileName));		
	}
		

		}


