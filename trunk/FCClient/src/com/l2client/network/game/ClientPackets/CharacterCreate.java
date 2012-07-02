package com.l2client.network.game.ClientPackets;

import com.l2client.model.network.NewCharSummary;

/**
 * 0x0C CharacterCreate Package, used for creation of a new character
 *
 */
public final class CharacterCreate extends GameClientPacket {
	
	/**
	 * Constructor based on a @see NewCharSummary. Entries used are 
	 * name
	 * race
	 * sex
	 * classId
	 * 
	 * @param ch
	 */
	public CharacterCreate(NewCharSummary ch) {
		writeC(0x0C);
		writeS(ch.name);// _name = readS();
		writeD(ch.race);// _race = readD();
		writeD(ch.sex);// _sex = (byte)readD();
		writeD(ch.classId);// _classId = readD();
		writeD(12);// _int = readD();
		writeD(12);// _str = readD();
		writeD(12);// _con = readD();
		writeD(12);// _men = readD();
		writeD(12);// _dex = readD();
		writeD(12);// _wit = readD();
		writeD(1);// _hairStyle = (byte)readD();
		writeD(1);// _hairColor = (byte)readD();
		writeD(1);// _face = (byte)readD();
	}
}
