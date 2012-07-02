package com.l2client.network.game.ClientPackets;

/**
 * 0x49 chat messages
 */
public final class Say extends GameClientPacket
{
	/**
	 * 
	 * @param type
	 * @param message
	 * @param target
	 */
	public Say(int type, String message, String target){
		writeC(0x49);
		writeS(message);
		writeD(type);
		if(target != null && target.length() > 0)
			writeS(target);
	}
}