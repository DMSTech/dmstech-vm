package edu.stanford.dmstech.vm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.logging.Logger;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamSource;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;
import com.sun.jersey.api.NotFoundException;


public class RDFUtils {

	static Logger logger = Logger.getLogger(RDFUtils.class.getName());
	
	private static final String XSLT_TO_HTML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
			"<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">\n" + 
			"\n" + 
			"<xsl:output method=\"xml\" version=\"1.0\" encoding=\"UTF-8\" indent=\"yes\" omit-xml-declaration=\"no\"/>\n" + 
			"\n" + 
			"<!-- identity template -->\n" + 
			"<xsl:template match=\"@*|node()\">\n" + 
			"    <xsl:copy>\n" + 
			"        <xsl:apply-templates select=\"@*|node()\"/>\n" + 
			"    </xsl:copy>\n" + 
			"</xsl:template>\n" + 
			"</xsl:stylesheet>";
	
	public static String serializeRDFToHTML(String xmlRDF) throws TransformerConfigurationException, TransformerFactoryConfigurationError, TransformerException {
		return transformToHTML(new javax.xml.transform.stream.StreamSource(new StringReader(xmlRDF)));

	}

	public static String getTextFromURI(String uri) throws IOException {
	     return (String) new URL(uri).getContent();
	}
	
	private static String transformToHTML(StreamSource xmlStreamSource)
			throws TransformerFactoryConfigurationError,
			TransformerConfigurationException, TransformerException {
		String htmlResult = null;
				
		try {
			StringWriter writer = new StringWriter();
		    StringReader xsltReader = new StringReader(XSLT_TO_HTML);
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer(
			        new javax.xml.transform.stream.StreamSource(xsltReader));
	
			transformer.transform(
			       xmlStreamSource, 
			        new javax.xml.transform.stream.StreamResult(writer));
	
			htmlResult = writer.toString();
	} catch (Exception e) {
	}
		return htmlResult;
	}
	
	public static String serializeRDFToHTML(File file) throws TransformerConfigurationException, FileNotFoundException, TransformerFactoryConfigurationError, TransformerException {
		return transformToHTML(new javax.xml.transform.stream.StreamSource(new FileReader(file)));

	}
	static public Model loadModelInHomeDir(String relativePathToModel, String format) throws Exception {
		return loadModelInAbsoluteDir(getFileInHomeDir(relativePathToModel).getAbsolutePath(), format);
		
	}
	
	public static Model loadModelFromInputStream(InputStream input, String format) throws Exception {
		 Model model = ModelFactory.createDefaultModel();
		 if (input != null) {
			 model.read(input, null, format);
		 } else {
			 logger.severe("Model File doesn't exist.");
			 throw new Exception("Input stream was empty.");
		 }
		 return model;	
	}
	
	public static Model loadModelInAbsoluteDir(String absolutePath, String format) throws Exception {
		 Model model = ModelFactory.createDefaultModel();
		 InputStream in = FileManager.get().open(absolutePath);
		 if (in != null) {
			 model.read(in, null, format);
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
	

	
	public static String serializeModelToString(Model model, String format) {
		StringWriter stringWriter = new StringWriter();
		  model.write(stringWriter, format);
		return stringWriter.toString();	
	
	}


	
}
