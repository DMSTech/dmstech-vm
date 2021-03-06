package edu.stanford.dmstech.vm.manuscriptgeneration;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.io.*;
import java.util.zip.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;

import edu.stanford.dmstech.vm.Config;
import edu.stanford.dmstech.vm.DMSTechRDFConstants;
import edu.stanford.dmstech.vm.RDFUtils;
import edu.stanford.dmstech.vm.indexing.SharedCanvasSOLRIndexer;
import edu.stanford.dmstech.vm.indexing.SharedCanvasTDBManager;
import gov.lanl.adore.djatoka.DjatokaDecodeParam;
import gov.lanl.adore.djatoka.DjatokaEncodeParam;
import gov.lanl.adore.djatoka.DjatokaException;
import gov.lanl.adore.djatoka.DjatokaExtractProcessor;
import gov.lanl.adore.djatoka.ICompress;
import gov.lanl.adore.djatoka.IExtract;
import gov.lanl.adore.djatoka.kdu.KduCompressExe;
import gov.lanl.adore.djatoka.kdu.KduExtractExe;
import gov.lanl.adore.djatoka.util.IOUtils;
import gov.lanl.adore.djatoka.util.ImageProcessingUtils;
import gov.lanl.adore.djatoka.util.ImageRecord;
import gov.lanl.adore.djatoka.util.ImageRecordUtils;
import gov.lanl.adore.djatoka.util.JP2ImageInfo;
import gov.lanl.adore.djatoka.util.SourceImageFileFilter;


public class SharedCanvasGenerator {

	 static Logger logger = Logger.getLogger(SharedCanvasGenerator.class);

	 SharedCanvas sharedCanvasInstance = null;
	 String directoryPathForManuscript = null;
	 String baseURIForManuscript = null;
	 String manuscriptIdForIngest = null;
	 String collectionIdForIngest = null;
	 
	public static void main(String[] args) {
		
		// Simple log4j configuration to log to the console.
	     BasicConfigurator.configure();
	     logger.setLevel(Level.ERROR);
	     Config config = new Config();
	 	config.initializeThisConfig();
	     
		SharedCanvasGenerator sharedCanvasGenerator = new SharedCanvasGenerator();
		sharedCanvasGenerator.ingestTestManu();		
	}
	
	public void ingestTestManu() {
		try {
			
			String fName = "/Users/jameschartrand/ImageUploadTest.zip";
		    FileInputStream instream = new FileInputStream(fName);
		    
			generateSharedCanvasInDefaultDir(
					"aManu name", 
					"a Manu title", 
					"kq131cs7229", 
					"manuId 35", 
					"altId 3", 
					"a repoName", 
					"institution name 2", 
					"some settlement", 
					"some region name", 
					"some country", 
					"test", 
					"ingested",
					false, 
					null);
			} catch (Exception e) {
				e.printStackTrace();
			}		
	}

	public String generateSharedCanvasInDefaultDir(
			String manuscriptName,
			String manuscriptTitle, 
			String collectionId, 
			String manuscriptId, 
			String alternateId, 
			String repositoryName, 
			String institutionName, 
			String settlementName, 
			String regionName, 
			String countryName, 
			String manuscriptIdForIngest, 
			String collectionIdForIngest,
			boolean parseTitle,
			InputStream inputStream
		) throws Exception {
	
		
		directoryPathForManuscript = Config.getAbsolutePathToManuDirInDefaultCollection(manuscriptIdForIngest);
		
		baseURIForManuscript = Config.getBaseURIForManuscriptInDefaultCollection(manuscriptIdForIngest);
		this.manuscriptIdForIngest = manuscriptIdForIngest;
		if (collectionIdForIngest != null && ! collectionIdForIngest.equals("")) { 
			this.collectionIdForIngest = collectionIdForIngest;
		} else {
			this.collectionIdForIngest = Config.defaultCollection;
		}
		
		if (inputStream != null ) {
			saveZippedFilesToManuscriptDir(inputStream);
		}

		
		sharedCanvasInstance = SharedCanvas.createNewSharedCanvasModel(
				baseURIForManuscript,
				manuscriptName,
				manuscriptTitle,
				collectionId,
				manuscriptId,
				alternateId,
				repositoryName,
				institutionName,
				settlementName,
				regionName,
				countryName);
		
		generateJP2sFromSourceImages();
		generateRDFAndJpgsFromJP2s(parseTitle);
		saveManuscriptRDF();
		indexManuscriptRDF();
		indexInSolr();
		addManuscriptToDefaultCollectionList();
		
		return baseURIForManuscript;
	
	}

