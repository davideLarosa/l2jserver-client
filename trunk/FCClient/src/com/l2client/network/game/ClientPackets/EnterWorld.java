package com.l2client.network.game.ClientPackets;

/**
 * 0x11 EnterWorld package, sent on intendig to enter the game world
 * l2j just ignores this one
 */
public class EnterWorld extends GameClientPacket
{
    public EnterWorld()
    {
        writeC(0x11);
//        writeC(0x55);
        writeB(new byte[32]);
        writeD(0);
        writeD(0);
        writeD(0);
        writeD(0);
        writeB(new byte[32]);
        writeD(0);
        //FIXME own IP
        writeC(127);
        writeC(0);
        writeC(0);
        writeC(1);
        writeB(new byte[16]);
    }
}
