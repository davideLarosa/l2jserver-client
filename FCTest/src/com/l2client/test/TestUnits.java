package com.l2client.test;

import com.l2client.controller.area.Tile;
import com.l2client.model.l2j.ServerValues;

public class TestUnits {

	public static void main(String[] args){
		System.out.println(ServerValues.getServerString(-9933.133f, 25.606129f, 8348.487f));
		System.out.println(ServerValues.getServerString(-9943.955f, 20.350714f, 8310.191f));
		System.out.println(ServerValues.getServerString(-9920.653f, 17.862331f, 8280.138f));
		System.out.println(ServerValues.getServerString(-9895.944f, 16.703045f, 8292.451f));
		System.out.println(ServerValues.getServerString(-9852.93f, 14.763317f, 8227.26f));
		System.out.println(ServerValues.getServerString(-9846.71f, 15.108395f, 8258.238f));
		
		System.out.println(ServerValues.getServerString(-4458.625f,-194f,16141.9375f));
		
		System.out.println("testarea "+ServerValues.getServerString(-9916.567f, 33.88786f, 8376.85f));
		
		//spawnPoints.xml
		System.out.println("human fighter "+ ServerValues.getClientString(-71453,258305,-3104));
		System.out.println("jme tile          "+ Tile.getTileFromWorldXPosition((int) ServerValues.getClientCoordX(-71453))+
				"/"+Tile.getTileFromWorldZPosition((int) ServerValues.getClientCoordZ(258305)));
		System.out.println("l2j tile          "+ Tile.getTileFromWorldXPosition((int) ServerValues.getClientCoordX(-71453))/8+
				"/"+Tile.getTileFromWorldZPosition((int) ServerValues.getClientCoordZ(258305))/8);
		
		System.out.println("human mage "+ ServerValues.getClientString(-90918,248070,-3570));
		System.out.println("jme tile          "+ Tile.getTileFromWorldXPosition((int) ServerValues.getClientCoordX(-90918))+
				"/"+Tile.getTileFromWorldZPosition((int) ServerValues.getClientCoordZ(248070)));
		System.out.println("l2j tile          "+ Tile.getTileFromWorldXPosition((int) ServerValues.getClientCoordX(-90918))/8+
				"/"+Tile.getTileFromWorldZPosition((int) ServerValues.getClientCoordZ(248070))/8);
		
		System.out.println("elf fighter/mage "+ ServerValues.getClientString(46115,41141,-3440));
		System.out.println("jme tile          "+ Tile.getTileFromWorldXPosition((int) ServerValues.getClientCoordX(46115))+
				"/"+Tile.getTileFromWorldZPosition((int) ServerValues.getClientCoordZ(41141)));
		System.out.println("l2j tile          "+ Tile.getTileFromWorldXPosition((int) ServerValues.getClientCoordX(46115))/8+
				"/"+Tile.getTileFromWorldZPosition((int) ServerValues.getClientCoordZ(41141))/8);
		
		System.out.println("darkelf fighter/mage "+ ServerValues.getClientString(28456, 10997, -4224));
		System.out.println("jme tile          "+ Tile.getTileFromWorldXPosition((int) ServerValues.getClientCoordX(28456))+
				"/"+Tile.getTileFromWorldZPosition((int) ServerValues.getClientCoordZ(10997)));
		System.out.println("l2j tile          "+ Tile.getTileFromWorldXPosition((int) ServerValues.getClientCoordX(28456))/8+
				"/"+Tile.getTileFromWorldZPosition((int) ServerValues.getClientCoordZ(10997))/8);
		
		System.out.println("dwarf "+ ServerValues.getClientString(108512,-174026,-400));
		System.out.println("jme tile          "+ Tile.getTileFromWorldXPosition((int) ServerValues.getClientCoordX(108512))+
				"/"+Tile.getTileFromWorldZPosition((int) ServerValues.getClientCoordZ(-174026)));
		System.out.println("l2j tile          "+ Tile.getTileFromWorldXPosition((int) ServerValues.getClientCoordX(108512))/8+
				"/"+Tile.getTileFromWorldZPosition((int) ServerValues.getClientCoordZ(-174026))/8);
		
		System.out.println("orc fighter/mage "+ ServerValues.getClientString(-56693,-113610,-690));
		System.out.println("jme tile          "+ Tile.getTileFromWorldXPosition((int) ServerValues.getClientCoordX(-56693))+
				"/"+Tile.getTileFromWorldZPosition((int) ServerValues.getClientCoordZ(-113610)));
		System.out.println("l2j tile          "+ Tile.getTileFromWorldXPosition((int) ServerValues.getClientCoordX(-56693))/8+
				"/"+Tile.getTileFromWorldZPosition((int) ServerValues.getClientCoordZ(-113610))/8);
		
		System.out.println("kamael "+ ServerValues.getClientString(-125464,37776,1152));
		System.out.println("jme tile          "+ Tile.getTileFromWorldXPosition((int) ServerValues.getClientCoordX(-125464))+
				"/"+Tile.getTileFromWorldZPosition((int) ServerValues.getClientCoordZ(37776)));	
		System.out.println("l2j tile          "+ Tile.getTileFromWorldXPosition((int) ServerValues.getClientCoordX(-125464))/8+
				"/"+Tile.getTileFromWorldZPosition((int) ServerValues.getClientCoordZ(37776))/8);
		
		}
}
