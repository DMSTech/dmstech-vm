package edu.stanford.dmstech.vm.uriresolvers.repository;

import java.io.File;
import java.io.StringWriter;
import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.hp.hpl.jena.rdf.model.Model;

import edu.stanford.dmstech.vm.Config;
import edu.stanford.dmstech.vm.RDFUtils;
import edu.stanford.dmstech.vm.SharedCanvasUtil;


public class RepositoryResourceMap {

	@GET
	@Path("Repository.xml") 
	@Produces("application/rdf+xml")
	public String getResourceMapAsXML(@PathParam("collectionId") final String collectionId) throws Exception {  
		return SharedCanvasUtil.getSerializedRDFFromHomeDir(Config.repositoryFileName, "RDF/XML");		
	}
		
	@GET
	@Path("Repository.ttl")  
	@Produces("text/turtle;charset=utf-8")
	public String getResourceMapAsTurtle(@PathParam("collectionId") final String collectionId) throws Exception {
		return SharedCanvasUtil.getSerializedRDFFromHomeDir(Config.repositoryFileName, "TURTLE");				
	}

	@GET
	@Path("Repository.html")  
	@Produces("text/html;charset=utf-8")
	public String getResourceMapAsHTML(@PathParam("collectionId") final String collectionId) throws Exception {		
		return RDFUtils.serializeRDFToHTML(SharedCanvasUtil.getSerializedRDFFromHomeDir(Config.repositoryFileName, "RDF/XML"));		
	}

	
}


