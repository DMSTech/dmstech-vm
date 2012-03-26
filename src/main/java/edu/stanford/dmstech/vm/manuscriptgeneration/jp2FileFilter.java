package edu.stanford.dmstech.vm.manuscriptgeneration;

import java.io.File;


	public class jp2FileFilter implements java.io.FileFilter {
	    public boolean accept(File f) {
	        if (f.isDirectory()) return true;
	        return f.getName().toLowerCase().endsWith("jp2") ;
	    }
	}
	

