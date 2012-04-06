package edu.stanford.dmstech.vm.uriresolvers.collection;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import edu.stanford.dmstech.vm.Config;
import edu.stanford.dmstech.vm.RDFUtils;
import edu.stanford.dmstech.vm.SharedCanvasUtil;

@Path("{collectionId}") 
public class CollectionResourceMap {

	@GET
	@Path("Collection.xml") 
	public String getResourceMapAsXML(@PathParam("collectionId") final String collectionId) throws Exception {  
		return SharedCanvasUtil.getSerializedRDFFromHomeDir(collectionId + "/" + Config.collectionFileName, "RDF/XML");		
	}
		
	@GET
	@Path("Collection.ttl") 
	public String getResourceMapAsTurtle(@PathParam("collectionId") final String collectionId) throws Exception {
		return SharedCanvasUtil.getSerializedRDFFromHomeDir(collectionId + "/" + Config.collectionFileName, "TURTLE");				
	}

	@GET
	@Path("Collection.html")  
	public String getResourceMapAsHTML(@PathParam("collectionId") final String collectionId) throws Exception {		
		return RDFUtils.serializeRDFToHTML(SharedCanvasUtil.getSerializedRDFFromHomeDir(collectionId + "/" + Config.collectionFileName, "RDF/XML"));		
	}

}


