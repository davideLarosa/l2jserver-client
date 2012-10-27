package com.l2client.network.game.ServerPackets;

import com.l2client.app.Singleton;

/**
 * Displays a dialog showing that the server closed the connection.
 * 
 */
public class ServerClose extends GameServerPacket
{
	/**
	 * Triggers the display action, by using Singleton.get().getGuiController().showErrorDialog
	 */
	public  void handlePacket()
	{
		log.fine("Read from Server "+this.getClass().getSimpleName());
		Singleton.get().getGameController().finish();
		Singleton.get().getGuiController().showErrorDialog("Server closed the connection!");		
	}

}
