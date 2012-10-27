package com.l2client.network.game.ServerPackets;

import com.l2client.app.Singleton;
import com.l2client.component.L2JComponent;
import com.l2client.model.network.EntityData;

public class StatusUpdate extends GameServerPacket {
    public static final int LEVEL = 0x01;
    public static final int EXP = 0x02;
    public static final int STR = 0x03;
    public static final int DEX = 0x04;
    public static final int CON = 0x05;
    public static final int INT = 0x06;
    public static final int WIT = 0x07;
    public static final int MEN = 0x08;

    public static final int CUR_HP = 0x09;
    public static final int MAX_HP = 0x0a;
    public static final int CUR_MP = 0x0b;
    public static final int MAX_MP = 0x0c;

    public static final int SP = 0x0d;
    public static final int CUR_LOAD = 0x0e;
    public static final int MAX_LOAD = 0x0f;

    public static final int P_ATK = 0x11;
    public static final int ATK_SPD = 0x12;
    public static final int P_DEF = 0x13;
    public static final int EVASION = 0x14;
    public static final int ACCURACY = 0x15;
    public static final int CRITICAL = 0x16;
    public static final int M_ATK = 0x17;
    public static final int CAST_SPD = 0x18;
    public static final int M_DEF = 0x19;
    public static final int PVP_FLAG = 0x1a;
    public static final int KARMA = 0x1b;

    public static final int CUR_CP = 0x21;
    public static final int MAX_CP = 0x22;
	@Override
	public void handlePacket()
	{
		log.finer("Read from Server "+this.getClass().getSimpleName());
        int id = readD();
        int attributes = readD();
        
        L2JComponent co = (L2JComponent) Singleton.get().getEntityManager().getComponent(id, L2JComponent.class);
        if(co != null) {
        	EntityData e = co.l2jEntity;
        for (int i=0;i<attributes;i++)
        {
            int attrID = readD();
            int val = readD();
            handleStatusUpdate(e, attrID, val);
        }
        } else {
        	log.severe("Received update but no L2JComponent found for entity:"+id);
        }
	}
	
	private void handleStatusUpdate(EntityData dat, int attribute, int value){
		switch(attribute){
	    case LEVEL: dat.setLevel(value);log.finest("UPDATE LVL:"+value+" for "+dat.getObjectId());break;
	    case EXP : dat.setExp(value);log.finest("UPDATE XP:"+value+" for "+dat.getObjectId());break;
	    case STR : break;
	    case DEX : break;
	    case CON : break;
	    case INT : break;
	    case WIT : break;
	    case MEN : break;

	    case CUR_HP : dat.setCurrentHp(value);log.fine("UPDATE CHP:"+value+" for "+dat.getObjectId());break;
	    case MAX_HP : dat.setMaxHp(value);break;
	    case CUR_MP : dat.setCurrentMp(value);log.finest("UPDATE CMP:"+value+" for "+dat.getObjectId());break;
	    case MAX_MP : dat.setMaxMp(value);break;

	    case SP : dat.setSp(value);break;
	    case CUR_LOAD : break;
	    case MAX_LOAD : break;

	    case P_ATK : break;
	    case ATK_SPD : break;
	    case P_DEF : break;
	    case EVASION : break;
	    case ACCURACY : break;
	    case CRITICAL : break;
	    case M_ATK : break;
	    case CAST_SPD : break;
	    case M_DEF : break;
	    case PVP_FLAG : break;
	    case KARMA : dat.setKarma(value);break;

	    case CUR_CP : break;
	    case MAX_CP : break;
		}
	}
}
