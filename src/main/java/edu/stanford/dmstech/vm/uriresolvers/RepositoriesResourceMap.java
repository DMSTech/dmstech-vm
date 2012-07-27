package edu.stanford.dmstech.vm.uriresolvers;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import edu.stanford.dmstech.vm.Config;
import edu.stanford.dmstech.vm.SharedCanvasUtil;

@Path("{fileName: Repositories\\.(?i)(xml|ttl|html)}")
public class RepositoriesResourceMap {
 
	@Context 
	UriInfo uriInfo;
		
	@GET
	public Response getRepresentation(@PathParam("fileName") final String requestedFileName) throws Exception{
		
		String originalRequest = uriInfo.getAbsolutePath().toASCIIString();
		if (originalRequest.toLowerCase().endsWith(".html")) return SharedCanvasUtil.redirectToHTMLPage(originalRequest);
		
		
		return SharedCanvasUtil.getSerializedRDFFromHomeDir(Config.repositoriesFileName, requestedFileName);
		
	}
	
	
	
	
	


	
}

