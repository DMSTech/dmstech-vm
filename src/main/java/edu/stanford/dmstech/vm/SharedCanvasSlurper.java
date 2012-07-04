package edu.stanford.dmstech.vm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.util.FileManager;

public class SharedCanvasSlurper {

	private static ArrayList<String> collectionsToSlurp = new ArrayList<String>(); 
	
	static {
		collectionsToSlurp.add("Stanford");
		collectionsToSlurp.add("Oxford");
		collectionsToSlurp.add("Parker");
		collectionsToSlurp.add("BnF");
	}
	
	private String formattedUriForCollection = "http://dms-data.stanford.edu/%s/Collection.xml";
	File collectionsDir = new File("/Users/jameschartrand/SHARED_CANVAS_HOME_sul-dms-demo/collections/");
	
	FileManager jenaFileManager = new FileManager();
	DMSTechRDFConstants rdfConst = DMSTechRDFConstants.getInstance();
	

	public static void main(String args[]) throws IOException {
		
		SharedCanvasSlurper slurper = new SharedCanvasSlurper();
		slurper.makeCollectionDirs();
		slurper.slurpCollections();
	}

	
	private void makeCollectionDirs() {
		for (String collectionName : collectionsToSlurp) {
			File collectionDir = new File(collectionsDir, collectionName);
			if (! collectionDir.exists()) collectionDir.mkdir();
		}
		
	}


	private void slurpCollections() throws IOException {
		for (String collectionName : collectionsToSlurp) {
			
			String collectionURI = String.format(formattedUriForCollection, collectionName);	
			Model collectionModel = jenaFileManager.loadModel(collectionURI);
			NodeIterator nodeIter = collectionModel.listObjectsOfProperty(rdfConst.oreAggregates);
			while (nodeIter.hasNext()) {
				Resource manifestResource = nodeIter.nextNode().asResource();
				slurpManifest(manifestResource, collectionName);	
				
			}
			File collectionFile = new File(new File(collectionsDir, collectionName), "Collection.nt");
			convertAndSave(collectionFile, collectionModel);
		}
		
		
	
	}

	private void slurpManifest(Resource manifestResource, String collectionName) throws IOException {
		String manifestURI = manifestResource.getURI();
		System.out.println("slurping: " + manifestURI);
		
		String manuscriptBaseURI = manifestURI.substring(0, manifestURI.lastIndexOf("/Manifest"));
		String manuscriptId = manifestURI.substring(manifestURI.lastIndexOf(collectionName) + collectionName.length() + 1, manifestURI.lastIndexOf("/Manifest"));
		makeManuscriptDir(collectionName, manuscriptId);
		
		Model manifestModel = jenaFileManager.loadModel(manifestURI + ".xml");		
		
		
		NodeIterator nodeIter = manifestModel.listObjectsOfProperty(rdfConst.oreAggregates);
		while (nodeIter.hasNext()) {
			Resource resource = nodeIter.nextNode().asResource();
			if (resource.getURI().endsWith("ImageAnnotations")) {
				slurpImageAnnotations(resource, manuscriptId, collectionName);
			} else {
				// deal with the sequence
				// add the optimized sequence uri
				String oldSequenceURI = resource.getURI();
				String newSequenceURI = manuscriptBaseURI + "/sequences/NormalSequence";
				String optimizedSequenceURI = manuscriptBaseURI + "/sequences/optmized/NormalSequence.json";
				Resource optimzedResource = manifestModel.getResource(optimizedSequenceURI);
				manifestModel.add(resource, rdfConst.scHasOptimizedSerialization, optimzedResource);
				// and assert the sequence endpoint
				String sequenceEnpoint = manuscriptBaseURI + "/sequences";
				Resource endpointResource = manifestModel.getResource(sequenceEnpoint);
				manifestModel.add(manifestResource, rdfConst.scNewSequenceEnpoint, endpointResource);
				// rename the sequence uri to match our local convention
				com.hp.hpl.jena.util.ResourceUtils.renameResource(manifestModel.getResource(oldSequenceURI), newSequenceURI);
				// and now deal with the sequence itself
				slurpNormalSequence(oldSequenceURI, manuscriptId, collectionName, manuscriptBaseURI);
			}
		}
				
		File manifestFile = new File(makeManuscriptDir(collectionName, manuscriptId), "rdf/Manifest.nt");
		convertAndSave(manifestFile, manifestModel);
	
		
	}


	private void slurpNormalSequence(String sequenceURI, String manuscriptId,
			String collectionName, String manuscriptBaseURI) throws IOException {
			Model sequenceModel = jenaFileManager.loadModel(sequenceURI + ".xml");
		
			
		String optimizedSequenceURI = manuscriptBaseURI + "/sequences/optmized/NormalSequence.json";
		Resource optimzedResource = sequenceModel.getResource(optimizedSequenceURI);
		Resource sequenceResource = sequenceModel.getResource(sequenceURI);
		sequenceModel.add(sequenceResource, rdfConst.scHasOptimizedSerialization, optimzedResource);
	
		// rename the sequence uri to match our local convention
		String newSequenceURI = manuscriptBaseURI + "/sequences/NormalSequence";
		com.hp.hpl.jena.util.ResourceUtils.renameResource(sequenceResource, newSequenceURI);
				
		File sequenceFile = new File(makeManuscriptDir(collectionName, manuscriptId), "rdf/sequences/NormalSequence.nt");
		convertAndSave(sequenceFile, sequenceModel);
		
	}

	private void slurpImageAnnotations(Resource stringAnnosResource, String manuscriptId,
			String collectionName) throws IOException {
		String imageAnnosURI = stringAnnosResource.getURI();
		Model imageAnnosModel = jenaFileManager.loadModel(imageAnnosURI + ".xml");
		File sequenceFile = new File(makeManuscriptDir(collectionName, manuscriptId), "rdf/ImageAnnotations.nt");
		convertAndSave(sequenceFile, imageAnnosModel);
		
	}
	
	private void convertAndSave(File fileToSave, Model model) throws IOException {
		String convertedRDFToSAve = RDFUtils.serializeModelToString(model, "N-TRIPLE").replaceAll("dms-data.stanford.edu/", "sul-dms-demo.stanford.edu/dms/sc/");
		FileUtils.writeStringToFile(fileToSave, convertedRDFToSAve, "UTF-8");
	}

	


	private File makeManuscriptDir(String collectionName, String manuscriptId) {
		File manuscriptDir = new File(new File(collectionsDir, collectionName), manuscriptId);
		if (! manuscriptDir.exists()) manuscriptDir.mkdir();	
		return manuscriptDir;
		
	}
		
		
	}
