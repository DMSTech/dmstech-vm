package edu.stanford.dmstech.vm.uriresolvers.admin;

import java.io.IOException;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.solr.client.solrj.SolrServerException;

import edu.stanford.dmstech.vm.SharedCanvasUtil;
import edu.stanford.dmstech.vm.indexing.SharedCanvasSOLRIndexer;
import edu.stanford.dmstech.vm.indexing.SharedCanvasTDBManager;

@Path("/reset")
public class ResetRequest {

@POST
public Response getSparqlResult(
		@FormParam("all") final boolean shouldDeleteAll, 
		@FormParam("solr") final boolean shouldDeleteSolr,
		@FormParam("triple") final boolean shouldDeleteTriple,
		@FormParam("logs") final boolean shouldDeleteLogs,
		@FormParam("ingested") final boolean shouldDeleteIngestedManuscripts,
		@FormParam("transactions") final boolean shouldDeleteTransactions,
		@FormParam("annos") final boolean shouldDeleteAnnotations
		
		) {
	
	
	try {
		if (shouldDeleteAll) {
			deleteSolr();
			deleteTripleStore();
			deleteIngestedManuscripts();
			deleteAnnotations();
			deleteLogs();
			deleteTransactions();
		} else {
			if (shouldDeleteSolr) {
		
			deleteSolr();
		}  
			if (shouldDeleteTriple) {
			deleteTripleStore();
		}  
			if (shouldDeleteLogs) {
			deleteLogs();
		}  
			if (shouldDeleteIngestedManuscripts) {
			deleteIngestedManuscripts();
		}  
			if (shouldDeleteAnnotations) {
			deleteAnnotations();
		}
			if (shouldDeleteTransactions) {
				deleteTransactions();
			}
		}
	
	} catch (Exception e) {
		e.printStackTrace();
		Response.serverError().build();
	} 
	
	return Response.ok().build();
}

private void deleteTransactions() {
	new SharedCanvasUtil().deleteTransactionFiles();
	
}

private void deleteAnnotations() {
	new SharedCanvasUtil().deleteAnnotationFiles();
	
}

private void deleteIngestedManuscripts() {
	new SharedCanvasUtil().deleteIngestedManuscripts();
	
}

private void deleteLogs() {
	new SharedCanvasUtil().deleteLogFiles();
	
}

private void deleteTripleStore() {
	new SharedCanvasTDBManager().emptyMainTDBIndex();
	
}

private void deleteSolr() throws SolrServerException, IOException {
	new SharedCanvasSOLRIndexer().deleteAll();
}
}
