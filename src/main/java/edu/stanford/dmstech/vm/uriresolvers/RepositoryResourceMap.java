package edu.stanford.dmstech.vm.uriresolvers;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import edu.stanford.dmstech.vm.Config;
import edu.stanford.dmstech.vm.SharedCanvasUtil;

@Path("{fileName: Repository\\.(?i)(xml|ttl|html)}")
public class RepositoryResourceMap {
 
	@Context 
	UriInfo uriInfo;
		
	@GET
	public Response getRepresentation(@PathParam("fileName") final String requestedFileName) throws Exception{
		return SharedCanvasUtil.getSerializedRDFFromHomeDir(Config.repositoryFileName, requestedFileName);
		
	}
	
	
	
	
	


	
}

