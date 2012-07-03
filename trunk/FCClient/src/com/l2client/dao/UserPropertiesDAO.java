package com.l2client.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Load and Store cServer.properties from user.home
 */
public class UserPropertiesDAO {
	
	final public static String SERVER_HOST_PROPERTY = "client.server.host";
	final public static String SERVER_PORT_PROPERTY = "client.server.port";
	final private static Logger log = Logger.getLogger(UserPropertiesDAO.class.getName());
	
	/**
	 * load last used sever port and host from properties into system properties
	 */
	public static void loadProperties(){
		String s = System.getProperty("user.home", "");
		
		Properties prop = new Properties();
		try {
			FileInputStream in = new FileInputStream(s+File.separator+"cServer.properties");
			prop.load(in);
			if(in != null)
				in.close();
			System.getProperties().putAll(prop);
			log.info("Successfully loaded properties from user.home <"+s+File.separator+"cServer.properties>");
		} catch(Exception e) {
			log.log(Level.SEVERE, "Failed loading properties from user.home <"+s+File.separator+"cServer.properties>", e);
			//set startup properties
			System.setProperty(SERVER_HOST_PROPERTY,"127.0.0.1");
			System.setProperty(SERVER_PORT_PROPERTY,"2106");
		} 
	}
	
	/**
	 * save last used sever port and host from system properties to user.home
	 */
	public static void saveProperties(){
		
		Properties prop = new Properties();
		String s = System.getProperty("user.home", "");
		try {
			//set startup properties
			prop.put(SERVER_HOST_PROPERTY, System.getProperty(SERVER_HOST_PROPERTY,"127.0.0.1"));
			prop.put(SERVER_PORT_PROPERTY, System.getProperty(SERVER_PORT_PROPERTY,"2106"));
			
			prop.store(new FileOutputStream(s+File.separator+"cServer.properties"), null/*no comment*/);
		} catch(Exception e) {//Ignore
			log.log(Level.SEVERE, "Failed saving properties to user.home <"+s+File.separator+"cServer.properties>", e);
		} 
	}

}
