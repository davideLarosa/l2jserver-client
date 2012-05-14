package com.l2client.network.game.ClientPackets;


/**
 *        
 *         4A
_degree = readD();
_side = readD();
 *
*/
public class StartRotating extends GameClientPacket {

	public StartRotating(int deg, int side){
		writeC(0x4a);
		writeD(deg);
		writeD(side);		
	}
}
