package edu.stanford.dmstech.vm.uriresolvers;

import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;
import com.sun.jersey.api.NotFoundException;

import edu.stanford.dmstech.vm.Config;
import edu.stanford.dmstech.vm.RDFUtils;
import edu.stanford.dmstech.vm.uriresolvers.canvas.CanvasImageAnnoResourceMap;

public class ResourceMapSerialization {

	
	
	String pathToRMInHomeDir;
	
	public ResourceMapSerialization(String pathToRMInHomeDir) {
		this.pathToRMInHomeDir = pathToRMInHomeDir;
	}

	@GET
	@Path("{mapName: *.xml}") 
	@Produces("application/rdf+xml")
	public String getResourceMapAsXML() throws Exception {   
		
		String format = "N-TRIPLE";
		Model textAnnotationsModel = RDFUtils.loadModelInHomeDir(pathToRMInHomeDir, format);
		StringWriter stringWriter = new StringWriter();
		textAnnotationsModel.write(stringWriter, "RDF/XML");
		return stringWriter.toString();		
	}
		
	@GET
	@Path("{mapName: *.ttl}")  
	@Produces("text/turtle;charset=utf-8")
	public String getResourceMapAsTurtle() throws Exception {
		String format = "N-TRIPLE";
		Model textAnnotationsModel = RDFUtils.loadModelInHomeDir(pathToRMInHomeDir, format);
		StringWriter stringWriter = new StringWriter();
		textAnnotationsModel.write(stringWriter, "TURTLE");
		return stringWriter.toString();		
	}

	@GET
	@Path("{mapName: *.html}")  
	@Produces("text/turtle;charset=utf-8")
	public String getResourceMapAsHTML() throws Exception {
		String format = "N-TRIPLE";
		Model textAnnotationsModel = RDFUtils.loadModelInHomeDir(pathToRMInHomeDir, format);
		StringWriter stringWriter = new StringWriter();
		textAnnotationsModel.write(stringWriter, "TURTLE");		
		return RDFUtils.serializeRDFToHTML(stringWriter.toString());		
	}
	
	
	
}
