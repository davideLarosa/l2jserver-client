package com.l2client.network.game.ServerPackets;

import com.l2client.app.Singleton;

/**
 * Displays a dialog showing a message of a successful character creation.
 * 
 */
public class CharCreateOk extends GameServerPacket
{
	/**
	 * Displays the dialog by using GuiController. Removes also all dialogs and initializes the
	 * character selection sequence
	 */
	public  void handlePacket()
	{
		log.fine("Read from Server "+this.getClass().getSimpleName());
		Singleton.get().getGuiController().removeAll();
		Singleton.get().getGameController().doCharSelection();
		Singleton.get().getGuiController().showInfoDialog("Character created succesfully");
	}
}
