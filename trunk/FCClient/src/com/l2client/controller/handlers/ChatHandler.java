package com.l2client.controller.handlers;

import java.util.logging.Logger;

import com.l2client.app.Singleton;
import com.l2client.gui.dialogs.ChatPanel;
import com.l2client.model.network.ClientFacade;
import com.l2client.network.game.ClientPackets.Say;

/**
 * Administration of npc data. An npc is every entity besides the player
 * character. From the current player other players are npcs too.
 * 
 */
//FIXME refactor to use a model view controller pattern (model for chat messages passed between gui and handler)
public class ChatHandler {

	private static Logger log = Logger.getLogger(ChatHandler.class.getName());
	private ChatPanel panel;

	/**
	 * Parse a message string and send the message types are: 
	 * ALL = 0; 
	 * SHOUT = 1; // ! 
	 * TELL = 2; // " 
	 * PARTY = 3; // # 
	 * CLAN = 4; // @ 
	 * TRADE = 8; // +
	 * ALLIANCE = 9; // $ 
	 * HERO_VOICE = 17; // %
	 * 
	 * 
	 * @param msg
	 *            A raw message, first char can be type
	 */
	public void sendMessage(String msg) {
		int type = 0;
		int index = 1;
		String text = "";
		String tgt = null;
		switch (msg.charAt(0)) {
		case '!':
			type = 1;
			break;
		case '"':
			type = 2;
			index = msg.indexOf(' ');
			break;
		case '#':
			type = 3;
			break;
		case '@':
			type = 4;
			break;
		case '+':
			type = 8;
			break;
		case '$':
			type = 9;
			break;
		case '%':
			type = 17;
			break;
		}
		if (type != 0)
			text = msg.substring(index);
		else
			text = msg;

		if (type == 2) {
			tgt = msg.substring(0, index);
		}

		Singleton.get().getClientFacade().sendGamePacket(new Say(type, text, tgt));
		log.finest("Sending chat message of type:" + type + " text:" + text
				+ " target:" + tgt);
	}

	public void receiveMessage(int objectID, int type, String cName, String msg) {
//		 * ALL = 0; 
//		 * SHOUT = 1; // ! 
//		 * TELL = 2; // " 
//		 * PARTY = 3; // # 
//		 * CLAN = 4; // @ 
//		 * TRADE = 8; // +
//		 * ALLIANCE = 9; // $ 
//		 * HERO_VOICE = 17; // %
		if(panel!=null){
			switch(type){
			case 3:panel.addTextParty(cName!=null?cName+": "+msg:msg);break;
			case 4:panel.addTextClan(cName!=null?cName+": "+msg:msg);break;
			case 8:panel.addTextTrade(cName!=null?cName+": "+msg:msg);break;
			case 9:panel.addTextAlliance(cName!=null?cName+": "+msg:msg);break;
			default: panel.addTextAll(cName!=null?cName+": "+msg:msg);
			}
		}
	}
	
	public void setChatPanel(ChatPanel pan){
		this.panel = pan;
	}
}
