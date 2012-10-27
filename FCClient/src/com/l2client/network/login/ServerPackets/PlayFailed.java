package com.l2client.network.login.ServerPackets;

import com.l2client.app.Singleton;

/**
 * Just display a message that play failed
 *
 */
//TODO insert fail code lookup
public class PlayFailed extends LoginServerPacket {

	@Override
	public void handlePacket() {
		Singleton.get().getGuiController().showErrorDialog("Received login to game server failed:"+readC()+":"+readC());
	}

}
