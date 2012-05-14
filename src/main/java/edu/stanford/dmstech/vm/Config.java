package edu.stanford.dmstech.vm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.digester.Digester;
import org.apache.log4j.BasicConfigurator;
import org.xml.sax.SAXException;

public class Config implements ServletContextListener {

	private static final String SHAREDCANVAS_CONFIG_FILE_NAME = "sharedcanvas-config.xml";
	private static final String LOG_FILE_NAME = "log.txt";
	private final static Logger LOGGER = Logger.getLogger(Config.class.getName());
	public static final String HOME_DIR_ENV_VAR = "SHARED_CANVAS_HOME";
	
	public static String homeDirPath = null;
	public static  String repositoryFileName = null;
	public static String collectionFileName = null;
	public static  String normalSequenceFileName = null;
	public static  String manifestFileName = null;
	public static  String imageAnnotationFileName = null;
	public static  String textAnnotationFileName = null;
	public static String zoneAnnotationFileName = null;
	public static String canvasFileName = null;
	public static String baseURIForIds = null;
	public static String baseURIForDocuments = null;	
	public static String collectionSubDir = null;
	public static String textAnnosSubDir = null;
	public static String textAnnosBodiesSubDir = null;
	public static String transactionsSubDir = null;
	public static String defaultCollection = null;
	public static String mainTDBDatasetDir = null;	
	public static String solrServer = null;
	public static String djatokaServer = null;
	
	public static  File homeDir = null;
	
	static private FileHandler textFileHandler;
	
	static private SimpleFormatter textFormatter;
	
	
		
	public static String getRepositoryFileName() {
		return repositoryFileName;
	}

	public void setRepositoryFileName(String repositoryFileName) {
		Config.repositoryFileName = repositoryFileName;
	}

	public static String getCollectionFileName() {
		return collectionFileName;
	}

