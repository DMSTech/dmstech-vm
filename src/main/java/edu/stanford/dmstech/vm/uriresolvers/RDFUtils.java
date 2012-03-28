package edu.stanford.dmstech.vm.uriresolvers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.logging.Logger;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;
import com.sun.jersey.api.NotFoundException;

import edu.stanford.dmstech.vm.Config;

public class RDFUtils {

	static Logger logger = Logger.getLogger(RDFUtils.class.getName());
	
	static public Model loadModelInHomeDir(String relativePathToModel) throws Exception {		
		 Model model = ModelFactory.createDefaultModel();
		 InputStream in = FileManager.get().open(getFileInHomeDir(relativePathToModel).getAbsolutePath());
		 if (in != null) {
			 model.read(in, null);
		 } else {
			 logger.severe("Model File doesn't exist.");
			 throw new Exception("Model File doesn't exist.");
		 }
		 return model;	
	}
				
	static public File getFileInHomeDir(String pathToModel) {
		final File file = new File(
				Config.homeDir, 
				pathToModel
				);
		if (!file.exists()) {
			throw new NotFoundException("File, " + file.getAbsolutePath() + ", is not found");
		}
		return file;
	}
	
	public static void serializeModelToHomeDir(Model model, String relativePathToFile, String format) {
		String fullPath = new File(
				Config.homeDir, 
				relativePathToFile
				).getAbsolutePath();
		serializeModelToFile(model, fullPath, format);
	}
	
	public static void serializeModelToFile(Model model, String pathToFile, String format) {
		//		model.write(System.out, "TURTLE");
		FileOutputStream fout = null;
		try {
			fout = new FileOutputStream(pathToFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
			model.write(fout, format);
	}
}
