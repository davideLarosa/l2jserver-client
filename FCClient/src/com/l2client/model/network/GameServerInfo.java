package com.l2client.model.network;

/**
 * A model of the game server information, storing data like ip, port, number of
 * players, etc.
 */
public class GameServerInfo {

	/**
	 * ID of the game server
	 */
	public int id;
	/**
	 * the ip adress string
	 */
	public String ip;

	/**
	 * the port of the game server for connections
	 */
	public int port;
	/**
	 * current number of logged in players on this game server
	 */
	public int players;
	/**
	 * number of maximum players this server supports
	 */
	public int maxplayers;
	/**
	 * flag if the server allows pvp
	 */
	public boolean pvp;
	/**
	 * is the server online?
	 */
	public boolean online;
	/**
	 * is it a test server
	 */
	public boolean test;

	public boolean brackets;
	
	/**
	 * age players should be at least 
	 */
	public int ageLimit;
	
	/**
	 *
	 *1: Normal, 2: Relax, 4: Public Test, 8: No Label, 16: Character Creation Restricted, 32: Event, 64: Free
	 */
	public int type;

	/**
	 * Constructs a new GameServerInfo
	 */
	public GameServerInfo() {
	}

	/**
	 * used for a short string summary of the server state
	 */
	@Override
	public String toString() {
		String g = "ip: " + ip + " free slots: " + (maxplayers - players)
				+ " online: " + online;
		return g;
	}
	
	public String toFullString() {
		String g = "id: "+id+" ip: " + ip + " free slots: " + (maxplayers - players)
				+ " online: " + online + " pvp: "+ pvp + " type: "+ type + " test: "+ test +" ageLimit: "+ageLimit;
		return g;
	}
}
