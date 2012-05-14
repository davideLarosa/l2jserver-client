package com.l2client.network.login.ServerPackets;

import com.l2client.network.login.ClientPackets.AuthGG;

/**
 * Initial packet received, containing login session id, the server protocol
 * the login password rsa key and the session blowfish key 
 * 
 * Will send a Auth Game Guard request in response
 *
 */
public class Init extends LoginServerPacket {
	
	@Override
	public void handlePacket() {
		int sid = readD();
//		loginHandler.setSessionId(sid);
		//FIXME check if loginsrver version > 50547 (epilogue server version)
		readD();//		loginHandler.setServerProtocol(readD());
		
		loginHandler.setRSAKey(readB(128));
		readD();
		readD();
		readD();
		readD();
		loginHandler.setBlowfishKey(readB(16));
		
		
		AuthGG agg = new AuthGG(sid);

		loginHandler.sendPacket(agg);
	}
}
