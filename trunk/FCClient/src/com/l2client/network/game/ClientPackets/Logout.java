package com.l2client.network.game.ClientPackets;

/**
 * 0x00 Gracefull logout package
 *
 */
public final class Logout extends GameClientPacket
{
	public Logout(){
		writeC(0x00);
	}
}