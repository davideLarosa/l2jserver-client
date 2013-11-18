package com.l2client.network.game.ServerPackets;

import com.l2client.model.l2j.ServerValues;
import com.l2client.model.network.EntityData;

/**
 * Actualized data for a player character on entering world etc. Currently we
 * create a new entity data model and pas it over to the pc handler for updating
 * the currently selected char with the provided data. Currently there seems to
 * be way more information than needed here.
 */
public final class UserInfo extends GameServerPacket {
	@Override
	public void handlePacket() {
		log.fine("Read from Server "+this.getClass().getSimpleName());
		EntityData p = new EntityData();
		int x = readD();
		int y = readD();
		int z = readD();

		p.setX(ServerValues.getClientCoordX(x));
		//reverted jme uses Y as up
		p.setY(ServerValues.getClientCoordY(z));
		p.setZ(ServerValues.getClientCoordZ(y));
		p.setServerZ(z);

		readD();// airshipID
		p.setObjectId(readD());
		log.finer("User "+p.getObjectId()+" at " + x + "," + z + "," + y + " placed in world at "+ServerValues.getClientString(x, y, z));
		p.setName(readS());
		p.setRace(readD());
		p.setSex(readD());

		p.setClassId(readD());

		p.setLevel(readD());
		p.setExp(readQ());
		readF();//high five xp %
		readD();// setSTR
		readD();// setDEX
		readD();// setCON
		readD();// setINT
		readD();// setWIT
		readD();// setMEN
		p.setMaxHp(readD());
		p.setCurrentHp(readD());
		p.setMaxMp(readD());
		p.setCurrentMp(readD());
		p.setSp(readD());
		readD();// currentLoad
		readD();// maxLoad

		readD();// ActiveWeaponItem 20 no weapon, 40 weapon equipped
		// FIXME paper doll entries are omitted at the moment
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();

		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();

		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();

		readD();// MaxTalismanCount
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();
		readD();

		readD();
		readD();

		readD();

		readD();
		readD();// Karma

		// from NpcInfo
		int speed = readD();// run speed
		int nSpeed = readD();// walk speed
		readD();// swim run speed
		readD();// swim walk speed
		readD();// fly run speed
		readD();// fly walk speed
		readD();// fly run speed
		readD();// fly walk speed
		float mult = (float) readF();// movement speed multiplier

		if (mult < 0.00001f)
			mult = 1.0f;
		// check mult should it ever be 0 ??
		p.setWalkSpeed(mult*ServerValues.getClientScaled(nSpeed));
		p.setRunSpeed(mult*ServerValues.getClientScaled(speed));

		readF();// attack speed multiplier

		readF();// collision radius
		readF();// collision heights

		p.setHairStyle(readD());
		p.setHairColor(readD());
		p.setFace(readD());
		p.setGM(readD() != 0);
		p.setTitle(readS());

		readD();// clanId
		readD();// clanCrestId
		readD();// allyId
		readD();// allyCrestId
		readD();// relation
		readC();// mount type
		readC();// privateStoreType
		readC();// hasDwarvenCraft 1=true
		readD();// pk kills
		readD();// pvp kills

		for (int id = readH()/* cubic size */; id > 0; id--)
			readH();// cubic

		readC();// 1-find party members
		readD();// AbnormalEffect
		readC();// is flying mounted
		readD();// clanPrivileges
		readH();// recommendations remaining
		readH();// recommendations received
		readD();// mountNpcId = activeChar.getMountNpcId() + 1000000 or 0
		readH();// inventory limit

		p.setClassId(readD());// done twice ?? classId
		readD();// special effects? circles around player...
		readD();// MaxCp
		readD();// CurrentCp
		readC();// is mounted ?
		readC();// team circle 1=blue, 2=red

		readD();// clanCrestLargeId
		readC();// is noble symbol on char menu ctrl+I
		readC();// gm or hero: Hero Aura

		readC();// Fishing Mode
		readD();// fishing x
		readD();// fishing y
		readD();// fishing z
		readD();// Name color

		readC();// is running, changes the Speed display on Status Window

		readD();// pledgeClass changes the text above CP on Status Window
		readD();// pledgeType

		readD();// titleColor
		readD();// cursed weapon id
		p.setTransformId(readD());

		readH();// attackAttribute
		readH();// attack attackAttribute
		readH();// defense fire
		readH();// defense water
		readH();// defense wind
		readH();// defense wind
		readH();// defense holy
		readH();// defense dark

		readD();// agathion id

		readD();// fame
		readD();
		readD();// Vitality Points
		readD();// special effect
		//NO Longer here
//		readD();//disguised

//		readD();//territory

		_client.getCharHandler().updateUserInfo(p);
	}
}
