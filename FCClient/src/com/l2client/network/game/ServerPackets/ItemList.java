package com.l2client.network.game.ServerPackets;

import com.l2client.app.Singleton;
import com.l2client.gui.CharacterController;
import com.l2client.model.l2j.ItemInstance;

/**
 * The list of items
 */
public class ItemList extends GameServerPacket {

	@Override
	public void handlePacket() {
		log.fine("Read from Server "+this.getClass().getSimpleName());
		CharacterController co = Singleton.get().getCharController();
		readH();//show window (0x01, 0x00)
		int items = readH();
		for(int i=0; i< items;i++){
			ItemInstance inst = new ItemInstance();
			inst.objectId = readD();
			inst.itemId = readD();
			inst.slot = readD();
			inst.count = readQ();
			inst.itemType = readH();
			inst.customType1 = readH();
			inst.equipped = readH();
			inst.bodyPart = readD();
			inst.enchantLevel = readH();
			inst.customType2 = readH();
			inst.augmentationId = readD();//0x00, or id of augment
			inst.mana = readD();
			inst.remainingTime = readD();//-9999 (not timed item) or remaining time in miliseconds
			inst.attackElementType = readH();
			inst.attackElementPower = readH();
			inst.attackElementAttr1 = readH();
			inst.attackElementAttr2 = readH();
			inst.attackElementAttr3 = readH();
			inst.attackElementAttr4 = readH();
			inst.attackElementAttr5 = readH();
			inst.attackElementAttr6 = readH();
			readH();//0x00
			readH();//0x00
			readH();//0x00
			co.addInventoryItem(inst);
			inst.name = Singleton.get().getDataManager().getItemDescription(inst.itemId);
			log.finer("Item added:"+inst.name+ " "+inst);
		}
		int inventory = readH();
		for(int i=0;i<inventory;i++){
			int id = readD();
			co.addInventoryBlockItem(id);
			log.finer("BlockItem added:"+id);
		}

	}

}
