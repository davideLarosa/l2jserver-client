package com.l2client.network.game.ServerPackets;

import com.l2client.model.l2j.ServerValues;
import com.l2client.model.network.NpcData;

/**
 * NpcInfo is a summary of npc related information. On receiving this message a new
 * npc data model is created and handed over to the npc handler
 *
 */
public final class NpcInfo extends GameServerPacket {

	@Override
	public void handlePacket() {
		log.finer("Read from Server "+this.getClass().getSimpleName());
		NpcData n = new NpcData();
		n.setObjectId(readD());
		n.setTemplateId(readD());
		//attackable
		readD();
		int x = readD();
		int y = readD();
		int z = readD();
log.finer("NPC at "+x+","+y+","+z+" placed in world at "+ServerValues.getClientString(x, y, z));
		n.setX(ServerValues.getClientCoordX(x));
		//reverted jme uses Y as up
		n.setY(ServerValues.getClientCoordY(z));
		n.setZ(ServerValues.getClientCoordZ(y));
		n.setHeading(ServerValues.getClientHeading(readD()));
		readD();
		readD();
		readD();
		//TODO diversify speed data, currently the fastest is stored
		int speed = readD();//run speed
		int nSpeed = readD();//walk speed
		//in L2J the following pairs are all the same as the two above
		readD();//swim run speed
		readD();//swim walk speed
		readD();//fly run speed
		readD();//fly walk speed
		readD();//fly run speed
		readD();//fly walk speed
		float mult = (float)readF();//movement speed multiplier

		if(mult <0.00001f)
			mult = 1.0f;
		n.setWalkSpeed(mult*ServerValues.getClientScaled(nSpeed));
		n.setRunSpeed(mult*ServerValues.getClientScaled(speed));
		readF();//attack speed multiplier
		readF();//collision radius
		readF();//collision height

		readD();//right hand weapon
		readD();//chest
		readD();//left hand weapon
		readC();//display name above char 1=true ?
		n.setRunning(readC()>0?true:false);//is running 1=true
		readC();//is in combat 1=true
		readC();//is like dead 1 = true
		readC();//is summoned 0=teleported 1=default 2=summoned
		readD();// -1 high five name
		n.setName(readS());
		readD();// -1 high five name
		n.setTitle(readS());
		readD();//Title color 0=client default
		readD();//0
		readD();//pvp flag

		readD();//AbnormalEffect
		readD();//clan id
		readD();//crest id
		readD();//0
		readD();//0
		readC();//is flying
		readC();//title color 0=client default ?

		readF();//collision radius?
		readF();//collision height ?
		readD();//0
		readD();//is flying
		readD();//0
		readD();//Pet form and skills
		readC();//1
		readC();//1
		readD();//special effect
		readD();//display effect
		

		_client.getNpcHandler().add(n);

	}
}
