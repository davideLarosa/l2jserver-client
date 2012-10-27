package com.l2client.network.login.ClientPackets;

/**
 * A packet for requesting the list of game servers registered on this login
 * server
 * 
 */
public class RequestServerList extends LoginClientPacket {
	/**
	 * Constructor for the server list packet
	 * @param sessionId	just the current session id
	 */
	public RequestServerList(int ok1, int ok2) {
//		System.out.println("C sending " + this.getClass() + " packet");
		writeC(0x05);
		writeD(ok1);
		writeD(ok2);
		writeC(0x0);// not used in l2j
	}
}
