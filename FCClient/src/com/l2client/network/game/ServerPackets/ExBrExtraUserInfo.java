package com.l2client.network.game.ServerPackets;

/**
 * Pretty dumb pack with little gain besided the haircut id :-( low prio
 * this is not only sent for the own user, but also for all other players encountered
 *
 * The packet is read but the content is ignored.
 */
public class ExBrExtraUserInfo extends GameServerPacket
{
    @Override
	public void handlePacket()
    {
    	log.fine("Read from Server "+this.getClass().getSimpleName());
     	readH();//remove sub id
    	readD();//char id
    	readD();//hair info
    	
    }
}
