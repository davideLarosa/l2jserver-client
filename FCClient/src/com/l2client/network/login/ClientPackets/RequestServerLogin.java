package com.l2client.network.login.ClientPackets;

/**
 * A packet for requesting login with a given session id on a specific game server
 * 
 */
public class RequestServerLogin  extends LoginClientPacket{
	
	/**
	 * Constructor of the login request
	 * @param sessionId	just the current session id
	 * @param serverId	the game server id to connect to
	 */
    public RequestServerLogin(int sessionKey1, int sessionKey2, int serverId){
    	System.out.println("C sending "+this.getClass()+" packet");
    	writeC(0x02);
        writeD(sessionKey1);
        writeD(sessionKey2);
        writeC(serverId);
    }
}
