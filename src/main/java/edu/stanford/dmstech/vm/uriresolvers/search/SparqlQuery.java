package edu.stanford.dmstech.vm.uriresolvers.search;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import edu.stanford.dmstech.vm.indexing.SharedCanvasTDBManager;

@Path("/sparql")
public class SparqlQuery {

@GET
public String getSparqlResult(@QueryParam("q") final String queryString) {
	return new SharedCanvasTDBManager().queryMainDataset(queryString);
}

}
