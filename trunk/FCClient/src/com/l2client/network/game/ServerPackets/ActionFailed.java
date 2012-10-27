package com.l2client.network.game.ServerPackets;


public class ActionFailed extends GameServerPacket {

	@Override
	public void handlePacket()
	{
		log.info("whatever you did it failed.");
	}
}
