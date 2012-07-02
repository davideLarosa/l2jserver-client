package com.l2client.network.game.ClientPackets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Abstract base class for all packets to be sent to the server. 
 * Methods for filing the internal ByteArrayOutputStream are provided
 *
 */
public abstract class GameClientPacket
{
    ByteArrayOutputStream _bao;
    
    /**
     * Standard constructor
     */
    protected GameClientPacket()
    {
        _bao = new ByteArrayOutputStream();
    }

    /**
     * Writes a (32 bit value) into the bytearray
     * @param value Int value to be buffered
     */
    protected void writeD(int value)
    {
        _bao.write(value &0xff);
        _bao.write(value >> 8 &0xff);
        _bao.write(value >> 16 &0xff);
        _bao.write(value >> 24 &0xff);
    }

    /**
     * Writes a (16 bit value) into the bytearray
     * @param value Int value to be buffered
     */
    protected void writeH(int value)
    {
        _bao.write(value &0xff);
        _bao.write(value >> 8 &0xff);
    }

    /**
     * Writes a byte (8 bit value) into the bytearray
     * @param value Int value to be buffered
     */
    protected void writeC(int value)
    {
        _bao.write(value &0xff);
    }

    /**
     * Writes a (64 bit value) into the byte array
     * @param value Double value to be buffered
     */
    protected void writeF(double org)
    {
        long value = Double.doubleToRawLongBits(org);
        _bao.write((int)(value &0xff));
        _bao.write((int)(value >> 8 &0xff));
        _bao.write((int)(value >> 16 &0xff));
        _bao.write((int)(value >> 24 &0xff));
        _bao.write((int)(value >> 32 &0xff));
        _bao.write((int)(value >> 40 &0xff));
        _bao.write((int)(value >> 48 &0xff));
        _bao.write((int)(value >> 56 &0xff));
    }

    /**
     * Writes a String into the byte array as a UTF-16LE String, terminated by two 0 bytes (or a UTF16 NULL)
     * @param text String representing the text to be written
     */
    protected void writeS(String text)
    {
        try
        {
            if (text != null)
            {
                _bao.write(text.getBytes("UTF-16LE"));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        _bao.write(0);
        _bao.write(0);
    }

    /**
     * Writes a raw byte array into the buffer
     * @param array Byte array to be written
     */
    protected void writeB(byte[] array)
    {
        try
        {
            _bao.write(array);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Length of the currently filled buffer
     * @return Int value representing the current fill size of the buffer
     */
    public int getLength()
    {
        return _bao.size();
    }

    /**
     * Returns the raw bytes from the buffer
     * @return A newly created byte array
     */
    public byte[] getBytes()
    {
        return _bao.toByteArray();
    }
}