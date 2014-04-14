package com.l2client.network.game.ServerPackets;

import com.l2client.app.Singleton;
import com.l2client.component.L2JComponent;


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
		Singleton s = Singleton.get();
		L2JComponent l2j = (L2JComponent) s.getEntityManager().getComponent(obj, L2JComponent.class);
		if(l2j != null){
			//TODO refactor item/npc handling, only components which should themselves know where to remove from, also refactor item/npc/pc instancing
			if(l2j.l2jItem != null) {
				_client.getItemHandler().removeItem(obj);
				log.fine("Delete of item id "+obj);
			} else {
				_client.getNpcHandler().remove(obj);
				log.fine("Delete of npc id "+obj);
			}
		} else {
			log.severe("Delete of objectid not possible no L2JComponent for:"+obj);
			s.getEntityManager().dumpComponents(obj);
		}

	}
}
