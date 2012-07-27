package edu.stanford.dmstech.vm.uriresolvers;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import edu.stanford.dmstech.vm.Config;
import edu.stanford.dmstech.vm.SharedCanvasUtil;

@Path("/transactions/{fileName: (.*\\.html$|.*\\.xml$|.*\\.ttl$)}")
public class TransactionRepresentation {
	
		@GET
			public Response getRepresentation(
					@Context UriInfo uriInfo,
					@PathParam("fileName") final String requestedFileName
					) throws Exception {
			String originalRequest = uriInfo.getAbsolutePath().toASCIIString();
			if (originalRequest.toLowerCase().endsWith(".html")) return SharedCanvasUtil.redirectToHTMLPage(originalRequest);
			
			
			String transactionId = requestedFileName.substring(0, requestedFileName.lastIndexOf("."));
			String pathToSourceRDF = Config.getAbsolutePathToTransactionsFile(transactionId);
			return SharedCanvasUtil.getSerializedRDFFromDir(pathToSourceRDF, requestedFileName);
	
			}


}
