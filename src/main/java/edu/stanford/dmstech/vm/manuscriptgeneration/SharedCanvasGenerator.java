package edu.stanford.dmstech.vm.manuscriptgeneration;

import gov.lanl.adore.djatoka.DjatokaEncodeParam;
import gov.lanl.adore.djatoka.DjatokaException;
import gov.lanl.adore.djatoka.ICompress;
import gov.lanl.adore.djatoka.kdu.KduCompressExe;
import gov.lanl.adore.djatoka.util.IOUtils;
import gov.lanl.adore.djatoka.util.ImageProcessingUtils;
import gov.lanl.adore.djatoka.util.ImageRecord;
import gov.lanl.adore.djatoka.util.ImageRecordUtils;
import gov.lanl.adore.djatoka.util.SourceImageFileFilter;

import java.io.File;
import java.util.ArrayList;


public class SharedCanvasGenerator {

	public boolean generateSharedCanvas(String directoryPath, String baseURIForIds, boolean parseTitleAndPageNum) throws Exception {
	
		boolean success = false;
		
		try {
			SharedCanvasModel sharedCanvasModel = SharedCanvasModel.createNewSharedCanvasModel(baseURIForIds);
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
			    String imageURI = baseURIForIds + jp2FileName;
			    String pageTitle = null;
		    	Integer pageNumber = null;		    	
			    if (parseTitleAndPageNum) {
			    	// if the image files have been named like page14_14, i.e. pageTitle_pageNumber
			    	pageTitle = fileNameWithoutExtension.substring(0, jp2FileName.indexOf("_"));
			    	pageNumber = Integer.valueOf(fileNameWithoutExtension.substring(fileNameWithoutExtension.indexOf("_"), fileNameWithoutExtension.length() + 1));
			    } 			    	
			    sharedCanvasModel.addImageToSharedCanvas(imageURI, pageTitle, pageNumber, String.valueOf(dim.getWidth()), String.valueOf(dim.getHeight()));					
				   
			    }
		} catch (DjatokaException e) {
			e.printStackTrace();
			throw new Exception("Djatoka exception while converting images.  Cause: " + e.getMessage());
		}
		
		return success;
	}
	
	
	
	
}
