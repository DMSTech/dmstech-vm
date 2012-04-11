package edu.stanford.dmstech.vm.uriresolvers;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import edu.stanford.dmstech.vm.Config;
import edu.stanford.dmstech.vm.SharedCanvasUtil;


@Path("/{collectionId}/{manuscriptId}/{fileName: ImageAnnotations\\.(?i)(xml|ttl|html)}")
public class CollectionResourceMap {

	@GET
		public Response getRepresentation(
				@PathParam("collectionId") final String collectionId,
				@PathParam("manuscriptId") final String manuscriptId,
				@PathParam("fileName") final String requestedFileName
				) throws Exception {
			return SharedCanvasUtil.getSerializedRDFFromHomeDir(Config.collectionSubDir + "/" + collectionId + "/" + Config.collectionFileName, requestedFileName);
				
		}

	

}


