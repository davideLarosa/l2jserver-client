package com.l2client.network.game.ServerPackets;


/**
 * A SystemMessage 0x62
 * 
 */
public class SystemMessage extends GameServerPacket
{
	
	private static final byte TYPE_SYSTEM_STRING = 13;
	private static final byte TYPE_PLAYER_NAME = 12;
	// id 11 - unknown
	private static final byte TYPE_INSTANCE_NAME = 10;
	private static final byte TYPE_ELEMENT_NAME = 9;
	// id 8 - same as 3
	private static final byte TYPE_ZONE_NAME = 7;
	private static final byte TYPE_ITEM_NUMBER = 6;
	private static final byte TYPE_CASTLE_NAME = 5;
	private static final byte TYPE_SKILL_NAME = 4;
	private static final byte TYPE_ITEM_NAME = 3;
	private static final byte TYPE_NPC_NAME = 2;
	private static final byte TYPE_NUMBER = 1;
	private static final byte TYPE_TEXT = 0;
	/**
	 * Triggers the display action, by using GuiController.getInstance().showErrorDialog
	 */
	public  void handlePacket()
	{
		int id = readD();//MessageId
		int pars = readD();//# of Params
		//TODO read Parameters of SystemMessage
		//..
		
		if(pars <= 0) {
			log.fine("Read from Server "+this.getClass().getSimpleName()+" with ID:"+id+" Params:"+pars);
			return;
		}
		String [] arr = new String[pars];
		
		StringBuilder bui = new StringBuilder("Read from Server ").append(
				this.getClass().getSimpleName()).append(" with ID:").append(id).append(" Params:").append(pars);
		
		for (int i = 0; i < pars; i++)
		{
			int typ = readD();
			
			switch (typ)
			{
				case TYPE_TEXT:
				case TYPE_PLAYER_NAME:
				{
					arr[i] = readS();
					break;
				}
				
				case TYPE_ITEM_NUMBER:
				{
					arr[i] = Long.toString(readQ());
					break;
				}
				
				case TYPE_ITEM_NAME:
				case TYPE_CASTLE_NAME:
				case TYPE_NUMBER:
				case TYPE_NPC_NAME:
				case TYPE_ELEMENT_NAME:
				case TYPE_SYSTEM_STRING:
				case TYPE_INSTANCE_NAME:
				{
					arr[i] = Integer.toString(readD());
					break;
				}
				
				case TYPE_SKILL_NAME:
				{
					//skilID + Skill level
					arr[i] = Integer.toString(readD())+"_"+Integer.toString(readD());
					break;
				}
				
				case TYPE_ZONE_NAME:
				{
					// x, y, z of zone
					arr[i] = Integer.toString(readD())+"_"+Integer.toString(readD())+"_"+Integer.toString(readD());
					break;
				}
				default:
					arr[i] = "TODO";
			}
			
			bui.append(arr[i]);
		}
		
		log.fine(bui.toString());
		
	}

}
