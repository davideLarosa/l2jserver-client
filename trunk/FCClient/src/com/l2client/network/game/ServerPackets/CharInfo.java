package com.l2client.network.game.ServerPackets;

import com.l2client.model.l2j.ServerValues;
import com.l2client.model.network.PlayerData;


/**
 * Package with information about other players, which will read some initial data
 * and pass it over to the npc handler. 
 * 
 */
public class CharInfo extends GameServerPacket {

	/**
	 * Reads x,y,z, objectId, name, race and classid only at the moment
	 */
	@Override
	public void handlePacket() {
		log.fine("Read from Server "+this.getClass().getSimpleName());
		PlayerData p = new PlayerData();
		p.setX(ServerValues.getClientCoordX(readD()));
		//reverted jme uses Y as up
		p.setZ(ServerValues.getClientCoordZ(readD()));
		p.setY(ServerValues.getClientCoordY(readD()));
		readD();
		p.setObjectId(readD());
		p.setName(readS());
		p.setRace(readD());
		p.setClassId(readD());
		//FIXME read in the rest if CharInfo: paperdoll etc,.. whole lot more is in
		_client.getNpcHandler().add(p);
	}
}
