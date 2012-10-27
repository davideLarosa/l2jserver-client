package com.l2client.network.login.ServerPackets;

import com.l2client.app.Singleton;

/**
 * Just display a message that login failed
 *
 */
//TODO insert fail code lookup
public class LoginFailed extends LoginServerPacket {

	@Override
	public void handlePacket() {
		Singleton.get().getGuiController().showErrorDialog("Received login failed:"+readC()+":"+readC());
	}

}
