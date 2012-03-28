package edu.stanford.dmstech.vm.uriresolvers.ingest;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import edu.stanford.dmstech.vm.Config;

@Path("/ingest/ingestdirectory")
public class IngestDirectoryLookup {

	
	@GET
	@Produces("text/plain")
	public String getIngestDirectoryName() throws IOException {
		return Config.getBaseDirForCollections();
		
	}
}
