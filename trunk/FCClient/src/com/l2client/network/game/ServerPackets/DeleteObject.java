package com.l2client.network.game.ServerPackets;


/**
 * Delete an npc
 		writeC(0x08);
		writeD(_objectId);
		writeD(0x00); // c2
 */
public final class DeleteObject extends GameServerPacket {

	@Override
	public void handlePacket() {
		log.finer("Read from Server "+this.getClass().getSimpleName());
		int obj = readD();
		_client.getNpcHandler().remove(obj);
		log.fine("Delete of objectid "+obj);

	}
}
