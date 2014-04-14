package com.l2client.network.game.ServerPackets;


/**
 * Spawn an Item
 * 		writeC(0x05);
		writeD(_objectId);
		writeD(_itemId);
		
		writeD(_x);
		writeD(_y);
		writeD(_z);
		// only show item count if it is a stackable item
		writeD(_stackable);
		writeQ(_count);
		writeD(0x00); // c2
		writeD(0x00); // freya unk
 */
public final class SpawnItem extends GameServerPacket {

	@Override
	public void handlePacket() {
		log.finer("Read from Server "+this.getClass().getSimpleName());
		int obj = readD();
		int item = readD();
		int x = readD();
		int y = readD();
		int z = readD();
		boolean stackable = readD() != 0x00 ? true : false;
		long count = readQ();
		_client.getNpcHandler().remove(obj);
		log.fine("Spawn of item id "+item);
		
		System.out.println("Spawn of item id:"+item);

	}
}
