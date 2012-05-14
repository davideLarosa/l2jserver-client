package com.l2client.network.game.ServerPackets;

import com.l2client.gui.GameController;
import com.l2client.model.l2j.ServerValues;
import com.l2client.model.network.EntityData;

/**
 * Character information for the character selection scene.
 * The order of the characters resembles the slot id's for the selection later
 * Only a minimum is read at the moment
 */
//TODO elaborate
public class CharSelectionInfo extends GameServerPacket {

	@Override
	public void handlePacket() {
		log.fine("Read from Server "
				+ this.getClass().getSimpleName());

		// Number of characters available
		int size = readD();// writeD(size);

		// if 0 no new chars may be created
		readD();
		readC();

		// for each char read on
		for (int i = 0; i < size; i++) {
			EntityData ch = new EntityData();

			ch.setName(readS());
			ch.setCharId(readD());
			readS();
			int id = readD();
			if (id != getClientFacade().sessionId)
				getClientFacade().sessionId = id;
			ch.setClanId(readD());
			readD();

			ch.setSex(readD());

			ch.setRace(readD());
			ch.setClassId(readD());

			// active ??
			readD();
			
			int x = readD();
			int y = readD();
			int z = readD();
			ch.setX(ServerValues.getClientCoord(x));
			//reverted jme uses Y as up
			ch.setY(ServerValues.getClientCoord(z));
			ch.setZ(ServerValues.getClientCoord(y));

			ch.setCurrentHp(readF());
			ch.setCurrentMp(readF());

			ch.setSp(readD());
			ch.setExp(readQ());
			readF();//high five exp %
			ch.setLevel(readD());

			ch.setKarma(readD());
			ch.setPkKills(readD());

			ch.setPvPKills(readD());
			readD();
			readD();
			readD();
			readD();
			readD();
			readD();
			readD();

			// FIXME TR add paperdoll support
			readD();
			readD();
			readD();
			readD();
			readD();
			readD();
			readD();
			readD();
			readD();
			readD();
			readD();
			readD();
			readD();
			readD();
			readD();
			readD();
			readD();
			readD();
			readD();
			readD();
			readD();
			readD();
			readD();
			readD();
			readD();
			readD();

			ch.setHairStyle(readD());
			ch.setHairColor(readD());
			ch.setFace(readD());
			ch.setMaxHp(readF());
			ch.setMaxMp(readF());
			ch.setDeleteTimer(readD());
			readD();

			// FIXME active
			readD();
			readC();
			readH();
			readH();
//			ch.setAugmentationId(readD());
			readD();
			// Freya by Vistall:
			readD(); // npdid - 16024    Tame Tiny Baby Kookaburra        A9E89C
			readD(); // level
			readD(); // ?
			readD(); // food? - 1200
			readF(); // max Hp
			readF(); // cur Hp
			
			// High Five by Vistall:
			readD();	// H5 Vitality

			getClientFacade().getCharHandler().addChar(ch);
		}
		GameController.getInstance().doCharSelection();

	}

}
