package edu.stanford.dmstech.vm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

public class Config implements ServletContextListener {

	private static final String LOG_FILE_NAME = "log.txt";
	private final static Logger LOGGER = Logger.getLogger(Config.class.getName());
	public static final String HOME_DIR_ENV_VAR = "KVM_HOME";
	
	public static  String centralRepositoryURL = null;
	public static  String normalSequenceFileName = null;
	public static  String manifestFileName = null;
	public static  String imageAnnotationFileName = null;
	public static  String textAnnotationFileName = null;
	public static String zoneAnnotationFileName = null;
	public static String canvasFileName = null;
	public static String baseURIForIds = null;
	public static String baseURIForDocs = null;
	public static String fileNameForOldTextAnnos = null;
	
	public static  File homeDir = null;
	
	static private FileHandler textFileHandler;
	
	static private SimpleFormatter textFormatter;
		
	public static String getCentralRepositoryURL() {
		return centralRepositoryURL;
	}

	public void setCentralRepositoryURL(String centralRepositoryURL) {
		Config.centralRepositoryURL = centralRepositoryURL;
	}


	public static String getNormalSequenceFileName() {
		return normalSequenceFileName;
	}


	public  void setNormalSequenceFileName(String normalSequenceFileName) {
		Config.normalSequenceFileName = normalSequenceFileName;
	}


	public static String getManifestFileName() {
		return manifestFileName;
	}


	public  void setManifestFileName(String manifestFileName) {
		Config.manifestFileName = manifestFileName;
	}


	public static String getImageAnnotationFileName() {
		return imageAnnotationFileName;
	}


	public  void setImageAnnotationFileName(String imageAnnotationFileName) {
		Config.imageAnnotationFileName = imageAnnotationFileName;
	}

	public static String getZoneAnnotationFileName() {
		return zoneAnnotationFileName;
	}

	public  void setZoneAnnotationFileName(String zoneAnnotationFileName) {
		Config.zoneAnnotationFileName = zoneAnnotationFileName;
	}
	
	public static String getTextAnnotationFileName() {
		return textAnnotationFileName;
	}
	
	public  void setTextAnnotationFileName(String textAnnotationFileName) {
		Config.textAnnotationFileName = textAnnotationFileName;
	}
	
	public static String getCanvasFileName() {
		return zoneAnnotationFileName;
	}

	public  void setCanvasFileName(String canvasFileName) {
		Config.canvasFileName = canvasFileName;
	}
	
	public static String getFileNameForOldTextAnnos() {
		return fileNameForOldTextAnnos;
	}
	public  void setTextFileNameForOldTextAnnos(String fileNameForOldTextAnnos) {
		Config.fileNameForOldTextAnnos = fileNameForOldTextAnnos;
	}

	public static String getBaseURIForIds() {
		return baseURIForIds;
	}


	public  void setBaseURIForIds(String baseURIForIds) {
		Config.baseURIForIds = baseURIForIds;
	}
	
	public static String getBaseURIForDocs() {
		return baseURIForDocs;
	}


	public  void setBaseURIForDocs(String baseURIForDocs) {
		Config.baseURIForDocs = baseURIForDocs;
	}
	
	public static File getHomeDir() {
		return homeDir;
	}


	public  void setHomeDir(File homeDir) {
		Config.homeDir = homeDir;
	}



	
    public void contextInitialized(ServletContextEvent event) {
    	
        String homeDirPath = System.getenv(HOME_DIR_ENV_VAR);
        
        if (homeDirPath == null || homeDirPath.trim().equals("")) {
        	System.out.println("The home directory environment variable, " + HOME_DIR_ENV_VAR + ", has not been set.");	
        	return;
        } 
        
        homeDir = new File(homeDirPath);
        if (! homeDir.exists()) {
        	System.out.println("The home directory pointed to by the environment variable, " + HOME_DIR_ENV_VAR + ", does not exist.");	
        	return;
        }       
        	
        if (! homeDir.canRead()) {
        	System.out.println("The home directory pointed to by the environment variable, " + HOME_DIR_ENV_VAR + ", is not readable.  Please check your permissions.");	
        	return;
        }
        
        if (! homeDir.isDirectory()) {
        	System.out.println("The home directory pointed to by the environment variable, " + HOME_DIR_ENV_VAR + ", doesn't appear to be a directory.");	
        	return;
        }	
        	
        if (! homeDir.canWrite()) {
        	System.out.println("The home directory pointed to by the environment variable, " + HOME_DIR_ENV_VAR + ", is not writable.  Please check your permissions.");	
        	return;
        }	
        	
        File manuscriptsDataAndImagesDirectory = new File(homeDir, "data");
        if ( ! manuscriptsDataAndImagesDirectory.exists() ) {
        	manuscriptsDataAndImagesDirectory.mkdir();
        }
        
        configureRootLogger(homeDir);		
		
        loadConfigFile(homeDir);  

    }


	private void loadConfigFile(File homeDir) {
		File configFile = new File(homeDir, "config.xml");
		if (!configFile.exists()) {
			LOGGER.severe("The config.xml file does not appear to exist in your " + HOME_DIR_ENV_VAR + " directory.");
			System.out.println("The config.xml file does not appear to exist in your " + HOME_DIR_ENV_VAR + " directory.");			
		}
		
		Digester digester = new Digester();  
	    digester.push(this);  
	    

	  
	    digester.addBeanPropertySetter("config/centralRepositoryURL");
	    digester.addBeanPropertySetter("config/normalSequenceFileName");
	    digester.addBeanPropertySetter("config/manifestFileName");
	    digester.addBeanPropertySetter("config/imageAnnotationFileName");
	    digester.addBeanPropertySetter("config/zoneAnnotationFileName");	    
	    digester.addBeanPropertySetter("config/textAnnotationFileName");
	    digester.addBeanPropertySetter("config/baseURIForIds");
	    digester.addBeanPropertySetter("config/baseURIForDocuments");
	    digester.addBeanPropertySetter("config/fileNameForOldTextAnnos");
	    

	    try {
	    	InputStream inputStream = new FileInputStream(configFile);  
			digester.parse(inputStream);
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}


	private void configureRootLogger(File homeDir) {
		// configure the root logger, from which our other loggers will inherit
			try {
				textFileHandler = new FileHandler(homeDir.getAbsolutePath() + "/" + LOG_FILE_NAME);
			} catch (SecurityException e) {
				e.printStackTrace();
				System.out.println("Couldn't open the log.txt file for logging in the directory to which the environment variable, " + HOME_DIR_ENV_VAR + ", points.");
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Couldn't open the log.txt file for logging in the directory to which the environment variable, " + HOME_DIR_ENV_VAR + ", points.");
			}
			Logger logger = Logger.getLogger("");
			logger.setLevel(Level.INFO);
			textFormatter = new SimpleFormatter();
			textFileHandler.setFormatter(textFormatter);
			logger.addHandler(textFileHandler);
	}

    
    public void contextDestroyed(ServletContextEvent event) {
        // nothing for now
    }







}
