package com.l2client.network.game.ClientPackets;

/**
 * 0x00 Gracefull logout package
 *
 */
public final class Appearing extends GameClientPacket
{
	public Appearing(){
		writeC(0x3a);
	}
}