package com.l2client.model;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;

public class PartSet implements Savable{
	String name;
	HashMap<String, PartSet> parts = new HashMap<String, PartSet>();
	String detail = null;
	String[] partsArray;
	volatile int next = 0;
	
	/**
	 * just for the savable
	 */
	public PartSet(){
	}
	
	public PartSet(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public void setPart(PartSet part){
		if(part != null){
			synchronized (parts) {
				parts.put(part.name, part);
				//FIXME there must be a better way than this!
				partsArray = parts.keySet().toArray(new String[0]);
			}
		}
	}
	
	public PartSet getPart(String id){
		PartSet ret = null;
		synchronized (parts) {
			ret = parts.get(id);
		}
		return ret;
	}
	
	public Collection<PartSet> getParts(){
		return parts.values();
	}
	
	public String getDetail(){
		return detail;
	}
	
	public void setDetail(String in){
		detail = in;
	}
	
	public int getSize(){
		synchronized (parts) {
			return parts.size();
		}
	}

	@Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule input = im.getCapsule(this);

        name = input.readString("name", "");

//        String[] keys = input.readStringArray("part_keys", null);
//        String[] values = input.readStringArray("part_values", null);
//        
//        if(keys != null && values != null &&(keys.length == values.length)){
//        	partsArray = new String[keys.length][2];
//        	for(int i=0; i<keys.length;i++){
//        		parts.put(keys[i], values[i]);
//        		partsArray[i][0]=keys[i];
//				partsArray[i][1]=values[i];
//        	}
//        }
//        initialize();
    }

	@Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule output = ex.getCapsule(this);
        output.write(name, "name", "");
//        output.write(parts.keySet().toArray(new String[0]), "part_keys", null);
//        output.write(parts.values().toArray(new String[0]), "part_values", null);
    }

	/**
	 * After loading, get the next model in map (round robin)
	 * @return
	 */
	public String getNext() {
		int size;
		String ret = "";
		synchronized (parts) {
			size = parts.size();

			if (size <= 0)
				return ret;
			else if(size <= 1)
				return partsArray[0];
			else {

				if (next >= size) {
					next = 0;
				}
				ret = partsArray[next];
				next++;
			}
		}
		return ret;
	}
	
	/**
	 * After loading, get a specific model in map
	 * @return
	 */
	public String get(int i) {
		int size;
		String ret = "";
		synchronized (parts) {
			size = parts.size();

			if (size <= 0 || i>=size)
				return ret;
			else if(size <= 1)
				return partsArray[0];
			else {
				ret = partsArray[i];
			}
		}
		return ret;
	}
	/**
	 * post finish initialize to be overridden
	 */
	public void initialize(){
	}
	
	@Override
	public String toString(){
		StringBuilder str = new StringBuilder();
		str.append(name).append(": ");
		synchronized (parts) {
//			str.append(parts.toString());
			for(PartSet p : parts.values()){
				str.append(p.toString()+"\n");
			}
		}
		if(detail != null){
			str.append(" -> ");
			str.append(detail);
		}
		return str.toString();
	}
}
