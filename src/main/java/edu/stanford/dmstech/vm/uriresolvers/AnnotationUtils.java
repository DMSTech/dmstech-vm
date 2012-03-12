package edu.stanford.dmstech.vm.uriresolvers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamSource;

public class AnnotationUtils {

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
}
