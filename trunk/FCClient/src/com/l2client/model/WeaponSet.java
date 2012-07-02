package com.l2client.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;

public class WeaponSet implements com.jme3.export.Savable {

	/**
	 * mesh set for the primary hand
	 */
	private String primhand;
	/**
	 * mesh set for the offhand (optional)
	 */
	private String offhand;
	/**
	 * 0 not optional
	 * 1-n each n-th model will have it
	 */
	private int offhandOptional;
	/**
	 * Anim Set to be used by this combination
	 */
	private String animSet;
	/**
	 * Default animSet to be used if animSet is just a complementary anim set (has only a subset of all animations)
	 */
	private String defaultSet;

	@Override
	public void read(JmeImporter im) throws IOException {
	     InputCapsule input = im.getCapsule(this);

	     primhand = input.readString("primhand", "");
	     offhand = input.readString("offhand", "");
	     offhandOptional= input.readInt("offhandOptional", 0);
	     animSet = input.readString("animSet", "");
	     defaultSet = input.readString("defaultSet", "");
	}

	@Override
	public void write(JmeExporter ex) throws IOException {
		OutputCapsule output = ex.getCapsule(this);
        output.write(primhand, "primhand", "");
        output.write(offhand, "offhand", "");
        output.write(offhandOptional, "offhandOptional", 0);
        output.write(animSet, "animSet", "");
        output.write(defaultSet, "defaultSet", "");
	}

	public String getPrimhand() {
		return primhand;
	}

//	public void setPrimhand(String primhand) {
//		this.primhand = primhand;
//	}

	public String getOffhand() {
		return offhand;
	}

//	public void setOffhand(String offhand) {
//		this.offhand = offhand;
//	}

	public int getOffhandOptional() {
		return offhandOptional;
	}

//	public void setOffhandOptional(int offhandOptional) {
//		this.offhandOptional = offhandOptional;
//	}

	public String getAnimSet() {
		return animSet;
	}

//	public void setAnimSet(String animSet) {
//		this.animSet = animSet;
//	}
	
	public String getDefaultSet() {
		return defaultSet;
	}
	
	public boolean readFromTextfile(String fname){
		BufferedReader r = null;
		try {
			r = new BufferedReader(new FileReader(fname));
			
			String line;
			do{
				line = r.readLine();
				//skip comments
				if(line.startsWith("#"))
					continue;
				String[] tok = line.split(";");
				if("primary".equals(tok[0])){
					if(tok.length>1)
						this.primhand = tok[1];
				} else if("offhand".equals(tok[0])){
					if(tok.length>1)
						this.offhand = tok[1];
					if(tok.length>2)
						this.offhandOptional = Integer.parseInt(tok[2]);
				} else if("anim".equals(tok[0])){
					if(tok.length>1)
						this.animSet = tok[1];
					if(tok.length>2)
						this.defaultSet = tok[2];
				} 
			}while(r.ready());
			r.close();
			return true;
		} catch (Exception e) {
System.out.println("Failed to read from "+fname);
			e.printStackTrace();
			if(r != null)
			{
				try {
					r.close();
				} catch (IOException e1) {
					//intentionally left empty
				}
			}
			return false;
		}
		
	}
}
