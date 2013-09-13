package com.l2client.network.game.ClientPackets;


/**
 * 0x23 Request command processing on server
 */
public final class RequestBypassToServer extends GameClientPacket {

	/**
	 * Sends a command to the server (mostly admin commands like: admin_move_to x y z)
	 * @param command command to be sent to the server
	 */
	public RequestBypassToServer(String command){
		
		writeC(0x23);		
		writeS(command);
	}
}
