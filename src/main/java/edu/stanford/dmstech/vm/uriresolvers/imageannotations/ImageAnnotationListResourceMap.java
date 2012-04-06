package edu.stanford.dmstech.vm.uriresolvers.imageannotations;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import edu.stanford.dmstech.vm.Config;
import edu.stanford.dmstech.vm.RDFUtils;
import edu.stanford.dmstech.vm.SharedCanvasUtil;

@Path("/{collectionId}/{manuscriptId}/")
public class ImageAnnotationListResourceMap {

	@GET
	@Path("/ImageAnnotations.xml") 
	@Produces("application/rdf+xml")
	public String getResourceMapAsXML(
			@PathParam("collectionId") final String collectionId,
			@PathParam("manuscriptId") final String manuscriptId
			) throws Exception {   
		return SharedCanvasUtil.getSerializedRDFFromHomeDir(collectionId + "/" + manuscriptId + "/" + Config.imageAnnotationFileName, "RDF/XML");
		
	}
		
	@GET
	@Path("/ImageAnnotations.ttl")  
	@Produces("text/turtle;charset=utf-8")
	public String getResourceMapAsTurtle(
			@PathParam("collectionId") final String collectionId,
			@PathParam("manuscriptId") final String manuscriptId
			) throws Exception {
		return SharedCanvasUtil.getSerializedRDFFromHomeDir(collectionId + "/" + manuscriptId + "/" + Config.imageAnnotationFileName, "TURTLE");
			
	}

	@GET
	@Path("/ImageAnnotations.html")  
	@Produces("text/html;charset=utf-8")
	public String getResourceMapAsHTML(
			@PathParam("collectionId") final String collectionId,
			@PathParam("manuscriptId") final String manuscriptId
			) throws Exception {
		return RDFUtils.serializeRDFToHTML(SharedCanvasUtil.getSerializedRDFFromHomeDir(collectionId + "/" + manuscriptId + "/" + Config.imageAnnotationFileName, "RDF/XML"));		
	}
		


	
				
}


