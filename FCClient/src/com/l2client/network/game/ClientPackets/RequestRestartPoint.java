package com.l2client.network.game.ClientPackets;

public class RequestRestartPoint extends GameClientPacket {
	
	/**
	 * 0: // village
	 * 1: // to clanhall
	 * 2: // to castle
	 * 3: // to fortress
	 * 4: // to siege HQ
	 * 5: // Fixed or Player is a festival participant
	 * 27: // to jail
	 * @param location
	 */
	public RequestRestartPoint(int location){
		writeC(0x7d);
		writeD(location);
	}
}
