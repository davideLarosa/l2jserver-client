package com.l2client.dao.csv;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;

import com.l2client.dao.IDAO;
import com.l2client.dao.builders.ActionResultBuilder;
import com.l2client.gui.actions.BaseUsable;

public class CSVDatastoreDAO implements IDAO {


	private static Logger logger = Logger.getLogger(CSVDatastoreDAO.class.getName());
	
	private static class SingletonHolder {
		public static final IDAO instance = new CSVDatastoreDAO();
	}

	public static IDAO get() {
		return SingletonHolder.instance;
	}
	/**
	 * internal Npc representation of file /db/npc.csv based on the L2JServer npc table
	 * id int
	 * idTemplate int
	 * name String
	 * gamemodel String (extension to L2J fields)
	 */
	private class CSVNpc {/**id is key*/Integer id = Integer.valueOf(0); Integer idTemplate= Integer.valueOf(0); String name=""; String gamemodel="";};
	/**
	 * internal Item representation of file /db/items.csv based on the L2JServer items*.xml files
	 * id int
	 * type String
	 * description String
	 */
	private class CSVItem {/**id is key*/Integer id = Integer.valueOf(0); String type =""; String descriptionShort="";};
	/**
	 * lazy loaded cache of npc's
	 */
	private HashMap<Integer, CSVNpc> npcCache = null;
	/**
	 * lazy loaded cache of system messages
	 */
	private HashMap<Integer, String> systemMessages = null;
	/**
	 * lazy loaded cache of items
	 */
	private HashMap<Integer, CSVItem> itemCache = null;
	
	@Override
	public String getItemDescription(int itemId){
		String desc  = "";
		if(itemCache  == null)
			loadItems();
		
		CSVItem n = itemCache.get(itemId);
		if(n != null && n.descriptionShort != null)
			desc = n.descriptionShort;
		return desc;
	}
	
	@Override
	public String getSystemMessage(int id){
		String ret = "";
		if(systemMessages == null)
			loadSystemMessages();
		
		if(systemMessages != null){
			String s = systemMessages.get(id);
			if(s != null)
				ret = s;
		}
		return ret;
	}

	private void loadSystemMessages() {
		systemMessages = new HashMap<Integer, String>(2500,0.9f);
		try {
			CSVReader reader = new CSVReader(new InputStreamReader(
					CSVDatastoreDAO.class.getResourceAsStream("/db/systemmessages.csv")), 1/*skip header*/, new CSVParser('\t','"','\\'));
			
			String[] line = reader.readNext();
			
			while (line != null) {
				if (line.length > 1) {
					systemMessages.put(Integer.valueOf(line[0]), line[1]);
				}
				line = reader.readNext();
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to load /db/systemmessages.csv", e);
		}	
	}

	@Override
	public String getNpcName(int templateID){
		String name  = "";
		if(npcCache  == null)
			loadNPCs();
		
		CSVNpc n = npcCache.get(templateID);
		if(n != null && n.name != null)
			name = n.name;
		return name;
	}
	
	@Override
	public String getNpcGameModel(int templateId) {
		String name  = null;
		if(npcCache  == null)
			loadNPCs();
		
		CSVNpc n = npcCache.get(templateId);
		if(n != null && n.gamemodel != null)
			name = n.gamemodel;
		return name;
	}
	
	@Override
	public BaseUsable[] loadAllActions(){
		BaseUsable[] ret = new BaseUsable[0];
		try {
			CSVReader reader = new CSVReader(new InputStreamReader(
					CSVDatastoreDAO.class.getResourceAsStream("/db/actions.csv")), 1/*skip header*/, new CSVParser('\t','"','\\'));
			ret = ActionResultBuilder.buildActions(reader);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to load /db/actions.csv", e);
		}
		return ret;
	}
	
	/**
	 * Load npc configuration from csv file npc.csv
	 */
	private void loadNPCs() {
		npcCache = new HashMap<Integer, CSVDatastoreDAO.CSVNpc>();
		try {
			CSVReader reader = new CSVReader(new InputStreamReader(
					CSVDatastoreDAO.class.getResourceAsStream("/db/npc.csv")), 1/*skip header*/, new CSVParser('\t','"','\\'));
			
			String[] line = reader.readNext();
			while (line != null) {
				if (line.length > 2) {
					CSVNpc n = new CSVNpc();
					n.id = Integer.valueOf(line[0]);
					n.idTemplate = Integer.valueOf(line[1]);
					n.name = line[2];
					if (line.length > 3)
						n.gamemodel = line[3];

					npcCache.put(n.idTemplate, n);
				}
				line = reader.readNext();
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to load /db/npc.csv", e);
		}		
	}
	
	/**
	 * Load item configuration from csv file items.csv
	 */
	private void loadItems() {
		itemCache = new HashMap<Integer, CSVDatastoreDAO.CSVItem>();
		try {
			CSVReader reader = new CSVReader(new InputStreamReader(
					CSVDatastoreDAO.class.getResourceAsStream("/db/items.csv")), 1/*skip header*/, new CSVParser('\t','"','\\'));
			
			String[] line = reader.readNext();
			while (line != null) {
				if (line.length > 2) {
					CSVItem n = new CSVItem();
					n.id = Integer.valueOf(line[0]);
					n.type = line[1];
					n.descriptionShort = line[2];

					itemCache.put(n.id, n);
				}
				line = reader.readNext();
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Failed to load /db/items.csv", e);
		}		
	}

	@Override
	public void init() {//intentionalyy left blank
	}

	@Override
	public void finit() {//intentionalyy left blank
	}
}
