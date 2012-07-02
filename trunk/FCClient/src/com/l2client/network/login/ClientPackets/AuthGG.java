package com.l2client.network.login.ClientPackets;


/**
 * Requests a game guard authentification packet (just the session id at the moment)
 * 
 */
//WARNING if you change the packetsize please also change the packetsize in LoginHandler::requestAuthGG
//FIXME actually the AuthGG sends 42 bytes, l2j just reads the 20 bytes from the sid on (5* readD())
public class AuthGG extends LoginClientPacket {

	public AuthGG(int sessionId) {

		//GG uses different fill mode
		fillmode = Fillmode.SI;

		writeC(0x07);//packet id
		writeD(sessionId);//the session id

		writeD(0);//following packets are just filler
		writeD(0);
		writeD(0);
		writeD(0);

		writeD(0);
		writeD(0);
		writeD(0);
		writeD(0);
		writeC(0);
		writeC(0);
		writeC(0);
	}
}
