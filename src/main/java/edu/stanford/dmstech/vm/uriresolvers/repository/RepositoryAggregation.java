package edu.stanford.dmstech.vm.uriresolvers.repository;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.Path;

import com.sun.jersey.api.NotFoundException;

@Path("/repository/")
public class RepositoryAggregation {

	private static final String DMSTECH_DOC_DIRECTORY = "/Users/jameschartrand/stanfordManifestTestFiles/";
	private static final String MANIFEST_FILE_NAME = "manifest.xml";



		@GET
		@Path("knownrepos")
		@Produces("application/rdf+xml")
		public Response getKnownRepositories() throws URISyntaxException {
				// I guess this should probably be defined as a resource map, representing some 'thing' called knownrepositories
			return Response.seeOther(new URI(".xml")).build();
		}

	
		
		// this could potentially be used for all incoming requests that don't have a file extension, i.e.,
				// requests for an abstract concept, but then we might lose out on a little bit of error checking, i.e.,
				// confirming that the requested uri does is in fact seemingly well formed.
		private Response redirectToRepresentation(String acceptHeader)
				throws URISyntaxException {
			
			// still have to deal with multiple types specified in the Accept header, and weights.		
			
				String fileExtension = ".xml";
				if (acceptHeader.equals("application/rdf+xml")) {
					// default so probably don't need to check
				} else if (acceptHeader.equals("text/turtle;charset=utf-8")) {
					fileExtension = ".ttl";
				} 
				// might want to use Response.seeOther() instead
				return Response.status(303).location(new URI(fileExtension)).build();
		}

	/*	need methods for:
			
			- proxy call, maybe call it remoteResource

			a call for URIs that are something other than the four main resource maps.  
				these could have URIs that indicate they're part of one of the four main resource maps, e.g.,
					/manuscript/{manuscriptId}/imageannotation/annotationId
				so looking it up would amount to loading one of the four text files and getting the bit we need
				by finding the requested URI in the text file.  Actually, I guess we'll always know which
				four files to load simply by knowing the manuscript id.
					
			
		    call to get all annotations
		    
		    call to post annotations, or annotations
		    	- read with jena, validate, and insert into graph for relevant document, then save.
		    	- mint uris, how?
		    	
		    
		    maybe some kind of search  (by annotation author, annotation title, annotation text, etc.)
		    
		    probably need to add some metadata to the four current rdf files.
		    
		    need the configurator
		    	- screens
		    	- calls to accept form params to trigger an import, anything else?  
		    	
		    For imported images, the kickstart will generate URIs, and corresponding representations, for the following six classes:

Manifest
NormalSequence
ImageAnnotations
Canvas
Image
ImageAnnotation


TextAnnotation

but in fact, everything will probably end up in the four main files.  If anything finer grained should be returned then we'll parse the file in Jena and extract what we need.
or maybe return the whole file, and use a hash to indicate which part should be pulled?
		    
		    */
		
		// test method
				@GET
				@Path("headertest")
				@Produces(MediaType.TEXT_PLAIN)
				public Response listHeaders(@Context HttpHeaders headers) {
					StringBuilder stringBuilder = new StringBuilder();
					// to see single header
				String userAgent = headers.getRequestHeader("user-agent").get(0);				 
				// to see all headers, for testing
						for(String header : headers.getRequestHeaders().keySet()){
							stringBuilder.append(header + ":  " + headers.getRequestHeaders().get(header) + "\n");
						}						
				//to return response
				return Response.status(200)
					.entity(stringBuilder.toString())
					.build();
			
				}
		}


