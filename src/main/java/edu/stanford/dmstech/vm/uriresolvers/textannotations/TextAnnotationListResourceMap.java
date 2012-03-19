package edu.stanford.dmstech.vm.uriresolvers.textannotations;

import java.io.File;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import com.hp.hpl.jena.rdf.model.Model;
import com.sun.jersey.api.NotFoundException;

import edu.stanford.dmstech.vm.Config;
import edu.stanford.dmstech.vm.uriresolvers.AnnotationUtils;

@Path("/{collectionId}/{manuscriptId}/{canvasId}/")
public class TextAnnotationListResourceMap {
 
	private final String W3CDTF_NOW = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")).format(new Date());
	Logger logger = Logger.getLogger(TextAnnotationListResourceMap.class.getName());
		
	/* Do I want this included in the  manifest:
	 * 
	 * <dms:Canvas rdf:about="http://dmss.stanford.edu/Parker/524/manifest/17V">
   <exif:height>9700</exif:height>
   <dc:title>f. 17 V</dc:title>
   <exif:width>8000</exif:width>
   <dms:acceptsAnnotations rdf:id="http://dmss.stanford.edu/Parker/524/manifest/17V/annotation"/>
   <dms:pleasePostAnnotationsTo rdf:id="http://someOtherServer/annotationService"/>
 </dms:Canvas>
 
 
 THIS NEEDS TO LOAD UP ALL CANVAS TEXT ANNOTATION RESOURCE MAPS, SO LOOP THROUGH THE DIRECTORIES, AND COMBINE THEM INTO ONE 
 MODEL, THEN SERIALIZE.  
 
 
 
	 */
	@GET
	@Path("textannotations.xml") 
	@Produces("application/rdf+xml")
	public File getResourceMapAsXML(@PathParam("manuscriptId") final String manuscriptId) throws URISyntaxException {
    	
		final File file = new File(Config.homeDir, "manuscripts/" + manuscriptId + "/rdf/" + Config.getTextAnnotationFileName());
		if (!file.exists()) {
			throw new NotFoundException("File, " + file.getAbsolutePath() + ", is not found");
		}
		return file;
	}
	
	@GET
	@Path("textannotations.ttl")  
	@Produces("text/turtle;charset=utf-8")
	public String getResourceMapAsTurtle(@PathParam("manuscriptId") final String manuscriptId) throws URISyntaxException {
		Model textAnnotationsModel = loadTextAnnotationsModel(manuscriptId);
		StringWriter stringWriter = new StringWriter();
		textAnnotationsModel.write(stringWriter, "TURTLE");
		return stringWriter.toString();
		
	}

	@GET
	@Path("textannos.html")  
	@Produces("text/turtle;charset=utf-8")
	public String getResourceMapAsHTML() throws Exception {
		return AnnotationUtils.serializeRDFToHTML(getResourceMapFile());		
	}
	
	
	these - the annotatinos for th whole manuscript will have to be dynamically constructed from all the individual canvas resrouce maps.
}



