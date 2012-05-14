package com.l2client.util;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.HashSet;

import com.l2client.model.PartSet;

public final class PartSetManager {
	private static PartSetManager singleton = null;
	
	HashMap<String, PartSet> sets = new HashMap<String, PartSet>();
	HashSet<String> templates = new HashSet<String>();
	
	/**
	 * singleton private constructor
	 */
	private PartSetManager() {
		singleton = this;
	}
	
	public void loadParts(String megaSet){
		try {
			LineNumberReader reader = new LineNumberReader(new InputStreamReader(PartSetManager.class.getClassLoader().getResourceAsStream(
					megaSet)));
			String row = reader.readLine();
			while(row != null){
				addPartSets(row);
				row = reader.readLine();
			}
			reader.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void addPartSets(String row) {
		String[] token = row.split(";");

		PartSet last = getCreatePart(token[0]);
		PartSet top = last;
		for(int i = 1; i<token.length; i++){
			PartSet next =  last.getPart(token[i]);
			if(next == null)
				next = new PartSet(token[i]);

			if(i==token.length-1)
				last.setDetail(token[i-1]);
			
			last.setPart(next);
			last = next;			
		}
		
		sets.put(token[0], top);
		templates.add(token[0]);
	}
	
	private PartSet getCreatePart(String id){
		PartSet set = sets.get(id);
		if(set == null){
			set = new PartSet(id);
		}
		return set;
	}
	
	
//	private void addPartSetsOld(String row){
//		String[] token = row.split(";");
//		
//		if(token.length>=4){
//		PartSet set = sets.get(token[0]+token[2]);
//		if(set == null){
//			if(token[0].startsWith("anim"))
//				set = new AnimPartSet(token[0]+token[2]);
//			else
//				set = new PartSet(token[0]+token[2]);
//		}
//		set.setVariant(token[3], token.length>4?token[4]:"");
//		sets.put(set.getName(), set);
//		}
//		
//		if(token.length>=3){
//		PartSet detail = sets.get(token[0]+token[1]);//mesh or anim or etc..
//		if(detail == null)
//			detail = new PartSet(token[0]+token[1]);
//		
//		detail.setVariant(token[2], token[0]+token[2]);
//		sets.put(detail.getName(), detail);
//		}
//		
//		if(token.length>=1){
//		PartSet actor = sets.get(token[0]);
//		if(actor == null)
//			actor = new PartSet(token[0]);
//		
//		actor.setVariant(token[1], token[0]+token[1]);
//		sets.put(actor.getName(), actor);
//		
//		templates.add(token[0]);
//		}
//		
//		
//	}


	/**
	 * Fetch the singleton instance (created in case not done so far)
	 * 
	 * @return The instance
	 */
	public static PartSetManager get() {
		if (singleton != null)
			return singleton;
		else {
			return new PartSetManager();
		}
	}
	
	public PartSet getPart(String partset){
		return sets.get(partset);
	}
	
	/**
	 * returns a list of the currently loaded templates
	 * @return Array of distnct Strings of template names
	 */
	public String[] getTemplates(){
		if(templates != null)
			return templates.toArray(new String[templates.size()]);
		else
			return new String[0];
	}
}
