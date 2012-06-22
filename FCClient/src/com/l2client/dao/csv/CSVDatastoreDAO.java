package com.l2client.dao.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
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
	 * idtemplate int
	 * name String
	 * gamemodel String (extension to L2J fields)
	 */
	private class CSVNpc {/**id is key*/Integer id = Integer.valueOf(0); Integer idTemplate= Integer.valueOf(0); String name=""; String gamemodel="";};
	/**
	 * lazy loaded cache of npc's
	 */
	private HashMap<Integer, CSVNpc> npcCache = null;

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
					CSVDatastoreDAO.class.getResourceAsStream("/db/actions.csv")), 1/*skip header*/, new CSVParser('\t','\'','"'));
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
					CSVDatastoreDAO.class.getResourceAsStream("/db/npc.csv")), 1/*skip header*/, new CSVParser('\t','\'','"'));
			
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

	@Override
	public void init() {//intentionalyy left blank
	}

	@Override
	public void finit() {//intentionalyy left blank
	}
}
