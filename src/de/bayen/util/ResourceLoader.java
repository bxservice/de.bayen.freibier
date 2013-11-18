package de.bayen.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

/**
 * Load special resources from the classpath using fixed conventions.
 * 
 * @author tbayen
 */
public class ResourceLoader {
	public static String load(Class<?> baseClass,String name){
		InputStream strm = baseClass.getResourceAsStream(name);
		try {
			return IOUtils.toString(strm,"UTF-8");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Bei Aufruf z.B. mit einem de.bayen.freibier.model.MBAYBla und "kopierbefehl" wird
	 * die Resource "/de/bayen/freibier/model/MBAYBla_kopierbefehl.sql" geladen.
	 * @param obj
	 * @param name
	 * @return
	 */
	public static String loadSQL(Object obj, String name){
		return load(obj.getClass(),obj.getClass().getSimpleName()+"_"+name+".sql");
	}
}
