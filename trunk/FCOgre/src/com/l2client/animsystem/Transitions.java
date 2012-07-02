package com.l2client.animsystem;

import java.util.HashMap;

/**
 * The concept describes transitions to be based on input value changes by
 * actions. My implementation uses plain animations, so if you would be in need
 * of a more sophisticated transition selection system you should change the
 * Transitions.java implementation, or overide it.
 * 
 */
public class Transitions {

	private HashMap<String, HashMap<String, String>> fromToBy = new HashMap<String, HashMap<String, String>>();

	public Transitions(String[][] ftb) {
		for (String[] s : ftb) {
			addTransition(s);
		}
	}

	private void addTransition(String[] s) {
		HashMap<String, String> ret = null;
		ret = fromToBy.get(s[0]);
		if (ret != null) {
			ret.put(s[1], s[2]);
		} else {
			ret = new HashMap<String, String>();
			ret.put(s[1], s[2]);
			fromToBy.put(s[0], ret);
		}
	}

	public String getTransition(String from, String to) {
		HashMap<String, String> ret = null;
		ret = fromToBy.get(from);
		if (ret != null) {
			return ret.get(to);
		}
		return null;
	}
}