	private void saveZippedFilesToManuscriptDir(InputStream inputStream) {
		 File directoryForManuscript = new File(directoryPathForManuscript);
		  if (!directoryForManuscript.exists()) directoryForManuscript.mkdir();
		 	ZipInputStream zippedInputStream = null;
			try {
				byte[] buf = new byte[1024];
				zippedInputStream = new ZipInputStream(inputStream);
				ZipEntry zentry = zippedInputStream.getNextEntry();
				System.out.println("Name of current Zip Entry : " + zentry + "\n");
				while (zentry != null) {
				
				  String entryName = zentry.getName();
				  
				  System.out.println("Name of  Zip Entry : " + entryName);
				  
				  if ( ! zentry.isDirectory() && ! entryName.contains("/") ) {
				 
					  File fileToSave = new File(directoryForManuscript, entryName);
					  FileOutputStream outstream = new FileOutputStream(fileToSave);
					  int n;
	
					  while ((n = zippedInputStream.read(buf, 0, 1024)) > -1) {
					    outstream.write(buf, 0, n);
	
					  }
					  System.out.println("Extracted Image File : "
					      + entryName);
					  outstream.close();
				  }
				  zippedInputStream.closeEntry();
				  zentry = zippedInputStream.getNextEntry();
				  
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				
				try {
					zippedInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		    
		
	}

	private void generateJP2sFromSourceImages() throws Exception {
		
		try {
			File inputDirectory = new File(directoryPathForManuscript);
			DjatokaEncodeParam p = new DjatokaEncodeParam();
			ICompress jp2 = new KduCompressExe();
			
			
			if (! inputDirectory.exists() ) {
				throw new Exception("The image directory specified doesn't exist.");
			} else if ( ! inputDirectory.isDirectory()) {
				throw new Exception("The image directory specified isn't a directory.");
			}
			ArrayList<File> files = IOUtils.getFileList(directoryPathForManuscript, new SourceImageFileFilter(), false);
			for (File f : files) {
				ImageRecord dim = ImageRecordUtils.getImageDimensions(f.getAbsolutePath());
				p.setLevels(ImageProcessingUtils.getLevelCount(dim.getWidth(), dim.getHeight()));
				String fileNameWithoutExtension = f.getName().substring(0, f.getName().lastIndexOf("."));
				String jp2FileName =  fileNameWithoutExtension + ".jp2";
			    File outFile = new File(directoryPathForManuscript, jp2FileName);
			    if (! outFile.exists()) {
			    	// don't overwrite
			    	jp2.compressImage(f.getAbsolutePath(), outFile.getAbsolutePath(), p);
			    }
			    
		
			   
			    }
						
		} catch (DjatokaException e) {
			e.printStackTrace();
			throw new Exception("Djatoka exception while converting images.  Cause: " + e.getMessage());
		}			
	}
	
	private void generateRDFAndJpgsFromJP2s(boolean parseTitle) throws Exception {
		
		DjatokaDecodeParam p = new DjatokaDecodeParam();
	    String format = "image/jpeg";    	
		IExtract ex = new KduExtractExe();
		DjatokaExtractProcessor e = new DjatokaExtractProcessor(ex);
		
		FileFilter jp2ImageFilter = new jp2FileFilter();
		ArrayList<File> files = IOUtils.getFileList(directoryPathForManuscript, jp2ImageFilter, false);
		for (File f : files) {
			
			String jp2FileName = f.getName();
			String fileNameWithoutExtension = jp2FileName.substring(0, f.getName().lastIndexOf("."));		
			
			String imageFileName = jp2FileName;
		    String pageTitle = null;		    	
		    if (parseTitle) {
		    	// if the image files have been named like 14_page14, i.e. pageNumber_pageTitle
		    	pageTitle = fileNameWithoutExtension.substring(fileNameWithoutExtension.indexOf("_"), fileNameWithoutExtension.length() + 1);
		    	  }
		    JP2ImageInfo imageInfo = new JP2ImageInfo(f);
		    ImageRecord dim = imageInfo.getImageRecord();
		    String width = String.valueOf(dim.getWidth());
		    String height = String.valueOf(dim.getHeight());
		 // generate the jpg if it doesn't already exist
		    String pathTojp2 = f.getAbsolutePath();
		    File jpgFile = new File(directoryPathForManuscript, fileNameWithoutExtension + ".jpg");
		 	if ( !jpgFile.exists()) {
		 		String pathTojpg = jpgFile.getAbsolutePath();
		 		e.extractImage(pathTojp2, pathTojpg,  p, format);
		 	}		
		 			
		//    System.out.println("width:  " + width + " and height: " + height + " and page title: " + pageTitle + " and filename: " + imageFileName);
		    sharedCanvasInstance.addImageToSharedCanvas(imageFileName, pageTitle, width, height);					   
		}
	}
	
	

	
	private void saveManuscriptRDF() {

		String format = "N-TRIPLE";
	//	String collectionId = Config.defaultCollection;
		String sequenceId = Config.normalSequenceFileName.substring(0, Config.normalSequenceFileName.lastIndexOf("."));
	//	String manuscriptId = manuscriptSubDirectory;
		File dirForRDF = new File(directoryPathForManuscript, "rdf");
		if (!dirForRDF.exists()) dirForRDF.mkdir();
		File dirForSequences = new File(dirForRDF, "sequences");
		if (!dirForSequences.exists()) dirForSequences.mkdir();
		String manifestFilePath = new File(dirForRDF, Config.manifestFileName).getAbsolutePath();
		String imageAnnotationsFilePath = new File(dirForRDF, Config.imageAnnotationFileName).getAbsolutePath();
		String normalSequenceFilePath = Config.getAbsolutePathToManuscriptSequenceSourceFile( collectionIdForIngest, manuscriptIdForIngest, sequenceId);
		
		sharedCanvasInstance.serializeManifestResourceMapToFile(manifestFilePath, format);
		sharedCanvasInstance.serializeImageAnnoResourceMapToFile(imageAnnotationsFilePath, format);
		sharedCanvasInstance.serializeNormalSequenceResourceMapToFile(normalSequenceFilePath, format);
		
	}
	
	private void indexManuscriptRDF() {
		String manifestFilePath = directoryPathForManuscript + "/rdf/" + Config.manifestFileName;
		String imageAnnotationsFilePath = directoryPathForManuscript + "/rdf/" + Config.imageAnnotationFileName;
		String normalSequenceFilePath = directoryPathForManuscript + "/rdf/sequences/" + Config.normalSequenceFileName;
		
		
		SharedCanvasTDBManager scTDBManager = new SharedCanvasTDBManager();
		scTDBManager.loadFileIntoMainRepoTBDDataset(manifestFilePath);
		scTDBManager.loadFileIntoMainRepoTBDDataset(imageAnnotationsFilePath);
		scTDBManager.loadFileIntoMainRepoTBDDataset(normalSequenceFilePath);
	}
	
	private void indexInSolr() throws IOException, SolrServerException {
		// NOTE:  for the moment we are assuming the manifest's uri is the URI for the manuscript
		new SharedCanvasSOLRIndexer().indexManuscript(sharedCanvasInstance.getManifestAggregationURI());
	}

	private void addManuscriptToDefaultCollectionList() throws Exception {
		
		DMSTechRDFConstants rdfConstants = DMSTechRDFConstants.getInstance();
		
		
		String absolutePathToDefaultCollectionRM = Config.getAbsolutePathToDefaultCollectionRM();
		String format = "N-TRIPLE";
		Model collectionManifestModel = RDFUtils.loadModelInAbsoluteDir(absolutePathToDefaultCollectionRM, format);

		Resource manifestAggregation = collectionManifestModel.createResource(sharedCanvasInstance.getManifestAggregationURI())
				.addProperty(RDF.type, rdfConstants.oreAggregationClass)		
				.addProperty(RDF.type, rdfConstants.scManifestClass);
				
		collectionManifestModel.createResource(sharedCanvasInstance.getManifestRMURI())
				.addProperty(RDF.type, rdfConstants.oreResourceMapClass)
				.addProperty(rdfConstants.oreDescribes, manifestAggregation)
				.addProperty(DC.format, "application/rdf+xml");						
		
		ResIterator resIter = collectionManifestModel.listSubjectsWithProperty(RDF.type, rdfConstants.scCollectionClass);
		while (resIter.hasNext()){
			Resource aggregationResource = resIter.nextResource();	
			aggregationResource.addProperty(rdfConstants.oreAggregates, manifestAggregation);
		}
		
		RDFUtils.serializeModelToFile(collectionManifestModel, absolutePathToDefaultCollectionRM, "N-TRIPLE");
		
		
	}
	
	
	
	/*		
	// to test imaging 
	ImageRecord dim = ImageRecordUtils.getImageDimensions("/Users/jameschartrand/STANFORD_KVM_HOME/testImages/test.jpg");
	p.setLevels(ImageProcessingUtils.getLevelCount(dim.getWidth(), dim.getHeight()));
	BufferedImage bi = new DjatokaReader().open("/Users/jameschartrand/STANFORD_KVM_HOME/testImages/test.jpg");
//	TIFWriter w = new TIFWriter();
	File f = new File("/Users/jameschartrand/STANFORD_KVM_HOME/testImages/testy.tif");
	FileOutputStream fos = new FileOutputStream(f);
	if (bi != null) {
		BufferedOutputStream bos = null;
		try {
			bos = new BufferedOutputStream(fos);
			ImageIO.write(bi, "tif", bos);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e,e);
		} finally {
			if (bos != null) {
				try {
					bos.flush();
					bos.close();
				} catch (IOException e) {
					logger.error(e,e);
				//	throw new FormatIOException(e);
				}
			}
		}
	}
	//BufferedOutputStream bos = new BufferedOutputStream(fos);
//	w.write(bi, fos);
//	fos.close();
	
//	jp2.compressImage("/Users/jameschartrand/STANFORD_KVM_HOME/testImages/test.jpg", "/Users/jameschartrand/STANFORD_KVM_HOME/testImages/test.jp2", p);
	
	// end test
*/		
	
	
	
}
