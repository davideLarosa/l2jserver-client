package com.l2client.network.game.ServerPackets;

public class AutoAttackStop extends GameServerPacket {

	@Override
	public void handlePacket() {
		log.fine("Read from Server "+this.getClass().getSimpleName());
		int tgt = readD();
		//FIXME remove target 	//FIXME remove of healthbar and label when target died or no longer selected!!
		log.severe("AUTOATTACK STOPPED on+"+tgt);
	}

}
