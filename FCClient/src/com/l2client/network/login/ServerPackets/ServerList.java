package com.l2client.network.login.ServerPackets;

import java.util.logging.Level;

import com.l2client.model.network.GameServerInfo;

/**
 * Received a list of game server informations @link {@link GameServerInfo}
 * 
 */
public class ServerList extends LoginServerPacket {
	
	/*
		writeC(0x04);
		writeC(_servers.size());
		writeC(_lastServer);
		for (ServerData server : _servers)
		{
			writeC(server._serverId); // server id
			
			writeC(server._ip[0] & 0xff);
			writeC(server._ip[1] & 0xff);
			writeC(server._ip[2] & 0xff);
			writeC(server._ip[3] & 0xff);
			
			writeD(server._port);
			writeC(server._ageLimit); // Age Limit 0, 15, 18
			writeC(server._pvp ? 0x01 : 0x00);
			writeH(server._currentPlayers);
			writeH(server._maxPlayers);
			writeC(server._status == ServerStatus.STATUS_DOWN ? 0x00 : 0x01);
			writeD(server._serverType); // 1: Normal, 2: Relax, 4: Public Test, 8: No Label, 16: Character Creation Restricted, 32: Event, 64: Free
			writeC(server._brackets ? 0x01 : 0x00);
		}
		writeH(0x00); // unknown
		if (_charsOnServers != null)
		{
			writeC(_charsOnServers.size());
			for (int servId : _charsOnServers.keySet())
			{
				writeC(servId);
				writeC(_charsOnServers.get(servId));
				if (_charsToDelete == null || !_charsToDelete.containsKey(servId))
					writeC(0x00);
				else
				{
					writeC(_charsToDelete.get(servId).length);
					for (long deleteTime : _charsToDelete.get(servId))
					{
						writeD((int)((deleteTime-System.currentTimeMillis())/1000));
					}
				}
			}
		}
		else
			writeC(0x00);
	 * */

	@Override
	public void handlePacket() {
		int serverCount = readC();
		readC();//lastServer ?!?! what is this for?
		GameServerInfo[] gameServers = new GameServerInfo[serverCount];

		for (int i = 0; i < serverCount; i++) {
			gameServers[i] = new GameServerInfo();
			gameServers[i].id = readC();
			gameServers[i].ip = new StringBuilder().append(readC()).append(".").append(readC()).append(".").append(readC()).append(".").append(readC()).toString();
			gameServers[i].port = readD();
			gameServers[i].ageLimit = readC();
			gameServers[i].pvp = readC() != 0 ? true : false;
			gameServers[i].players = readH();
			gameServers[i].maxplayers = readH();
			gameServers[i].online = readC() != 0 ? true : false;
			gameServers[i].type = readD(); // 1: Normal, 2: Relax, 4: Public Test, 8: No Label, 16: Character Creation Restricted, 32: Event, 64: Free
			gameServers[i].brackets = readC() != 0 ? true : false;
//			if(Level.FINEST.equals(log.getLevel()))
				log.info(gameServers[i].toFullString());
		}
		
		readH(); // unknown
		//TODO chars on servers
//		if (readC() > 0) //chars on servers follow
//		{
//			int chars = readC();
//			for(int i = 0 ; i < chars;i++){
//				readC();//serverID
//				readC();//chars on that server
//				int dels = readC();//chars to delete 
//				if(dels > 0 ){
//					for(int j = 0; j < dels; j++)
//						readD();//deleteTime-currentTile ?!?!? wtf
//				}
//			};
//		}
		loginHandler.setGameServers(gameServers);
		loginHandler.onServerListReceived(gameServers);
	}

}
