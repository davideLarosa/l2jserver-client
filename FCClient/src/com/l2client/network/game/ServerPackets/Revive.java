package com.l2client.network.game.ServerPackets;

import com.l2client.animsystem.jme.actions.CallActions;
import com.l2client.app.Singleton;

public class Revive extends GameServerPacket {

	@Override
	public void handlePacket() {
		log.fine("Read from Server "+this.getClass().getSimpleName());
		int objId = readD();
		if(_client.getCharHandler().getSelectedObjectId() == objId){
			//TODO remove grayscaling and view swirling and rimlighting of everything
			Singleton.get().getAnimSystem().callAction(CallActions.Revive, objId);
		}
	}

}
