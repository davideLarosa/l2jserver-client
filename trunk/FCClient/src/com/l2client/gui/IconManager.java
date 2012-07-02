package com.l2client.gui;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public final class IconManager {

	private static Logger logger = Logger.getLogger(IconManager.class.getName());
	
	private final static IconManager instance = new IconManager();
	private HashMap<String, Icon> map = new HashMap<String, Icon>();
	private IconManager(){
	}
	
	public static int ICONSIZE = 32;
	public static IconManager getInstance(){
		return instance;
	}
	
	public Icon getIcon(String file){
		if(map.containsKey(file))
			return map.get(file);
		else {
			try {
				ImageIcon icon = new ImageIcon(this.getClass().getClassLoader().getResource("icons/" + file+".png"));
				icon.setImage(icon.getImage().getScaledInstance(IconManager.ICONSIZE, IconManager.ICONSIZE, 16));
				map.put(file, icon);
				return icon;
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Failed to load icon "+file+".png",e);
				map.put(file, null);
				return null;
			}
		}
	}
	
	//FIXME implement release gdi objects for icons
	public void release(){
	}
}
