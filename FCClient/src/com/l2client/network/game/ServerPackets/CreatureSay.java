package com.l2client.network.game.ServerPackets;


/**
 * 0x4a CreatureSay chat message received
 * 
 */
//FIXME do I receive my own send message?
public class CreatureSay extends GameServerPacket
{
	/**
	 * Fill the chat handler of the new chat message
	 */
	public  void handlePacket()
	{
		log.fine("Read from Server "+this.getClass().getSimpleName());

		int objectID = readD();
		int type = readD();
		String cName = "";
		if(type != 11) //Say2.BOAT uses ID :-<
			cName = "TODO: CharID: "+readD();//TODO replace CHAR ID with looked up name
		else
			cName = readS();
		readD();//high five npc string
		//FIXME currently we rely on the fact that there is a parameter or a say string, this can blow
		String msg = readS();

//		if (_text != null)
//			writeS(_text);
//		else
//		{
//			if (_parameters != null)
//			{
//				for (String s : _parameters)
//					writeS(s);
//			}
//		}
		
		getClientFacade().getChatHandler().receiveMessage(objectID,type,cName,msg);
	}
}
