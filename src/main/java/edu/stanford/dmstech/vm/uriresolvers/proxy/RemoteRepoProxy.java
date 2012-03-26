package edu.stanford.dmstech.vm.uriresolvers.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.io.IOUtils;

	/**
	 * Sample call to this proxy:

	http://localhost:8080/remoteproxy?url=http%3A//sports.espn.go.com/rpm/daytona2008/columns/story%3Fcolumnist%3Dblount_terry%26id%3D3236786

	This site can be used to encode the 'url'
	http://meyerweb.com/eric/tools/dencoder/
	and provides a js routine for encoding as well

	 */

	public class RemoteRepoProxy {

		// no POSTS or PUTs allowed for the moment.
		@Path("/remoteproxy")
		@GET
		public Response forwardCall(@QueryParam("url") final String target){
				if(target==null){
				return	Response.noContent().build();
			}
				//final String contentType = null;
    		  StreamingOutput streamingOutput = new StreamingOutput() {
    		    public void write(OutputStream outputStream) throws IOException {
    		    	InputStream inputStream = null;
    		    try {
    		    
    		    	String encodedTarget = org.apache.commons.httpclient.util.URIUtil.encodePathQuery(target, "UTF-8");
    				HttpClient client = new HttpClient();
    				GetMethod method = new GetMethod(encodedTarget);	    
    			    
    			    method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
    			    		new DefaultHttpMethodRetryHandler(3, false));

    			      int statusCode = client.executeMethod(method);

    			      if (statusCode != HttpStatus.SC_OK) {
    			        System.err.println("Remote method call failed: " + method.getStatusLine());
    			        throw new IOException("Remote method call failed: " + method.getStatusLine());
    			        
    			      } else {
    			    	  //contentType = method.getResponseHeader("Content-Type").getValue();
    			    	  inputStream = method.getResponseBodyAsStream();
    			      		IOUtils.copy(inputStream, outputStream);
    			      }
    		    } finally {
    				if(inputStream != null)
    					inputStream.close();
    				
    			}
    		   }
    		  };
		    	
    		  
    		  
    		  return Response.ok(streamingOutput).build();
    		  

		}
	}
