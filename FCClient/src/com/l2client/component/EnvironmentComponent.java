package com.l2client.component;


/**
 * A Component for internal Vars, Perception, Mind, etc.
 */
public class EnvironmentComponent implements Component {

	public boolean hidden = false;
	/**
	 * how much damage have we produced, yeah!
	 */
	public int damageDealt = 0;
	/**
	 * how much damage have we received, ouch..
	 */
	public int damageReceived = 0;
	/**
	 * Type of damage dealt, 0 normal, 32 ??, 128 ??
	 */
	public int damageDealtType = -1;
	/**
	 * Type of damage received, 0 normal, 32 ??, 128 ??
	 */
	public int damageReceivedType = -1;
	/**
	 * if teamed how much health has the whole team
	 */
	public int teamHealthPercent = -1;
	/**
	 * flag if something changed at all, should be reset by the environment system
	 */
	public boolean changed = false;
	/**
	 * -1 not moving, 0 walk, 1 run
	 */
	public int movement = -1; 
	
}