	public void setCollectionFileName(String collectionFileName) {
		Config.collectionFileName = collectionFileName;
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

	public static String getBaseURIForIds() {
		return baseURIForIds;
	}

	public void setBaseURIForIds(String baseURIForIds) {
		Config.baseURIForIds = baseURIForIds;
	}	
	
	public static String getBaseURIForDocuments() {
		return baseURIForDocuments;
	}

	public  void setBaseURIForDocuments(String baseURIForDocs) {
		Config.baseURIForDocuments = baseURIForDocs;
	}	

	public static String getCollectionSubDir() {
		return collectionSubDir;
	}

	public void setCollectionSubDir(String collectionSubDir) {
		Config.collectionSubDir = collectionSubDir;
	}

	
	public static String getTextAnnosSubDir() {
		return textAnnosSubDir;
	}

	public  void setTextAnnosSubDir(String textAnnosSubDir) {
		Config.textAnnosSubDir = textAnnosSubDir;
	}

	public static String getTextAnnosBodiesSubDir() {
		return textAnnosBodiesSubDir;
	}

	public  void setTextAnnosBodiesSubDir(String textAnnosBodiesSubDir) {
		Config.textAnnosBodiesSubDir = textAnnosBodiesSubDir;
	}
	
	public static String getTransactionsSubDir() {
		return transactionsSubDir;
	}

	public  void setTransactionsSubDir(String transactionsSubDir) {
		Config.transactionsSubDir = transactionsSubDir;
	}

	public static String getLogsSubDir() {
		// this is hardcoded rather than read from the 
		// config file to allow logging problems reading the config file.
		return "logs";
	}
	
	public static String getDefaultCollection() {
		return defaultCollection;
	}
	
	public  void setDefaultCollection(String defaultCollection) {
		Config.defaultCollection = defaultCollection;
	}
	
	public static String getMainTDBDatasetDir() {
		return mainTDBDatasetDir;
	}
	
	public void setMainTDBDatasetDir(String mainTDBDatasetDir) {
		Config.mainTDBDatasetDir = mainTDBDatasetDir;
	}
	
	public static String getSolrServer() {
		return solrServer;
	}

	public  void setSolrServer(String solrServer) {
		Config.solrServer = solrServer;
	}
	
	public static String getDjatokaServer() {
		return djatokaServer;
	}

	public  void setDjatokaServer(String djatokaServer) {
		Config.djatokaServer = djatokaServer;
	}
	
	public static File getHomeDir() {
		return homeDir;
	}


	public  void setHomeDir(File homeDir) {
		Config.homeDir = homeDir;
	}

	
	public static String getAbsolutePathToTextAnnosDir() {
		return (new File(homeDirPath, getTextAnnosSubDir())).getAbsolutePath();
	}
	
	public static String getAbsolutePathToTextAnnosBodiesDir() {
		return (new File(homeDirPath, getTextAnnosBodiesSubDir())).getAbsolutePath();
	}
	
	public static String getAbsolutePathToTransactionsDir() {
		return (new File(homeDirPath, getTransactionsSubDir())).getAbsolutePath();
	}

	public static String getAbsolutePathToLogsDir() {
		return (new File(homeDirPath, getLogsSubDir())).getAbsolutePath();
	}
	
	public static String getAbsolutePathToMainTBDDir() {
		return (new File(homeDirPath, mainTDBDatasetDir)).getAbsolutePath();
	}
	
	public static String getAbsolutePathToCollectionsDir() {
		return (new File(homeDirPath, getCollectionSubDir())).getAbsolutePath();
	}
	
	public static String getAbsolutePathToDefaultCollectionsDir() {
		return (new File(getAbsolutePathToCollectionsDir(), defaultCollection)).getAbsolutePath();
	}
	public static String getAbsolutePathToDefaultCollectionRM() {
		return new File(getAbsolutePathToDefaultCollectionsDir(), "Collection.nt").getAbsolutePath();
	}
	
	public static String getAbsolutePathToManuscriptDir(String defaultCollectionSubDir, String manuscriptDir) {		
		File collectionsDir = new File(homeDirPath, getCollectionSubDir());
		return new File(new File(collectionsDir, defaultCollectionSubDir), manuscriptDir).getAbsolutePath();
	}
	
	public static String getAbsolutePathToManuDirInDefaultCollection(
			String manuscriptSubDirectory) {
		return getAbsolutePathToManuscriptDir(getDefaultCollection(), manuscriptSubDirectory);
	}
	
	public static String getAbsolutePathToManuscriptSequenceSourceFile(String collectionId, String manuscriptId, String sequenceId) {
		return new File(getAbsolutePathToManuscriptsSequenceDir(collectionId, manuscriptId), sequenceId + ".nt").getAbsolutePath();
	}
	
	public static String getAbsolutePathToManuscriptManifestFile(String collectionId, String manuscriptId) {
		return new File(getAbsolutePathToManuscriptRDFDir(collectionId, manuscriptId), getManifestFileName()).getAbsolutePath();
	}
	
	public static String getAbsolutePathToManuscriptImageAnnoFile(String collectionId, String manuscriptId) {
		return new File(getAbsolutePathToManuscriptRDFDir(collectionId, manuscriptId), getImageAnnotationFileName()).getAbsolutePath();
	}
	
	public static String getAbsolutePathToManuscriptRDFDir(String collectionId, String manuscriptId) {
		return new File(getAbsolutePathToManuscriptDir(collectionId, manuscriptId), "rdf").getAbsolutePath();
	}
	
	public static String getAbsolutePathToManuscriptsSequenceDir(String collectionId, String manuscriptId) {
		return new File(getAbsolutePathToManuscriptDir(collectionId, manuscriptId), "rdf/sequences").getAbsolutePath();
	}
	
	public static void main(String[] args) {
		BasicConfigurator.configure();
		Config config = new Config();
		config.initializeThisConfig();
		System.out.println(Config.getAbsolutePathToManuscriptDir("someColl", "someManu"));
	}
	
	
	public static String getBaseURIForManuscriptInDefaultCollection(
			String manuscriptSubDirectory) throws URISyntaxException {
		return UriBuilder.fromUri(getBaseURIForIds()).path(getDefaultCollection()).path(manuscriptSubDirectory).build().toString();
		
		
	}
	
    public void contextInitialized(ServletContextEvent event) {
    	System.out.println("IN THE CONFIG.");
       initializeThisConfig();
    }

    public void initializeThisConfig() {
    	 homeDirPath = System.getenv(HOME_DIR_ENV_VAR);
         
    	 if (homeDirPath == null) {
    		 homeDirPath = System.getProperty(HOME_DIR_ENV_VAR);
    	 }
         if (homeDirPath == null || homeDirPath.trim().equals("")) {
         	System.out.println("The home directory environment variable, " + HOME_DIR_ENV_VAR + ", has not been set.");	
         	return;
         } 
         
         homeDir = new File(homeDirPath);
         if (! homeDir.exists()) {
         	System.out.println("The home directory pointed to by the environment variable, " + HOME_DIR_ENV_VAR + ": " + homeDirPath + ", does not exist.");	
         	return;
         }       
         	
         if (! homeDir.canRead()) {
         	System.out.println("The home directory pointed to by the environment variable, " + HOME_DIR_ENV_VAR + ": " + homeDirPath + ", is not readable.  Please check your permissions.");	
         	return;
         }
         
         if (! homeDir.isDirectory()) {
         	System.out.println("The home directory pointed to by the environment variable, " + HOME_DIR_ENV_VAR + ": " + homeDirPath + ", doesn't appear to be a directory.");	
         	return;
         }	
         	
         if (! homeDir.canWrite()) {
         	System.out.println("The home directory pointed to by the environment variable, " + HOME_DIR_ENV_VAR + ": " + homeDirPath + ", is not writable.  Please check your permissions.");	
         	return;
         }	
         	
         File logDir = new File(homeDir, getLogsSubDir());
         if (!logDir.exists()) {
        	 logDir.mkdir();
         }
         
         configureRootLogger(homeDir);		
  		
         loadConfigFile(homeDir);  
         
         
         
         File collectionsDir = new File(homeDir, Config.collectionSubDir);
         if ( ! collectionsDir.exists() ) {
        	 collectionsDir.mkdir();
         }
         File defaultCollectionDir = new File(collectionsDir, Config.defaultCollection);
         if (!defaultCollectionDir.exists()) {
        	 collectionsDir.mkdir();
         }
         File transactionsDir = new File(homeDir, Config.transactionsSubDir);
         if (!transactionsDir.exists()) {
        	 transactionsDir.mkdir();
         }
         File textAnnosDir = new File(homeDir, Config.textAnnosSubDir);
         if (!textAnnosDir.exists()) {
        	 textAnnosDir.mkdir();
         }
         File annotationBodyDir = new File(homeDir, Config.textAnnosBodiesSubDir);
         if (!annotationBodyDir.exists()) {
        	 annotationBodyDir.mkdir();
         }
         File repositoryTDBIndexDir = new File(homeDir, Config.mainTDBDatasetDir);
         if (!repositoryTDBIndexDir.exists()) {
        	 repositoryTDBIndexDir.mkdir();
         }
         File tempDir = new File(homeDir, "temp");
         if (!tempDir.exists()) {
        	 tempDir.mkdir();
         }
         

    }

	private void loadConfigFile(File homeDir) {
		File configFile = new File(homeDir, SHAREDCANVAS_CONFIG_FILE_NAME);
		if (!configFile.exists()) {
			LOGGER.severe("The config.xml file does not appear to exist in your " + HOME_DIR_ENV_VAR + " directory.");
			System.out.println("The config.xml file does not appear to exist in your " + HOME_DIR_ENV_VAR + " directory.");			
		}
		
		Digester digester = new Digester();  
	    digester.push(this);  
	  
	    digester.addBeanPropertySetter("config/repositoryFileName");
	    digester.addBeanPropertySetter("config/collectionFileName");
	    digester.addBeanPropertySetter("config/normalSequenceFileName");
	    digester.addBeanPropertySetter("config/manifestFileName");
	    digester.addBeanPropertySetter("config/imageAnnotationFileName");
	    digester.addBeanPropertySetter("config/zoneAnnotationFileName");	    
	    digester.addBeanPropertySetter("config/textAnnotationFileName");
	    digester.addBeanPropertySetter("config/baseURIForIds");
	    digester.addBeanPropertySetter("config/baseURIForDocuments");
	    digester.addBeanPropertySetter("config/collectionSubDir");
	    digester.addBeanPropertySetter("config/textAnnosSubDir");
	    digester.addBeanPropertySetter("config/textAnnosBodiesSubDir");
	    digester.addBeanPropertySetter("config/transactionsSubDir");
	    digester.addBeanPropertySetter("config/defaultCollection");
	    digester.addBeanPropertySetter("config/mainTDBDatasetDir");
	    digester.addBeanPropertySetter("config/solrServer");
	    digester.addBeanPropertySetter("config/djatokaServer");
	    

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
				textFileHandler = new FileHandler(homeDir.getAbsolutePath() + "/logs/" + LOG_FILE_NAME);
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
