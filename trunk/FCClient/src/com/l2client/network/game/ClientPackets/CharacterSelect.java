package com.l2client.network.game.ClientPackets;

/**
 * 0x12 CharacterSelect package, sent in the hall, on selection of the char to be used for playing the game
 */
public class CharacterSelect extends GameClientPacket
{
	/**
	 * Constructor of the CharacterSelect package
	 * @param slot	Int value of the char slot to be used 0...N
	 */
    public CharacterSelect(int slot)
	{
                writeC(0x12);
                writeD(slot);
                writeH(0);
                writeD(0);
                writeD(0);
                writeD(0);
	}
}
