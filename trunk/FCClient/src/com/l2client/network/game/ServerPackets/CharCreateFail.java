package com.l2client.network.game.ServerPackets;

import com.l2client.app.Singleton;

/**
 * Displays a dialog showing a message of a failed character creation displaying the reason.
 * 
 */
public class CharCreateFail extends GameServerPacket
{
	/**
	 * Triggers the display action, by using Singleton.get().getGuiController().showInfoDialog
	 */
	public  void handlePacket()
	{
		log.fine("Read from Server "+this.getClass().getSimpleName());
		int error = readD();
		switch(error){
		case 0x01: Singleton.get().getGuiController().showInfoDialog("Character creation failed: Too many characters");break;
		case 0x02:Singleton.get().getGuiController().showInfoDialog("Character creation failed: Name already used");break;
		case 0x03: Singleton.get().getGuiController().showInfoDialog("Character creation failed: Name longer than 16 chars");
		default: Singleton.get().getGuiController().showInfoDialog("Character creation failed");
		}
	}

}
