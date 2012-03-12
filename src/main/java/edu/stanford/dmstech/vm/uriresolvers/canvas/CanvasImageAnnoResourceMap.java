package edu.stanford.dmstech.vm.uriresolvers.canvas;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import edu.stanford.dmstech.vm.Config;
import edu.stanford.dmstech.vm.uriresolvers.ResourceMapSerialization;


public class CanvasImageAnnoResourceMap {
 	
	@Path("/{collectionId}/{manuscriptId}/{canvasId}/")
	public ResourceMapSerialization redirectToResourceMap(
			@PathParam("collectionId") final String collectionId,
			@PathParam("manuscriptId") final String manuscriptId,
			@PathParam("canvasId") final String canvasId) {
		String pathToRMInHomeDir = "collections/" + collectionId + "/manuscripts/" + manuscriptId + "/canvases/" + canvasId + "/rdf/" + Config.getImageAnnotationFileName();	
		return new ResourceMapSerialization(pathToRMInHomeDir);
	}

}



