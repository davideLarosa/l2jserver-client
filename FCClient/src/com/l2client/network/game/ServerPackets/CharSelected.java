package com.l2client.network.game.ServerPackets;

import com.l2client.model.l2j.ServerValues;
import com.l2client.model.network.EntityData;
import com.l2client.network.game.ClientPackets.EnterWorld;

/**
 * The char selected package confirms the selection of a character. The current implementation also triggers the enter world event which should be separated and triggered at user will
 * Reads only a minimum of information at the moment
 */
//FIXME move EnterWorld request to own action
public class CharSelected extends GameServerPacket {
	public void handlePacket() {
		log.fine("Read from Server "+this.getClass().getSimpleName());
		EntityData ch = _client.getCharHandler()
				.getSelectedChar();
		// FIXME needed or is it just the same stuff from char selectioninfopackages
		ch.setName(readS());
		ch.setCharId(readD());
		ch.setTitle(readS());
		int id = readD();
		if (id != _client.sessionId)
			_client.sessionId = id;

		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		
		int x = readD();
		int y = readD();
		int z = readD();
		ch.setX(ServerValues.getClientCoord(x));
		//reverted jme uses Y as up
		ch.setY(ServerValues.getClientCoord(z));
		ch.setZ(ServerValues.getClientCoord(y));

		readD();
		readD();
		readD();
		readQ();
		readD();
		readD();
		readD();
		// rest ignored
		
		//TODO RequestManorList, RequestAllFortressInfo, RequestKeyMapping
		_client.sendGamePacket(new EnterWorld());
	}
}
