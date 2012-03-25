package edu.stanford.dmstech.vm.manuscriptgeneration;

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
	
	public boolean generateSharedCanvas(String directoryPath, String baseURIForIds, boolean parseTitle) throws Exception {
	
		boolean success = false;
		
		try {
			SharedCanvasModel sharedCanvasModel = SharedCanvasModel.createNewSharedCanvasModel(baseURIForIds);
			File inputDirectory = new File(directoryPath);
			DjatokaEncodeParam p = new DjatokaEncodeParam();
			ICompress jp2 = new KduCompressExe();
	/*		
			// to test
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
			    String imageURI = baseURIForIds + jp2FileName;
			    String pageTitle = null;		    	
			    if (parseTitle) {
			    	// if the image files have been named like 14_page14, i.e. pageNumber_pageTitle
			    	pageTitle = fileNameWithoutExtension.substring(fileNameWithoutExtension.indexOf("_"), fileNameWithoutExtension.length() + 1);
			    	  } 			    	
			    sharedCanvasModel.addImageToSharedCanvas(imageURI, pageTitle, String.valueOf(dim.getWidth()), String.valueOf(dim.getHeight()));					
				   
			    }
			String format = "RDF/XML";
			sharedCanvasModel.serializeManifestResourceMapToFile(directoryPath + "/rdf/Manifest.xml", format);
			sharedCanvasModel.serializeImageAnnoResourceMapToFile(directoryPath + "/rdf/ImageAnnotations.xml", format);
			sharedCanvasModel.serializeNormalSequenceResourceMapToFile(directoryPath + "/rdf/NormalSequence.xml", format);
		} catch (DjatokaException e) {
			e.printStackTrace();
			throw new Exception("Djatoka exception while converting images.  Cause: " + e.getMessage());
		}
		
		return success;
	}
	
	
	
	
}