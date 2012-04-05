package edu.stanford.dmstech.vm.uriresolvers.repository;

import java.io.File;
import java.io.StringWriter;
import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.hp.hpl.jena.rdf.model.Model;

import edu.stanford.dmstech.vm.Config;
import edu.stanford.dmstech.vm.RDFUtils;


public class RepositoryResourceMap {



	@GET
	@Path("/Repository.xml") 
	@Produces("application/rdf+xml")
	public File getResourceMapAsXML() throws URISyntaxException {   	
		return RDFUtils.getFileInHomeDir(Config.repositoryFileName);
	}
		
	@GET
	@Path("/Repository.ttl")  
	@Produces("text/turtle;charset=utf-8")
	public String getResourceMapAsTurtle() throws Exception {
		Model textAnnotationsModel = RDFUtils.loadModelInHomeDir(Config.repositoryFileName);
		StringWriter stringWriter = new StringWriter();
		textAnnotationsModel.write(stringWriter, "TURTLE");
		return stringWriter.toString();		
	}

	@GET
	@Path("/Repository.html")  
	@Produces("text/turtle;charset=utf-8")
	public String getResourceMapAsHTML() throws Exception {
		return RDFUtils.serializeRDFToHTML(RDFUtils.getFileInHomeDir(Config.repositoryFileName));		
	}

}


