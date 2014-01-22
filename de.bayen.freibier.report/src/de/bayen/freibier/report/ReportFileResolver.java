package de.bayen.freibier.report;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.FileResolver;
import net.sf.jasperreports.engine.util.LocalJasperReportsContext;

import org.adempiere.exceptions.AdempiereException;

class ReportFileResolver implements FileResolver {

	static File cacheDir = null;

	static final String SUFFIX_JASPER = "jasper";
	static final String SUFFIX_JRXML = "jrxml";
	static final String DEFAULT_RESOURCEPATH = ReportFileResolver.class.getPackage().getName().replace('.', '/')+"/";

	public ReportFileResolver() {
		if (cacheDir == null) {
			try {
				cacheDir = File.createTempFile("JasperReportsCache", "");
				// createTempFile creates a new file, but I need a directory
				String tmpDirName = cacheDir.getAbsolutePath();
				cacheDir.delete();
				cacheDir = new File(tmpDirName);
				cacheDir.mkdir();
				cacheDir.deleteOnExit();
			} catch (IOException e) {
				throw new AdempiereException();
			}
		}
	}

	@Override
	public File resolveFile(String fileName) {
		System.out.println("resolveFile(\"" + fileName + "\"");
		// parse the filename
		fileName.replace('\\', '/');
		int i1 = fileName.lastIndexOf("/");
		int i2 = fileName.lastIndexOf(".");
		// includes '/'
		String path = i1 == -1 ? "" : fileName.substring(0, i1 + 1);
		String name = fileName.substring(i1 == -1 ? 0 : i1 + 1, i2 == -1 ? fileName.length() : i2);
		// includes not '.'
		String suffix = i2 == -1 ? "" : fileName.substring(i2 + 1);

		File cacheFile = loadFile(path, name, suffix);
		return cacheFile;
		// return new
		// File("/home/tbayen/Projekte/iDempiere/JasperReports/Reporte/" +
		// fileName);
	}

	/**
	 * Does the definitely file loading.
	 * Candidate for overloading for different access methods.
	 * 
	 * @param path
	 * @param name
	 * @param suffix
	 * @return
	 */
	protected File loadFile(String path, String name, String suffix) {
		String fullSuffix = suffix != null ? "." + suffix : "";
		File cacheFile = new File(cacheDir + "/" + path + name + fullSuffix);

		// check if the file is actual (classpath resources never change)
		if (cacheFile.exists())
			return cacheFile;

		// copy file to cache
		String fullPath = DEFAULT_RESOURCEPATH + name + fullSuffix;
		InputStream originalFileStream = getClass().getClassLoader().getResourceAsStream(fullPath);
		if(originalFileStream==null && SUFFIX_JASPER.equals(suffix)){
			loadFile(path, name, SUFFIX_JRXML);
			return compileJrxml(cacheDir + "/" + path + name);
		}else{
			return copyStreamToCache(originalFileStream, cacheFile);
		}
	}

	protected File copyStreamToCache(InputStream originalFileStream, File cacheFile) {
		try {
			OutputStream out = null;
			try {
				out = new FileOutputStream(cacheFile);
				if (out != null) {
					byte buf[] = new byte[1024];
					int len;
					while ((len = originalFileStream.read(buf)) > 0) {
						out.write(buf, 0, len);
					}
				}
			} catch (Exception e) {
				throw new AdempiereException(e);
			} finally {
				if (out != null)
					out.close();
				if (originalFileStream != null)
					originalFileStream.close();
			}
		} catch (IOException e) {
			throw new AdempiereException();
		}
		return cacheFile;
	}
	
	/**
	 * The path parameter is a filepath without extension. The path has to be a real file path (in the cache directory).
	 * 
	 * @param path
	 * @return
	 */
	protected File compileJrxml(String path){
		File inputFile=new File(path+"."+SUFFIX_JRXML);
		File outputFile=new File(path+"."+SUFFIX_JASPER);
        try {
        	LocalJasperReportsContext context = new LocalJasperReportsContext(DefaultJasperReportsContext.getInstance());
        	context.setClassLoader(JasperReport.class.getClassLoader());
        	JasperCompileManager manager = JasperCompileManager.getInstance(context);

			/*
			 * I do not really understand what is happening. But when I call the
			 * compileToFile() method here it can't find a class
			 * (java.lang.ClassNotFoundException:
			 * org/apache/xerces/jaxp/SAXParserFactoryImpl). To set the context
			 * of the manager seems not to help. We have to change the Thread's
			 * ClassLoader.
			 */
			ClassLoader cl1 = Thread.currentThread().getContextClassLoader();
			try {
				Thread.currentThread().setContextClassLoader(JasperReport.class.getClassLoader());
				manager.compileToFile(inputFile.getAbsolutePath(), outputFile.getAbsolutePath());
			} finally {
				Thread.currentThread().setContextClassLoader(cl1);
			}
            outputFile.setLastModified(inputFile.lastModified()); //Synchronize Dates
            return outputFile;
        } catch (JRException e) {
        	throw new AdempiereException(e);
        }
	}

}
