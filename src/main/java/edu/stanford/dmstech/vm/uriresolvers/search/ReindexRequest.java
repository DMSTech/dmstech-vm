package edu.stanford.dmstech.vm.uriresolvers.search;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.solr.client.solrj.SolrServerException;

import edu.stanford.dmstech.vm.indexing.SharedCanvasSOLRIndexer;
import edu.stanford.dmstech.vm.indexing.SharedCanvasTDBManager;

@Path("/reindex")
public class ReindexRequest {

@GET
public Response getSparqlResult(@QueryParam("solr") final boolean reindexSolr, @QueryParam("triple") final boolean reindexTripleStore) {
	
	
	try {
		if (reindexTripleStore) new SharedCanvasTDBManager().reindexAllLocalRDFData();
		if (reindexSolr) new SharedCanvasSOLRIndexer().reindexAllLocalDataInSolr();		
	} catch (IOException e) {
		e.printStackTrace();
		Response.serverError().build();
	} catch (SolrServerException e) {
		e.printStackTrace();
		Response.serverError().build();
	}
	
	return Response.ok().build();
}


}