package com.l2client.model.l2j;

/**
 * races the player can choose, currently mapps to the races Human, Elf, DarkElf, Orc, 
 * Dwarf and Kamael and uses the startup classes for the races.
 *
 */
//FIXME replace with template/data driven based class for races
public enum Race
{
	Human("Human",3),
	Elf("Elf",3),
	DarkElf("DarkElf",3),
	Orc("Orc",3),
	Dwarf("Dwarf",1),
	Kamael("Kamael",12);
	
	private String str;
	private int startClasses;
	
	/**
	 * Constructor for creating a base race
	 * @param stri	String representing the name of the race
	 * @param sta	Int used as a start class for that race
	 */
	private Race(String stri, int sta){
		str = stri;
		startClasses = sta;
	}
	
	/**
	 * Name String of the used race
	 */
	public String toString(){
		return str;
	}
	
	/**
	 * Returns the starting classes for that race. 3 means fighter+mage, 1 only fighter, 12 kamael soldier
	 * @return int value representing the start class id
	 */
	public int getStartClass(){
		return startClasses;
	}
	
	/**
	 * Creates a fixed sized array of race names
	 * @return String [] of names in order of Race.values()
	 */
	public static String [] getRaces(){
		Race[] r =Race.values();
		String [] arr = new String[r.length];
		for(int i=0; i<r.length; i++)
			arr[i] = r[i].toString();
		
		return arr;	
	}
}
