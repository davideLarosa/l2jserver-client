package com.l2client.network.login.ServerPackets;

import com.l2client.gui.GuiController;

/**
 * Just display a message that play failed
 *
 */
//TODO insert fail code lookup
public class PlayFailed extends LoginServerPacket {

	@Override
	public void handlePacket() {
		GuiController.getInstance().showErrorDialog("Received login to game server failed:"+readC()+":"+readC());
	}

}
