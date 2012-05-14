package com.l2client.network.login.ServerPackets;

import com.l2client.model.network.GameServerInfo;

/**
 * Play OK received, disconnect from login server, send game key and session id to login into the gameserver
 *
 */
public class PlayOK extends LoginServerPacket {

	GameServerInfo server = null;
	
	public PlayOK(GameServerInfo gameServerInfo) {
		server = gameServerInfo;
	}

	@Override
	public void handlePacket() {
		loginHandler.playOK1 = readD();
		loginHandler.playOK2 = readD();
		loginHandler.doDisconnect(true, server.ip,
				server.port);
	}

}
