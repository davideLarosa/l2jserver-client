package com.l2client.network.game.ServerPackets;

import com.l2client.gui.GameController;
import com.l2client.gui.GuiController;

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

		GuiController.getInstance().removeAll();
		GameController.getInstance().doCharSelection();
		GuiController.getInstance().showInfoDialog("Character created succesfully");
	}
}
