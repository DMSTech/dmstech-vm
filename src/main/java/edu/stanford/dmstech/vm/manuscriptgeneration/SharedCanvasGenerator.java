package edu.stanford.dmstech.vm.manuscriptgeneration;

import edu.stanford.dmstech.vm.indexing.SharedCanvasTBDIndexer;
import gov.lanl.adore.djatoka.DjatokaEncodeParam;
import gov.lanl.adore.djatoka.DjatokaException;
import gov.lanl.adore.djatoka.ICompress;
import gov.lanl.adore.djatoka.io.reader.DjatokaReader;
import gov.lanl.adore.djatoka.io.writer.TIFWriter;
import gov.lanl.adore.djatoka.kdu.KduCompressExe;
import gov.lanl.adore.djatoka.util.IOUtils;
import gov.lanl.adore.djatoka.util.ImageProcessingUtils;
import gov.lanl.adore.djatoka.util.ImageRecord;
import gov.lanl.adore.djatoka.util.ImageRecordUtils;
import gov.lanl.adore.djatoka.util.SourceImageFileFilter;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;


public class SharedCanvasGenerator {

	 static Logger logger = Logger.getLogger(SharedCanvasGenerator.class);

	public static void main(String[] args) {
		
		// Simple log4j configuration to log to the console.
	     BasicConfigurator.configure();
	     logger.setLevel(Level.ALL);
	     
		SharedCanvasGenerator sharedCanvasGenerator = new SharedCanvasGenerator();
		try {
			sharedCanvasGenerator.generateSharedCanvas("/Users/jameschartrand/STANFORD_KVM_HOME/testImages", "http://localhost:8080/vm/", false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void generateSharedCanvas(String directoryPath, String baseURIForIds, boolean parseTitle) throws Exception {
	

		generateJP2sFromSourceImages(directoryPath);
		generateRDFFromJP2s(directoryPath, baseURIForIds, parseTitle);
		

	}

	private void generateJP2sFromSourceImages(String directoryPath) throws Exception {
		
		try {
			File inputDirectory = new File(directoryPath);
			DjatokaEncodeParam p = new DjatokaEncodeParam();
			ICompress jp2 = new KduCompressExe();

			
			if (! inputDirectory.exists() ) {
				throw new Exception("The image directory specified doesn't exist.");
			} else if ( ! inputDirectory.isDirectory()) {
				throw new Exception("The image directory specified isn't a directory.");
			}
			ArrayList<File> files = IOUtils.getFileList(directoryPath, new SourceImageFileFilter(), false);
			for (File f : files) {
				ImageRecord dim = ImageRecordUtils.getImageDimensions(f.getAbsolutePath());
				p.setLevels(ImageProcessingUtils.getLevelCount(dim.getWidth(), dim.getHeight()));
				String fileNameWithoutExtension = f.getName().substring(0, f.getName().indexOf("."));
				String jp2FileName =  fileNameWithoutExtension + ".jp2";
			    File outFile = new File(directoryPath, jp2FileName);
			    jp2.compressImage(f.getAbsolutePath(), outFile.getAbsolutePath(), p);
			    
			    }
						
		} catch (DjatokaException e) {
			e.printStackTrace();
			throw new Exception("Djatoka exception while converting images.  Cause: " + e.getMessage());
		}
				
	}
	
	private void generateRDFFromJP2s(String directoryPath, String baseURIForIds, boolean parseTitle) {
		
		FileFilter jp2ImageFilter = new jp2FileFilter();
		ArrayList<File> files = IOUtils.getFileList(directoryPath, jp2ImageFilter, false);
		for (File f : files) {
			
			SharedCanvasModel sharedCanvasModel = SharedCanvasModel.createNewSharedCanvasModel(baseURIForIds);
			String jp2FileName = f.getName();
			String fileNameWithoutExtension = jp2FileName.substring(0, f.getName().indexOf("."));			
			String imageURI = baseURIForIds + jp2FileName;
		    String pageTitle = null;		    	
		    if (parseTitle) {
		    	// if the image files have been named like 14_page14, i.e. pageNumber_pageTitle
		    	pageTitle = fileNameWithoutExtension.substring(fileNameWithoutExtension.indexOf("_"), fileNameWithoutExtension.length() + 1);
		    	  }
		    ImageRecord dim = ImageRecordUtils.getImageDimensions(f.getAbsolutePath());
		    sharedCanvasModel.addImageToSharedCanvas(imageURI, pageTitle, String.valueOf(dim.getWidth()), String.valueOf(dim.getHeight()));					
		    saveAndIndexRDFForManuscript(directoryPath, sharedCanvasModel);
		}
	}
	
	private void saveAndIndexRDFForManuscript(String directoryPath,
			SharedCanvasModel sharedCanvasModel) {
		//"RDF/XML", "RDF/XML-ABBREV", "N-TRIPLE", "N3" and "TURTLE". 
		// for N3 output the language can be specified as: 
		// "N3-PP", "N3-PLAIN" or "N3-TRIPLE", which controls the style of N3 produced.
		String format = "N-TRIPLE";
		String manifestFilePath = directoryPath + "/rdf/Manifest.nt";
		String imageAnnotationsFilePath = directoryPath + "/rdf/ImageAnnotations.nt";
		String normalSequenceFilePath = directoryPath + "/rdf/NormalSequence.nt";
		sharedCanvasModel.serializeManifestResourceMapToFile(manifestFilePath, format);
		sharedCanvasModel.serializeImageAnnoResourceMapToFile(imageAnnotationsFilePath, format);
		sharedCanvasModel.serializeNormalSequenceResourceMapToFile(normalSequenceFilePath, format);
		
		String mainRepositoryTBDIndex = "/Users/jameschartrand/STANFORD_KVM_HOME/repository_tbd_index";
		SharedCanvasTBDIndexer scTbdIndexer = new SharedCanvasTBDIndexer();
		scTbdIndexer.loadFileIntoTBDModel(mainRepositoryTBDIndex, manifestFilePath);
		scTbdIndexer.loadFileIntoTBDModel(mainRepositoryTBDIndex, imageAnnotationsFilePath);
		scTbdIndexer.loadFileIntoTBDModel(mainRepositoryTBDIndex, normalSequenceFilePath);

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
