package edu.stanford.dmstech.vm.uriresolvers.lookup;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import edu.stanford.dmstech.vm.Config;

@Path("/lookup/remote")
public class RemoteRepositoriesLookup {

	
	@GET
	@Produces("text/plain")
	public String getCentralReposListURI() throws IOException {
		return Config.getCentralReposListURI();
		
	}
}
