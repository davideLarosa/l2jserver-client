package com.l2client.network.login.ServerPackets;

import com.l2client.network.login.ClientPackets.RequestServerList;


/**
 * Login OK
 * 0x03
 */
public class LoginOk extends LoginServerPacket {

	@Override
	public void handlePacket() {
		this.loginHandler.loginOK1 = readD();
		this.loginHandler.loginOK2 = readD();
//		writeD(0x00);
//		writeD(0x00);
//		writeD(0x000003ea);
//		writeD(0x00);
//		writeD(0x00);
//		writeD(0x00);
//		writeB(new byte[16]);
		log.fine("Received login ok:"+loginHandler.loginOK1+":"+loginHandler.loginOK2);
		loginHandler.sendPacket(new RequestServerList(loginHandler.loginOK1, loginHandler.loginOK2));
	}

}
