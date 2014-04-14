package com.l2client.network.game.ServerPackets;


/**
 * Spawn an Item
 * 		writeC(0x16);
		writeD(_charObjId);
		writeD(_item.getObjectId());
		writeD(_item.getDisplayId());
		
		writeD(_x);
		writeD(_y);
		writeD(_z);
		// only show item count if it is a stackable item
		writeD(_stackable);
		writeQ(_count);
		
		writeD(0x01); // unknown
 */
public final class DropItem extends GameServerPacket {

	@Override
	public void handlePacket() {
		log.finer("Read from Server "+this.getClass().getSimpleName());
		int charId = readD();
		int objId = readD();
		int itemId = readD();
		int x = readD();
		int y = readD();
		int z = readD();
		boolean stackable = readD() != 0x00 ? true : false;
		long count = readQ();
		_client.getItemHandler().addItem(charId, objId, itemId, x,y,z,stackable, count);
		log.fine("Spawn of item id "+itemId);
		
		System.out.println("Drop of item:"+itemId);

	}
}
