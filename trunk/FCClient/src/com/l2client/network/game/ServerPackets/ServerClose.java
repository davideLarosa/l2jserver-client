package com.l2client.network.game.ServerPackets;

import com.l2client.gui.GameController;
import com.l2client.gui.GuiController;

/**
 * Displays a dialog showing that the server closed the connection.
 * 
 */
public class ServerClose extends GameServerPacket
{
	/**
	 * Triggers the display action, by using GuiController.getInstance().showErrorDialog
	 */
	public  void handlePacket()
	{
		log.fine("Read from Server "+this.getClass().getSimpleName());
		GameController.getInstance().finish();
		GuiController.getInstance().showErrorDialog("Server closed the connection!");		
	}

}
